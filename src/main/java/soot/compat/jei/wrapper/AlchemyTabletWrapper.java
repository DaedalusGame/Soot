package soot.compat.jei.wrapper;

import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.api.recipe.IStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import soot.recipe.RecipeAlchemyTablet;
import soot.util.AspectList;
import soot.util.IHasAspects;

import java.util.ArrayList;
import java.util.List;

public class AlchemyTabletWrapper implements IRecipeWrapper, IHasAspects {
    public RecipeAlchemyTablet recipe;
    private IJeiHelpers jeiHelpers;

    public AlchemyTabletWrapper(IJeiHelpers jeiHelpers, RecipeAlchemyTablet recipe)
    {
        this.recipe = recipe;
        this.jeiHelpers = jeiHelpers;
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        ArrayList<Ingredient> inputs = new ArrayList<>();
        inputs.add(recipe.centerInput);
        inputs.addAll(recipe.inputs);

        IStackHelper stackHelper = jeiHelpers.getStackHelper();
        if (inputs.size() > 0){
            List<List<ItemStack>> inputLists = stackHelper.expandRecipeItemStackInputs(inputs);
            ingredients.setInputLists(ItemStack.class, inputLists);
        }
        ingredients.setOutput(ItemStack.class, recipe.output);
    }

    @Override
    public AspectList.AspectRangeList getAspects() {
        return recipe.getAspects();
    }
}
