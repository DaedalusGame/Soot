package soot.compat.jei;

import net.minecraft.item.ItemStack;

import java.util.List;

public abstract class ExtraRecipeInfo {
    List<ItemStack> stacks;

    public ExtraRecipeInfo(List<ItemStack> stacks) {
        this.stacks = stacks;
    }

    public List<ItemStack> getStacks() {
        return stacks;
    }

    public abstract void modifyTooltip(List<String> strings);
}
