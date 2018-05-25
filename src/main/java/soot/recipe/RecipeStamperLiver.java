package soot.recipe;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.FluidStack;
import soot.Registry;
import soot.item.ItemSulfurClump;
import teamroots.embers.RegistryManager;

public class RecipeStamperLiver extends RecipeStamper {
    public RecipeStamperLiver() {
        super(Ingredient.fromItem(Registry.SULFUR_CLUMP), null, new ItemStack(Registry.SULFUR), Ingredient.fromItem(RegistryManager.stamp_flat));
    }

    @Override
    public ItemStack getResult(TileEntity tile, ItemStack item, FluidStack fluid, ItemStack stamp) {
        int amount = 1;
        if(item.getItem() instanceof ItemSulfurClump)
            amount = ((ItemSulfurClump)item.getItem()).getSize(item);
        return new ItemStack(Registry.SULFUR,amount);
    }
}
