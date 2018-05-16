package soot.compat.jei.wrapper;

import com.google.common.collect.Lists;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import soot.recipe.RecipeStamper;
import teamroots.embers.compat.jei.StampingRecipeWrapper;
import teamroots.embers.recipe.ItemStampingRecipe;

import java.util.ArrayList;

public class StamperWrapper extends StampingRecipeWrapper {
    RecipeStamper recipe;

    public StamperWrapper(RecipeStamper recipe) {
        super((ItemStampingRecipe)null);
        this.recipe = recipe;
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        ArrayList<ItemStack> inputStacks = Lists.newArrayList(recipe.input.getMatchingStacks());
        ArrayList<ItemStack> stampStacks = Lists.newArrayList(recipe.stamp.getMatchingStacks());
        ingredients.setInputLists(ItemStack.class, Lists.newArrayList(inputStacks,stampStacks));
        ingredients.setInput(FluidStack.class, recipe.inputFluid);
        ingredients.setOutput(ItemStack.class, recipe.output);
    }
}
