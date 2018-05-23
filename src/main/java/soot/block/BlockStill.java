package soot.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import soot.tile.TileEntityStillBase;
import soot.tile.TileEntityStillTip;
import teamroots.embers.tileentity.ITileEntityBase;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class BlockStill extends Block {
    public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
    public static final PropertyInteger PART = PropertyInteger.create("part",0,2);

    public BlockStill() {
        super(Material.IRON);
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
        return new BlockStateContainer(this, FACING, PART);
    }

    @Override
    public int getMetaFromState(IBlockState state){
        return state.getValue(FACING).getHorizontalIndex() | state.getValue(PART) << 2;
    }

    @Override
    public IBlockState getStateFromMeta(int meta){
        return getDefaultState().withProperty(FACING, EnumFacing.getHorizontal(meta & 3)).withProperty(PART, meta >> 2);
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        int part = state.getValue(PART);
        EnumFacing facing = state.getValue(FACING);
        switch (part) {
            case (2):
                switch (facing) {
                    case NORTH:
                        return new AxisAlignedBB(0.3125, 0, 0.3125, 0.6875, 0.875, 1.0);
                    case SOUTH:
                        return new AxisAlignedBB(0.3125, 0, 0.0, 0.6875, 0.875, 0.6875);
                    case EAST:
                        return new AxisAlignedBB(0.0, 0, 0.3125, 0.6875, 0.875, 0.6875);
                    case WEST:
                        return new AxisAlignedBB(0.3125, 0, 0.3125, 1.0, 0.875, 0.6875);
                    default:
                        return new AxisAlignedBB(0.3125, 0, 0.3125, 0.6875, 0.875, 0.6875);
                }

            case (1):
                return new AxisAlignedBB(0.1875, 0, 0.1875, 0.8125, 0.875, 0.8125);
            default:
                return FULL_BLOCK_AABB;
        }
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        updateConnections(state, worldIn, pos);
        super.neighborChanged(state, worldIn, pos, blockIn, fromPos);
    }

    public void updateConnections(IBlockState state, World worldIn, BlockPos pos) {
        TileEntity tile = worldIn.getTileEntity(pos);
        if(tile instanceof TileEntityStillTip) {
            TileEntityStillTip tip = (TileEntityStillTip) tile;
            tip.updateConnections();
        }
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

    private boolean isIntact(World world, BlockPos pos)
    {
        IBlockState state = world.getBlockState(pos);
        int part = state.getValue(PART);
        EnumFacing facing = state.getValue(FACING);
        BlockPos base = getBase(pos, part, facing);
        return world.getBlockState(base).getBlock() == this
                && world.getBlockState(base.up()).getBlock() == this
                && world.getBlockState(base.up().offset(facing)).getBlock() == this;
    }

    private void deleteParts(World world, BlockPos pos, boolean notifyTile, EntityPlayer player)
    {
        IBlockState state = world.getBlockState(pos);
        int part = state.getValue(PART);
        EnumFacing facing = state.getValue(FACING);
        BlockPos base = getBase(pos, part, facing);

        deletePart(world, base, notifyTile, player);
        deletePart(world, base.up(), notifyTile, player);
        deletePart(world, base.up().offset(facing), notifyTile, player);
    }

    private void deletePart(World world, BlockPos pos, boolean notifyTile, EntityPlayer player) {
        if(notifyTile)
        {
            TileEntity te = world.getTileEntity(pos);
            if(te instanceof ITileEntityBase)
                ((ITileEntityBase) te).breakBlock(world,pos,world.getBlockState(pos),player);
        }
        world.setBlockToAir(pos);
    }

    private BlockPos getBase(BlockPos pos, int part, EnumFacing facing) {
        BlockPos base = pos;
        switch (part)
        {
            case(2):base = base.down().offset(facing.getOpposite()); break;
            case(1):base = base.down(); break;
        }
        return base;
    }

    public BlockPos getTip(BlockPos pos, IBlockState state) {
        EnumFacing facing = state.getValue(FACING);
        BlockPos base = getBase(pos,state.getValue(PART),facing);
        return base.up().offset(facing);
    }

    @Override
    public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        return new ArrayList<>();
    }

    @Override
    public void onBlockAdded(World world, BlockPos pos, IBlockState state){
        int part = state.getValue(PART);
        EnumFacing facing = state.getValue(FACING);
        BlockPos base = pos;
        switch (part)
        {
            case(2):base = base.down().offset(facing.getOpposite()); break;
            case(1):base = base.down(); break;
        }

        if(part != 0) world.setBlockState(base,state.withProperty(PART,0));
        if(part != 1) world.setBlockState(base.up(),state.withProperty(PART,1));
        if(part != 2) world.setBlockState(base.up().offset(facing),state.withProperty(PART,2));
        updateConnections(state,world,pos);
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return state.getValue(PART) != 1;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        int part = state.getValue(PART);
        switch(part)
        {
            case(0): return new TileEntityStillBase();
            case(2): return new TileEntityStillTip();
            default: return null;
        }
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
