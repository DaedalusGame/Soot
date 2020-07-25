package soot.recipe;

import com.google.common.collect.Lists;
import mezz.jei.util.Translator;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import soot.Registry;
import soot.compat.jei.ExtraRecipeInfo;
import soot.tile.TileEntityStillBase;
import soot.util.FluidUtil;
import teamroots.embers.api.upgrades.UpgradeUtil;

import java.util.ArrayList;
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
        //tooltip.add(tooltip.size() - 1, TextFormatting.BOLD + Translator.translateToLocalFormatted("distilling.effect.double_distillation"));
        addModifierLinear(tooltip, "concentration", 10, true);
        addModifierPercent(tooltip, "concentration", 80, true);
        addModifierPercent(tooltip, "volume", 10, false);
        addModifierLoss(tooltip, 33);
    }

    @Override
    public List<ExtraRecipeInfo> getExtraInfo() {
        List<ExtraRecipeInfo> extraInfo = super.getExtraInfo();
        extraInfo.add(0, new ExtraRecipeInfo(Lists.newArrayList(new ItemStack(Registry.DISTILLATION_PIPE))) {
            @Override
            public void modifyTooltip(List<String> strings) {
                strings.clear();
                strings.add(Translator.translateToLocalFormatted("distilling.effect.double_distillation"));
                strings.add(Translator.translateToLocalFormatted("distilling.effect.double_distillation.desc"));
            }
        });
        return extraInfo;
    }
}
