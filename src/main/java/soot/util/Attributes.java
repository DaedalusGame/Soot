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

    @SubscribeEvent
    public static void onEntityConstructEvent(EntityEvent.EntityConstructing event)
    {
        Entity entity = event.getEntity();
        if(entity instanceof EntityLivingBase)
            ((EntityLivingBase) entity).getAttributeMap().registerAttribute(PHYSICAL_DAMAGE_RATE);
    }

    @SubscribeEvent
    public static void onHurtEvent(LivingHurtEvent event)
    {
        EntityLivingBase entity = event.getEntityLiving();
        DamageSource damageSource = event.getSource();
        float damage = event.getAmount();
        if(entity != null && isPhysicalDamage(damageSource))
        {
            IAttributeInstance physicalDamageRate = entity.getEntityAttribute(PHYSICAL_DAMAGE_RATE);
            damage *= physicalDamageRate.getAttributeValue();
        }
        event.setAmount(damage);
    }

    private static boolean isPhysicalDamage(DamageSource damageSource)
    {
        return damageSource.getImmediateSource() != null && !damageSource.isProjectile() && !damageSource.isExplosion() && !damageSource.isFireDamage() && !damageSource.isMagicDamage() && !damageSource.isDamageAbsolute();
    }
}
