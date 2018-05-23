package soot.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import soot.util.MiscUtil;
import teamroots.embers.particle.ParticleUtil;

import javax.annotation.Nullable;

public class EntitySnowpoff extends EntitySnowball {
    public EntitySnowpoff(World worldIn) {
        super(worldIn);
    }

    public EntitySnowpoff(World worldIn, EntityLivingBase throwerIn) {
        super(worldIn, throwerIn);
    }

    public EntitySnowpoff(World worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void handleStatusUpdate(byte id)
    {
        if (id == 3) {
            for (int i = 0; i < 8; ++i) {
                ParticleUtil.spawnParticleGlow(world, (float) this.posX, (float) this.posY, (float) this.posZ, (rand.nextFloat() - 0.5f) * 0.1f, (rand.nextFloat() - 0.5f) * 0.1f, (rand.nextFloat() - 0.5f) * 0.1f, 200, 240, 255, 128, 5.0f, 50);
            }
        }
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if(world.isRemote)
        {
            ParticleUtil.spawnParticleGlow(world, (float) this.posX + (rand.nextFloat() - 0.5f) * 0.4f, (float) this.posY + (rand.nextFloat() - 0.5f) * 0.4f, (float) this.posZ + (rand.nextFloat() - 0.5f) * 0.4f, 0, 0, 0, 200, 240, 255, 128, 2.0f, 100);
        }
    }

    @Override
    protected void onImpact(RayTraceResult result)
    {
        if (result.entityHit != null && result.entityHit != this.getThrower()) {
            MiscUtil.damageWithoutInvulnerability(result.entityHit,causeSnowpoffDamage(this,getThrower()),3);
        }

        if (!this.world.isRemote)
        {
            this.world.setEntityState(this, (byte)3);
            this.setDead();
        }
    }

    public static DamageSource causeSnowpoffDamage(Entity source, @Nullable Entity indirectEntityIn)
    {
        return (new EntityDamageSourceIndirect("snowpoff", source, indirectEntityIn)).setProjectile();
    }
}
