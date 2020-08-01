package soot.recipe.breweffects;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fluids.FluidStack;
import soot.brewing.FluidModifier;
import soot.util.FluidUtil;

import java.util.List;

public interface IBrewEffect {
    void modify(FluidStack output, NBTTagCompound compound);

    void modifyTooltip(List<String> tooltip);

    default float getModifierOrDefault(String name, NBTTagCompound compound, FluidStack fluid) {
        return FluidUtil.getModifier(compound, fluid != null ? fluid.getFluid() : null, name);
    }

    default TextFormatting getTextColor(FluidModifier.EffectType type, boolean invert) {
        switch (type) {
            case POSITIVE:
                return invert ? TextFormatting.RED : TextFormatting.GREEN;
            case NEGATIVE:
                return invert ? TextFormatting.GREEN : TextFormatting.RED;
            case NEUTRAL:
                return TextFormatting.BLUE;
            default:
                return TextFormatting.GRAY;
        }
    }
}
