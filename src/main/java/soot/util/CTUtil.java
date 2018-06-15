package soot.util;

import com.blamejared.mtlib.helpers.InputHelper;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.oredict.IngredientOreDict;
import net.minecraft.item.crafting.Ingredient;
import stanhebben.zenscript.value.IntRange;
import teamroots.embers.api.alchemy.AspectList;
import teamroots.embers.compat.crafttweaker.IngredientCraftTweaker;

public class CTUtil {
    public static Ingredient toIngredient(IIngredient ingredient) {
        if(ingredient == null)
            return Ingredient.EMPTY;
        if(ingredient instanceof IItemStack)
            return Ingredient.fromStacks(InputHelper.toStack((IItemStack) ingredient));
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
