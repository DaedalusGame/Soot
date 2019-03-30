package soot.handler;

import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import soot.Config;
import teamroots.embers.RegistryManager;

import java.util.HashMap;
import java.util.Map;

public class GolemHandler {
    static Map<Class,Boolean> golemClassCache = new HashMap<>();

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onEntityDamaged(LivingHurtEvent event)
    {
        EntityLivingBase target = event.getEntityLiving();
        float damage = event.getAmount();
        DamageSource source = event.getSource();

        if(Config.GOLEMS_TYRFING_WEAK && source.getImmediateSource() instanceof EntityLivingBase)
        {
            EntityLivingBase attacker = (EntityLivingBase) source.getImmediateSource();
            if(attacker.getHeldItemMainhand().getItem() == RegistryManager.tyrfing && isGolem(target))
                event.setAmount(damage * 2.5f + 1.0f);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onEntityTick(LivingEvent.LivingUpdateEvent event)
    {
        EntityLivingBase target = event.getEntityLiving();
        if(Config.GOLEMS_POISON_IMMUNE && isGolem(target) && target.isPotionActive(MobEffects.POISON))
        {
            target.removePotionEffect(MobEffects.POISON);
        }
    }

    private static boolean isGolem(EntityLivingBase target) {
        Class<? extends EntityLivingBase> targetClass = target.getClass();
        if(golemClassCache.containsKey(targetClass)) {
            return golemClassCache.get(targetClass);
        }
        else {
            ResourceLocation location = EntityList.getKey(target);
            boolean isGolem = location != null && location.getResourcePath().toLowerCase().contains("golem");
            golemClassCache.put(targetClass,isGolem);
            return isGolem;
        }
    }
}
