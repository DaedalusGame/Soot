package soot.util;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.Fluid;

import java.util.HashMap;

public class FluidModifier {
    public String name;
    public float defaultValue;
    public HashMap<Fluid,Float> defaultValues = new HashMap<>();

    public FluidModifier(String name, float defaultValue) {
        this.name = name;
        this.defaultValue = defaultValue;
    }

    public void applyEffect(EntityLivingBase target, NBTTagCompound compound, Fluid fluid) {
        //NOOP
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
}
