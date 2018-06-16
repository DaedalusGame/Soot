package soot.potion;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import soot.Soot;
import soot.entity.EntityMuse;
import soot.network.PacketHandler;
import soot.network.message.MessageInspirationFX;
import soot.network.message.MessageInspirationFX.Type;
import soot.network.message.MessageMuseSpawnFX;
import soot.util.InspirationSound;

import java.awt.*;
import java.util.HashSet;
import java.util.Random;
import java.util.UUID;

public class PotionInspiration extends PotionBase {
    HashSet<UUID> affectedPlayers = new HashSet<>();
    boolean isAffected;

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
    public void applyAttributesModifiersToEntity(EntityLivingBase entityLivingBaseIn, AbstractAttributeMap attributeMapIn, int amplifier) {
        super.applyAttributesModifiersToEntity(entityLivingBaseIn, attributeMapIn, amplifier);
        if(entityLivingBaseIn instanceof EntityPlayerMP) {
            boolean lastAffected = affectedPlayers.contains(entityLivingBaseIn.getUniqueID());
            PacketHandler.INSTANCE.sendTo(new MessageInspirationFX(lastAffected ? Type.Refresh : Type.Start), (EntityPlayerMP) entityLivingBaseIn);
        }
    }

    @Override
    public void removeAttributesModifiersFromEntity(EntityLivingBase entityLivingBaseIn, AbstractAttributeMap attributeMapIn, int amplifier) {
        super.removeAttributesModifiersFromEntity(entityLivingBaseIn, attributeMapIn, amplifier);
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
            //player.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 0.1F, (rng.nextFloat() - rng.nextFloat()) * 0.35F + 0.9F);
            if(duration % 5 == 0 && !entity.world.isRemote)
                player.addExperience(amplifier + 1);
            if(duration % (20*Math.max(15-amplifier*5,5)) == 0 && !entity.world.isRemote) {
                BlockPos blockpos = player.getPosition().add(-radius + rng.nextInt(2*radius+1), 1, -radius + rng.nextInt(2*radius+1));
                EntityMuse muse = new EntityMuse(player.world);
                muse.moveToBlockPosAndAngles(blockpos,0,0);
                muse.setBoundPlayer(player);
                muse.setLimitedLife(600);
                player.world.spawnEntity(muse);
                if(player instanceof EntityPlayerMP)
                    PacketHandler.INSTANCE.sendTo(new MessageMuseSpawnFX(muse.posX,muse.posY+muse.height/2,muse.posZ), (EntityPlayerMP) player);
            }
        }
        else {
            entity.setRevengeTarget(null);
        }
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if(event.phase == TickEvent.Phase.END || event.side == Side.CLIENT)
            return;

        EntityPlayer player = event.player;
        UUID uuid = player.getUniqueID();
        boolean lastAffected = affectedPlayers.contains(uuid);
        boolean isPotionActive = player.isPotionActive(this);
        if(isPotionActive && !lastAffected) {
            affectedPlayers.add(uuid);
        }
        if(!isPotionActive && lastAffected) {
            if(player instanceof EntityPlayerMP)
                PacketHandler.INSTANCE.sendTo(new MessageInspirationFX(Type.Stop), (EntityPlayerMP) player);
            affectedPlayers.remove(uuid);
        }
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if(event.phase == TickEvent.Phase.END || event.side == Side.SERVER)
            return;

        EntityPlayer player = Soot.proxy.getMainPlayer();
        if(player == null)
            return;
        boolean isPotionActive = player.isPotionActive(this);
        if(!isAffected && isPotionActive)
            Minecraft.getMinecraft().getSoundHandler().playSound(new InspirationSound(player, soot.SoundEvents.INSPIRATION_LOOP, SoundCategory.AMBIENT, 1.0f, 1.0f));
        isAffected = isPotionActive;
    }
}
