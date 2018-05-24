package soot.handler;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import soot.Registry;

import java.util.ArrayList;

public class AnvilHandler {
    @SubscribeEvent
    public static void onAnvilRecipe(AnvilUpdateEvent event) {
        ItemStack first = event.getLeft();
        ItemStack second = event.getRight();
        if(isTextStamp(first.getItem()) && isTextStamp(second.getItem())) {
            ItemStack output = first.copy();
            if(!second.isEmpty() && first.hasDisplayName() && second.hasDisplayName()) {
                addLore(output, second);
                event.setOutput(output);
                event.setCost(1);
            }
        }
    }

    private static void addLore(ItemStack stack, ItemStack addStack) {
        ArrayList<String> addedLore = new ArrayList<>();
        NBTTagCompound compound = addStack.getTagCompound();
        if(addStack.hasDisplayName())
            addedLore.add(addStack.getDisplayName());
        if(compound != null && compound.hasKey("display",10)) {
            NBTTagCompound display = compound.getCompoundTag("display");
            if(display.hasKey("Lore",9)) {
                NBTTagList lore = display.getTagList("Lore",8);
                if(!lore.hasNoTags())
                    for (int i = 0; i < lore.tagCount(); ++i)
                        addedLore.add(lore.getStringTagAt(i));
            }
        }
        compound = stack.getTagCompound();
        if(compound != null && compound.hasKey("display",10)) {
            NBTTagCompound display = compound.getCompoundTag("display");
            NBTTagList lore;
            if(display.hasKey("Lore",9))
                lore = display.getTagList("Lore", 8);
            else {
                lore = new NBTTagList();
                display.setTag("Lore",lore);
            }
            for(String loreString : addedLore)
                lore.appendTag(new NBTTagString(loreString));
        }
    }

    private static boolean isTextStamp(Item item) {
        return item == Registry.STAMP_TEXT;
    }
}
