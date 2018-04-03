package soot.tile;

import net.minecraft.item.ItemStack;
import soot.recipe.CraftingRegistry;
import soot.recipe.RecipeDawnstoneAnvil;
import teamroots.embers.tileentity.TileEntityDawnstoneAnvil;

public class TileEntityDawnstoneAnvilImproved extends TileEntityDawnstoneAnvil {
    @Override
    public boolean isValid(ItemStack stack1, ItemStack stack2) {
        RecipeDawnstoneAnvil recipe = CraftingRegistry.getDawnstoneAnvilRecipe(stack1,stack2);

        return recipe != null || super.isValid(stack1, stack2);
    }

    @Override
    public ItemStack[] getResult(ItemStack stack1, ItemStack stack2) {
        RecipeDawnstoneAnvil recipe = CraftingRegistry.getDawnstoneAnvilRecipe(stack1,stack2);
        if(recipe != null) {
            inventory.setStackInSlot(1, ItemStack.EMPTY);
            inventory.setStackInSlot(0, ItemStack.EMPTY);
            markDirty();
            return recipe.getResult(stack1, stack2);
        }
        if(CraftingRegistry.isDawnstoneAnvilRecipeBlacklisted(stack1,stack2))
            return new ItemStack[0];
        return super.getResult(stack1, stack2);
    }
}
