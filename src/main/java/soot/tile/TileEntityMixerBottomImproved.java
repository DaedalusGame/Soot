package soot.tile;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import soot.capability.CapabilityMixerOutput;
import soot.util.ResettingFluidTank;
import teamroots.embers.recipe.FluidMixingRecipe;
import teamroots.embers.recipe.RecipeRegistry;
import teamroots.embers.tileentity.TileEntityMixerBottom;
import teamroots.embers.tileentity.TileEntityMixerTop;

import java.util.ArrayList;

public class TileEntityMixerBottomImproved extends TileEntityMixerBottom {
    CapabilityMixerOutput mixerOutput;

    public TileEntityMixerBottomImproved()
    {
        super();
        mixerOutput = new CapabilityMixerOutput(this);
        north = new ResettingFluidTank(8000);
        east = new ResettingFluidTank(8000);
        south = new ResettingFluidTank(8000);
        west = new ResettingFluidTank(8000);
    }

    //Unfortunately I have to do this, the interfaces are all out of whack in the deobf jar
    @Override
    public void update() {
        TileEntityMixerTop top = (TileEntityMixerTop) getWorld().getTileEntity(getPos().up());
        if (top != null) {
            if (top.capability.getEmber() >= 2.0) {
                ArrayList<FluidStack> fluids = new ArrayList<FluidStack>();
                if (north.getFluid() != null) {
                    fluids.add(north.getFluid());
                }
                if (south.getFluid() != null) {
                    fluids.add(south.getFluid());
                }
                if (east.getFluid() != null) {
                    fluids.add(east.getFluid());
                }
                if (west.getFluid() != null) {
                    fluids.add(west.getFluid());
                }
                FluidMixingRecipe recipe = RecipeRegistry.getMixingRecipe(fluids);
                if (recipe != null) {
                    IFluidHandler tank = top.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
                    int amount = tank.fill(recipe.output, false);
                    if (amount != 0) {
                        tank.fill(recipe.output, true);
                        for (FluidStack fluid : fluids) {
                            boolean doContinue = true;
                            for (int j = 0; j < recipe.inputs.size() && doContinue; j++) {
                                if (recipe.inputs.get(j) != null && fluid != null && recipe.inputs.get(j).getFluid() == fluid.getFluid()) {
                                    doContinue = false;
                                    fluid.amount -= recipe.inputs.get(j).amount;
                                }
                            }
                        }
                        top.capability.removeAmount(2.0, true);
                        markDirty();
                        IBlockState state = getWorld().getBlockState(getPos());
                        top.markDirty();
                        IBlockState topState = getWorld().getBlockState(getPos().up());
                    }
                }
            }
        }
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY || super.hasCapability(capability,facing);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && (facing == null || facing.getAxis() == EnumFacing.Axis.Y) ? (T)mixerOutput : super.getCapability(capability, facing);
    }
}
