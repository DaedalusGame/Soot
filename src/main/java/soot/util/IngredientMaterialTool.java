package soot.util;

import net.minecraft.item.*;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.ArrayList;

public class IngredientMaterialTool extends Ingredient {
    String toolclass;
    String materialname;
    ItemStack[] matchingStacks = new ItemStack[0];
    boolean matchingStacksCached;

    public IngredientMaterialTool(String toolclass, String materialname) {
        super(0);
        this.toolclass = toolclass;
        this.materialname = materialname;
    }

    @Override
    public boolean apply(@Nullable ItemStack stack) {
        if(stack == null)
            stack = ItemStack.EMPTY;
        Item item = stack.getItem();

        if(item instanceof ItemTool)
            return item.getToolClasses(stack).contains(toolclass) && ((ItemTool) item).getToolMaterialName().toLowerCase().contains(materialname);
        if(item instanceof ItemSword && toolclass.equals("sword"))
            return ((ItemSword) item).getToolMaterialName().toLowerCase().contains(materialname);

        return false;
    }

    @Override
    public ItemStack[] getMatchingStacks() {
        if(!matchingStacksCached)
            cacheMatchingStacks();
        return matchingStacks;
    }

    public void cacheMatchingStacks()
    {
        ArrayList<ItemStack> matches = new ArrayList<>();
        for (Item item: ForgeRegistries.ITEMS) {
            if(item instanceof ItemTool || item instanceof ItemSword)
            {
                ItemStack testStack = new ItemStack(item);
                if(apply(testStack))
                    matches.add(testStack);
            }
        }
        matchingStacks = matches.toArray(matchingStacks);
        matchingStacksCached = true;
    }
}
