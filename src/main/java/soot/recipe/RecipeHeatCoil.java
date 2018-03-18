package soot.recipe;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class RecipeHeatCoil {
    Ingredient input = Ingredient.EMPTY;
    ItemStack output = ItemStack.EMPTY;

    public RecipeHeatCoil() {
    }

    public RecipeHeatCoil(ItemStack output, Ingredient input) {
        this.input = input;
        this.output = output;
    }

    public boolean matches(ItemStack stack)
    {
        return input.apply(stack);
    }

    public ItemStack getResult(World world, TileEntity tile, ItemStack stack)
    {
        return output.copy();
    }
}
