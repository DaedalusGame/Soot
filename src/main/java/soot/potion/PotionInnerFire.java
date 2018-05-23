package soot.potion;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import soot.Registry;
import soot.util.Attributes;

import java.awt.*;

public class PotionInnerFire extends PotionBase {
    public PotionInnerFire() {
        super(false, new Color(240,32,32).getRGB());
        setPotionName("effect.inner_fire");
        setIconIndex(1,0);
        setBeneficial();
        MinecraftForge.EVENT_BUS.register(this);
        registerPotionAttributeModifier(Attributes.FIRE_ASPECT,"c47b334d-cf2e-4df9-94d6-327f73223ca4",60,0);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onHurt(LivingHurtEvent event) {
        EntityLivingBase living = event.getEntityLiving();
        PotionEffect effect = living.getActivePotionEffect(this);

        if(effect != null && event.getSource().isFireDamage() && event.getAmount() > 0.5f)
            event.setAmount(0.5f);
    }
}
