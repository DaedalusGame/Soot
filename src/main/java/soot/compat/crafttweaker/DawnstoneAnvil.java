package soot.compat.crafttweaker;

import com.blamejared.mtlib.helpers.InputHelper;
import com.blamejared.mtlib.utils.BaseListAddition;
import com.google.common.collect.Lists;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import net.minecraft.item.ItemStack;
import soot.recipe.RecipeDawnstoneAnvil;
import soot.recipe.CraftingRegistry;
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

    @ZenMethod
    public static void remove(IIngredient inputBottom, IIngredient inputTop) {
        RecipeDawnstoneAnvil recipe = new RecipeDawnstoneAnvil(new ItemStack[0], CTUtil.toIngredient(inputBottom), CTUtil.toIngredient(inputTop));
        CraftTweakerAPI.apply(new Remove(recipe));
    }

    public static class Add extends BaseListAddition<RecipeDawnstoneAnvil>
    {
        public Add(RecipeDawnstoneAnvil recipe) {
            super("DawnstoneAnvil", CraftingRegistry.dawnstoneAnvilRecipes, Lists.newArrayList(recipe));
        }

        @Override
        protected String getRecipeInfo(RecipeDawnstoneAnvil recipe) {
            return recipe.toString();
        }
    }

    public static class Remove extends BaseListAddition<RecipeDawnstoneAnvil>
    {
        public Remove(RecipeDawnstoneAnvil recipe) {
            super("DawnstoneAnvil", CraftingRegistry.dawnstoneAnvilBlacklist, Lists.newArrayList(recipe));
        }

        @Override
        protected String getRecipeInfo(RecipeDawnstoneAnvil recipe) {
            return recipe.toString();
        }
    }
}
