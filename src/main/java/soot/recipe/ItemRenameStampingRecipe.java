package soot.recipe;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.FluidStack;
import soot.Registry;
import soot.util.IngredientAny;
import soot.util.MiscUtil;
import teamroots.embers.recipe.ItemStampingRecipe;

import java.util.List;
import java.util.stream.Collectors;

public class ItemRenameStampingRecipe extends ItemStampingRecipe {
    public ItemRenameStampingRecipe() {
        super(new IngredientAny(), null, Ingredient.fromItem(Registry.STAMP_TEXT), ItemStack.EMPTY);
    }

    @Override
    public List<ItemStack> getOutputs() {
        return getInputs().stream().map(stack -> {
            ItemStack rstack = stack.copy();
            rstack.setStackDisplayName("Renamed Item");
            return rstack;
        }).collect(Collectors.toList());
    }

    @Override
    public ItemStack getResult(TileEntity tile, ItemStack item, FluidStack fluid, ItemStack stamp) {
        ItemStack output = item.copy();
        output.setCount(1);
        List<String> lore = MiscUtil.getLore(stamp.getTagCompound());
        if(stamp.hasDisplayName())
            output.setStackDisplayName(stamp.getDisplayName());
        else
            output.clearCustomName();
        MiscUtil.setLore(lore,output.getTagCompound(),false);
        return output;
    }
}
