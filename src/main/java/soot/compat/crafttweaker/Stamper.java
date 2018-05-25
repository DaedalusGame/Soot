package soot.compat.crafttweaker;

import com.blamejared.mtlib.helpers.InputHelper;
import com.blamejared.mtlib.utils.BaseListAddition;
import com.blamejared.mtlib.utils.BaseListRemoval;
import com.google.common.collect.Lists;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.IAction;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.liquid.ILiquidStack;
import crafttweaker.api.oredict.IOreDictEntry;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import soot.Config;
import soot.recipe.CraftingRegistry;
import soot.recipe.RecipeStamper;
import soot.util.CTUtil;
import soot.util.IngredientCraftTweaker;
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
    public static void add(IItemStack output, ILiquidStack liquid, @NotNull IIngredient stamp, @Optional IIngredient input) {
        if(Config.OVERRIDE_STAMPER)
            addInternal(output,liquid,stamp,input);
        else if(input instanceof IItemStack && stamp instanceof IItemStack)
            addInternalOld(output,liquid,(IItemStack)stamp,(IItemStack)input);
        else
            CraftTweakerAPI.logWarning("Stamper recipe for "+output.getDisplayName()+" is invalid if stamper override is disabled.");
    }

    private static void addInternal(IItemStack output, ILiquidStack liquid, IIngredient stamp, IIngredient input) {
        RecipeStamper recipe = new RecipeStamper(CTUtil.toIngredient(input),InputHelper.toFluid(liquid),InputHelper.toStack(output),CTUtil.toIngredient(stamp));
        CraftTweakerAPI.apply(new Add(recipe));
    }

    private static void addInternalOld(IItemStack output, ILiquidStack liquid, IItemStack stamp, IItemStack input) {
        ItemStack stack = InputHelper.toStack(input);
        ItemStack stampStack = InputHelper.toStack(stamp); //This is pointless but also the easiest way.
        ItemStampingRecipe recipe = new ItemStampingRecipe(stack,InputHelper.toFluid(liquid), EnumStampType.getType(stampStack),InputHelper.toStack(output),stack.getMetadata() != OreDictionary.WILDCARD_VALUE,stack.hasTagCompound());
        CraftTweakerAPI.apply(new AddOld(recipe));
    }

    @ZenMethod
    public static void remove(IItemStack output)
    {
        if(Config.OVERRIDE_STAMPER)
            CraftTweakerAPI.apply(new Remove(InputHelper.toStack(output)));
        else
            CraftTweakerAPI.apply(new RemoveOld(InputHelper.toStack(output)));
    }

    private static List<ItemStampingRecipe> getOldRecipesByOutput(ItemStack stack)
    {
        return RecipeRegistry.stampingRecipes.stream().filter(recipe -> ItemStack.areItemStacksEqual(stack,recipe.result)).collect(Collectors.toCollection(ArrayList::new));
    }

    private static List<RecipeStamper> getRecipesByOutput(ItemStack stack)
    {
        return CraftingRegistry.stamperRecipes.stream().filter(recipe -> ItemStack.areItemStacksEqual(stack,recipe.output)).collect(Collectors.toCollection(ArrayList::new));
    }

    public static class Add extends BaseListAddition<RecipeStamper>
    {
        public Add(RecipeStamper recipe) {
            super("Stamper", CraftingRegistry.stamperRecipes, Lists.newArrayList(recipe));
        }

        @Override
        protected String getRecipeInfo(RecipeStamper recipe) {
            return recipe.toString();
        }
    }

    public static class AddOld extends BaseListAddition<ItemStampingRecipe>
    {
        public AddOld(ItemStampingRecipe recipe) {
            super("Stamper", RecipeRegistry.stampingRecipes, Lists.newArrayList(recipe));
        }

        @Override
        protected String getRecipeInfo(ItemStampingRecipe recipe) {
            return recipe.toString();
        }
    }

    public static class RemoveOld extends BaseListRemoval<ItemStampingRecipe>
    {
        protected RemoveOld(ItemStack output) {
            super("Stamper", RecipeRegistry.stampingRecipes, getOldRecipesByOutput(output));
        }

        @Override
        protected String getRecipeInfo(ItemStampingRecipe recipe) {
            return recipe.toString();
        }
    }

    public static class Remove extends BaseListRemoval<RecipeStamper>
    {
        protected Remove(ItemStack output) {
            super("Stamper", CraftingRegistry.stamperRecipes, getRecipesByOutput(output));
        }

        @Override
        protected String getRecipeInfo(RecipeStamper recipe) {
            return recipe.toString();
        }
    }
}