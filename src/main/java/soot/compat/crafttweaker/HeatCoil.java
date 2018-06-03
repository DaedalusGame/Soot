package soot.compat.crafttweaker;

import com.blamejared.mtlib.helpers.InputHelper;
import com.blamejared.mtlib.utils.BaseListAddition;
import com.google.common.collect.Lists;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import soot.recipe.CraftingRegistry;
import soot.recipe.RecipeHeatCoil;
import soot.util.CTUtil;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass(HeatCoil.clazz)
public class HeatCoil {
    public static final String clazz = "mods.embers.HeatCoil";

    @ZenMethod
    public static void add(IItemStack output, IIngredient input) {
        RecipeHeatCoil recipe = new RecipeHeatCoil(InputHelper.toStack(output), CTUtil.toIngredient(input));
        CraftTweakerAPI.apply(new Add(recipe));
    }

    public static class Add extends BaseListAddition<RecipeHeatCoil>
    {
        public Add(RecipeHeatCoil recipe) {
            super("HeatCoil", CraftingRegistry.heatCoilRecipes, Lists.newArrayList(recipe));
        }

        @Override
        protected String getRecipeInfo(RecipeHeatCoil recipe) {
            return recipe.toString();
        }
    }
}
