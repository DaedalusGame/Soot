package soot.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import soot.tile.TileEntityAlchemyGlobe;
import teamroots.embers.particle.ParticleUtil;

import javax.annotation.Nullable;
import java.util.Random;

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
    public boolean isFullBlock(IBlockState state) {
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
