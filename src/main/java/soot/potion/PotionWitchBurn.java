package soot.potion;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import soot.Soot;
import soot.network.PacketHandler;
import soot.network.message.MessageInspirationFX;
import soot.network.message.MessageWitchBurnFX;
import teamroots.embers.particle.ParticleUtil;

import java.awt.*;
import java.util.Random;
import java.util.WeakHashMap;

public class PotionWitchBurn extends PotionBase {
    public static DamageSource damageSource = new DamageSource("witchBurn");
    public static WeakHashMap<EntityLivingBase,Boolean> appliedEntities = new WeakHashMap<>();

    public PotionWitchBurn() {
        super(true, new Color(64, 255, 16).getRGB());
        setPotionName("effect.witchburn");
        setIconIndex(2, 1);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public boolean isReady(int duration, int amplifier) {
        return duration % 20 == 0;
    }

    @Override
    public void performEffect(EntityLivingBase entity, int amplifier) {
        entity.attackEntityFrom(damageSource, 2.0f);
    }

    @Override
    public boolean shouldRender(PotionEffect effect) {
        return false;
    }

    @Override
    public boolean shouldRenderHUD(PotionEffect effect) {
        return false;
    }

    @Override
    public boolean shouldRenderInvText(PotionEffect effect) {
        return false;
    }

    @SubscribeEvent
    public void onTick(LivingEvent.LivingUpdateEvent event) {
        EntityLivingBase entity = event.getEntityLiving();
        boolean wasApplied = appliedEntities.getOrDefault(entity,false);
        boolean isApplied = entity.isPotionActive(this);

        if (wasApplied && entity.world.isRemote) {
                Random rand = entity.getRNG();
                for (int i = 0; i < 10; i++)
                    ParticleUtil.spawnParticleVapor(entity.world, (float) entity.posX + (rand.nextFloat() - 0.5f) * entity.width, (float) entity.posY + rand.nextFloat() * entity.height, (float) entity.posZ + (rand.nextFloat() - 0.5f) * entity.width, (rand.nextFloat() - 0.5f) * 0.02f, rand.nextFloat() * 0.04f, (rand.nextFloat() - 0.5f) * 0.02f, 64, 255, 16, 1.0f, 2.0f, 4.0f, 10 + rand.nextInt(20));
        }

        if(isApplied != wasApplied && !entity.world.isRemote) {
            PacketHandler.INSTANCE.sendToAll(new MessageWitchBurnFX(entity));
            appliedEntities.put(entity,isApplied);
        }
    }

    @SubscribeEvent
    public void onJoinWorld(EntityJoinWorldEvent event) {
        Entity entity = event.getEntity();
        World world = event.getWorld();
        if(!world.isRemote && entity instanceof EntityPlayer)
        for (EntityLivingBase key : appliedEntities.keySet()) {
            if(key.world == world && appliedEntities.get(key))
                PacketHandler.INSTANCE.sendTo(new MessageWitchBurnFX(key),(EntityPlayerMP)entity);
        }
    }

    @SubscribeEvent
    public void onLeaveWorld(EntityJoinWorldEvent event) {
        Entity entity = event.getEntity();
        if(entity == Soot.proxy.getMainPlayer()) {
            appliedEntities.clear();
        }
    }

    @SubscribeEvent
    public void onHeal(LivingHealEvent event) {
        EntityLivingBase entity = event.getEntityLiving();
        PotionEffect effect = entity.getActivePotionEffect(this);

        if (effect != null) {
            event.setAmount(0);
        }
    }
}
