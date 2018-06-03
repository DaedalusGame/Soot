package soot.tile;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import soot.block.BlockSulfurOre;
import soot.util.MiscUtil;
import teamroots.embers.particle.ParticleUtil;

import java.util.Random;

public class TileEntitySulfurOre extends TileEntity implements ITickable {
    int lifetime;
    Random random = new Random();

    @Override
    public void update() {
        lifetime++;
        IBlockState state = world.getBlockState(pos);
        IBlockState topState = world.getBlockState(pos);
        if(state.getBlock() instanceof BlockSulfurOre && !topState.isFullBlock()) {
            if (lifetime > 70) {
                world.setBlockState(pos, state.withProperty(BlockSulfurOre.ACTIVE, false));
                return;
            }

            double x = getPos().getX() + 0.5;
            double y = getPos().getY() + 0.5;
            double z = getPos().getZ() + 0.5;
            AxisAlignedBB aabb = null;
            if (lifetime < 10) {
                aabb = new AxisAlignedBB(x - 0.5, y, z - 0.5, x + 0.5, y + 2.0, z + 0.5);
            } else if(lifetime < 50) {
                aabb = new AxisAlignedBB(x - 1.5, y, z - 1.5, x + 1.5, y + 3.0, z + 1.5);
                if(world.isRemote)
                for(int i = 0; i < 8; i++)
                ParticleUtil.spawnParticleGlow(getWorld(), (float)x + (random.nextFloat()-0.5f)*3.0f, (float)y + (random.nextFloat()-0.5f)*3.0f + 2.0f, (float)z + (random.nextFloat()-0.5f)*3.0f, (random.nextFloat()-0.5f)*0.1f, (random.nextFloat()-0.5f)*0.1f, (random.nextFloat()-0.5f)*0.1f, 64, 64, 16, 4.0f+random.nextFloat()*4.0f, 24);
            }
            if(lifetime < 25 && world.isRemote)
                ParticleUtil.spawnParticleGlow(getWorld(), (float)x, (float)y + 0.7f, (float)z, (random.nextFloat()-0.5f)*0.04f, 0.1f, (random.nextFloat()-0.5f)*0.04f, 64, 64, 16, 3.0f, 48);

            if(lifetime < 50 && lifetime % 5 == 0 && !world.isRemote) {
                for(EntityLivingBase entity : world.getEntitiesWithinAABB(EntityLivingBase.class,aabb)) {
                    MiscUtil.degradeEquipment(entity, 3);
                }
            }
        }
    }

}
