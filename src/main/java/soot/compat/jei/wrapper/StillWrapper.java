package soot.compat.jei.wrapper;

import com.google.common.collect.Lists;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fluids.FluidStack;
import soot.compat.jei.ExtraRecipeInfo;
import soot.recipe.RecipeStill;
import soot.recipe.RecipeStillModifier;

import java.util.ArrayList;
import java.util.List;

public class StillWrapper implements IRecipeWrapper {
    RecipeStill recipe;

    public StillWrapper(RecipeStill recipe) {
        this.recipe = recipe;
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        ArrayList<List<FluidStack>> inputFluids = new ArrayList<>();
        inputFluids.add(recipe.getInputs());
        ArrayList<List<FluidStack>> outputFluids = new ArrayList<>();
        outputFluids.add(recipe.getOutputs());
        ingredients.setInputLists(FluidStack.class,inputFluids);
        ingredients.setOutputLists(FluidStack.class,outputFluids);
        ingredients.setInputs(ItemStack.class, recipe.getCatalysts());
        ingredients.setOutputs(ItemStack.class, recipe.getEssenceItems());
    }

    @Override
    public List<String> getTooltipStrings(int mouseX, int mouseY) {
        if(mouseX >= 8 && mouseY >= 47 && mouseX < 8 + 16 && mouseY < 47 + 16)
            return Lists.newArrayList(recipe.id.toString());
        return IRecipeWrapper.super.getTooltipStrings(mouseX,mouseY);
    }

    public void modifyTooltip(List<String> tooltip) {
        recipe.modifyTooltip(tooltip);
    }

    public List<ExtraRecipeInfo> getExtraInfo() {
        return recipe.getExtraInfo();
    }
}
