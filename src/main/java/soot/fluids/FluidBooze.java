package soot.fluids;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import soot.util.FluidUtil;

public class FluidBooze extends Fluid {
    public FluidBooze(String fluidName, ResourceLocation still, ResourceLocation flowing) {
        super(fluidName, still, flowing);
    }

    @Override
    public String getLocalizedName(FluidStack stack) {
        NBTTagCompound compound = FluidUtil.getModifiers(stack);
        String customname = compound.getString("custom_name");
        return !customname.isEmpty() ? customname : super.getLocalizedName(stack);
    }

    @Override
    public int getTemperature(FluidStack stack) {
        NBTTagCompound compound = FluidUtil.getModifiers(stack);
        return Math.max(0,super.getTemperature(stack) + (int)compound.getFloat("heat"));
    }

    @Override
    public int getLuminosity(FluidStack stack) {
        NBTTagCompound compound = FluidUtil.getModifiers(stack);
        return Math.max(0,super.getLuminosity(stack) + compound.getInteger("light"));
    }

    @Override
    public int getViscosity(FluidStack stack) {
        NBTTagCompound compound = FluidUtil.getModifiers(stack);
        return Math.max(0,super.getLuminosity(stack) + compound.getInteger("viscosity"));
    }
}
