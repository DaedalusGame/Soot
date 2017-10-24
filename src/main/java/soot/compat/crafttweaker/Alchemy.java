package soot.compat.crafttweaker;

import com.blamejared.mtlib.helpers.InputHelper;
import com.blamejared.mtlib.utils.BaseListAddition;
import com.blamejared.mtlib.utils.BaseListRemoval;
import com.google.common.collect.Lists;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import net.minecraft.item.ItemStack;
import stanhebben.zenscript.annotations.NotNull;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;
import stanhebben.zenscript.value.IntRange;
import teamroots.embers.recipe.AlchemyRecipe;
import teamroots.embers.recipe.RecipeRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@ZenRegister
@ZenClass(Alchemy.clazz)
public class Alchemy {
    public static final String clazz = "mods.embers.Alchemy";

    @ZenMethod
    public static void add(IItemStack output,@NotNull IItemStack[] input, IntRange iron, IntRange copper, IntRange lead, IntRange silver, IntRange dawnstone) {
        AlchemyRecipe recipe = new AlchemyRecipe(iron.getFrom(),iron.getTo(),
                dawnstone.getFrom(),dawnstone.getTo(),
                copper.getFrom(),copper.getTo(),
                silver.getFrom(),silver.getTo(),
                lead.getFrom(),lead.getTo(),
                InputHelper.toStack(input[0]),InputHelper.toStack(input[1]),InputHelper.toStack(input[2]),InputHelper.toStack(input[3]),InputHelper.toStack(input[4]),
                InputHelper.toStack(output));
        CraftTweakerAPI.apply(new Add(recipe));
    }

    @ZenMethod
    public static void remove(IItemStack output)
    {
        CraftTweakerAPI.apply(new Remove(InputHelper.toStack(output)));
    }

    private static List<AlchemyRecipe> getRecipesByOutput(ItemStack stack)
    {
       return RecipeRegistry.alchemyRecipes.stream().filter(recipe -> ItemStack.areItemsEqual(recipe.result, stack)).collect(Collectors.toCollection(ArrayList::new));
    }

    public static class Add extends BaseListAddition<AlchemyRecipe>
    {
        public Add(AlchemyRecipe recipe) {
            super("Alchemy", RecipeRegistry.alchemyRecipes, Lists.newArrayList(recipe));
        }

        @Override
        protected String getRecipeInfo(AlchemyRecipe recipe) {
            return recipe.toString();
        }
    }

    public static class Remove extends BaseListRemoval<AlchemyRecipe>
    {
        protected Remove(ItemStack input) {
            super("Alchemy", RecipeRegistry.alchemyRecipes, getRecipesByOutput(input));
        }

        @Override
        protected String getRecipeInfo(AlchemyRecipe recipe) {
            return recipe.toString();
        }
    }
}
