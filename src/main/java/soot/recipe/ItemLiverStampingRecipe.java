package soot.recipe;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.FluidStack;
import soot.Registry;
import soot.item.ItemSulfurClump;
import teamroots.embers.RegistryManager;
import teamroots.embers.recipe.ItemStampingRecipe;

public class ItemLiverStampingRecipe extends ItemStampingRecipe {
    public ItemLiverStampingRecipe() {
        super(Ingredient.fromItem(Registry.SULFUR_CLUMP), null, Ingredient.fromItem(RegistryManager.stamp_flat), new ItemStack(Registry.SULFUR));
    }

    @Override
    public ItemStack getResult(TileEntity tile, ItemStack item, FluidStack fluid, ItemStack stamp) {
        int amount = 1;
        if(item.getItem() instanceof ItemSulfurClump)
            amount = ((ItemSulfurClump)item.getItem()).getSize(item);
        return new ItemStack(Registry.SULFUR,amount);
    }
}
