package soot.recipe.breweffects;

import mezz.jei.util.Translator;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fluids.FluidStack;
import soot.brewing.FluidModifier;
import soot.util.FluidUtil;
import teamroots.embers.util.DecimalFormats;

import java.text.NumberFormat;
import java.util.List;

public class EffectAdd implements IBrewEffect {
    String modifier;
    float amount;
    float limit;
    boolean hidden;

    public EffectAdd(String modifier, float amount, boolean hidden) {
        this(modifier, amount, amount * Float.POSITIVE_INFINITY, hidden);
    }

    public EffectAdd(String modifier, float amount, float limit, boolean hidden) {
        this.modifier = modifier;
        this.amount = amount;
        this.limit = limit;
        this.hidden = hidden;
    }

    private FluidModifier.EffectType getEffectType() {
        return FluidUtil.getEffectType(modifier);
    }

    @Override
    public void modify(FluidStack output, NBTTagCompound compound) {
        float value = getModifierOrDefault(modifier, compound, output);
        compound.setFloat(modifier, add(value));
    }

    private float add(float base) {
        if(amount > 0 && base < limit)
            return Math.min(base + amount, limit);
        else if(amount < 0 && base > limit)
            return Math.max(base + amount, limit);
        else
            return base;
    }

    @Override
    public void modifyTooltip(List<String> tooltip) {
        String formatType = FluidUtil.getFormatType(modifier);
        NumberFormat format = DecimalFormats.getDecimalFormat("embers.decimal_format.modifier."+formatType);
        if(formatType == null || hidden)
            return;
        FluidModifier.EffectType type = getEffectType();
        String key;
        TextFormatting color;
        if(amount > 0) {
            key = "distilling.effect.add";
            color = getTextColor(type, false);
        } else {
            key = "distilling.effect.sub";
            color = getTextColor(type, true);
        }
        tooltip.add(tooltip.size() - 1, color + Translator.translateToLocalFormatted(key, Translator.translateToLocal("distilling.modifier."+modifier+".name"), format.format(Math.abs(amount))));
    }
}
