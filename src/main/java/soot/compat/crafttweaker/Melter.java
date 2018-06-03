package soot.compat.crafttweaker;

import com.blamejared.mtlib.helpers.InputHelper;
import com.blamejared.mtlib.utils.BaseListAddition;
import com.blamejared.mtlib.utils.BaseListRemoval;
import com.google.common.collect.Lists;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.liquid.ILiquidStack;
import crafttweaker.api.oredict.IOreDictEntry;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;
import teamroots.embers.recipe.ItemMeltingOreRecipe;
import teamroots.embers.recipe.ItemMeltingRecipe;
import teamroots.embers.recipe.RecipeRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@ZenClass(Melter.clazz)
public class Melter {
    public static final String clazz = "mods.embers.Melter";

    @ZenMethod
    public static void add(ILiquidStack output, IItemStack input) {
        ItemStack stack = InputHelper.toStack(input);
        ItemMeltingRecipe recipe = new ItemMeltingRecipe(stack,InputHelper.toFluid(output),stack.getMetadata() != OreDictionary.WILDCARD_VALUE,stack.hasTagCompound());
        CraftTweakerAPI.apply(new Add(recipe));
    }

    @ZenMethod
    public static void add(ILiquidStack output, IOreDictEntry input) {
        ItemMeltingOreRecipe recipe = new ItemMeltingOreRecipe(input.getName(),InputHelper.toFluid(output));
        CraftTweakerAPI.apply(new AddOre(recipe));
    }

    @ZenMethod
    public static void remove(IItemStack input)
    {
        CraftTweakerAPI.apply(new RemoveByInput(InputHelper.toStack(input)));
    }

    @ZenMethod
    public static void remove(ILiquidStack output)
    {
        CraftTweakerAPI.apply(new RemoveByOutput(InputHelper.toFluid(output)));
        CraftTweakerAPI.apply(new RemoveByOreOutput(InputHelper.toFluid(output)));
    }

    @ZenMethod
    public static void remove(IOreDictEntry input)
    {
        CraftTweakerAPI.apply(new RemoveByOre(input.getName()));
    }

    private static List<ItemMeltingRecipe> getRecipesByOutput(FluidStack stack)
    {
        return RecipeRegistry.meltingRecipes.stream().filter(recipe -> stack.isFluidStackIdentical(recipe.getFluid())).collect(Collectors.toCollection(ArrayList::new));
    }

    private static List<ItemMeltingOreRecipe> getOreRecipesByOutput(FluidStack stack)
    {
        return RecipeRegistry.meltingOreRecipes.stream().filter(recipe -> stack.isFluidStackIdentical(recipe.getFluid())).collect(Collectors.toCollection(ArrayList::new));
    }

    private static List<ItemMeltingRecipe> getRecipesByInput(ItemStack stack)
    {
        return RecipeRegistry.meltingRecipes.stream().filter(recipe -> ItemStack.areItemStacksEqual(recipe.getStack(),stack)).collect(Collectors.toCollection(ArrayList::new));
    }

    private static List<ItemMeltingOreRecipe> getRecipesByInput(String ore)
    {
        return RecipeRegistry.meltingOreRecipes.stream().filter(recipe -> recipe.getOreName().equals(ore)).collect(Collectors.toCollection(ArrayList::new));
    }


    public static class Add extends BaseListAddition<ItemMeltingRecipe>
    {
        public Add(ItemMeltingRecipe recipe) {
            super("Melter", RecipeRegistry.meltingRecipes, Lists.newArrayList(recipe));
        }

        @Override
        protected String getRecipeInfo(ItemMeltingRecipe recipe) {
            return recipe.toString();
        }
    }

    public static class AddOre extends BaseListAddition<ItemMeltingOreRecipe>
    {
        public AddOre(ItemMeltingOreRecipe recipe) {
            super("Melter", RecipeRegistry.meltingOreRecipes, Lists.newArrayList(recipe));
        }

        @Override
        protected String getRecipeInfo(ItemMeltingOreRecipe recipe) {
            return recipe.toString();
        }
    }

    public static class RemoveByOutput extends BaseListRemoval<ItemMeltingRecipe>
    {
        protected RemoveByOutput(FluidStack output) {
            super("Melter", RecipeRegistry.meltingRecipes, getRecipesByOutput(output));
        }

        @Override
        protected String getRecipeInfo(ItemMeltingRecipe recipe) {
            return recipe.toString();
        }
    }

    public static class RemoveByInput extends BaseListRemoval<ItemMeltingRecipe>
    {
        protected RemoveByInput(ItemStack input) {
            super("Melter", RecipeRegistry.meltingRecipes, getRecipesByInput(input));
        }

        @Override
        protected String getRecipeInfo(ItemMeltingRecipe recipe) {
            return recipe.toString();
        }
    }

    public static class RemoveByOreOutput extends BaseListRemoval<ItemMeltingOreRecipe>
    {
        protected RemoveByOreOutput(FluidStack output) {
            super("Melter", RecipeRegistry.meltingOreRecipes, getOreRecipesByOutput(output));
        }

        @Override
        protected String getRecipeInfo(ItemMeltingOreRecipe recipe) {
            return recipe.toString();
        }
    }

    public static class RemoveByOre extends BaseListRemoval<ItemMeltingOreRecipe>
    {
        protected RemoveByOre(String input) {
            super("Melter", RecipeRegistry.meltingOreRecipes, getRecipesByInput(input));
        }

        @Override
        protected String getRecipeInfo(ItemMeltingOreRecipe recipe) {
            return recipe.toString();
        }
    }
}
