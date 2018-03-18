package soot.recipe;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class RecipeHeatCoil {
    Ingredient input;
    ItemStack output;

    public boolean matches(ItemStack stack)
    {
        return input.apply(stack);
    }

    public ItemStack getResult(World world, TileEntity tile, ItemStack stack)
    {
        return output.copy();
    }
}
