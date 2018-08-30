package soot.entity;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import teamroots.embers.particle.ParticleUtil;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class EntityCustomCloud extends Entity {
    private static final DataParameter<Float> RADIUS = EntityDataManager.<Float>createKey(EntityCustomCloud.class, DataSerializers.FLOAT);
    private static final DataParameter<Integer> COLOR = EntityDataManager.<Integer>createKey(EntityCustomCloud.class, DataSerializers.VARINT);
    private static final DataParameter<Boolean> IGNORE_RADIUS = EntityDataManager.<Boolean>createKey(EntityCustomCloud.class, DataSerializers.BOOLEAN);
    private final List<PotionEffect> effects;
    private final Map<Entity, Integer> reapplicationDelayMap;
    private int duration;
    private int waitTime;
    private int reapplicationDelay;
    private boolean colorSet;
    private int durationOnUse;
    private float radiusOnUse;
    private float radiusPerTick;
    private EntityLivingBase owner;
    private UUID ownerUniqueId;

    public EntityCustomCloud(World worldIn) {
        super(worldIn);
        this.effects = Lists.<PotionEffect>newArrayList();
        this.reapplicationDelayMap = Maps.<Entity, Integer>newHashMap();
        this.duration = 600;
        this.waitTime = 20;
        this.reapplicationDelay = 20;
        this.noClip = true;
        this.isImmuneToFire = true;
        this.setRadius(3.0F);
    }

    public EntityCustomCloud(World worldIn, double x, double y, double z) {
        this(worldIn);
        this.setPosition(x, y, z);
    }

    protected void entityInit() {
        this.getDataManager().register(COLOR, 0);
        this.getDataManager().register(RADIUS, 0.5F);
        this.getDataManager().register(IGNORE_RADIUS, false);
    }

    public void setRadius(float radiusIn) {
        double d0 = this.posX;
        double d1 = this.posY;
        double d2 = this.posZ;
        this.setSize(radiusIn * 2.0F, 0.5F);
        this.setPosition(d0, d1, d2);

        if (!this.world.isRemote) {
            this.getDataManager().set(RADIUS, radiusIn);
        }
    }

    public float getRadius() {
        return this.getDataManager().get(RADIUS);
    }

    private void updateFixedColor() {
        if (this.effects.isEmpty()) {
            this.getDataManager().set(COLOR, 0);
        } else {
            this.getDataManager().set(COLOR, PotionUtils.getPotionColorFromEffectList(this.effects));
        }
    }

    public void addEffect(PotionEffect effect) {
        this.effects.add(effect);

        if (!this.colorSet) {
            this.updateFixedColor();
        }
    }

    public int getColor() {
        return this.getDataManager().get(COLOR);
    }

    public void setColor(int colorIn) {
        this.colorSet = true;
        this.getDataManager().set(COLOR, colorIn);
    }

    /**
     * Sets if the radius should be ignored, and the effect should be shown in a single point instead of an area
     */
    protected void setIgnoreRadius(boolean ignoreRadius) {
        this.getDataManager().set(IGNORE_RADIUS, ignoreRadius);
    }

    /**
     * Returns true if the radius should be ignored, and the effect should be shown in a single point instead of an area
     */
    public boolean shouldIgnoreRadius() {
        return this.getDataManager().get(IGNORE_RADIUS);
    }

    public int getDuration() {
        return this.duration;
    }

    public void setDuration(int durationIn) {
        this.duration = durationIn;
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate() {
        super.onUpdate();
        boolean ignoreRadius = this.shouldIgnoreRadius();
        float radius = this.getRadius();

        if (this.world.isRemote) {
            if (!ignoreRadius || this.rand.nextBoolean()) {
                float area = (float) Math.PI * radius * radius;

                for (int i = 0; i < (ignoreRadius ? 2 : area); ++i) {
                    float angle = this.rand.nextFloat() * ((float) Math.PI * 2f);
                    float distance = MathHelper.sqrt(this.rand.nextFloat()) * (ignoreRadius ? 0.2f : radius);
                    float offsetX = MathHelper.cos(angle) * distance;
                    float offsetZ = MathHelper.sin(angle) * distance;

                    float velX = (0.5f - this.rand.nextFloat()) * 0.05f;
                    float velY = (this.rand.nextFloat()) * 0.05f;
                    float velZ = (0.5f - this.rand.nextFloat()) * 0.05f;

                    int color = this.getColor();
                    int r = color >> 16 & 255;
                    int g = color >> 8 & 255;
                    int b = color & 255;
                    int a = color >> 24 & 255;

                    ParticleUtil.spawnParticleVapor(world, (float) this.posX + offsetX, (float) this.posY, (float) this.posZ + offsetZ, velX, velY, velZ, r, g, b, a, 2.0f, 5.0f, 50);
                }
            }
        } else {
            if (this.ticksExisted >= this.waitTime + this.duration) {
                this.setDead();
                return;
            }

            boolean notTriggered = this.ticksExisted < this.waitTime;

            if (ignoreRadius != notTriggered) {
                this.setIgnoreRadius(notTriggered);
            }

            if (notTriggered) {
                return;
            }

            if (this.radiusPerTick != 0.0F) {
                radius += this.radiusPerTick;

                if (radius < 0.5F) {
                    this.setDead();
                    return;
                }

                this.setRadius(radius);
            }

            if (this.ticksExisted % 5 == 0) {
                CheckReapply();

                /*if (effects.isEmpty()) {
                    this.reapplicationDelayMap.clear();
                } else {*/
                    List<EntityLivingBase> list = this.world.getEntitiesWithinAABB(EntityLivingBase.class, this.getEntityBoundingBox());

                    if (!list.isEmpty()) {
                        for (EntityLivingBase entitylivingbase : list) {
                            if (!this.reapplicationDelayMap.containsKey(entitylivingbase)) {
                                double distX = entitylivingbase.posX - this.posX;
                                double distZ = entitylivingbase.posZ - this.posZ;
                                double distance = distX * distX + distZ * distZ;

                                if (distance <= (double) (radius * radius)) {
                                    boolean hit = applyEffectToEntity(entitylivingbase);

                                    if(!hit)
                                        continue;

                                    if (this.radiusOnUse != 0.0F) {
                                        radius += this.radiusOnUse;

                                        if (radius < 0.5F) {
                                            this.setDead();
                                            return;
                                        }

                                        this.setRadius(radius);
                                    }

                                    if (this.durationOnUse != 0) {
                                        this.duration += this.durationOnUse;

                                        if (this.duration <= 0) {
                                            this.setDead();
                                            return;
                                        }
                                    }
                                }
                            }
                        }
                    //}
                }
            }
        }
    }

    public void CheckReapply() {
        Iterator<Map.Entry<Entity, Integer>> iterator = this.reapplicationDelayMap.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<Entity, Integer> entry = iterator.next();

            if (this.ticksExisted >= entry.getValue()) {
                iterator.remove();
            }
        }
    }

    public boolean applyEffectToEntity(EntityLivingBase entitylivingbase) {
        if (!entitylivingbase.canBeHitWithPotion()) {
            return false;
        }

        this.reapplicationDelayMap.put(entitylivingbase, this.ticksExisted + this.reapplicationDelay);

        for (PotionEffect potioneffect : effects) {
            if (potioneffect.getPotion().isInstant()) {
                potioneffect.getPotion().affectEntity(this, this.getOwner(), entitylivingbase, potioneffect.getAmplifier(), 0.5D);
            } else {
                entitylivingbase.addPotionEffect(new PotionEffect(potioneffect));
            }
        }

        return true;
    }

    public void setRadiusOnUse(float radiusOnUseIn) {
        this.radiusOnUse = radiusOnUseIn;
    }

    public void setRadiusPerTick(float radiusPerTickIn) {
        this.radiusPerTick = radiusPerTickIn;
    }

    public void setWaitTime(int waitTimeIn) {
        this.waitTime = waitTimeIn;
    }

    public void setOwner(@Nullable EntityLivingBase ownerIn) {
        this.owner = ownerIn;
        this.ownerUniqueId = ownerIn == null ? null : ownerIn.getUniqueID();
    }

    @Nullable
    public EntityLivingBase getOwner() {
        if (this.owner == null && this.ownerUniqueId != null && this.world instanceof WorldServer) {
            Entity entity = ((WorldServer) this.world).getEntityFromUuid(this.ownerUniqueId);

            if (entity instanceof EntityLivingBase) {
                this.owner = (EntityLivingBase) entity;
            }
        }

        return this.owner;
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    protected void readEntityFromNBT(NBTTagCompound compound) {
        this.ticksExisted = compound.getInteger("Age");
        this.duration = compound.getInteger("Duration");
        this.waitTime = compound.getInteger("WaitTime");
        this.reapplicationDelay = compound.getInteger("ReapplicationDelay");
        this.durationOnUse = compound.getInteger("DurationOnUse");
        this.radiusOnUse = compound.getFloat("RadiusOnUse");
        this.radiusPerTick = compound.getFloat("RadiusPerTick");
        this.setRadius(compound.getFloat("Radius"));
        this.ownerUniqueId = compound.getUniqueId("OwnerUUID");

        if (compound.hasKey("Color", 99)) {
            this.setColor(compound.getInteger("Color"));
        }

        if (compound.hasKey("Effects", 9)) {
            NBTTagList nbttaglist = compound.getTagList("Effects", 10);
            this.effects.clear();

            for (int i = 0; i < nbttaglist.tagCount(); ++i) {
                PotionEffect potioneffect = PotionEffect.readCustomPotionEffectFromNBT(nbttaglist.getCompoundTagAt(i));

                this.addEffect(potioneffect);
            }
        }
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    protected void writeEntityToNBT(NBTTagCompound compound) {
        compound.setInteger("Age", this.ticksExisted);
        compound.setInteger("Duration", this.duration);
        compound.setInteger("WaitTime", this.waitTime);
        compound.setInteger("ReapplicationDelay", this.reapplicationDelay);
        compound.setInteger("DurationOnUse", this.durationOnUse);
        compound.setFloat("RadiusOnUse", this.radiusOnUse);
        compound.setFloat("RadiusPerTick", this.radiusPerTick);
        compound.setFloat("Radius", this.getRadius());

        if (this.ownerUniqueId != null) {
            compound.setUniqueId("OwnerUUID", this.ownerUniqueId);
        }

        if (this.colorSet) {
            compound.setInteger("Color", this.getColor());
        }

        if (!this.effects.isEmpty()) {
            NBTTagList nbttaglist = new NBTTagList();

            for (PotionEffect potioneffect : this.effects) {
                nbttaglist.appendTag(potioneffect.writeCustomPotionEffectToNBT(new NBTTagCompound()));
            }

            compound.setTag("Effects", nbttaglist);
        }
    }

    public void notifyDataManagerChange(DataParameter<?> key) {
        if (RADIUS.equals(key)) {
            this.setRadius(this.getRadius());
        }

        super.notifyDataManagerChange(key);
    }

    public EnumPushReaction getPushReaction() {
        return EnumPushReaction.IGNORE;
    }
}