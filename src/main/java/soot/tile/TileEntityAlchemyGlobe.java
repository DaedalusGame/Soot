package soot.tile;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.capabilities.Capability;
import soot.block.BlockAlchemyGlobe;
import soot.upgrade.UpgradeAlchemyGlobe;
import teamroots.embers.api.alchemy.AspectList;
import teamroots.embers.api.capabilities.EmbersCapabilities;
import teamroots.embers.particle.ParticleUtil;
import teamroots.embers.tileentity.TileEntityAlchemyPedestal;
import teamroots.embers.util.AlchemyUtil;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class TileEntityAlchemyGlobe extends TileEntity implements ITickable {
    public static final int UPDATE_INTERVAL = 20;

    AspectList aspectList = new AspectList();

    int lifeTime;
    int nextCheck;

    UpgradeAlchemyGlobe upgrade;

    public TileEntityAlchemyGlobe()
    {
        super();
        upgrade = new UpgradeAlchemyGlobe(this);
    }

    public AspectList getAspects() {
        return aspectList;
    }

    public void consumeAsh() {
        List<TileEntityAlchemyPedestal> pedestals = AlchemyUtil.getNearbyPedestals(getWorld(),getPos());
        for(TileEntityAlchemyPedestal pedestal : pedestals)
        {
            pedestal.inventory.setStackInSlot(0, ItemStack.EMPTY);
        }
    }

    public EnumFacing getFacing()
    {
        IBlockState state = world.getBlockState(pos);
        if(state.getBlock() instanceof BlockAlchemyGlobe)
            return state.getValue(BlockAlchemyGlobe.FACING);
        return null;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        return (capability == EmbersCapabilities.UPGRADE_PROVIDER_CAPABILITY && getFacing().getOpposite() == facing) || super.hasCapability(capability, facing);
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        return (capability == EmbersCapabilities.UPGRADE_PROVIDER_CAPABILITY && getFacing().getOpposite() == facing) ? (T)upgrade : super.getCapability(capability, facing);
    }

    @Override
    public void update() {
        lifeTime++;

        if(lifeTime > nextCheck)
        {
            nextCheck = lifeTime + UPDATE_INTERVAL;
            List<TileEntityAlchemyPedestal> pedestals = AlchemyUtil.getNearbyPedestals(getWorld(),getPos());

            aspectList.reset();
            aspectList.collect(pedestals);
        }
        if(world.isRemote)
        {
            Random random = this.world.rand;
            for (int i = 0; i < 3; i ++){
                ParticleUtil.spawnParticleTyrfing(world, pos.getX()+0.5f, pos.getY()+0.5f, pos.getZ()+0.5f, (random.nextFloat()-0.5f)*0.01f, (random.nextFloat()-0.5f)*0.007f, (random.nextFloat()-0.5f)*0.01f, 2.5f, 20);
            }
            for (int i = 0; i < 5; i ++){
                float velocityFactor = random.nextFloat() * 1.0f + 0.5f;
                double yaw = random.nextFloat() * Math.PI * 2;
                double pitch = random.nextFloat() * Math.PI * 2;
                float dist = 0.4f;
                Vec3d vector = new Vec3d(Math.sin(yaw)*Math.cos(pitch),Math.sin(pitch),Math.cos(yaw)*Math.cos(pitch)).normalize();
                ParticleUtil.spawnParticleGlow(world, pos.getX() + 0.5f + (float)vector.x * dist, pos.getY() + 0.5f + (float)vector.y * dist, pos.getZ() + 0.5f + (float)vector.z * dist, (float)vector.x * -0.01f*velocityFactor, (float)vector.y * -0.01f*velocityFactor, (float)vector.z * -0.01f*velocityFactor,64,32,90, (1.5f / velocityFactor) * 0.5f, (int)(50/velocityFactor));
            }
        }

        //TODO: Spawn particles
    }
}
