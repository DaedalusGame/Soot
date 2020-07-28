package soot.recipe;

import mezz.jei.util.Translator;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import soot.recipe.breweffects.EffectAdd;
import soot.recipe.breweffects.EffectMax;
import soot.tile.TileEntityStillBase;
import soot.util.FluidUtil;

import java.util.Collection;
import java.util.List;

public class RecipeStillModifierFood extends RecipeStillModifier {
    public RecipeStillModifierFood(ResourceLocation id, Collection<Fluid> validFluids, Ingredient catalystInput, int catalystConsumed, int hunger, float saturation) {
        super(id, validFluids, catalystInput, catalystConsumed);
        addEffect(new EffectAdd("hunger", hunger, false));
        addEffect(new EffectMax("saturation", saturation, true));
    }
}
