package soot.compat.jei.wrapper;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.item.ItemStack;
import soot.recipe.RecipeDawnstoneAnvil;
import java.util.Arrays;

public class DawnstoneAnvilWrapper implements IRecipeWrapper {
    RecipeDawnstoneAnvil recipe;

    public DawnstoneAnvilWrapper(RecipeDawnstoneAnvil recipe)
    {
        this.recipe = recipe;
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        ingredients.setInputLists(ItemStack.class, Arrays.asList(Arrays.asList(recipe.top.getMatchingStacks()), Arrays.asList(recipe.bottom.getMatchingStacks())));
        ingredients.setOutputs(ItemStack.class, Arrays.asList(recipe.result));
    }
}
