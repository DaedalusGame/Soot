package soot.compat.crafttweaker;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.IAction;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.liquid.ILiquidStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraftforge.fluids.FluidStack;
import soot.recipe.CraftingRegistry;
import soot.recipe.RecipeAlchemicalMixer;
import stanhebben.zenscript.annotations.NotNull;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;
import stanhebben.zenscript.value.IntRange;
import teamroots.embers.api.alchemy.AspectList;
import teamroots.embers.api.alchemy.AspectList.AspectRangeList;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ZenRegister
@ZenClass(AlchemicalMixer.CLASS)
public class AlchemicalMixer {
    public static final String NAME = "Alchemical Mixer";
    public static final String CLASS = "mods.soot.AlchemicalMixer";

    private static AspectList.AspectRangeList toAspectRange(IntRange iron, IntRange copper, IntRange lead, IntRange silver, IntRange dawnstone)
    {
        AspectList min = AspectList.createStandard(iron.getFrom(),dawnstone.getFrom(),copper.getFrom(),silver.getFrom(),lead.getFrom());
        AspectList max = AspectList.createStandard(iron.getTo(),dawnstone.getTo(),copper.getTo(),silver.getTo(),lead.getTo());
        return new AspectList.AspectRangeList(min,max);
    }

    @ZenMethod
    public static void add(ILiquidStack output, @NotNull ILiquidStack[] inputs, IntRange iron, IntRange copper, IntRange lead, IntRange silver, IntRange dawnstone) {
        addInternal(output,inputs,toAspectRange(iron,copper,lead,silver,dawnstone));
    }

    @ZenMethod
    public static void add(ILiquidStack output, @NotNull ILiquidStack[] inputs, Map<String,IntRange> aspects) {
        AspectList minAspects = new AspectList();
        AspectList maxAspects = new AspectList();
        for (Map.Entry<String, IntRange> entry : aspects.entrySet()) {
            String aspect = entry.getKey();
            minAspects.addAspect(aspect,entry.getValue().getFrom());
            maxAspects.addAspect(aspect,entry.getValue().getTo());
        }
        addInternal(output, inputs, new AspectRangeList(minAspects,maxAspects));
    }

    private static void addInternal(ILiquidStack output, @NotNull ILiquidStack[] inputs, AspectRangeList aspects)
    {
        RecipeAlchemicalMixer recipe = new RecipeAlchemicalMixer(CraftTweakerMC.getLiquidStacks(inputs),CraftTweakerMC.getLiquidStack(output), aspects);
        CraftTweakerAPI.apply(new Add(recipe));
    }

    @ZenMethod
    public static void remove(ILiquidStack output)
    {
        CraftTweakerAPI.apply(new RemoveByOutput(CraftTweakerMC.getLiquidStack(output)));
    }

    private static List<RecipeAlchemicalMixer> getRecipesByOutput(FluidStack stack)
    {
        return CraftingRegistry.alchemicalMixingRecipes.stream().filter(recipe -> recipe.output.isFluidStackIdentical(stack)).collect(Collectors.toCollection(ArrayList::new));
    }

    public static class Add implements IAction
    {
        RecipeAlchemicalMixer recipe;

        public Add(RecipeAlchemicalMixer recipe) {
            this.recipe = recipe;
        }

        @Override
        public void apply() {
            CraftingRegistry.alchemicalMixingRecipes.add(recipe);
        }

        @Override
        public String describe() {
            return String.format("Adding %s recipe: %s",NAME,recipe.toString());
        }
    }

    public static class RemoveByOutput implements IAction
    {
        FluidStack output;

        protected RemoveByOutput(FluidStack output) {
            this.output = output;
        }

        @Override
        public void apply() {
            CraftingRegistry.alchemicalMixingRecipes.removeAll(getRecipesByOutput(output));
        }

        @Override
        public String describe() {
            return String.format("Removing %s recipes with output: %s",NAME,output.toString());
        }
    }

    public static class RemoveAll implements IAction
    {
        protected RemoveAll() {

        }

        @Override
        public void apply() {
            CraftingRegistry.alchemicalMixingRecipes.clear();
        }

        @Override
        public String describe() {
            return String.format("Removing all %s recipes",NAME);
        }
    }
}
