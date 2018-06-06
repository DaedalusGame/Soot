package soot.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import soot.Registry;
import soot.Soot;
import teamroots.embers.particle.ParticleUtil;

import javax.annotation.Nullable;

public class EntityMuse extends EntityMob {
    protected static final DataParameter<Byte> MUSE_FLAGS = EntityDataManager.createKey(EntityMuse.class, DataSerializers.BYTE);
    private EntityPlayer boundPlayer;
    private boolean limitedLifespan;
    private int limitedLifeTicks;

    public EntityMuse(World worldIn)
    {
        super(worldIn);
        this.isImmuneToFire = true;
        this.moveHelper = new EntityMuse.AIMoveControl(this);
        this.setSize(1.0f, 2.0f);
        this.experienceValue = 3;
    }

    @Override
    public void move(MoverType type, double x, double y, double z)
    {
        super.move(type, x, y, z);
        this.doBlockCollisions();
    }

    @Override
    public boolean attackEntityAsMob(Entity target) {
        if (!(target instanceof EntityLivingBase) || !isVisibleTo((EntityLivingBase) target)) {
            return false;
        }

        boolean flag = super.attackEntityAsMob(target);

        if(flag && target instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) target;
            player.addExperienceLevel(-1);
        }

        return flag;
    }

    @Override
    public void onUpdate()
    {
        this.noClip = true;
        super.onUpdate();
        this.noClip = false;
        this.setNoGravity(true);

        if(world.isRemote) {
            EntityPlayer mainPlayer = Soot.proxy.getMainPlayer();
            if(mainPlayer != null && isVisibleTo(mainPlayer)) {
                ParticleUtil.spawnParticleGlow(world, (float) this.posX + (rand.nextFloat() - 0.5f) * 0.4f, (float) this.posY + 1.0f + (rand.nextFloat() - 0.5f) * 0.4f, (float) this.posZ + (rand.nextFloat() - 0.5f) * 0.4f, (rand.nextFloat() - 0.5f) * 0.1f, rand.nextFloat() * 0.1f, (rand.nextFloat() - 0.5f) * 0.1f, 64, 255, 64, 16, 10.0f, 50);
            }
        }

        if (boundPlayer == null || !isVisibleTo(boundPlayer) || (this.limitedLifespan && --this.limitedLifeTicks <= 0))
        {
            this.limitedLifeTicks = 10;
            this.attackEntityFrom(DamageSource.STARVE, 100.0F);
        }
    }

    public boolean isVisibleTo(EntityLivingBase entity) {
        if(entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entity;
            if(player.isSpectator() || player.isCreative())
                return true;
        }
        return entity.isPotionActive(Registry.POTION_INSPIRATION);
    }

    @Override
    public boolean canEntityBeSeen(Entity entityIn) {
        return (!(entityIn instanceof EntityLivingBase) || isVisibleTo((EntityLivingBase) entityIn)) && super.canEntityBeSeen(entityIn);
    }

    @Override
    protected void initEntityAI()
    {
        super.initEntityAI();
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(4, new EntityMuse.AIChargeAttack());
        this.tasks.addTask(8, new EntityMuse.AIMoveRandom());
        this.tasks.addTask(9, new EntityAIWatchClosest(this, EntityPlayer.class, 3.0F, 1.0F));
        this.tasks.addTask(10, new EntityAIWatchClosest(this, EntityLiving.class, 8.0F));
        this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true, EntityMuse.class));
        this.targetTasks.addTask(3, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, true));
    }

    @Override
    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(30.0D);
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(2.0D);
    }

    @Override
    protected void entityInit()
    {
        super.entityInit();
        this.dataManager.register(MUSE_FLAGS, (byte) 0);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound)
    {
        super.readEntityFromNBT(compound);

        if (compound.hasKey("LifeTicks"))
        {
            this.setLimitedLife(compound.getInteger("LifeTicks"));
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound)
    {
        super.writeEntityToNBT(compound);

        if (this.limitedLifespan)
        {
            compound.setInteger("LifeTicks", this.limitedLifeTicks);
        }
    }

    @Nullable
    public BlockPos getBoundOrigin() {
        return boundPlayer != null ? boundPlayer.getPosition() : null;
    }

    @Nullable
    public EntityPlayer getBoundPlayer() {
        return boundPlayer;
    }

    public void setBoundPlayer(EntityPlayer player) {
        boundPlayer = player;
    }

    private boolean getMuseFlag(int mask)
    {
        int i = this.dataManager.get(MUSE_FLAGS);
        return (i & mask) != 0;
    }

    private void setMuseFlag(int mask, boolean value)
    {
        int i = this.dataManager.get(MUSE_FLAGS);

        if (value)
        {
            i = i | mask;
        }
        else
        {
            i = i & ~mask;
        }

        this.dataManager.set(MUSE_FLAGS, (byte) (i & 255));
    }

    public boolean isCharging() {
        return this.getMuseFlag(1);
    }

    public void setCharging(boolean charging) {
        this.setMuseFlag(1, charging);
    }

    public void setLimitedLife(int limitedLifeTicksIn) {
        this.limitedLifespan = true;
        this.limitedLifeTicks = limitedLifeTicksIn;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_VEX_AMBIENT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_VEX_DEATH;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.ENTITY_VEX_HURT;
    }

    @Override
    public boolean isPotionApplicable(PotionEffect potionEffect) {
        return false;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public int getBrightnessForRender()
    {
        return 15728880;
    }

    @Override
    public float getBrightness()
    {
        return 1.0F;
    }

    class AIChargeAttack extends EntityAIBase
    {
        Vec3d target;

        public AIChargeAttack()
        {
            this.setMutexBits(1);
        }

        /**
         * Returns whether the EntityAIBase should begin execution.
         */
        public boolean shouldExecute()
        {
            if (EntityMuse.this.getAttackTarget() != null && !EntityMuse.this.getMoveHelper().isUpdating() && EntityMuse.this.rand.nextInt(3) == 0)
            {
                return EntityMuse.this.getDistanceSq(EntityMuse.this.getAttackTarget()) > 3.0D;
            }
            else
            {
                return false;
            }
        }

        /**
         * Returns whether an in-progress EntityAIBase should continue executing
         */
        public boolean shouldContinueExecuting()
        {
            return EntityMuse.this.getMoveHelper().isUpdating() && EntityMuse.this.isCharging() && EntityMuse.this.getAttackTarget() != null && EntityMuse.this.getAttackTarget().isEntityAlive();
        }

        /**
         * Execute a one shot task or start executing a continuous task
         */
        public void startExecuting()
        {
            EntityLivingBase entitylivingbase = EntityMuse.this.getAttackTarget();
            Vec3d start = EntityMuse.this.getPositionVector();
            Vec3d mid = entitylivingbase.getPositionVector();
            target = start.add(mid.subtract(start).scale(2.0));
            EntityMuse.this.moveHelper.setMoveTo(target.x, target.y, target.z, 1.0D);
            EntityMuse.this.setCharging(true);
            EntityMuse.this.playSound(SoundEvents.ENTITY_VEX_CHARGE, 1.0F, 1.0F);
        }

        /**
         * Reset the task's internal state. Called when this task is interrupted by another one
         */
        public void resetTask()
        {
            EntityMuse.this.setCharging(false);
        }

        /**
         * Keep ticking a continuous task that has already been started
         */
        public void updateTask()
        {
            EntityLivingBase entitylivingbase = EntityMuse.this.getAttackTarget();

            if (EntityMuse.this.getEntityBoundingBox().intersects(entitylivingbase.getEntityBoundingBox()))
            {
                EntityMuse.this.attackEntityAsMob(entitylivingbase);
                EntityMuse.this.setCharging(false);
            }
            else
            {
                double d0 = EntityMuse.this.getDistanceSq(entitylivingbase);

                if (d0 < 9.0D)
                {
                    EntityMuse.this.moveHelper.setMoveTo(target.x, target.y, target.z, 1.0D);
                }
            }
        }
    }

    class AIMoveControl extends EntityMoveHelper
    {
        public AIMoveControl(EntityMuse vex)
        {
            super(vex);
        }

        public void onUpdateMoveHelper()
        {
            if (this.action == EntityMoveHelper.Action.MOVE_TO)
            {
                double d0 = this.posX - EntityMuse.this.posX;
                double d1 = this.posY - EntityMuse.this.posY;
                double d2 = this.posZ - EntityMuse.this.posZ;
                double d3 = d0 * d0 + d1 * d1 + d2 * d2;
                d3 = (double) MathHelper.sqrt(d3);

                if (d3 < EntityMuse.this.getEntityBoundingBox().getAverageEdgeLength())
                {
                    this.action = EntityMoveHelper.Action.WAIT;
                    EntityMuse.this.motionX *= 0.5D;
                    EntityMuse.this.motionY *= 0.5D;
                    EntityMuse.this.motionZ *= 0.5D;
                }
                else
                {
                    EntityMuse.this.motionX += d0 / d3 * 0.05D * this.speed;
                    EntityMuse.this.motionY += d1 / d3 * 0.05D * this.speed;
                    EntityMuse.this.motionZ += d2 / d3 * 0.05D * this.speed;

                    if (EntityMuse.this.getAttackTarget() == null)
                    {
                        EntityMuse.this.rotationYaw = -((float)MathHelper.atan2(EntityMuse.this.motionX, EntityMuse.this.motionZ)) * (180F / (float)Math.PI);
                        EntityMuse.this.renderYawOffset = EntityMuse.this.rotationYaw;
                    }
                    else
                    {
                        double d4 = EntityMuse.this.getAttackTarget().posX - EntityMuse.this.posX;
                        double d5 = EntityMuse.this.getAttackTarget().posZ - EntityMuse.this.posZ;
                        EntityMuse.this.rotationYaw = -((float)MathHelper.atan2(d4, d5)) * (180F / (float)Math.PI);
                        EntityMuse.this.renderYawOffset = EntityMuse.this.rotationYaw;
                    }
                }
            }
        }
    }

    class AIMoveRandom extends EntityAIBase
    {
        public AIMoveRandom()
        {
            this.setMutexBits(1);
        }

        /**
         * Returns whether the EntityAIBase should begin execution.
         */
        public boolean shouldExecute()
        {
            return !EntityMuse.this.getMoveHelper().isUpdating() && EntityMuse.this.rand.nextInt(7) == 0;
        }

        /**
         * Returns whether an in-progress EntityAIBase should continue executing
         */
        public boolean shouldContinueExecuting()
        {
            return false;
        }

        /**
         * Keep ticking a continuous task that has already been started
         */
        public void updateTask()
        {
            BlockPos blockpos = EntityMuse.this.getBoundOrigin();

            if (blockpos == null)
            {
                blockpos = new BlockPos(EntityMuse.this);
            }

            for (int i = 0; i < 3; ++i)
            {
                BlockPos blockpos1 = blockpos.add(EntityMuse.this.rand.nextInt(15) - 7, EntityMuse.this.rand.nextInt(11) - 5, EntityMuse.this.rand.nextInt(15) - 7);

                if (EntityMuse.this.world.isAirBlock(blockpos1))
                {
                    EntityMuse.this.moveHelper.setMoveTo((double)blockpos1.getX() + 0.5D, (double)blockpos1.getY() + 0.5D, (double)blockpos1.getZ() + 0.5D, 1.0D);

                    if (EntityMuse.this.getAttackTarget() == null)
                    {
                        EntityMuse.this.getLookHelper().setLookPosition((double)blockpos1.getX() + 0.5D, (double)blockpos1.getY() + 0.5D, (double)blockpos1.getZ() + 0.5D, 180.0F, 20.0F);
                    }

                    break;
                }
            }
        }
    }
}
