package soot.recipe;

import com.google.common.collect.Lists;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.fluids.FluidStack;
import soot.compat.jei.ExtraRecipeInfo;
import soot.tile.TileEntityStillBase;

import java.util.List;

public class RecipeStill {
    public FluidStack input;
    public Ingredient catalystInput;
    public int catalystConsumed;
    public FluidStack output;
    boolean exactMatch = false;

    public RecipeStill(FluidStack input, Ingredient catalystInput, int catalystConsumed, FluidStack output) {
        this.input = input;
        this.catalystInput = catalystInput;
        this.output = output;
        this.catalystConsumed = catalystConsumed;
    }

    public RecipeStill setExact()
    {
        exactMatch = true;
        return this;
    }

    public List<FluidStack> getInputs()
    {
        return Lists.newArrayList(input);
    }

    public List<FluidStack> getOutputs()
    {
        return Lists.newArrayList(output);
    }

    public void modifyTooltip(List<String> tooltip) {
        //NOOP
    }

    public int getInputConsumed()
    {
        return input != null ? input.amount : 0;
    }

    public boolean matches(TileEntityStillBase tile, FluidStack stack, ItemStack catalyst)
    {
        return catalystInput.apply(catalyst) && (input == null || (stack != null && (exactMatch ? input.isFluidEqual(stack) : input.getFluid() == stack.getFluid()) && stack.amount >= getInputConsumed()));
    }

    public FluidStack getOutput(TileEntityStillBase tile, FluidStack input)
    {
        return output.copy();
    }

    public List<ExtraRecipeInfo> getExtraInfo() {
        return Lists.newArrayList();
    }
}
