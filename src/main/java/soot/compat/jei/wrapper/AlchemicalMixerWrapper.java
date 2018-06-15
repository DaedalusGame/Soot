package soot.compat.jei.wrapper;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraftforge.fluids.FluidStack;
import soot.recipe.RecipeAlchemicalMixer;
import teamroots.embers.api.alchemy.AspectList;
import teamroots.embers.util.IHasAspects;

public class AlchemicalMixerWrapper implements IRecipeWrapper, IHasAspects {
    public RecipeAlchemicalMixer recipe;

    public AlchemicalMixerWrapper(RecipeAlchemicalMixer recipe)
    {
        this.recipe = recipe;
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        if (recipe.inputs != null){
            if (recipe.inputs.size() > 0){
                ingredients.setInputs(FluidStack.class, recipe.inputs);
            }
        }
        ingredients.setOutput(FluidStack.class, recipe.output);
    }

    @Override
    public AspectList.AspectRangeList getAspects() {
        return recipe.getAspects();
    }
}
