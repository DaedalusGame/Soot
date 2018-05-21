package soot.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import soot.util.FluidModifier;
import soot.util.FluidUtil;
import teamroots.embers.block.IDial;
import teamroots.embers.network.PacketHandler;
import teamroots.embers.network.message.MessageTEUpdateRequest;

import java.util.ArrayList;
import java.util.List;

public class BlockAlchemyGauge extends Block implements IDial {
    public static final PropertyDirection FACING = PropertyDirection.create("facing");

    public BlockAlchemyGauge(Material blockMaterialIn) {
        super(blockMaterialIn);
    }

    @Override
    public BlockStateContainer createBlockState(){
        return new BlockStateContainer(this, FACING);
    }

    @Override
    public int getMetaFromState(IBlockState state){
        return state.getValue(FACING).getIndex();
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public IBlockState getStateFromMeta(int meta){
        return getDefaultState().withProperty(FACING, EnumFacing.getFront(meta));
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing face, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer){
        return getDefaultState().withProperty(FACING, face);
    }

    @Override
    public boolean isSideSolid(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side){
        return false;
    }

    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos){
        if (world.isAirBlock(pos.offset(state.getValue(FACING),-1))){
            dropBlockAsItem(world, pos, state, 0);
            world.setBlockToAir(pos);
        }
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos){
        switch (state.getValue(FACING)){
            case UP:
                return new AxisAlignedBB(0.3125,0,0.3125,0.6875,0.125,0.6875);
            case DOWN:
                return new AxisAlignedBB(0.3125,0.875,0.3125,0.6875,1.0,0.6875);
            case NORTH:
                return new AxisAlignedBB(0.3125,0.3125,0.875,0.6875,0.6875,1.0);
            case SOUTH:
                return new AxisAlignedBB(0.3125,0.3125,0,0.6875,0.6875,0.125);
            case WEST:
                return new AxisAlignedBB(0.875,0.3125,0.3125,1.0,0.6875,0.6875);
            case EAST:
                return new AxisAlignedBB(0.0,0.3125,0.3125,0.125,0.6875,0.6875);
        }
        return new AxisAlignedBB(0.25,0,0.25,0.75,0.125,0.75);
    }

    @Override
    public List<String> getDisplayInfo(World world, BlockPos pos, IBlockState state) {
        ArrayList<String> text = new ArrayList<>();
        EnumFacing facing = state.getValue(FACING);
        BlockPos checkPos = pos.offset(facing.getOpposite());
        TileEntity tile = world.getTileEntity(checkPos);
        if (tile != null){
            if (tile.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, facing)){
                IFluidHandler handler = tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, facing);
                if (handler != null){
                    IFluidTankProperties[] properties = handler.getTankProperties();
                    for (IFluidTankProperties property : properties) {
                        FluidStack contents = property.getContents();
                        if (contents != null) {
                            fillBrewData(text, contents);
                        }
                    }
                }
            }
        }
        return text;
    }

    public void fillBrewData(ArrayList<String> text, FluidStack contents) {
        Fluid fluid = contents.getFluid();
        text.add(TextFormatting.BOLD+fluid.getLocalizedName(contents));
        NBTTagCompound compound = FluidUtil.getModifiers(contents);
        for(String key : FluidUtil.SORTED_MODIFIER_KEYS) {
            FluidModifier modifier = FluidUtil.MODIFIERS.get(key);
            if(!modifier.isDefault(compound, fluid)) {
                String formattedText = modifier.getFormattedText(compound, fluid);
                if(!formattedText.isEmpty())
                    text.add(formattedText);
            }
        }
    }

    @Override
    public void updateTEData(World world, IBlockState state, BlockPos pos) {
        EnumFacing facing = state.getValue(FACING);
        TileEntity tile = world.getTileEntity(pos.offset(facing.getOpposite()));
        if (tile != null){
            PacketHandler.INSTANCE.sendToServer(new MessageTEUpdateRequest(Minecraft.getMinecraft().player.getUniqueID(),pos));
        }
    }
}
