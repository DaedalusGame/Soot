package soot.potion;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;
import java.util.UUID;

public class PotionLifedrinker extends PotionBase {
    static final UUID HEALTH_BOOST_UUID = UUID.fromString("ce99d4f2-ae8f-499c-a4e6-66c88dcf0938");

    public PotionLifedrinker() {
        super(false, new Color(90,20,20).getRGB());
        setPotionName("effect.lifedrinker");
        setIconIndex(7,0);
        setBeneficial();
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onKill(LivingDeathEvent event)
    {
        DamageSource source = event.getSource();
        EntityLivingBase killer = null;
        if(source.getImmediateSource() instanceof EntityLivingBase)
            killer = (EntityLivingBase) source.getImmediateSource();
        else if(source.getTrueSource() instanceof EntityLivingBase)
            killer = (EntityLivingBase) source.getTrueSource();

        if(killer != null && !killer.world.isRemote && killer.isPotionActive(this))
        {
            IAttributeInstance instance = killer.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH);
            AttributeModifier modifier = instance.getModifier(HEALTH_BOOST_UUID);
            int newHealth = 2;
            if(modifier != null && modifier.getOperation() == 0) {
                newHealth += modifier.getAmount();
                instance.removeModifier(modifier);
            }
            newHealth = Math.min(newHealth,20); //Lets not go overboard
            instance.applyModifier(new AttributeModifier(HEALTH_BOOST_UUID, getName(),newHealth,0));
        }
    }

    @Override
    public void removeAttributesModifiersFromEntity(EntityLivingBase entityLivingBaseIn, AbstractAttributeMap attributeMapIn, int amplifier) {
        IAttributeInstance instance = attributeMapIn.getAttributeInstance(SharedMonsterAttributes.MAX_HEALTH);
        instance.removeModifier(HEALTH_BOOST_UUID);
    }
}
