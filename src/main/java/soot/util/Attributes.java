package soot.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;


public class Attributes {
    public static final IAttribute PHYSICAL_DAMAGE_RATE = new RangedAttribute(null, "generic.physicalDamageRate", 1.0D, 0.0D, 2048.0D);
    public static final IAttribute FIRE_DAMAGE_RATE = new RangedAttribute(null, "generic.fireDamageRate", 1.0D, 0.0D, 2048.0D);
    public static final IAttribute FIRE_ASPECT = new RangedAttribute(null, "generic.fireAspect", 0.0D, 0.0D, 72000.0D);
    public static final IAttribute BAREHANDED_POWER = new RangedAttribute(null, "generic.barehandedPower", 1.0D, 0.0D, 2048.0D);
    public static final IAttribute WITCHBURN = new RangedAttribute(null, "generic.witchburn", 0.0D, 0.0D, Double.MAX_VALUE).setShouldWatch(true);

    @SubscribeEvent
    public static void onEntityConstructEvent(EntityEvent.EntityConstructing event)
    {
        Entity entity = event.getEntity();
        if(entity instanceof EntityLivingBase) {
            ((EntityLivingBase) entity).getAttributeMap().registerAttribute(PHYSICAL_DAMAGE_RATE);
            ((EntityLivingBase) entity).getAttributeMap().registerAttribute(FIRE_DAMAGE_RATE);
            ((EntityLivingBase) entity).getAttributeMap().registerAttribute(FIRE_ASPECT);
            ((EntityLivingBase) entity).getAttributeMap().registerAttribute(BAREHANDED_POWER);
            ((EntityLivingBase) entity).getAttributeMap().registerAttribute(WITCHBURN);
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

}
