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

public class EffectMultiply implements IBrewEffect {
    String modifier;
    float multiplier;
    float minLimit, maxLimit;
    boolean hidden;

    public EffectMultiply(String modifier, float multiplier, float minLimit, float maxLimit, boolean hidden) {
        this.modifier = modifier;
        this.multiplier = multiplier;
        this.minLimit = minLimit;
        this.maxLimit = maxLimit;
        this.hidden = hidden;
    }

    public EffectMultiply(String modifier, float multiplier, boolean hidden) {
        this(modifier, multiplier, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, hidden);
    }

    private FluidModifier.EffectType getEffectType() {
        return FluidUtil.getEffectType(modifier);
    }

    @Override
    public void modify(FluidStack output, NBTTagCompound compound) {
        float value = getModifierOrDefault(modifier, compound, output);
        compound.setFloat(modifier, multiply(value));
    }

    private float multiply(float base) {
        if(base > minLimit && base < maxLimit)
            return MathHelper.clamp(base * multiplier, minLimit, maxLimit);
        return base;
    }

    @Override
    public void modifyTooltip(List<String> tooltip) {
        if(hidden)
            return;
        FluidModifier.EffectType type = getEffectType();
        DecimalFormat format = new DecimalFormat("#.####");
        float amount = (multiplier - 1) * 100;
        String key;
        TextFormatting color;
        if(amount > 0) {
            key = "distilling.effect.add_percent";
            color = getTextColor(type, false);
        } else {
            key = "distilling.effect.sub_percent";
            color = getTextColor(type, true);
        }
        tooltip.add(tooltip.size() - 1, color + Translator.translateToLocalFormatted(key, Translator.translateToLocal("distilling.modifier."+modifier+".name"), format.format(Math.abs(amount))));
    }
}
