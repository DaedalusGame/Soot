package soot.recipe;

import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import teamroots.embers.api.alchemy.AlchemyResult;
import teamroots.embers.api.alchemy.AspectList;
import teamroots.embers.api.alchemy.AspectList.AspectRangeList;
import teamroots.embers.recipe.FluidMixingRecipe;
import teamroots.embers.util.IHasAspects;

public class RecipeAlchemicalMixer extends FluidMixingRecipe implements IHasAspects {
    public AspectRangeList aspectRange;

    public RecipeAlchemicalMixer(FluidStack[] inputs, FluidStack output, AspectRangeList aspectRange) {
        super(inputs, output);
        this.aspectRange = aspectRange;
    }

    public AlchemyResult matchAshes(AspectList list, World world)
    {
        return AlchemyResult.create(list,aspectRange,world);
    }

    @Override
    public AspectRangeList getAspects() {
        return aspectRange;
    }
}
