package soot.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;


public class Attributes {
    public static final IAttribute PHYSICAL_DAMAGE_RATE = new RangedAttribute(null, "generic.physicalDamageRate", 1.0D, 0.0D, 2048.0D);
    public static final IAttribute FIRE_DAMAGE_RATE = new RangedAttribute(null, "generic.fireDamageRate", 1.0D, 0.0D, 2048.0D);
    public static final IAttribute FIRE_ASPECT = new RangedAttribute(null, "generic.physicalDamageRate", 1.0D, 0.0D, 2048.0D);

    @SubscribeEvent
    public static void onEntityConstructEvent(EntityEvent.EntityConstructing event)
    {
        Entity entity = event.getEntity();
        if(entity instanceof EntityLivingBase) {
            ((EntityLivingBase) entity).getAttributeMap().registerAttribute(PHYSICAL_DAMAGE_RATE);
            ((EntityLivingBase) entity).getAttributeMap().registerAttribute(FIRE_DAMAGE_RATE);
        }
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
            if(isPhysicalDamage(damageSource))
                damageRate = entity.getEntityAttribute(PHYSICAL_DAMAGE_RATE);
            if(damageSource.isFireDamage())
                damageRate = entity.getEntityAttribute(FIRE_DAMAGE_RATE);

            if(damageRate != null)
                damage *= damageRate.getAttributeValue();
        }
        event.setAmount(damage);
    }

    private static boolean isPhysicalDamage(DamageSource damageSource)
    {
        return damageSource.getImmediateSource() != null && !damageSource.isProjectile() && !damageSource.isExplosion() && !damageSource.isFireDamage() && !damageSource.isMagicDamage() && !damageSource.isDamageAbsolute();
    }
}
