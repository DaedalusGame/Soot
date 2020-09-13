package soot.util;

import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Tuple;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import soot.Registry;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MiscUtil {
    @SideOnly(Side.CLIENT)
    public static void addPotionEffectTooltip(List<PotionEffect> list, List<String> lores, float durationFactor)
    {
        List<Tuple<String, AttributeModifier>> attributeModifiers = Lists.newArrayList();

        if (list.isEmpty())
        {
            String s = I18n.translateToLocal("effect.none").trim();
            lores.add(TextFormatting.GRAY + s);
        }
        else
        {
            for (PotionEffect potioneffect : list)
            {
                String s1 = I18n.translateToLocal(potioneffect.getEffectName()).trim();
                Potion potion = potioneffect.getPotion();
                Map<IAttribute, AttributeModifier> map = potion.getAttributeModifierMap();

                if (!map.isEmpty())
                {
                    for (Map.Entry<IAttribute, AttributeModifier> entry : map.entrySet())
                    {
                        AttributeModifier attributemodifier = entry.getValue();
                        AttributeModifier attributemodifier1 = new AttributeModifier(attributemodifier.getName(), potion.getAttributeModifierAmount(potioneffect.getAmplifier(), attributemodifier), attributemodifier.getOperation());
                        attributeModifiers.add(new Tuple(entry.getKey().getName(), attributemodifier1));
                    }
                }

                if (potioneffect.getAmplifier() > 0)
                {
                    s1 = s1 + " " + I18n.translateToLocal("potion.potency." + potioneffect.getAmplifier()).trim();
                }

                if (potioneffect.getDuration() > 20)
                {
                    s1 = s1 + " (" + Potion.getPotionDurationString(potioneffect, durationFactor) + ")";
                }

                if (potion.isBadEffect())
                {
                    lores.add(TextFormatting.RED + s1);
                }
                else
                {
                    lores.add(TextFormatting.BLUE + s1);
                }
            }
        }

        if (!attributeModifiers.isEmpty())
        {
            lores.add("");
            lores.add(TextFormatting.DARK_PURPLE + I18n.translateToLocal("potion.whenDrank"));

            for (Tuple<String, AttributeModifier> tuple : attributeModifiers)
            {
                AttributeModifier attributemodifier2 = tuple.getSecond();
                double d0 = attributemodifier2.getAmount();
                double d1;

                if (attributemodifier2.getOperation() != 1 && attributemodifier2.getOperation() != 2)
                {
                    d1 = attributemodifier2.getAmount();
                }
                else
                {
                    d1 = attributemodifier2.getAmount() * 100.0D;
                }

                if (d0 > 0.0D)
                {
                    lores.add(TextFormatting.BLUE + I18n.translateToLocalFormatted("attribute.modifier.plus." + attributemodifier2.getOperation(), ItemStack.DECIMALFORMAT.format(d1), I18n.translateToLocal("attribute.name." + (String)tuple.getFirst())));
                }
                else if (d0 < 0.0D)
                {
                    d1 = d1 * -1.0D;
                    lores.add(TextFormatting.RED + I18n.translateToLocalFormatted("attribute.modifier.take." + attributemodifier2.getOperation(), ItemStack.DECIMALFORMAT.format(d1), I18n.translateToLocal("attribute.name." + (String)tuple.getFirst())));
                }
            }
        }
    }

    public static void damageWithoutInvulnerability(Entity entity, DamageSource source, float amount)
    {
        if(entity.attackEntityFrom(source,amount))
            entity.hurtResistantTime = 0;
    }

    public static void degradeEquipment(EntityLivingBase entity, int amt) {
        for (ItemStack armor : entity.getArmorInventoryList()) {
            if(armor.isItemStackDamageable())
                armor.damageItem(amt, entity);
        }
    }

    public static boolean isPhysicalDamage(DamageSource damageSource)
    {
        return damageSource.getImmediateSource() != null && !damageSource.isProjectile() && !damageSource.isExplosion() && !damageSource.isFireDamage() && !damageSource.isMagicDamage() && !damageSource.isDamageAbsolute();
    }

    public static boolean isBarehandedDamage(DamageSource damageSource, EntityLivingBase attacker)
    {
        return attacker.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND).isEmpty() && isPhysicalDamage(damageSource);
    }

    public static boolean isEitrDamage(DamageSource damageSource, EntityLivingBase attacker)
    {
        return attacker.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND).getItem() == Registry.EITR && isPhysicalDamage(damageSource);
    }

    public static void setLore(List<String> addedLore, NBTTagCompound compound, boolean append) {
        if(compound != null && compound.hasKey("display",10)) {
            NBTTagCompound display = compound.getCompoundTag("display");
            NBTTagList lore;
            if(display.hasKey("Lore",9) && append)
                lore = display.getTagList("Lore", 8);
            else {
                lore = new NBTTagList();
                display.setTag("Lore",lore);
            }
            for(String loreString : addedLore)
                lore.appendTag(new NBTTagString(loreString));
        }
    }

    public static List<String> getLore(NBTTagCompound compound) {
        ArrayList<String> addedLore = new ArrayList<>();
        if(compound != null && compound.hasKey("display",10)) {
            NBTTagCompound display = compound.getCompoundTag("display");
            if(display.hasKey("Lore",9)) {
                NBTTagList lore = display.getTagList("Lore",8);
                if(!lore.hasNoTags())
                    for (int i = 0; i < lore.tagCount(); ++i)
                        addedLore.add(lore.getStringTagAt(i));
            }
        }
        return addedLore;
    }

    public static Item.ToolMaterial getToolMaterial(Item item) {
        if(item instanceof ItemTool) {
            return ObfuscationReflectionHelper.getPrivateValue(ItemTool.class, (ItemTool)item, "field_77862_b");
        }
        if(item instanceof ItemHoe) {
            return ObfuscationReflectionHelper.getPrivateValue(ItemHoe.class, (ItemHoe)item, "field_77843_a");
        }
        if(item instanceof ItemSword) {
            return ObfuscationReflectionHelper.getPrivateValue(ItemSword.class, (ItemSword)item, "field_150933_b");
        }
        return null;
    }

    public static ItemArmor.ArmorMaterial getArmorMaterial(Item item)
    {
        if(item instanceof ItemArmor) {
            return ObfuscationReflectionHelper.getPrivateValue(ItemArmor.class, (ItemArmor)item, "field_77878_bZ");
        }
        return null;
    }

    public static String generateFormatMatchCode(int code) {
        String formatCode = "";
        for(int i = 0; i < 4; i++) { //Short.maxValue should be good enough for codes
            formatCode += TextFormatting.fromColorIndex((code >> i*4) & 15);
        }
        return formatCode + TextFormatting.RESET;
    }

    public static String generateEmptyString(String original) { //Possibly taken from Quark but don't tell anybody
        int len = Minecraft.getMinecraft().fontRenderer.getStringWidth(original);
        String spaces = "";
        while(Minecraft.getMinecraft().fontRenderer.getStringWidth(spaces) < len)
            spaces += " ";
        return spaces;
    }

    public static Color parseColor(int[] rgb) {
        Color color = Color.WHITE;
        if (rgb != null && rgb.length >= 3 && rgb.length <= 4)
            color = new Color(rgb[0], rgb[1], rgb[2], rgb.length == 4 ? rgb[3] : 255);
        return color;
    }

    public static Color lerpColor(Color color1, Color color2, double scale) {
        return new Color(
                lerp(color1.getRed(),color2.getRed(),scale),
                lerp(color1.getGreen(),color2.getGreen(),scale),
                lerp(color1.getBlue(),color2.getBlue(),scale),
                lerp(color1.getAlpha(),color2.getAlpha(),scale)
        );
    }

    public static Color maxColor(Color color) {
        float multiplier = 1.0f / Math.max(color.getRed(),Math.max(color.getGreen(),color.getBlue()));
        return new Color(color.getRed()*multiplier,color.getGreen()*multiplier,color.getBlue()*multiplier,color.getAlpha()/255.0f);
    }

    public static int lerp(int a, int b, double scale) {
        return (int)(a*(1-scale)+b*(scale));
    }
}
