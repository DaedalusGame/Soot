package soot.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.translation.I18n;
import soot.brewing.EssenceStack;
import soot.brewing.EssenceType;
import soot.util.IItemColored;

import java.awt.*;

public class ItemEssence extends Item implements IItemColored, IEssenceContainer {
    public static final int CAPACITY = 1000;

    public ItemStack getStack(EssenceType type)
    {
        return getStack(type,1);
    }

    public ItemStack getStack(EssenceType type, int n) {
        ItemStack stack = new ItemStack(this, n);
        stack.setTagInfo("type",new NBTTagString(type.getName()));
        return stack;
    }

    public static EssenceType getType(ItemStack stack) {
        NBTTagCompound compound = stack.getTagCompound();
        if(compound != null)
            return EssenceType.getType(compound.getString("type"));
        return EssenceType.NULL;
    }

    @Override
    public EssenceStack getEssence(ItemStack stack) {
        return new EssenceStack(getType(stack), CAPACITY);
    }

    @Override
    public int getCapacity(ItemStack stack) {
        return CAPACITY;
    }

    @Override
    public ItemStack addEssence(ItemStack stack, EssenceStack essence) {
        return getStack(essence.getEssence());
    }

    @Override
    public ItemStack removeEssence(ItemStack stack, EssenceStack essence) {
        return getStack(EssenceType.NULL);
    }

    public ItemEssence() {
        setHasSubtypes(true);
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        EssenceType type = getType(stack);
        String essenceName = I18n.translateToLocal("distilling.essence."+type.getName()+".name");
        return I18n.translateToLocalFormatted(this.getUnlocalizedNameInefficiently(stack) + ".name", essenceName).trim();
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
