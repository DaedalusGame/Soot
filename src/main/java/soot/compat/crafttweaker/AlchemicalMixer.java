package soot.compat.crafttweaker;

import com.blamejared.mtlib.helpers.InputHelper;
import com.blamejared.mtlib.utils.BaseListAddition;
import com.blamejared.mtlib.utils.BaseListRemoval;
import com.google.common.collect.Lists;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.liquid.ILiquidStack;
import net.minecraftforge.fluids.FluidStack;
import soot.recipe.CraftingRegistry;
import soot.recipe.RecipeAlchemicalMixer;
import soot.util.CTUtil;
import stanhebben.zenscript.annotations.NotNull;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;
import stanhebben.zenscript.value.IntRange;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@ZenRegister
@ZenClass(AlchemicalMixer.clazz)
public class AlchemicalMixer {
    public static final String clazz = "mods.soot.AlchemicalMixer";

    @ZenMethod
    public static void add(ILiquidStack output, @NotNull ILiquidStack[] inputs,  int ironMin, int ironMax, int copperMin, int copperMax, int leadMin, int leadMax, int silverMin, int silverMax, int dawnstoneMin, int dawnstoneMax) {
        add(output, inputs, new IntRange(ironMin,ironMax),new IntRange(copperMin,copperMax),new IntRange(leadMin,leadMax),new IntRange(silverMin,silverMax),new IntRange(dawnstoneMin,dawnstoneMax));
    }

    @ZenMethod
    public static void add(ILiquidStack output, @NotNull ILiquidStack[] inputs, IntRange iron, IntRange copper, IntRange lead, IntRange silver, IntRange dawnstone) {
        RecipeAlchemicalMixer recipe = new RecipeAlchemicalMixer(InputHelper.toFluids(inputs),InputHelper.toFluid(output), CTUtil.toAspectRange(iron,copper,lead,silver,dawnstone));
        CraftTweakerAPI.apply(new Add(recipe));
    }

    @ZenMethod
    public static void remove(ILiquidStack output)
    {
        CraftTweakerAPI.apply(new Remove(InputHelper.toFluid(output)));
    }

    private static List<RecipeAlchemicalMixer> getRecipesByOutput(FluidStack stack)
    {
        return CraftingRegistry.alchemicalMixingRecipes.stream().filter(recipe -> recipe.output.isFluidStackIdentical(stack)).collect(Collectors.toCollection(ArrayList::new));
    }

    public static class Add extends BaseListAddition<RecipeAlchemicalMixer>
    {
        public Add(RecipeAlchemicalMixer recipe) {
            super("AlchemicalMixer", CraftingRegistry.alchemicalMixingRecipes, Lists.newArrayList(recipe));
        }

        @Override
        protected String getRecipeInfo(RecipeAlchemicalMixer recipe) {
            return recipe.toString();
        }
    }

    public static class Remove extends BaseListRemoval<RecipeAlchemicalMixer>
    {
        protected Remove(FluidStack input) {
            super("AlchemicalMixer", CraftingRegistry.alchemicalMixingRecipes, getRecipesByOutput(input));
        }

        @Override
        protected String getRecipeInfo(RecipeAlchemicalMixer recipe) {
            return recipe.toString();
        }
    }
}
