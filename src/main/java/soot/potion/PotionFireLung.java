package soot.potion;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import soot.Registry;
import soot.util.ItemUtil;

import java.awt.*;

public class PotionFireLung extends PotionBase {
    public PotionFireLung() {
        super(false, new Color(152,93,63).getRGB());
        setPotionName("FireLung");
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
    public static void onItemUse(LivingEntityUseItemEvent event)
    {
        EntityLivingBase living = event.getEntityLiving();
        ItemStack stack = event.getItem();
        PotionEffect effect = living.getActivePotionEffect(Registry.POTION_FIRE_LUNG);

        if(ItemUtil.matchesOreDict(stack,"torch") && effect != null)
        {
            RayTraceResult raytraceresult = rayTrace(living.world, living, true);



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

    private static RayTraceResult rayTrace(World worldIn, EntityLivingBase playerIn, boolean useLiquids)
    {
        float pitch = playerIn.rotationPitch;
        float yaw = playerIn.rotationYaw;
        double xPos = playerIn.posX;
        double yPos = playerIn.posY + (double)playerIn.getEyeHeight();
        double zPos = playerIn.posZ;
        Vec3d vec3d = new Vec3d(xPos, yPos, zPos);
        float f2 = MathHelper.cos(-yaw * 0.017453292F - (float)Math.PI);
        float f3 = MathHelper.sin(-yaw * 0.017453292F - (float)Math.PI);
        float f4 = -MathHelper.cos(-pitch * 0.017453292F);
        float f5 = MathHelper.sin(-pitch * 0.017453292F);
        float f6 = f3 * f4;
        float f7 = f2 * f4;
        double d3 = 5.0D;
        if (playerIn instanceof EntityPlayerMP)
        {
            d3 = ((EntityPlayerMP)playerIn).interactionManager.getBlockReachDistance();
        }
        Vec3d vec3d1 = vec3d.addVector((double)f6 * d3, (double)f5 * d3, (double)f7 * d3);
        return worldIn.rayTraceBlocks(vec3d, vec3d1, useLiquids, !useLiquids, false);
    }
}
