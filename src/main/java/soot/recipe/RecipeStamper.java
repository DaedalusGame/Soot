package soot.recipe;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import soot.util.IHasSize;

public class RecipeStamper {
    public Ingredient input;
    public FluidStack inputFluid;
    public ItemStack output;
    public Ingredient stamp;
    boolean exactMatch = false;

    public RecipeStamper(Ingredient input, FluidStack inputFluid, ItemStack output, Ingredient stamp) {
        this.input = input;
        this.inputFluid = inputFluid;
        this.output = output;
        this.stamp = stamp;
    }

    public int getInputConsumed()
    {
        return input instanceof IHasSize ? ((IHasSize) input).getSize() : 1;
    }

    public ItemStack getResult(TileEntity tile, ItemStack item, FluidStack fluid, ItemStack stamp) {
        return output.copy();
    }

    public boolean matches(ItemStack item, FluidStack fluid, ItemStack stamp) {
        return this.input.apply(item) && this.stamp.apply(stamp) && (inputFluid == null || (fluid != null && (exactMatch ? inputFluid.isFluidEqual(fluid) : inputFluid.getFluid() == fluid.getFluid())));
    }
}
