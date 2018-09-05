package soot.recipe;

import mezz.jei.util.Translator;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import soot.tile.TileEntityStillBase;
import soot.util.FluidUtil;
import teamroots.embers.api.upgrades.UpgradeUtil;

import java.util.Collection;
import java.util.List;

public class RecipeStillDoubleDistillation extends RecipeStillModifier {

    public static final String TAG_DOUBLE_DISTILL = "can_double_distill";

    public RecipeStillDoubleDistillation(Collection<Fluid> validFluids, Ingredient catalystInput, int catalystConsumed) {
        super(validFluids, catalystInput, catalystConsumed);
    }

    @Override
    public boolean matches(TileEntityStillBase tile, FluidStack stack, ItemStack catalyst) {
        boolean canDoubleDistill = tile == null || UpgradeUtil.getOtherParameter(tile, TAG_DOUBLE_DISTILL,false,tile.upgrades);
        return canDoubleDistill && super.matches(tile, stack, catalyst);
    }

    @Override
    public int getInputConsumed() {
        return 3;
    }
    @Override
    public void modifyOutput(TileEntityStillBase tile, FluidStack output) {
        NBTTagCompound compound = FluidUtil.createModifiers(output);
        output.amount = 2;
        float concentration = getModifierOrDefault("concentration", compound, output);
        float volume = getModifierOrDefault("volume", compound, output);
        if (concentration < 120)
            compound.setFloat("concentration", Math.min((concentration + 10) * 1.8f, 120));
        compound.setFloat("volume", volume * 1.1f);
    }

    @Override
    public void modifyTooltip(List<String> tooltip) {
        super.modifyTooltip(tooltip);
        tooltip.add(tooltip.size() - 1, TextFormatting.BOLD + Translator.translateToLocalFormatted("distilling.effect.double_distillation"));
        tooltip.add(tooltip.size() - 1, TextFormatting.BLUE + Translator.translateToLocalFormatted("distilling.effect.add", Translator.translateToLocal("distilling.modifier.concentration.name"), 10));
        tooltip.add(tooltip.size() - 1, TextFormatting.BLUE + Translator.translateToLocalFormatted("distilling.effect.add_percent", Translator.translateToLocal("distilling.modifier.concentration.name"), 80));
        tooltip.add(tooltip.size() - 1, TextFormatting.RED + Translator.translateToLocalFormatted("distilling.effect.add_percent", Translator.translateToLocal("distilling.modifier.volume.name"), 10));
        tooltip.add(tooltip.size() - 1, TextFormatting.RED + Translator.translateToLocalFormatted("distilling.effect.loss", 33));
    }
}
