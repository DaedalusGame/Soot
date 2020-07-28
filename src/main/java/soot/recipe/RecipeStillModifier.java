package soot.recipe;

import mezz.jei.util.Translator;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import soot.recipe.breweffects.IBrewEffect;
import soot.tile.TileEntityStillBase;
import soot.util.FluidUtil;

import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

public class RecipeStillModifier extends RecipeStill {
    public HashSet<Fluid> validFluids = new HashSet<>();
    public List<IBrewEffect> effects = new ArrayList<>();

    public RecipeStillModifier(ResourceLocation id, Collection<Fluid> validFluids, Ingredient catalystInput, int catalystConsumed) {
        super(id,null, catalystInput, catalystConsumed, null);
        this.validFluids = new HashSet<>(validFluids);
    }

    public RecipeStillModifier addEffect(IBrewEffect effect) {
        effects.add(effect);
        return this;
    }

    @Override
    public boolean matches(TileEntityStillBase tile, FluidStack stack, ItemStack catalyst) {
        return stack != null && catalystInput.apply(catalyst) && validFluids.contains(stack.getFluid()) && stack.amount >= getInputConsumed();
    }

    @Override
    public int getInputConsumed() {
        return 1;
    }

    @Override
    public List<FluidStack> getInputs() {
        return validFluids.stream().map(fluid -> new FluidStack(fluid, getInputConsumed())).collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public List<FluidStack> getOutputs() {
        return getInputs();
    }

    @Override
    public FluidStack getOutput(TileEntityStillBase tile, FluidStack input)
    {
        FluidStack outputStack = input.copy();
        outputStack.amount = 1;
        modifyOutput(tile, outputStack);
        return outputStack;
    }

    public void modifyOutput(TileEntityStillBase tile, FluidStack output) {
        NBTTagCompound compound = FluidUtil.createModifiers(output);
        for (IBrewEffect effect : effects) {
            effect.modify(output, compound);
        }
    }

    @Override
    public void modifyTooltip(List<String> tooltip) {
        super.modifyTooltip(tooltip);
        tooltip.remove(1);
        tooltip.add(1, TextFormatting.LIGHT_PURPLE+Translator.translateToLocalFormatted("distilling.effect.header"));
        for (IBrewEffect effect : effects) {
            effect.modifyTooltip(tooltip);
        }
    }

    protected void addModifier(List<String> tooltip, String modifier, boolean positive) {
        TextFormatting color = positive ? TextFormatting.GREEN : TextFormatting.RED;
        tooltip.add(tooltip.size() - 1, color + Translator.translateToLocalFormatted("distilling.effect."+modifier));
    }

    protected void addModifierLinear(List<String> tooltip, String modifier, float amount, boolean positive) {
        DecimalFormat format = new DecimalFormat("#.####");
        String key;
        TextFormatting color;
        if(amount > 0) {
            key = "distilling.effect.add";
            color = positive ? TextFormatting.GREEN : TextFormatting.RED;
        } else {
            key = "distilling.effect.sub";
            color = positive ? TextFormatting.RED : TextFormatting.GREEN;
        }
        tooltip.add(tooltip.size() - 1, color + Translator.translateToLocalFormatted(key, Translator.translateToLocal("distilling.modifier."+modifier+".name"), format.format(Math.abs(amount))));
    }

    protected void addModifierPercent(List<String> tooltip, String modifier, float amount, boolean positive) {
        DecimalFormat format = new DecimalFormat("#.####");
        String key;
        TextFormatting color;
        if(amount > 0) {
            key = "distilling.effect.add_percent";
            color = positive ? TextFormatting.GREEN : TextFormatting.RED;
        } else {
            key = "distilling.effect.sub_percent";
            color = positive ? TextFormatting.RED : TextFormatting.GREEN;
        }
        tooltip.add(tooltip.size() - 1, color + Translator.translateToLocalFormatted(key, Translator.translateToLocal("distilling.modifier."+modifier+".name"), format.format(Math.abs(amount))));
    }

    protected void addModifierLoss(List<String> tooltip, float amount) {
        DecimalFormat format = new DecimalFormat("#.####");
        tooltip.add(tooltip.size() - 1, TextFormatting.RED + Translator.translateToLocalFormatted("distilling.effect.loss", format.format(amount)));
    }

    public float getModifierOrDefault(String name, NBTTagCompound compound, FluidStack fluid)
    {
        return FluidUtil.getModifier(compound, fluid!=null?fluid.getFluid():null, name);
    }
}
