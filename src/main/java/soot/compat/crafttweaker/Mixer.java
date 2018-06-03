package soot.compat.crafttweaker;

import com.blamejared.mtlib.helpers.InputHelper;
import com.blamejared.mtlib.utils.BaseListAddition;
import com.blamejared.mtlib.utils.BaseListRemoval;
import com.google.common.collect.Lists;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.liquid.ILiquidStack;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import stanhebben.zenscript.annotations.NotNull;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;
import teamroots.embers.recipe.FluidMixingRecipe;
import teamroots.embers.recipe.RecipeRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@ZenClass(Mixer.clazz)
public class Mixer {
    public static final String clazz = "mods.embers.Mixer";

    @ZenMethod
    public static void add(ILiquidStack output, @NotNull ILiquidStack[] inputs) {
        FluidMixingRecipe recipe = new FluidMixingRecipe(InputHelper.toFluids(inputs),InputHelper.toFluid(output));
        CraftTweakerAPI.apply(new Add(recipe));
    }

    @ZenMethod
    public static void remove(ILiquidStack output)
    {
        CraftTweakerAPI.apply(new Remove(InputHelper.toFluid(output)));
    }

    private static List<FluidMixingRecipe> getRecipesByOutput(FluidStack stack)
    {
        return RecipeRegistry.mixingRecipes.stream().filter(recipe -> recipe.output.isFluidStackIdentical(stack)).collect(Collectors.toCollection(ArrayList::new));
    }

    public static class Add extends BaseListAddition<FluidMixingRecipe>
    {
        public Add(FluidMixingRecipe recipe) {
            super("Mixer", RecipeRegistry.mixingRecipes, Lists.newArrayList(recipe));
        }

        @Override
        protected String getRecipeInfo(FluidMixingRecipe recipe) {
            return recipe.toString();
        }
    }

    public static class Remove extends BaseListRemoval<FluidMixingRecipe>
    {
        protected Remove(FluidStack input) {
            super("Mixer", RecipeRegistry.mixingRecipes, getRecipesByOutput(input));
        }

        @Override
        protected String getRecipeInfo(FluidMixingRecipe recipe) {
            return recipe.toString();
        }
    }
}
