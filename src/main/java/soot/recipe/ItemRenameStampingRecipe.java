package soot.recipe;

import com.google.common.collect.Lists;
import mezz.jei.api.recipe.IFocus;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.FluidStack;
import soot.Registry;
import soot.util.IngredientAny;
import soot.util.MiscUtil;
import teamroots.embers.recipe.IFocusRecipe;
import teamroots.embers.recipe.IWrappableRecipe;
import teamroots.embers.recipe.ItemStampingRecipe;

import java.util.List;
import java.util.stream.Collectors;

public class ItemRenameStampingRecipe extends ItemStampingRecipe implements IFocusRecipe, IWrappableRecipe {
    public ItemRenameStampingRecipe() {
        super(new IngredientAny(), null, Ingredient.fromItem(Registry.STAMP_TEXT), ItemStack.EMPTY);
    }

    private static ItemStack apply(ItemStack stack) {
        ItemStack rstack = stack.copy();
        rstack.setStackDisplayName("Renamed Item");
        return rstack;
    }

    @Override
    public List<ItemStack> getOutputs() {
        return getInputs().stream().map(ItemRenameStampingRecipe::apply).collect(Collectors.toList());
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

    @Override
    public List<ItemStack> getOutputs(IFocus<ItemStack> focus, int slot) {
        if(slot == 2)
            return Lists.newArrayList(apply(focus.getValue()));
        return Lists.newArrayList();
    }

    @Override
    public List<ItemStack> getInputs(IFocus<ItemStack> focus, int slot) {
        if(slot == 0)
            return Lists.newArrayList(focus.getValue());
        if(slot == 1)
            return getStamps();
        return Lists.newArrayList();
    }

    @Override
    public List<IWrappableRecipe> getWrappers() {
        return Lists.newArrayList();
    }
}
