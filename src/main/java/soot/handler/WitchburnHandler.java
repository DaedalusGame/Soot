package soot.handler;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import soot.util.Attributes;
import teamroots.embers.particle.ParticleUtil;

import java.awt.*;
import java.util.Random;

public class WitchburnHandler {
    public static final DamageSource DAMAGE_SOURCE = new DamageSource("witchBurn");
    public static final Color COLOR = new Color(64, 255, 16);

    public static void setWitchburn(EntityLivingBase entity, int ticks)
    {
        IAttributeInstance attribute = entity.getEntityAttribute(Attributes.WITCHBURN);
        attribute.setBaseValue(ticks);
    }

    @SubscribeEvent
    public static void onUpdateEvent(LivingEvent.LivingUpdateEvent event)
    {
        EntityLivingBase entity = event.getEntityLiving();
        IAttributeInstance attribute = entity.getEntityAttribute(Attributes.WITCHBURN);
        int witchburn = (int) attribute.getAttributeValue();
        if(witchburn > 0) {
            if (!entity.world.isRemote) {
                if (witchburn % 20 == 0) {
                    entity.attackEntityFrom(DAMAGE_SOURCE, calculateWitchBurnDamage(entity));
                }
                attribute.setBaseValue(attribute.getBaseValue() - 1);
            }
            if (entity.world.isRemote) {
                Random rand = entity.getRNG();
                for (int i = 0; i < 10; i++)
                    ParticleUtil.spawnParticleVapor(entity.world, (float) entity.posX + (rand.nextFloat() - 0.5f) * entity.width, (float) entity.posY + rand.nextFloat() * entity.height, (float) entity.posZ + (rand.nextFloat() - 0.5f) * entity.width, (rand.nextFloat() - 0.5f) * 0.02f, rand.nextFloat() * 0.04f, (rand.nextFloat() - 0.5f) * 0.02f, COLOR.getRed(), COLOR.getGreen(), COLOR.getBlue(), 1.0f, 2.0f, 4.0f, 10 + rand.nextInt(20));
            }
        }
    }

    public static float calculateWitchBurnDamage(EntityLivingBase entity) {
        return 2.0f;
    }

    @SubscribeEvent
    public static void onHeal(LivingHealEvent event) {
        EntityLivingBase entity = event.getEntityLiving();
        IAttributeInstance attribute = entity.getEntityAttribute(Attributes.WITCHBURN);

        if (attribute.getAttributeValue() > 0) {
            event.setAmount(0);
        }
    }
}
