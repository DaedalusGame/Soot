package soot.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import soot.tile.TileEntityScale;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class BlockScale extends Block {
    public static final PropertyBool TOP = PropertyBool.create("top");
    public static final PropertyBool HORIZONTAL = PropertyBool.create("horizontal");

    public BlockScale(Material blockMaterialIn) {
        super(blockMaterialIn);
        setDefaultState(getDefaultState().withProperty(TOP,false));
    }

    public EnumFacing.Axis getAxis(IBlockState state) {
        return state.getValue(HORIZONTAL) ? EnumFacing.Axis.X : EnumFacing.Axis.Z;
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
    public boolean canProvidePower(IBlockState state) {
        return true;
    }

    @Override
    public int getWeakPower(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        if(side == null || side.getAxis().isVertical())
            return 0; //NOOOOOOOO
        EnumFacing.Axis axis = getAxis(state);
        EnumFacing rotatedSide = side.rotateY();
        if(!state.getValue(TOP) && axis.apply(rotatedSide)) {
            TileEntity tile = world.getTileEntity(pos.up());
            if(tile instanceof TileEntityScale) {
                int diff = ((TileEntityScale) tile).getWeightDifference();
                return (rotatedSide.getAxisDirection() == EnumFacing.AxisDirection.NEGATIVE && diff <= 0) || (rotatedSide.getAxisDirection() == EnumFacing.AxisDirection.POSITIVE && diff >= 0) ? 15 : 0;
            }
        }
        return 0;
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(TOP,(meta & 1) == 1).withProperty(HORIZONTAL,(meta & 2) == 2);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return (state.getValue(TOP) ? 1 : 0) | (state.getValue(HORIZONTAL) ? 2 : 0);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this,TOP,HORIZONTAL);
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        EnumFacing playerFacing = placer.getHorizontalFacing();
        return getDefaultState().withProperty(HORIZONTAL, playerFacing.getAxis() == EnumFacing.Axis.X);
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
        return worldIn.getBlockState(pos.up()).getBlock().isReplaceable(worldIn, pos.up()) && super.canPlaceBlockAt(worldIn, pos);
    }

    @Override
    public void onBlockHarvested(World world, BlockPos pos, IBlockState state, EntityPlayer player){
        boolean top = state.getValue(TOP);
        if ((top && world.getBlockState(pos.down()).getBlock() == this) || (!top && world.getBlockState(pos.up()).getBlock() == this)){
            if (!world.isRemote && !player.capabilities.isCreativeMode){
                world.spawnEntity(new EntityItem(world,pos.getX()+0.5,pos.getY()+0.5,pos.getZ()+0.5,new ItemStack(this,1,0)));
            }
        }
        if (!top)
            world.setBlockToAir(pos.up());
        else
            world.setBlockToAir(pos.down());
    }

    @Override
    public void onBlockExploded(World world, BlockPos pos, Explosion explosion){
        if (!world.isRemote){
            world.spawnEntity(new EntityItem(world,pos.getX()+0.5,pos.getY()+0.5,pos.getZ()+0.5,new ItemStack(this,1,0)));
        }
        IBlockState state = world.getBlockState(pos);
        boolean top = state.getValue(TOP);
        if (!top)
            world.setBlockToAir(pos.up());
        else
            world.setBlockToAir(pos.down());
        world.setBlockToAir(pos);
    }

    @Override
    public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune){
        return new ArrayList<>();
    }

    @Override
    public void onBlockAdded(World world, BlockPos pos, IBlockState state){
        if (!state.getValue(TOP))
            world.setBlockState(pos.up(), state.withProperty(TOP, true));
        else
            world.setBlockState(pos.down(), state.withProperty(TOP, false));
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return state.getValue(TOP);
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return state.getValue(TOP) ? new TileEntityScale() : null;
    }
}
