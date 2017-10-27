package soot.compat.crafttweaker;

import com.blamejared.mtlib.helpers.InputHelper;
import com.blamejared.mtlib.utils.BaseListAddition;
import com.google.common.collect.Lists;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import soot.recipe.RecipeDawnstoneAnvil;
import soot.recipe.RecipeRegistry;
import soot.util.CTUtil;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenRegister
@ZenClass(DawnstoneAnvil.clazz)
public class DawnstoneAnvil {
    public static final String clazz = "mods.embers.DawnstoneAnvil";

    @ZenMethod
    public static void add(IItemStack[] output, IIngredient inputBottom, IIngredient inputTop) {
        RecipeDawnstoneAnvil recipe = new RecipeDawnstoneAnvil(InputHelper.toStacks(output), CTUtil.toIngredient(inputBottom), CTUtil.toIngredient(inputTop));
        CraftTweakerAPI.apply(new Add(recipe));
    }

    public static class Add extends BaseListAddition<RecipeDawnstoneAnvil>
    {
        public Add(RecipeDawnstoneAnvil recipe) {
            super("Melter", RecipeRegistry.dawnstoneAnvilRecipes, Lists.newArrayList(recipe));
        }

        @Override
        protected String getRecipeInfo(RecipeDawnstoneAnvil recipe) {
            return recipe.toString();
        }
    }
}
