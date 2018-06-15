package soot.potion;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import soot.Registry;
import soot.entity.EntityFireCloud;
import teamroots.embers.particle.ParticleUtil;
import teamroots.embers.util.ItemUtil;

import java.awt.*;

public class PotionFireLung extends PotionBase {
    public PotionFireLung() {
        super(false, new Color(152,93,63).getRGB());
        setPotionName("effect.fire_lung");
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
    public void onItemUse(PlayerInteractEvent.RightClickItem event)
    {
        World world = event.getWorld();
        EntityLivingBase living = event.getEntityLiving();
        ItemStack stack = event.getItemStack();
        PotionEffect effect = living.getActivePotionEffect(this);

        if(ItemUtil.matchesOreDict(stack,"torch") && effect != null)
        {
            Vec3d hitPos = rayTrace(living.world, living, true);
            if(!world.isRemote) {
                EntityFireCloud cloud = new EntityFireCloud(world, hitPos.x, hitPos.y, hitPos.z);
                cloud.setDuration(20 * 10);
                cloud.setRadius(2.0f);
                cloud.setOwner(living);
                cloud.setColor(0xFFFF4010);
                cloud.setInvisible(true);
                world.spawnEntity(cloud);
            }
            else {
                Vec3d eyePos = living.getPositionEyes(1);
                Vec3d vel = hitPos.subtract(eyePos).scale(0.003);
                ParticleUtil.spawnParticleGlow(world, (float)eyePos.x, (float)eyePos.y, (float)eyePos.z, (float)vel.x, (float)vel.y, (float)vel.x, 255, 64, 16, 255, 2.0f, 50);
            }

            if(effect.getDuration() > 10)
            {
                PotionEffect reducedEffect = new PotionEffect(Registry.POTION_FIRE_LUNG, effect.getDuration() - 5, effect.getAmplifier(), effect.getIsAmbient(), effect.doesShowParticles());
                reducedEffect.setCurativeItems(effect.getCurativeItems());
                living.addPotionEffect(reducedEffect);
            }
            else
            {
                living.removePotionEffect(Registry.POTION_FIRE_LUNG);
            }

            event.setCanceled(true);
        }
    }

    private static Vec3d rayTrace(World worldIn, EntityLivingBase playerIn, boolean useLiquids)
    {
        float pitch = playerIn.rotationPitch;
        float yaw = playerIn.rotationYaw;
        double xPos = playerIn.posX;
        double yPos = playerIn.posY + (double)playerIn.getEyeHeight();
        double zPos = playerIn.posZ;
        Vec3d startPos = new Vec3d(xPos, yPos, zPos);
        float f2 = MathHelper.cos(-yaw * 0.017453292F - (float)Math.PI);
        float f3 = MathHelper.sin(-yaw * 0.017453292F - (float)Math.PI);
        float f4 = -MathHelper.cos(-pitch * 0.017453292F);
        float f5 = MathHelper.sin(-pitch * 0.017453292F);
        float f6 = f3 * f4;
        float f7 = f2 * f4;
        double distance = 5.0D;
        if (playerIn instanceof EntityPlayerMP)
        {
            distance = ((EntityPlayerMP) playerIn).interactionManager.getBlockReachDistance();
        }
        Vec3d endPos = startPos.addVector((double) f6 * distance, (double) f5 * distance, (double) f7 * distance);
        RayTraceResult result = worldIn.rayTraceBlocks(startPos, endPos, useLiquids, !useLiquids, false);
        return result != null ? result.hitVec : endPos;
    }

}
