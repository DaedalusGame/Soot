package soot.util;

import crafttweaker.api.item.IIngredient;
import net.minecraft.item.crafting.Ingredient;
import stanhebben.zenscript.value.IntRange;

public class CTUtil {
    public static Ingredient toIngredient(IIngredient ingredient)
    {
        return new IngredientCraftTweaker(ingredient);
    }

    //Supports basic list (iron,copper,lead,silver,dawnstone)
    public static AspectList.AspectRangeList toAspectRange(IntRange iron, IntRange copper, IntRange lead, IntRange silver, IntRange dawnstone)
    {
        AspectList min = AspectList.createStandard(iron.getFrom(),dawnstone.getFrom(),copper.getFrom(),silver.getFrom(),lead.getFrom());
        AspectList max = AspectList.createStandard(iron.getTo(),dawnstone.getTo(),copper.getTo(),silver.getTo(),lead.getTo());
        return new AspectList.AspectRangeList(min,max);
    }
}
