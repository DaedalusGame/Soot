package soot.recipe;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;

public class CatalystInfo {
    public Ingredient matcher;
    public int amt;

    public CatalystInfo(Ingredient matcher, int amt) {
        this.matcher = matcher;
        this.amt = amt;
    }

    public boolean matches(ItemStack stack)
    {
        return matcher.apply(stack);
    }

    public int getAmount(ItemStack stack)
    {
        return amt;
    }

    @Override
    public String toString() {
        return String.format("%s (%s)",matcher,amt);
    }
}
