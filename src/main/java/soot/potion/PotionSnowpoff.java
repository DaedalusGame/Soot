package soot.potion;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import soot.Registry;
import soot.entity.EntitySnowpoff;
import soot.util.Attributes;

import java.awt.*;
import java.util.Random;

public class PotionSnowpoff extends PotionBase {
    public PotionSnowpoff() {
        super(false, new Color(240,255,255).getRGB());
        setPotionName("effect.snowpoff");
        setIconIndex(4,0);
        setBeneficial();
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onAttack(LivingAttackEvent event) {
        EntityLivingBase entity = event.getEntityLiving();
        PotionEffect effect = entity.getActivePotionEffect(Registry.POTION_SNOWPOFF);
        if(effect != null && !entity.world.isRemote) {
            int maxBalls = 3 + effect.getAmplifier();
            Random rng = entity.getRNG();
            double angleOffset = rng.nextInt(360);
            for(int i = 0; i < maxBalls; i++) {
                double angle = i * 360.0 / maxBalls + angleOffset;
                double xVel = Math.sin(angle * 0.017453292F);
                double yVel = 0 + rng.nextDouble() * 1;
                double zVel = Math.cos(angle * 0.017453292F);

                EntitySnowpoff poff = new EntitySnowpoff(entity.world,entity);
                poff.shoot(xVel,yVel,zVel,0.4f,0.5f);
                entity.world.spawnEntity(poff);
            }
        }
    }
}
