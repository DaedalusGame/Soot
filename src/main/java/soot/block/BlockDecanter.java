package soot.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import soot.tile.TileEntityDecanterBottom;
import soot.tile.TileEntityDecanterTop;
import teamroots.embers.tileentity.ITileEntityBase;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class BlockDecanter extends Block {
    public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
    public static final PropertyBool TOP = PropertyBool.create("top");

    public BlockDecanter() {
        super(Material.ROCK);
    }

    @Override
    public boolean getUseNeighborBrightness(IBlockState state) {
        return true;
    }

    @Override
    public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer) {
        return BlockRenderLayer.SOLID == layer || BlockRenderLayer.TRANSLUCENT == layer;
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
    public BlockStateContainer createBlockState(){
        return new BlockStateContainer(this, FACING, TOP);
    }

    @Override
    public int getMetaFromState(IBlockState state){
        return state.getValue(FACING).getHorizontalIndex() | (state.getValue(TOP) ? 4 : 0);
    }

    @Override
    public IBlockState getStateFromMeta(int meta){
        return getDefaultState().withProperty(FACING, EnumFacing.getHorizontal(meta & 3)).withProperty(TOP, (meta >> 2) > 0);
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        boolean isTop = state.getValue(TOP);
        if(isTop)
            return new AxisAlignedBB(0.1875,0.0, 0.1875, 0.8125,1.0, 0.8125);
        else
            return FULL_BLOCK_AABB;
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        super.neighborChanged(state, worldIn, pos, blockIn, fromPos);
    }


    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ){
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof ITileEntityBase) {
            return ((ITileEntityBase) tileEntity).activate(world, pos, state, player, hand, side, hitX, hitY, hitZ);
        }
        return false;
    }

    @Override
    public void onBlockExploded(World world, BlockPos pos, Explosion explosion){
        if (!world.isRemote){
            world.spawnEntity(new EntityItem(world,pos.getX()+0.5,pos.getY()+0.5,pos.getZ()+0.5,new ItemStack(this,1,0)));
        }
        deleteParts(world,pos, true, null);
    }

    @Override
    public void onBlockHarvested(World world, BlockPos pos, IBlockState state, EntityPlayer player){
        if (!world.isRemote && !player.capabilities.isCreativeMode && isIntact(world,pos)){
            world.spawnEntity(new EntityItem(world,pos.getX()+0.5,pos.getY()+0.5,pos.getZ()+0.5,new ItemStack(this,1,0)));
        }
        deleteParts(world,pos, true, player);
    }

    private boolean isIntact(World world, BlockPos pos) {
        IBlockState state = world.getBlockState(pos);
        BlockPos base = getBase(pos, state.getValue(TOP));
        return world.getBlockState(base).getBlock() == this
                && world.getBlockState(base.up()).getBlock() == this;
    }

    private void deleteParts(World world, BlockPos pos, boolean notifyTile, EntityPlayer player) {
        IBlockState state = world.getBlockState(pos);
        BlockPos base = getBase(pos, state.getValue(TOP));

        deletePart(world, base, notifyTile, player);
        deletePart(world, base.up(), notifyTile, player);
    }

    private void deletePart(World world, BlockPos pos, boolean notifyTile, EntityPlayer player) {
        if(notifyTile) {
            TileEntity te = world.getTileEntity(pos);
            if(te instanceof ITileEntityBase)
                ((ITileEntityBase) te).breakBlock(world,pos,world.getBlockState(pos),player);
        }
        world.setBlockToAir(pos);
    }

    private BlockPos getBase(BlockPos pos, boolean isTop) {
        if(isTop)
            return pos.down();
        else
            return pos;
    }

    @Override
    public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        return new ArrayList<>();
    }

    @Override
    public boolean canPlaceBlockAt(World world, BlockPos pos){
        if (world.getBlockState(pos.up()) == Blocks.AIR.getDefaultState()){
            return true;
        }
        return false;
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer){
        EnumFacing placementFacing = facing.getOpposite();
        if(placementFacing.getAxis().isVertical())
            placementFacing = placer.getHorizontalFacing();
        return getDefaultState().withProperty(TOP,false).withProperty(FACING, placementFacing);
    }

    @Override
    public void onBlockAdded(World world, BlockPos pos, IBlockState state){
        boolean isTop = state.getValue(TOP);
        BlockPos base = pos;
        if(isTop)
            base = base.down();

        if(isTop) world.setBlockState(base,state.withProperty(TOP,false));
        if(!isTop) world.setBlockState(base.up(),state.withProperty(TOP,true));
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        boolean isTop = state.getValue(TOP);
        if(isTop)
            return new TileEntityDecanterTop();
        else
            return new TileEntityDecanterBottom();
    }

    @Override
    public IBlockState withRotation(IBlockState state, Rotation rot) {
        return state.withProperty(FACING, rot.rotate(state.getValue(FACING)));
    }

    @Override
    public IBlockState withMirror(IBlockState state, Mirror mirrorIn) {
        return state.withProperty(FACING, mirrorIn.mirror(state.getValue(FACING)));
    }
}
