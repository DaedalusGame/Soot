package soot.recipe;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreIngredient;
import soot.util.AlchemyResult;
import soot.util.AspectList;
import soot.util.IHasAspects;
import teamroots.embers.recipe.AlchemyRecipe;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

public class RecipeAlchemyTablet implements IHasAspects {
    public Ingredient centerInput;
    public ArrayList<Ingredient> inputs;
    public ItemStack output;
    public AspectList.AspectRangeList aspectRange;

    public RecipeAlchemyTablet(ItemStack output, Ingredient center, ArrayList<Ingredient> outside, AspectList.AspectRangeList range)
    {
        this.output = output;
        this.centerInput = center;
        this.inputs = outside;
        this.aspectRange = range;
    }

    public AlchemyResult matchAshes(AspectList list, World world)
    {
        return AlchemyResult.create(list,aspectRange,world);
    }

    public ItemStack getResult(World world, TileEntity tile, AspectList aspects)
    {
        AlchemyResult result = matchAshes(aspects,world);
        if(result.getAccuracy() == 1.0)
            return output.copy();
        else
            return result.createFailure();
    }

    public boolean matches(ItemStack center, List<ItemStack> test) {
        if(!centerInput.apply(center))
            return false;

        ArrayList<Ingredient> ingredients = new ArrayList<>(inputs);
        for (ItemStack stack : test) {
            Optional<Ingredient> found = ingredients.stream().filter(x -> x.apply(stack)).findFirst();
            if(found.isPresent())
                ingredients.remove(found.get());
            else
                return false;
        }

        return true;
    }

    @Override
    public AspectList.AspectRangeList getAspects() {
        return aspectRange;
    }
}
