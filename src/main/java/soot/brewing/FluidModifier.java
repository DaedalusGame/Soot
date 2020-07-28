package soot.brewing;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.fluids.Fluid;
import soot.brewing.CaskManager.CaskPotionEffect;
import teamroots.embers.Embers;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FluidModifier {
    public String name;
    public float defaultValue;
    public boolean showAlways;
    public HashMap<Fluid,Float> defaultValues = new HashMap<>();
    public String formatType = "linear";
    public EnumType type;
    public EffectType effectType;

    public FluidModifier(String name, float defaultValue, EnumType type, EffectType effectType) {
        this.name = name;
        this.defaultValue = defaultValue;
        this.type = type;
        this.effectType = effectType;
    }

    public FluidModifier setShowAlways() {
        showAlways = true;
        return this;
    }

    public FluidModifier setFormatType(String type) {
        formatType = type;
        return this;
    }

    public void applyEffect(EntityLivingBase target, NBTTagCompound compound, Fluid fluid) {
        //NOOP
    }

    public void providePotionEffects(List<PotionEffect> effects, NBTTagCompound compound, Fluid fluid) {
        //NOOP
    }

    public void providePotionEffects(EntityLivingBase target, ArrayList<CaskPotionEffect> effects, NBTTagCompound compound, Fluid fluid) {
        //NOOP
    }

    public boolean isDefault(NBTTagCompound compound, Fluid fluid) {
        return getOrDefault(compound,fluid) == defaultValue;
    }

    public boolean hasDefault(Fluid fluid) {
        return defaultValues.containsKey(fluid);
    }

    public boolean has(NBTTagCompound compound, Fluid fluid) {
        return compound.hasKey(name,99) || defaultValues.containsKey(fluid);
    }

    public float getOrDefault(NBTTagCompound compound, Fluid fluid)
    {
        if(compound.hasKey(name,99))
            return compound.getFloat(name);
        else
            return defaultValues.containsKey(fluid) ? defaultValues.get(fluid) : defaultValue;
    }

    public void set(NBTTagCompound compound, float value)
    {
        compound.setFloat(name,value);
    }

    public void setDefault(Fluid fluid, float value) {
        defaultValues.put(fluid,value);
    }

    public String getFormattedText(NBTTagCompound compound, Fluid fluid) {
        if(formatType == null)
            return "";
        float value = getOrDefault(compound,fluid);
        DecimalFormat format = Embers.proxy.getDecimalFormat("embers.decimal_format.distilling."+formatType);
        return I18n.format("distilling.modifier.dial."+formatType, getLocalizedName(), format.format(value));
    }

    public String getLocalizedName() {
        return I18n.format("distilling.modifier."+name+".name");
    }

    public EssenceStack toEssence(float amount) {
        return EssenceStack.EMPTY;
    }

    public enum EnumType {
        PRIMARY,
        SECONDARY,
        TERTIARY
    }

    public enum EffectType {
        POSITIVE,
        NEGATIVE,
        NEUTRAL
    }
}
