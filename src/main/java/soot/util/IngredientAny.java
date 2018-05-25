package soot.util;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.ArrayList;

public class IngredientAny extends Ingredient {
    ItemStack[] matchingStacks = new ItemStack[0];
    boolean matchingStacksCached;

    public IngredientAny() {
        super(0);
    }

    @Override
    public boolean apply(@Nullable ItemStack stack) {
        if(stack == null)
            stack = ItemStack.EMPTY;
        return !stack.isEmpty();
    }

    @Override
    public ItemStack[] getMatchingStacks() {
        if(!matchingStacksCached)
            cacheMatchingStacks();
        return matchingStacks;
    }

    public void cacheMatchingStacks()
    {
        ArrayList<ItemStack> matches = new ArrayList<>();
        for (Item item: ForgeRegistries.ITEMS) {
            if(item instanceof ItemTool || item instanceof ItemSword)
            {
                ItemStack testStack = new ItemStack(item);
                if(apply(testStack))
                    matches.add(testStack);
            }
        }
        matchingStacks = matches.toArray(matchingStacks);
        matchingStacksCached = true;
    }
}
