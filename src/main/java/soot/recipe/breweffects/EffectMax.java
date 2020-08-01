package soot.recipe.breweffects;

import mezz.jei.util.Translator;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fluids.FluidStack;
import soot.brewing.FluidModifier;
import soot.util.FluidUtil;

import java.text.DecimalFormat;
import java.util.List;

public class EffectMax implements IBrewEffect {
    String modifier;
    float amount;
    boolean hidden;

    public EffectMax(String modifier, float amount, boolean hidden) {
        this.modifier = modifier;
        this.amount = amount;
        this.hidden = hidden;
    }

    private FluidModifier.EffectType getEffectType() {
        return FluidUtil.getEffectType(modifier);
    }

    @Override
    public void modify(FluidStack output, NBTTagCompound compound) {
        float value = getModifierOrDefault(modifier, compound, output);
        compound.setFloat(modifier, Math.max(value, amount));
    }

    @Override
    public void modifyTooltip(List<String> tooltip) {
        //NOOP
    }
}
