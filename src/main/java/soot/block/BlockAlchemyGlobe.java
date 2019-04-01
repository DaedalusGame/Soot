package soot.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import soot.tile.TileEntityAlchemyGlobe;

import javax.annotation.Nullable;

public class BlockAlchemyGlobe extends Block {
    public static final PropertyDirection FACING = PropertyDirection.create("facing");

    public BlockAlchemyGlobe(Material blockMaterialIn) {
        super(blockMaterialIn);
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileEntityAlchemyGlobe();
    }

    /*@Override
    public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random random){

        for (int i = 0; i < 3; i ++){
            ParticleUtil.spawnParticleTyrfing(world, pos.getX()+0.5f, pos.getY()+0.5f, pos.getZ()+0.5f, (random.nextFloat()-0.5f)*0.01f, (random.nextFloat()-0.5f)*0.007f, (random.nextFloat()-0.5f)*0.01f, 2.5f, 120);
        }
        for (int i = 0; i < 5; i ++){
            float velocityFactor = random.nextFloat() * 1.0f + 0.5f;
            double yaw = random.nextFloat() * Math.PI * 2;
            double pitch = random.nextFloat() * Math.PI * 2;
            Vec3d vector = new Vec3d(Math.sin(yaw)*Math.cos(pitch),Math.sin(pitch),Math.cos(yaw)*Math.cos(pitch)).normalize();
            ParticleUtil.spawnParticleGlow(world, pos.getX() + 0.5f + (float)vector.x * 0.5f, pos.getY() + 0.5f + (float)vector.y * 0.5f, pos.getZ() + 0.5f + (float)vector.z * 0.5f, (float)vector.x * -0.01f*velocityFactor, (float)vector.y * -0.01f*velocityFactor, (float)vector.z * -0.01f*velocityFactor,64,32,90, 1.5f / velocityFactor, (int)(50/velocityFactor));
        }
    }*/

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if(!worldIn.isRemote)
        {
            TileEntity tileEntity = worldIn.getTileEntity(pos);
            if(tileEntity instanceof TileEntityAlchemyGlobe) {
                TileEntityAlchemyGlobe globe = (TileEntityAlchemyGlobe) tileEntity;
                globe.activate(playerIn, hand, facing, hitX, hitY, hitZ);
            }
        }
        return true;
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
        return BlockRenderLayer.TRANSLUCENT;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        switch (state.getValue(FACING)) {
            case UP:
                return new AxisAlignedBB(0.125, 0.0, 0.125, 0.875, 0.875, 0.875);
            case DOWN:
                return new AxisAlignedBB(0.125, 0.125, 0.125, 0.875, 1.0, 0.875);
            case NORTH:
                return new AxisAlignedBB(0.125, 0.125, 0.125, 0.875, 0.875, 1.0);
            case SOUTH:
                return new AxisAlignedBB(0.125, 0.125, 0.0, 0.875, 0.875, 0.875);
            case WEST:
                return new AxisAlignedBB(0.125, 0.125, 0.125, 1.0, 0.875, 0.875);
            case EAST:
                return new AxisAlignedBB(0.0, 0.125, 0.125, 0.875, 0.875, 0.875);
        }
        return FULL_BLOCK_AABB;
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        return getDefaultState().withProperty(FACING,facing);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(FACING,EnumFacing.getFront(meta & 7));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(FACING).getIndex() & 7;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this,FACING);
    }
}
