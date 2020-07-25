package soot.item;

import net.minecraft.item.ItemStack;
import soot.brewing.EssenceStack;

public interface IEssenceContainer {
    EssenceStack getEssence(ItemStack stack);

    int getCapacity(ItemStack stack);

    ItemStack addEssence(ItemStack stack, EssenceStack essence);

    ItemStack removeEssence(ItemStack stack, EssenceStack essence);
}
