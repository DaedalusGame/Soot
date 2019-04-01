package soot.tile;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.*;
import net.minecraftforge.common.capabilities.Capability;
import soot.block.BlockAlchemyGlobe;
import soot.particle.ParticleUtilSoot;
import soot.upgrade.UpgradeAlchemyGlobe;
import soot.upgrade.UpgradeAlchemyGlobe.Status;
import teamroots.embers.api.alchemy.AspectList;
import teamroots.embers.api.capabilities.EmbersCapabilities;
import teamroots.embers.particle.ParticleUtil;
import teamroots.embers.tileentity.TileEntityAlchemyPedestal;
import teamroots.embers.util.AlchemyUtil;
import teamroots.embers.util.Misc;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TileEntityAlchemyGlobe extends TileEntity implements ITickable {
    public static final int UPDATE_INTERVAL = 20;

    AspectList aspectList = new AspectList();
    boolean hasAspects = false;

    static Random random = new Random();
    Status lastStatus;
    int lifeTime;
    int nextCheck;
    int preStartTick = 0;

    UpgradeAlchemyGlobe upgrade;
    private List<TileEntityAlchemyPedestal> pedestals = new ArrayList<>();

    public TileEntityAlchemyGlobe()
    {
        super();
        upgrade = new UpgradeAlchemyGlobe(this);
    }

    public boolean hasAspects() {
        return hasAspects;
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
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        upgrade.setStatus(Status.values()[compound.getInteger("status")],compound.getInteger("statusTick"));
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setInteger("statusTick",upgrade.getStatusTick());
        compound.setInteger("status",upgrade.getStatus().ordinal());
        return compound;
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return writeToNBT(new NBTTagCompound());
    }

    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(getPos(), 0, getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        readFromNBT(pkt.getNbtCompound());
    }

    @Override
    public void markDirty() {
        super.markDirty();
        Misc.syncTE(this);
    }

    @Override
    public void update() {
        lifeTime++;

        Status status = upgrade.getStatus();
        if(status != lastStatus)
            markDirty();

        if (lifeTime > nextCheck && status != Status.Idle) {
            nextCheck = lifeTime + UPDATE_INTERVAL;
            pedestals = AlchemyUtil.getNearbyPedestals(getWorld(), getPos());

            aspectList.reset();
            aspectList.collect(pedestals);
        }

        if (!world.isRemote) {
            preStartTick++;
            if(status == Status.PreStarting && preStartTick > 60)
                upgrade.setStatus(Status.Idle);
        }
        if(world.isRemote)
        {
            Color color = new Color(64,32,90);
            //Color color = new Color(255,255,255);
            Color colorFlame = new Color(181,90,255);
            //double multiplier = 255.0 / Math.max(colorFlame.getRed(),Math.max(colorFlame.getGreen(),colorFlame.getBlue()));
            //colorFlame = new Color((int)(colorFlame.getRed() * multiplier),(int)(colorFlame.getGreen() * multiplier),(int)(colorFlame.getBlue() * multiplier));
            /*for (int i = 0; i < 3; i ++){
                ParticleUtil.spawnParticleTyrfing(world, pos.getX()+0.5f, pos.getY()+0.5f, pos.getZ()+0.5f, (random.nextFloat()-0.5f)*0.01f, (random.nextFloat()-0.5f)*0.007f, (random.nextFloat()-0.5f)*0.01f, 2.5f, 20);
            }*/


            //double m = 1;
            //double triangle = m - Math.abs((lifeTime*0.001) % (2*m) - m);

            float ox = pos.getX() + 0.5f;
            float oy = pos.getY() + 0.5f;
            float oz = pos.getZ() + 0.5f;

            if(status == Status.Starting || status == Status.Success || status == Status.Crafting) {
                double coeff;
                if(status == Status.Starting)
                    coeff = (double)upgrade.getStatusTick() / UpgradeAlchemyGlobe.STARTUP_TIME;
                else if(status == Status.Crafting)
                    coeff = 0.5;
                else
                    coeff = 1.0;
                for (int i = 0; i < 3; i++) {
                    double chance = random.nextDouble();
                    if (chance > (1 - coeff) * (1 - coeff))
                        continue;
                    double yaw = random.nextFloat() * Math.PI * 2;
                    double pitch = random.nextFloat() * Math.PI * 2;
                    float dx = (float) (Math.sin(yaw) * Math.cos(pitch));
                    float dy = (float) (Math.sin(pitch));
                    float dz = (float) (Math.cos(yaw) * Math.cos(pitch));
                    double dist = 8;
                    double sx = ox + dx * 0.6;
                    double sy = oy + dy * 0.6;
                    double sz = oz + dz * 0.6;
                    if (world.collidesWithAnyBlock(new AxisAlignedBB(sx, sy, sz, sx, sy, sz)))
                        continue;
                    RayTraceResult raytraceresult = this.world.rayTraceBlocks(new Vec3d(sx, sy, sz), new Vec3d(ox + dx * dist, oy + dy * dist, oz + dz * dist), true, true, false);
                    if (raytraceresult != null && raytraceresult.hitVec != null) {
                        ParticleUtilSoot.spawnLightning(world, ox, oy, oz, raytraceresult.hitVec.x, raytraceresult.hitVec.y, raytraceresult.hitVec.z, 8, 0.3, color, MathHelper.clampedLerp(1.0, 1.5, coeff), 5);
                    }
                }
                for (TileEntityAlchemyPedestal pedestal : pedestals) {
                    if(pedestal.inventory.getStackInSlot(1).isEmpty())
                        continue;
                    double chance = random.nextDouble();
                    //chance = chance * chance;
                    if (chance > coeff * coeff)
                        continue;
                    BlockPos pedestalPos = pedestal.getPos();
                    float tx = pedestalPos.getX() + 0.5f;
                    float ty = pedestalPos.getY() + 0.9f;
                    float tz = pedestalPos.getZ() + 0.5f;
                    ParticleUtilSoot.spawnLightning(world, ox, oy, oz, tx, ty, tz, 8, MathHelper.clampedLerp(0.4, 0.1, coeff), color, MathHelper.clampedLerp(1.0, 2.0, coeff), 5);
                }
                if(status == Status.Starting)
                for (int i = 0; i < 5; i++) {
                    float velocityFactor = random.nextFloat() * 1.0f + 0.5f;
                    double yaw = random.nextFloat() * Math.PI * 2;
                    double pitch = random.nextFloat() * Math.PI * 2;
                    float dist = 0.4f * (float) MathHelper.clampedLerp(4.0, 0.0, coeff);
                    float dx = (float) (Math.sin(yaw) * Math.cos(pitch));
                    float dy = (float) (Math.sin(pitch));
                    float dz = (float) (Math.cos(yaw) * Math.cos(pitch));
                    float scale = (1.5f / velocityFactor) * 0.5f;
                    int lifetime = (int) (50 / velocityFactor);
                    float velocity = velocityFactor * dist / lifetime;
                    ParticleUtil.spawnParticleGlow(world, ox + dx * dist, oy + dy * dist, oz + dz * dist, dx * -velocity, dy * -velocity, dz * -velocity, colorFlame.getRed(), colorFlame.getGreen(), colorFlame.getBlue(), colorFlame.getAlpha() / 255f, scale, lifetime);
                    //ParticleUtilSoot.spawnParticleSolidGlow(world, ox + dx * dist, oy + dy * dist, oz + dz * dist, dx * -velocity, dy * -velocity, dz * -velocity, 0, 0, 0, 1f, scale * 2, lifetime);
                }
            }
            double holeScale = 0.5+Math.sin(lifeTime * Math.PI * 2 * 0.01) * 0.5;
            ParticleUtil.spawnParticleSmoke(world, ox, oy, oz, 0, 0, 0, 0, 0, 0, 1, (float) MathHelper.clampedLerp(2, 6, holeScale), 5 + random.nextInt(5));
            if(status == Status.Idle || status == Status.Crafting || status == Status.Success || status == Status.PreStarting) {
                float flameSize;
                if(status == Status.Idle || status == Status.PreStarting)
                    flameSize = 1.0f;
                else
                    flameSize = 1.5f;
                for (int i = 0; i < 5; i++) {
                    float velocityFactor = random.nextFloat() * 1.0f + 0.5f;
                    double yaw = random.nextDouble() * Math.PI * 2;
                    double pitch = random.nextDouble() * Math.PI * 2;
                    float dist = 0.1f;
                    float dx = (float) (Math.sin(yaw) * Math.cos(pitch));
                    float dy = (float) (Math.sin(pitch));
                    float dz = (float) (Math.cos(yaw) * Math.cos(pitch));
                    float scale = (1.5f / velocityFactor) * 0.5f * flameSize;
                    int lifetime = (int) (50 / velocityFactor);
                    if(status != Status.Idle)
                        velocityFactor *= -1;
                    ParticleUtil.spawnParticleGlow(world, ox + dx * dist, oy - 0.1f + dy * dist, oz + dz * dist, dx * -0.01f * velocityFactor, dy * -0.01f * velocityFactor + 0.007f, dz * -0.01f * velocityFactor, colorFlame.getRed(), colorFlame.getGreen(), colorFlame.getBlue(), colorFlame.getAlpha() / 255f, scale, lifetime);
                    //ParticleUtilSoot.spawnParticleSolidGlow(world, ox + dx * dist, oy + dy * dist, oz + dz * dist, dx * -0.01f*velocityFactor, dy * -0.01f*velocityFactor + 0.01f, dz * -0.01f*velocityFactor,0,0,0,0.5f, scale*2, lifetime);
                }
            }
        }

        lastStatus = status;

        //TODO: Spawn particles
    }

    public void activate(EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        Status status = upgrade.getStatus();
        if(status == Status.Idle) {
            upgrade.setStatus(Status.PreStarting);
            preStartTick = 0;
        }
        if(status == Status.Crafting || status == Status.Success) {
            upgrade.setStatus(Status.Idle);
        }
    }
}
