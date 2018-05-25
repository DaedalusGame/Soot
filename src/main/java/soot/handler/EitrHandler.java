package soot.handler;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import soot.util.MiscUtil;

public class EitrHandler {
    @SubscribeEvent
    public static void onHit(LivingHurtEvent event) {
        EntityLivingBase entity = event.getEntityLiving();
        DamageSource damageSource = event.getSource();
        float damage = event.getAmount();
        EntityLivingBase attacker = null;
        if(damageSource.getImmediateSource() instanceof EntityLivingBase)
            attacker = (EntityLivingBase) damageSource.getImmediateSource();
        else if(damageSource.getTrueSource() instanceof EntityLivingBase)
            attacker = (EntityLivingBase) damageSource.getTrueSource();

        if (entity != null && attacker != null && MiscUtil.isEitrDamage(damageSource, attacker)) {
            event.setAmount(damage * 0.5f);
            MiscUtil.degradeEquipment(entity, (int) Math.ceil(damage));
        }
    }
}
