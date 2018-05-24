package soot.tile;

import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import soot.block.BlockScale;

public class TileEntityScale extends TileEntity implements ITickable {
    int weightDifference = 0;

    public int getWeightDifference() {
        return weightDifference;
    }

    @Override
    public void update() {
        IBlockState state = world.getBlockState(pos);
        if(state.getBlock() instanceof BlockScale) {
            int lastWeightDifference = weightDifference;
            weightDifference = 0;
            BlockScale block = (BlockScale) state.getBlock();
            EnumFacing.Axis axis = block.getAxis(state);
            EnumFacing left = EnumFacing.getFacingFromAxis(EnumFacing.AxisDirection.NEGATIVE, axis);
            EnumFacing right = EnumFacing.getFacingFromAxis(EnumFacing.AxisDirection.POSITIVE, axis);
            TileEntity leftTile = world.getTileEntity(pos.offset(left));
            TileEntity rightTile = world.getTileEntity(pos.offset(right));
            if(leftTile != null && rightTile != null) {
                if(leftTile.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY,right) && rightTile.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY,left)) {
                    weightDifference = compareItemHandlers(leftTile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, right), rightTile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, left));
                } if(leftTile.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY,right) && rightTile.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY,left)) {
                    weightDifference = compareFluidHandlers(leftTile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, right), rightTile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, left));
                }
            }

            if(weightDifference != lastWeightDifference) {
                world.notifyNeighborsOfStateChange(pos.down(),block,false);
            }
        }
    }

    private int compareFluidHandlers(IFluidHandler left, IFluidHandler right) {
        FluidStack leftFluid = left.drain(Integer.MAX_VALUE,false);
        FluidStack rightFluid = right.drain(Integer.MAX_VALUE,false);
        return Integer.compare(leftFluid != null ? leftFluid.amount : 0,rightFluid != null ? rightFluid.amount : 0);
    }

    private int compareItemHandlers(IItemHandler left, IItemHandler right) {
        int leftItems = 0;
        int rightItems = 0;
        for(int i = 0; i < left.getSlots(); i++)
            leftItems += left.getStackInSlot(i).getCount();
        for(int i = 0; i < right.getSlots(); i++)
            rightItems += right.getStackInSlot(i).getCount();
        return Integer.compare(leftItems, rightItems);
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
        return oldState.getBlock() != newSate.getBlock();
    }
}
