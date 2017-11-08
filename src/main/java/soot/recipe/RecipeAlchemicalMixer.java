package soot.recipe;

import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import soot.util.AlchemyResult;
import soot.util.AspectList;
import soot.util.AspectList.AspectRangeList;
import teamroots.embers.recipe.FluidMixingRecipe;

public class RecipeAlchemicalMixer extends FluidMixingRecipe {
    public AspectRangeList aspectRange;

    public RecipeAlchemicalMixer(FluidStack[] inputs, FluidStack output, AspectRangeList aspectRange) {
        super(inputs, output);
        this.aspectRange = aspectRange;
    }

    public AlchemyResult matchAshes(AspectList list, World world)
    {
        return AlchemyResult.create(list,aspectRange,world);
    }
}
