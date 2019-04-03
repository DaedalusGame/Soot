package soot.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.NonNullList;
import soot.util.EssenceType;
import soot.util.IItemColored;

import java.awt.*;

public class ItemEssence extends Item implements IItemColored {
    public ItemStack getStack(EssenceType type)
    {
        return getStack(type,1);
    }

    public ItemStack getStack(EssenceType type, int n)
    {
        ItemStack stack = new ItemStack(this, n);
        stack.setTagInfo("type",new NBTTagString(type.getName()));
        return stack;
    }

    public static EssenceType getType(ItemStack stack)
    {
        NBTTagCompound compound = stack.getTagCompound();
        if(compound != null)
            return EssenceType.getType(compound.getString("type"));
        return EssenceType.NULL;
    }

    public ItemEssence() {
        setHasSubtypes(true);
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        EssenceType type = getType(stack);
        return super.getUnlocalizedName(stack) + "." + type.getName();
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (this.isInCreativeTab(tab)) {
            for (EssenceType type : EssenceType.getAllTypes()) {
                items.add(getStack(type));
            }
        }
    }

    @Override
    public int getColorFromItemstack(ItemStack stack, int tintIndex) {
        EssenceType type = getType(stack);
        if(tintIndex == 0)
            return type.getFillColor().getRGB();
        else if(tintIndex == 1)
            return type.getOverlayColor().getRGB();
        else
            return Color.WHITE.getRGB();
    }
}
