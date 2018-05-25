package soot.recipe;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.fluids.FluidStack;

public class RecipeStamperMigrated extends RecipeStamper {
    public RecipeStamperMigrated(Ingredient input, FluidStack inputFluid, ItemStack output, Ingredient stamp) {
        super(input, inputFluid, output, stamp);
    }


}
