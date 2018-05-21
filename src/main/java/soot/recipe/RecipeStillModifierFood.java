package soot.recipe;

import mezz.jei.util.Translator;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import soot.tile.TileEntityStillBase;
import soot.util.FluidUtil;

import java.util.Collection;
import java.util.List;

public class RecipeStillModifierFood extends RecipeStillModifier {

    private int hungerAdded;
    private float saturationModifier;

    public RecipeStillModifierFood(Collection<Fluid> validFluids, Ingredient catalystInput, int catalystConsumed, int hunger, float saturation) {
        super(validFluids, catalystInput, catalystConsumed);
        hungerAdded = hunger;
        saturationModifier = saturation;
    }

    @Override
    public void modifyOutput(TileEntityStillBase tile, FluidStack output) {
        NBTTagCompound compound = FluidUtil.createModifiers(output);
        float hunger = getModifierOrDefault("hunger",compound,output);
        float saturation = getModifierOrDefault("saturation",compound,output);
        compound.setFloat("hunger",hunger+ hungerAdded);
        compound.setFloat("saturation",Math.max(saturation, saturationModifier));
    }

    @Override
    public void modifyTooltip(List<String> tooltip) {
        super.modifyTooltip(tooltip);
        tooltip.add(tooltip.size()-1,TextFormatting.BLUE+Translator.translateToLocalFormatted("distilling.effect.add",Translator.translateToLocal("distilling.modifier.hunger.name"),hungerAdded));
    }
}
