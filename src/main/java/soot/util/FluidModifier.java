package soot.util;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.Fluid;

import java.util.HashMap;

public class FluidModifier {
    public String name;
    public float defaultValue;
    public boolean showAlways;
    public HashMap<Fluid,Float> defaultValues = new HashMap<>();
    public FormatType formatType = FormatType.LINEAR;

    public FluidModifier(String name, float defaultValue) {
        this.name = name;
        this.defaultValue = defaultValue;
    }

    public FluidModifier setShowAlways() {
        showAlways = true;
        return this;
    }

    public FluidModifier setFormatType(FormatType type) {
        formatType = type;
        return this;
    }

    public void applyEffect(EntityLivingBase target, NBTTagCompound compound, Fluid fluid) {
        //NOOP
    }

    public boolean isDefault(NBTTagCompound compound, Fluid fluid) {
        return getOrDefault(compound,fluid) == defaultValue;
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
        float value = getOrDefault(compound,fluid);
        switch (formatType) {
            case LINEAR:
                return I18n.format("distilling.modifier.dial.linear", getLocalizedName(), (int)value);
            case PERCENTAGE:
                return I18n.format("distilling.modifier.dial.percent", getLocalizedName(), (int)value);
            case MULTIPLIER:
                return I18n.format("distilling.modifier.dial.percent", getLocalizedName(), (int)(value * 100));
            case NAME_ONLY:
                return I18n.format("distilling.modifier.dial.name", getLocalizedName());
            default:
                return "";
        }
    }

    public String getLocalizedName() {
        return I18n.format("distilling.modifier."+name+".name");
    }

    public enum FormatType {
        LINEAR,
        MULTIPLIER,
        PERCENTAGE,
        NAME_ONLY,
        NONE,
    }
}
