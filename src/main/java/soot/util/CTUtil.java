package soot.util;

import crafttweaker.api.item.IIngredient;
import net.minecraft.item.crafting.Ingredient;

public class CTUtil {
    public static Ingredient toIngredient(IIngredient ingredient)
    {
        return new IngredientCraftTweaker(ingredient);
    }
}
