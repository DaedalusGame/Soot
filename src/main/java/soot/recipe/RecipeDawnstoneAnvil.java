package soot.recipe;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;

public class RecipeDawnstoneAnvil {
    public Ingredient bottom;
    public Ingredient top;
    public ItemStack result;

    public RecipeDawnstoneAnvil(ItemStack result, Ingredient bottom, Ingredient top)
    {
        this.result = result;
        this.bottom = bottom;
        this.top = top;
    }

    public boolean matches(ItemStack input1, ItemStack input2)
    {
        return bottom.apply(input1) && (top == null || top.apply(input2));
    }

    public ItemStack getResult(ItemStack input1, ItemStack input2) //For when you need your own handling
    {
        return result;
    }

    public ItemStack getJEIResult()
    {
        return result;
    }
}
