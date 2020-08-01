package soot.recipe.breweffects;

import mezz.jei.util.Translator;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fluids.FluidStack;
import soot.util.FluidUtil;

import java.util.List;

public class EffectInfo implements IBrewEffect {
    String modifier;
    String color;

    public EffectInfo(String modifier) {
        this(modifier, getModifierColor(modifier).toString());
    }

    public EffectInfo(String modifier, TextFormatting color) {
        this(modifier, color.toString());
    }

    public EffectInfo(String modifier, String color) {
        this.modifier = modifier;
        this.color = color;
    }

    private static TextFormatting getModifierColor(String modifier) {
        switch (FluidUtil.getEffectType(modifier)) {
            case POSITIVE:
                return TextFormatting.GREEN;
            case NEGATIVE:
                return TextFormatting.RED;
            case NEUTRAL:
                return TextFormatting.BLUE;
            default:
                return TextFormatting.GRAY;
        }
    }

    @Override
    public void modify(FluidStack output, NBTTagCompound compound) {
        //NOOP
    }

    @Override
    public void modifyTooltip(List<String> tooltip) {
        tooltip.add(tooltip.size() - 1, color + Translator.translateToLocalFormatted("distilling.effect."+modifier));
    }
}
