package soot.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import soot.tile.TileEntityDistillationPipe;

import javax.annotation.Nullable;

public class BlockDistillationPipe extends Block {
    public static final PropertyDirection FACING = BlockHorizontal.FACING;

    public BlockDistillationPipe(Material blockMaterialIn) {
        super(blockMaterialIn);
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileEntityDistillationPipe();
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
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public boolean getUseNeighborBrightness(IBlockState state) {
        return true;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return new AxisAlignedBB(0.375, 0, 0.375, 0.625, 1.0, 0.625);
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        checkAndDrop(worldIn,state,pos);
        super.neighborChanged(state, worldIn, pos, blockIn, fromPos);
    }

    public void checkAndDrop(World worldIn, IBlockState state, BlockPos pos) {
        if (!worldIn.isRemote && !canBlockStay(worldIn, pos, state.getValue(FACING))) {
            this.dropBlockAsItem(worldIn, pos, state, 0);
            worldIn.setBlockToAir(pos);
        }
    }

    public boolean canBlockStay(World worldIn, BlockPos pos, EnumFacing attachDir) {
        BlockPos attachPos = pos.offset(attachDir);
        IBlockState attach = worldIn.getBlockState(attachPos);
        return attach.getBlock() instanceof BlockStill && attach.getValue(BlockStill.PART) == 0;
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
        boolean canStay = false;

        for(EnumFacing facing : EnumFacing.VALUES)
            if(canBlockStay(worldIn,pos,facing))
                canStay = true;

        return super.canPlaceBlockAt(worldIn, pos) && canStay;
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        EnumFacing attachDir;
        if (facing.getAxis() != EnumFacing.Axis.Y)
            attachDir = facing;
        else
            attachDir = placer.getHorizontalFacing();

        if (canBlockStay(world, pos, attachDir))
            return this.getDefaultState().withProperty(FACING, attachDir);
        else {
            for (EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL)
                if (canBlockStay(world, pos, enumfacing))
                    return this.getDefaultState().withProperty(FACING, enumfacing);

            return this.getDefaultState();
        }
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(FACING,EnumFacing.getHorizontal(meta & 3));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(FACING).getHorizontalIndex() & 3;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this,FACING);
    }
}
