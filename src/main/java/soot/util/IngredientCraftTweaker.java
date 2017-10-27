package soot.util;

import com.blamejared.mtlib.helpers.InputHelper;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;

import javax.annotation.Nullable;
import java.util.List;

public class IngredientCraftTweaker extends Ingredient {
    IIngredient predicate;

    public IngredientCraftTweaker(IIngredient ingredient)
    {
        predicate = ingredient;
    }

    @Override
    public ItemStack[] getMatchingStacks() {
        List<IItemStack> stacks = predicate.getItems();
        return InputHelper.toStacks(stacks.toArray(new IItemStack[stacks.size()]));
    }

    @Override
    public boolean apply(@Nullable ItemStack stack) {
        return predicate.matches(InputHelper.toIItemStack(stack));
    }
}
