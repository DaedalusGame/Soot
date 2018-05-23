package soot.potion;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import soot.entity.EntityMuse;

import java.awt.*;
import java.util.Random;

public class PotionInspiration extends PotionBase {
    public PotionInspiration() {
        super(false, new Color(64,255,32).getRGB());
        setPotionName("effect.inspiration");
        setIconIndex(5,0);
        setBeneficial();
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public boolean isReady(int duration, int amplifier) {
        return true;
    }

    @Override
    public void performEffect(EntityLivingBase entity, int amplifier) {
        PotionEffect effect = entity.getActivePotionEffect(this);
        assert effect != null;
        int radius = 4;
        int duration = effect.getDuration();
        if(entity instanceof EntityPlayer) {
            Random rng = entity.getRNG();
            EntityPlayer player = (EntityPlayer) entity;
            player.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 0.1F, (rng.nextFloat() - rng.nextFloat()) * 0.35F + 0.9F);
            if(duration % 5 == 0 && !entity.world.isRemote)
                player.addExperience(amplifier + 1);
            if(duration % (20*Math.max(15-amplifier*5,5)) == 0 && !entity.world.isRemote) {
                BlockPos blockpos = player.getPosition().add(-radius + rng.nextInt(2*radius+1), 1, -radius + rng.nextInt(2*radius+1));
                EntityMuse muse = new EntityMuse(player.world);
                muse.moveToBlockPosAndAngles(blockpos,0,0);
                muse.setBoundPlayer(player);
                muse.setLimitedLife(600);
                player.world.spawnEntity(muse);
            }
        }
        else {
            entity.setRevengeTarget(null);
        }
    }
}
