package soot.recipe;

import com.google.common.collect.Lists;
import mezz.jei.util.Translator;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import soot.Registry;
import soot.compat.jei.ExtraRecipeInfo;
import soot.recipe.breweffects.EffectAdd;
import soot.recipe.breweffects.EffectLoss;
import soot.recipe.breweffects.EffectMultiply;
import soot.tile.TileEntityStillBase;
import soot.util.FluidUtil;
import teamroots.embers.api.upgrades.UpgradeUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RecipeStillDoubleDistillation extends RecipeStillModifier {
    public static final String TAG_DOUBLE_DISTILL = "can_double_distill";

    public RecipeStillDoubleDistillation(ResourceLocation id, Collection<Fluid> validFluids, Ingredient catalystInput, int catalystConsumed) {
        super(id, validFluids, catalystInput, catalystConsumed);
    }

    @Override
    public boolean matches(TileEntityStillBase tile, FluidStack stack, ItemStack catalyst) {
        boolean canDoubleDistill = tile == null || UpgradeUtil.getOtherParameter(tile, TAG_DOUBLE_DISTILL,false,tile.upgrades);
        return canDoubleDistill && super.matches(tile, stack, catalyst);
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
