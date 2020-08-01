package soot.recipe.breweffects;

import mezz.jei.util.Translator;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fluids.FluidStack;

import java.text.DecimalFormat;
import java.util.List;

public class EffectLoss implements IBrewEffect {
    int ratioInput;
    int ratioOutput;

    public EffectLoss(int ratioInput, int ratioOutput) {
        this.ratioInput = ratioInput;
        this.ratioOutput = ratioOutput;
    }

    @Override
    public void modify(FluidStack output, NBTTagCompound compound) {
        output.amount = ratioOutput;
    }

    @Override
    public void modifyTooltip(List<String> tooltip) {
        DecimalFormat format = new DecimalFormat("#%");
        if(ratioOutput > ratioInput)
            tooltip.add(tooltip.size() - 1, TextFormatting.GREEN + Translator.translateToLocalFormatted("distilling.effect.gain", format.format(ratioOutput * 1f / ratioInput - 1f)));
        else
            tooltip.add(tooltip.size() - 1, TextFormatting.RED + Translator.translateToLocalFormatted("distilling.effect.loss", format.format( ratioOutput * 1f / ratioInput - 1f)));
    }
}
