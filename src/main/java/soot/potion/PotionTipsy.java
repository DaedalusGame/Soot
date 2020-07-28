package soot.potion;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import soot.util.Attributes;
import teamroots.embers.EventManager;

import java.awt.*;
import java.lang.reflect.Field;
import java.util.Random;

public class PotionTipsy extends PotionBase {
    public PotionTipsy() {
        super(true, new Color(90,70,20).getRGB());
        setPotionName("effect.tipsy");
        setIconIndex(1,1);
        registerPotionAttributeModifier(SharedMonsterAttributes.KNOCKBACK_RESISTANCE,"ba230016-5662-419c-a577-bd14d3f4a551",0.4,2);
        registerPotionAttributeModifier(Attributes.BAREHANDED_POWER,"89a374d8-f058-4bc8-840f-a3a89ea1f904",0.4,2);
    }

    Field handDropChances;

    @Override
    public boolean isReady(int duration, int amplifier) {
        return true;
    }

    private void turnEntity(Entity entity, float angle) {
        entity.rotationYaw += angle;

        if (entity.getRidingEntity() != null) {
            entity.getRidingEntity().applyOrientationToEntity(entity);
        }
    }

    @Override
    public void performEffect(EntityLivingBase entity, int amplifier) {
        Random random = entity.getRNG();
        PotionEffect effect = entity.getActivePotionEffect(this);

        if(entity instanceof EntityPlayer == entity.world.isRemote) {
            double dx = entity.motionX;
            double dy = entity.motionY;
            double dz = entity.motionZ;
            float speed = (float) MathHelper.clamp(Math.sqrt(dx * dx + dz * dz),0.2,1);
            if(speed != 0)
            turnEntity(entity, (float) Math.sin(EventManager.ticks / 30.0) * 4f * speed * (amplifier+1));
        }

        if(entity.world.isRemote) {
            return;
        }

        if(effect != null && effect.getDuration() % 10 == 0) { //Every half second, have a % chance to drop item from hand
            if (handDropChances == null)
                handDropChances = ObfuscationReflectionHelper.findField(EntityLiving.class, "field_82174_bp");

            if (random.nextInt(100) < amplifier) {
                boolean canDropMain = true;
                boolean canDropOffhand = true;
                if (entity instanceof EntityLiving) {
                    try {
                        float[] chances = (float[]) handDropChances.get(entity);
                        canDropMain = chances[EntityEquipmentSlot.MAINHAND.getIndex()] > 0;
                        canDropOffhand = chances[EntityEquipmentSlot.OFFHAND.getIndex()] > 0;
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
                if (random.nextBoolean() && canDropMain) {
                    dropItem(entity, EntityEquipmentSlot.MAINHAND);
                } else if (canDropOffhand) {
                    dropItem(entity, EntityEquipmentSlot.OFFHAND);
                }
            }
        }
    }

    public void dropItem(EntityLivingBase entity, EntityEquipmentSlot slot)
    {
        ItemStack droppedItem = entity.getItemStackFromSlot(slot);
        entity.setItemStackToSlot(slot,ItemStack.EMPTY);
        double d0 = entity.posY - 0.30000001192092896D + (double)entity.getEyeHeight();
        EntityItem entityitem = new EntityItem(entity.world, entity.posX, d0, entity.posZ, droppedItem);
        entityitem.setPickupDelay(40);
        entityitem.setThrower(entity.getName());
        float f2 = 0.3F;
        entityitem.motionX = (double)(-MathHelper.sin(entity.rotationYaw * 0.017453292F) * MathHelper.cos(entity.rotationPitch * 0.017453292F) * f2);
        entityitem.motionZ = (double)(MathHelper.cos(entity.rotationYaw * 0.017453292F) * MathHelper.cos(entity.rotationPitch * 0.017453292F) * f2);
        entityitem.motionY = (double)(-MathHelper.sin(entity.rotationPitch * 0.017453292F) * f2 + 0.1F);
        Random rng = entity.getRNG();
        float f3 = rng.nextFloat() * ((float)Math.PI * 2F);
        f2 = 0.02F * rng.nextFloat();
        entityitem.motionX += Math.cos((double)f3) * (double)f2;
        entityitem.motionY += (double)((rng.nextFloat() - rng.nextFloat()) * 0.1F);
        entityitem.motionZ += Math.sin((double)f3) * (double)f2;
        entity.world.spawnEntity(entityitem);
    }
}
