package soot.recipe;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import soot.Registry;
import soot.item.ItemSulfurClump;

public class CatalystInfoSulfur extends CatalystInfo {
    public CatalystInfoSulfur() {
        super(Ingredient.fromItem(Registry.SULFUR_CLUMP), 100);
    }

    @Override
    public int getAmount(ItemStack stack) {
        Item item = stack.getItem();
        if(item instanceof ItemSulfurClump) {
            return ((ItemSulfurClump) item).getSize(stack) * amt;
        }
        return 0;
    }
}
