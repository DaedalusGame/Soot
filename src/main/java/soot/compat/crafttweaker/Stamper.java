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
import net.minecraftforge.oredict.OreDictionary;
import stanhebben.zenscript.annotations.NotNull;
import stanhebben.zenscript.annotations.Optional;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;
import teamroots.embers.item.EnumStampType;
import teamroots.embers.recipe.ItemStampingOreRecipe;
import teamroots.embers.recipe.ItemStampingRecipe;
import teamroots.embers.recipe.RecipeRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@ZenRegister
@ZenClass(Stamper.clazz)
public class Stamper {
    public static final String clazz = "mods.embers.Stamper";

    @ZenMethod
    public static void add(IItemStack output, ILiquidStack liquid, @NotNull IItemStack stamp, @Optional IItemStack input) {
        ItemStack stack = InputHelper.toStack(input);
        ItemStack stampStack = InputHelper.toStack(stamp); //This is pointless but also the easiest way.
        ItemStampingRecipe recipe = new ItemStampingRecipe(stack,InputHelper.toFluid(liquid), EnumStampType.getType(stampStack),InputHelper.toStack(output),stack.getMetadata() != OreDictionary.WILDCARD_VALUE,stack.hasTagCompound());
        CraftTweakerAPI.apply(new Add(recipe));
    }

    @ZenMethod
    public static void add(IItemStack output, ILiquidStack liquid, @NotNull IItemStack stamp, @NotNull IOreDictEntry ore) {
        ItemStack stampStack = InputHelper.toStack(stamp); //This is pointless but also the easiest way.
        ItemStampingOreRecipe recipe = new ItemStampingOreRecipe(ore.getName(),InputHelper.toFluid(liquid), EnumStampType.getType(stampStack),InputHelper.toStack(output),true,true);
        CraftTweakerAPI.apply(new AddOre(recipe));
    }

    @ZenMethod
    public static void remove(IItemStack output)
    {
        CraftTweakerAPI.apply(new Remove(InputHelper.toStack(output)));
        CraftTweakerAPI.apply(new RemoveOre(InputHelper.toStack(output)));
    }

    private static List<ItemStampingRecipe> getRecipesByOutput(ItemStack stack)
    {
        return RecipeRegistry.stampingRecipes.stream().filter(recipe -> ItemStack.areItemStacksEqual(stack,recipe.result)).collect(Collectors.toCollection(ArrayList::new));
    }

    private static List<ItemStampingOreRecipe> getOreRecipesByOutput(ItemStack stack)
    {
        return RecipeRegistry.stampingOreRecipes.stream().filter(recipe -> ItemStack.areItemStacksEqual(stack,recipe.result)).collect(Collectors.toCollection(ArrayList::new));
    }

    public static class Add extends BaseListAddition<ItemStampingRecipe>
    {
        public Add(ItemStampingRecipe recipe) {
            super("Stamper", RecipeRegistry.stampingRecipes, Lists.newArrayList(recipe));
        }

        @Override
        protected String getRecipeInfo(ItemStampingRecipe recipe) {
            return recipe.toString();
        }
    }

    public static class AddOre extends BaseListAddition<ItemStampingOreRecipe>
    {
        public AddOre(ItemStampingOreRecipe recipe) {
            super("Stamper", RecipeRegistry.stampingOreRecipes, Lists.newArrayList(recipe));
        }

        @Override
        protected String getRecipeInfo(ItemStampingOreRecipe recipe) {
            return recipe.toString();
        }
    }

    public static class Remove extends BaseListRemoval<ItemStampingRecipe>
    {
        protected Remove(ItemStack output) {
            super("Stamper", RecipeRegistry.stampingRecipes, getRecipesByOutput(output));
        }

        @Override
        protected String getRecipeInfo(ItemStampingRecipe recipe) {
            return recipe.toString();
        }
    }

    public static class RemoveOre extends BaseListRemoval<ItemStampingOreRecipe>
    {
        protected RemoveOre(ItemStack output) {
            super("Stamper", RecipeRegistry.stampingOreRecipes, getOreRecipesByOutput(output));
        }

        @Override
        protected String getRecipeInfo(ItemStampingOreRecipe recipe) {
            return recipe.toString();
        }
    }
}