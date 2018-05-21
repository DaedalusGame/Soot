package soot.recipe;

import mezz.jei.util.Translator;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import soot.tile.TileEntityStillBase;
import soot.util.FluidUtil;

import java.util.*;
import java.util.stream.Collectors;

public abstract class RecipeStillModifier extends RecipeStill {
    public HashSet<Fluid> validFluids = new HashSet<>();

    public RecipeStillModifier(Collection<Fluid> validFluids, Ingredient catalystInput, int catalystConsumed) {
        super(null, catalystInput, catalystConsumed, null);
        this.validFluids = new HashSet<>(validFluids);
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

    public abstract void modifyOutput(TileEntityStillBase tile, FluidStack output);

    @Override
    public void modifyTooltip(List<String> tooltip) {
        super.modifyTooltip(tooltip);
        tooltip.remove(1);
        tooltip.add(1, TextFormatting.LIGHT_PURPLE+Translator.translateToLocalFormatted("distilling.effect.header"));
    }

    public float getModifierOrDefault(String name, NBTTagCompound compound, FluidStack fluid)
    {
        return FluidUtil.getModifier(compound, fluid!=null?fluid.getFluid():null, name);
    }
}
