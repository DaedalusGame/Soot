package soot.handler;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import soot.Registry;
import soot.util.MiscUtil;

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
        addedLore.addAll(MiscUtil.getLore( compound));
        compound = stack.getTagCompound();
        MiscUtil.setLore(addedLore, compound, true);
    }

    private static boolean isTextStamp(Item item) {
        return item == Registry.STAMP_TEXT;
    }
}
