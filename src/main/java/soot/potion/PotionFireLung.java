package soot.potion;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import soot.util.ItemUtil;

import java.awt.*;

public class PotionFireLung extends PotionBase {
    public PotionFireLung() {
        super(false, new Color(152,93,63).getRGB());
        setPotionName("InnerFire");
        setIconIndex(3,0);
        setBeneficial();
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public void performEffect(EntityLivingBase living, int amplifier) {
        PotionEffect effect = living.getActivePotionEffect(this);

        if(effect != null) //Why are you here?
        {
            if(effect.getDuration() == 1)
            {
                living.addPotionEffect(new PotionEffect(MobEffects.NAUSEA,1000));
            }
        }

        super.performEffect(living, amplifier);
    }

    @SubscribeEvent
    public void onItemUse(LivingEntityUseItemEvent event)
    {
        EntityLivingBase living = event.getEntityLiving();
        ItemStack stack = event.getItem();
        PotionEffect effect = living.getActivePotionEffect(this);

        if(ItemUtil.matchesOreDict(stack,"torch") && effect != null)
        {
            //TODO: Blow a cloud of fire out
            if(effect.getDuration() > 10)
            {
                PotionEffect reducedEffect = new PotionEffect(this, effect.getDuration() - 5, effect.getAmplifier(), effect.getIsAmbient(), effect.doesShowParticles());
                reducedEffect.setCurativeItems(effect.getCurativeItems());
                living.addPotionEffect(reducedEffect);
            }
            else
            {
                living.removePotionEffect(this);
            }
        }
    }
}
