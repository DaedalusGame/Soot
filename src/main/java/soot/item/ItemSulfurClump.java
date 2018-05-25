package soot.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class ItemSulfurClump extends Item {
    public ItemStack withSize(int size) {
        ItemStack stack = new ItemStack(this);
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setInteger("size",size);
        stack.setTagCompound(nbt);
        return stack;
    }

    public int getSize(ItemStack stack) {
        NBTTagCompound nbt = stack.getTagCompound();
        return nbt != null ? nbt.getInteger("size") : 0;
    }
}
