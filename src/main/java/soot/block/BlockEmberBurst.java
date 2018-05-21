package soot.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import soot.tile.TileEntityEmberBurst;
import teamroots.embers.block.BlockTEBase;
import teamroots.embers.tileentity.ITileEntityBase;
import teamroots.embers.tileentity.TileEntityEmitter;

import javax.annotation.Nullable;

public class BlockEmberBurst extends Block {
    public static final PropertyDirection facing = PropertyDirection.create("facing");

    public BlockEmberBurst(Material material) {
        super(material, MapColor.ADOBE);
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
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
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
    public void onBlockHarvested(World world, BlockPos pos, IBlockState state, EntityPlayer player){
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof ITileEntityBase) {
            ((ITileEntityBase) tileEntity).breakBlock(world,pos,state,player);
        }
    }

    @Override
    public boolean canConnectRedstone(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        return true;
    }

    @Override
    public BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, facing);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(facing).getIndex();
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(facing, EnumFacing.getFront(meta));
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing face, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        return getDefaultState().withProperty(facing, face);
    }

    @Override
    public boolean isSideSolid(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        return side != state.getValue(facing);
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileEntityEmberBurst();
    }

    @Override
    public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof TileEntityEmberBurst) {
            ((TileEntityEmberBurst) tileEntity).updateNeighbors(world);
        }
    }

    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos) {
        TileEntity t = world.getTileEntity(pos);
        if (t instanceof TileEntityEmberBurst) {
            ((TileEntityEmberBurst) t).updateNeighbors(world);
            t.markDirty();
        }
        if (world.isAirBlock(pos.offset(state.getValue(facing), -1))) {
            world.setBlockToAir(pos);
            this.dropBlockAsItem(world, pos, state, 0);
        }
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        switch (state.getValue(facing)) {
            case UP:
                return new AxisAlignedBB(0.25, 0.0, 0.25, 0.75, 1.0, 0.75);
            case DOWN:
                return new AxisAlignedBB(0.25, 0.0, 0.25, 0.75, 1.0, 0.75);
            case NORTH:
                return new AxisAlignedBB(0.25, 0.25, 0.0, 0.75, 0.75, 1.0);
            case SOUTH:
                return new AxisAlignedBB(0.25, 0.25, 0.0, 0.75, 0.75, 1.0);
            case WEST:
                return new AxisAlignedBB(0.0, 0.25, 0.25, 1.0, 0.75, 0.75);
            case EAST:
                return new AxisAlignedBB(0.0, 0.25, 0.25, 1.0, 0.75, 0.75);
        }
        return FULL_BLOCK_AABB;
    }

}
