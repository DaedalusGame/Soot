package soot.item;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import teamroots.embers.util.ItemUtil;

import javax.annotation.Nullable;
import java.util.List;

public class ItemEitr extends ItemSword {
    public ItemEitr(ToolMaterial material) {
        super(material);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(" " + TextFormatting.BLUE + I18n.format("soot.tooltip.eitr"));
    }

    @Override
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
        return ItemUtil.matchesOreDict(repair,"dustSulfur") || super.getIsRepairable(toRepair, repair);
    }
}
