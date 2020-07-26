package soot.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import soot.particle.ParticleUtilSoot;

import java.awt.*;
import java.util.Random;


public class Attributes {
    public static final IAttribute PHYSICAL_DAMAGE_RATE = new RangedAttribute(null, "generic.physicalDamageRate", 1.0D, 0.0D, 2048.0D);
    public static final IAttribute EXPERIENCE_RATE = new RangedAttribute(null, "generic.experienceRate", 1.0D, 0.0D, 2048.0D);
    public static final IAttribute FIRE_DAMAGE_RATE = new RangedAttribute(null, "generic.fireDamageRate", 1.0D, 0.0D, 2048.0D);
    public static final IAttribute FIRE_ASPECT = new RangedAttribute(null, "generic.fireAspect", 0.0D, 0.0D, 72000.0D);
    public static final IAttribute BAREHANDED_POWER = new RangedAttribute(null, "generic.barehandedPower", 1.0D, 0.0D, 2048.0D);
    public static final IAttribute WITCHBURN = new RangedAttribute(null, "generic.witchburn", 0.0D, 0.0D, Double.MAX_VALUE).setShouldWatch(true);
    public static final IAttribute ATTRACTION = new RangedAttribute(null, "generic.attraction", 0.0D, 0.0D, Double.MAX_VALUE).setShouldWatch(true);
    public static final IAttribute ATTRACTION_GENERATION = new RangedAttribute(null, "generic.attraction_generation", 0.0D, 0.0D, Double.MAX_VALUE).setShouldWatch(true);
    public static final double ATTRACTION_STACK = 100;

    private static final Random random = new Random();

    @SubscribeEvent
    public static void onEntityConstructEvent(EntityEvent.EntityConstructing event)
    {
        Entity entity = event.getEntity();
        if(entity instanceof EntityLivingBase) {
            ((EntityLivingBase) entity).getAttributeMap().registerAttribute(PHYSICAL_DAMAGE_RATE);
            ((EntityLivingBase) entity).getAttributeMap().registerAttribute(EXPERIENCE_RATE);
            ((EntityLivingBase) entity).getAttributeMap().registerAttribute(FIRE_DAMAGE_RATE);
            ((EntityLivingBase) entity).getAttributeMap().registerAttribute(FIRE_ASPECT);
            ((EntityLivingBase) entity).getAttributeMap().registerAttribute(BAREHANDED_POWER);
            ((EntityLivingBase) entity).getAttributeMap().registerAttribute(WITCHBURN);
            ((EntityLivingBase) entity).getAttributeMap().registerAttribute(ATTRACTION);
            ((EntityLivingBase) entity).getAttributeMap().registerAttribute(ATTRACTION_GENERATION);
        }
    }

    @SubscribeEvent
    public static void onExperienceDrop(LivingExperienceDropEvent event) {
       EntityPlayer player = event.getAttackingPlayer();
       if(player != null) {
           IAttributeInstance experienceMod = player.getEntityAttribute(EXPERIENCE_RATE);
           event.setDroppedExperience((int) (event.getDroppedExperience() * experienceMod.getAttributeValue()));
       }
    }

    @SubscribeEvent
    public static void onBreakBlock(BlockEvent.BreakEvent event) {
        EntityPlayer player = event.getPlayer();
        if(player != null) {
            IAttributeInstance experienceMod = player.getEntityAttribute(EXPERIENCE_RATE);
            event.setExpToDrop((int) (event.getExpToDrop() * experienceMod.getAttributeValue()));
        }
    }

    @SubscribeEvent
    public static void onUpdateEvent(LivingEvent.LivingUpdateEvent event)
    {
        Entity entity = event.getEntity();
        IAttributeInstance attraction = ((EntityLivingBase) entity).getEntityAttribute(ATTRACTION);
        double value = attraction.getAttributeValue();
        if(value > 0 && entity.world.isRemote) {
            double size = entity.getRenderBoundingBox().getAverageEdgeLength();
            double height = 0.5 * entity.getEyeHeight() + 0.2;
            double currentStack = (value % ATTRACTION_STACK) / ATTRACTION_STACK;
            size = size * size * size;
            double angleSpread = 0.3;
            double yaw = -Math.toRadians(((EntityLivingBase) entity).rotationYawHead+180);
            for(int i=1; i<value/ATTRACTION_STACK; i++) {
                ParticleUtilSoot.spawnParticleCrystal(entity, 0, height, 0, 0, 0, 0, (float)(yaw + (random.nextDouble() - 0.5) * Math.PI * angleSpread), (float)((random.nextDouble() - 0.5) * Math.PI * angleSpread), Color.WHITE, (float) (size*random.nextDouble()*0.1), 20);
            }

            ParticleUtilSoot.spawnParticleCrystal(entity, 0, height, 0, 0, 0, 0, (float)(yaw + (random.nextDouble() - 0.5) * Math.PI * angleSpread), (float)((random.nextDouble() - 0.5) * Math.PI * angleSpread), Color.WHITE, (float) (size*0.1*currentStack), 20);
        }
    }

    @SubscribeEvent
    public static void onStrikeEvent(LivingHurtEvent event)
    {
        EntityLivingBase entity = event.getEntityLiving();
        DamageSource damageSource = event.getSource();
        float damage = event.getAmount();
        EntityLivingBase attacker = null;
        if(damageSource.getImmediateSource() instanceof EntityLivingBase)
            attacker = (EntityLivingBase) damageSource.getImmediateSource();
        else if(damageSource.getTrueSource() instanceof EntityLivingBase)
            attacker = (EntityLivingBase) damageSource.getTrueSource();

        if(entity != null && attacker != null) {
            int fire_aspect = (int) entity.getEntityAttribute(FIRE_ASPECT).getAttributeValue();
            if(MiscUtil.isBarehandedDamage(damageSource,attacker))
                damage *= entity.getEntityAttribute(BAREHANDED_POWER).getAttributeValue();
            if(fire_aspect > 0)
                entity.setFire(fire_aspect);
        }

        event.setAmount(damage);
    }

    @SubscribeEvent
    public static void onHurtEvent(LivingHurtEvent event)
    {
        EntityLivingBase entity = event.getEntityLiving();
        DamageSource damageSource = event.getSource();
        float damage = event.getAmount();
        if(entity != null)
        {
            IAttributeInstance damageRate = null;
            if(MiscUtil.isPhysicalDamage(damageSource))
                damageRate = entity.getEntityAttribute(PHYSICAL_DAMAGE_RATE);
            if(damageSource.isFireDamage())
                damageRate = entity.getEntityAttribute(FIRE_DAMAGE_RATE);

            if(damageRate != null)
                damage *= damageRate.getAttributeValue();
        }
        event.setAmount(damage);
    }

    public static boolean isAttracted(Entity entity) {
        if(entity instanceof EntityLivingBase) {
            IAttributeInstance attraction = ((EntityLivingBase) entity).getEntityAttribute(ATTRACTION);
            return attraction.getAttributeValue() > 0;
        }
        return false;
    }

    public static boolean isGenerating(Entity entity) {
        if(entity instanceof EntityLivingBase) {
            IAttributeInstance attraction = ((EntityLivingBase) entity).getEntityAttribute(ATTRACTION_GENERATION);
            return attraction.getAttributeValue() > 0;
        }
        return false;
    }


    public static void resetAttraction(Entity entity, int stacks) {
        if(entity instanceof EntityLivingBase) {
            IAttributeInstance attraction = ((EntityLivingBase) entity).getEntityAttribute(ATTRACTION);
            double amount = attraction.getBaseValue();
            amount -= amount % ATTRACTION_STACK; //Take away the buildup to the next stack
            amount -= ATTRACTION_STACK * stacks; //And the stack itself
            attraction.setBaseValue(Math.max(amount,0));
        }
    }

    public static void increaseAttraction(Entity entity, double amount) {
        if(entity instanceof EntityLivingBase) {
            IAttributeInstance attraction = ((EntityLivingBase) entity).getEntityAttribute(ATTRACTION);
            attraction.setBaseValue(attraction.getBaseValue()+amount);
        }
    }

    public static void increaseGeneration(Entity entity, double amount) {
        if(entity instanceof EntityLivingBase) {
            IAttributeInstance attraction = ((EntityLivingBase) entity).getEntityAttribute(ATTRACTION_GENERATION);
            attraction.setBaseValue(attraction.getBaseValue()+amount);
        }
    }

    public static void decreaseGeneration(Entity entity, double amount) {
        if(entity instanceof EntityLivingBase) {
            IAttributeInstance attraction = ((EntityLivingBase) entity).getEntityAttribute(ATTRACTION_GENERATION);
            attraction.setBaseValue(Math.max(attraction.getBaseValue()-amount,0));
        }
    }
}
