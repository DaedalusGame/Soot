package soot.util;

import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;

import javax.annotation.Nullable;

public class IngredientSized extends Ingredient implements IHasSize {
    Ingredient internal;
    int size;

    public IngredientSized(Ingredient internal, int size) {
        super(0);
        this.internal = internal;
        this.size = size;
    }

    @Override
    public ItemStack[] getMatchingStacks() {
        return internal.getMatchingStacks();
    }

    @Override
    public boolean apply(@Nullable ItemStack stack) {
        return internal.apply(stack) && stack != null && stack.getCount() >= size;
    }

    @Override
    public IntList getValidItemStacksPacked() {
        return internal.getValidItemStacksPacked();
    }

    public int getSize() {
        return size;
    }
}
