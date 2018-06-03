package soot.compat.crafttweaker;

import com.blamejared.mtlib.helpers.InputHelper;
import com.blamejared.mtlib.utils.BaseListAddition;
import com.blamejared.mtlib.utils.BaseListRemoval;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.collect.Streams;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.IAction;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import net.minecraft.item.ItemStack;
import soot.Config;
import soot.recipe.CraftingRegistry;
import soot.recipe.RecipeAlchemyTablet;
import soot.util.AlchemyUtil;
import soot.util.AspectList;
import soot.util.AspectList.AspectRangeList;
import soot.util.CTUtil;
import stanhebben.zenscript.annotations.NotNull;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;
import stanhebben.zenscript.value.IntRange;
import teamroots.embers.recipe.AlchemyRecipe;
import teamroots.embers.recipe.RecipeRegistry;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ZenClass(Alchemy.clazz)
public class Alchemy {
    public static final String clazz = "mods.embers.Alchemy";
    private static final HashSet<String> validAspects = Sets.newHashSet("iron","copper","dawnstone","lead","silver");

    @ZenMethod
    public static void addAspect(String name, IIngredient item)
    {
        CraftTweakerAPI.apply(new AddAspect(name,item));
    }

    @ZenMethod
    public static void add(IItemStack output, @NotNull IIngredient[] input, Map<String,IntRange> aspects) {
        AspectList minAspects = new AspectList();
        AspectList maxAspects = new AspectList();
        for (Map.Entry<String, IntRange> entry : aspects.entrySet()) {
            String aspect = entry.getKey();
            minAspects.addAspect(aspect,entry.getValue().getFrom());
            maxAspects.addAspect(aspect,entry.getValue().getTo());
        }
        AspectRangeList aspectRange = new AspectRangeList(minAspects, maxAspects);
        add(output, input, aspectRange);
    }

    @ZenMethod
    public static void add(IItemStack output, @NotNull IIngredient[] input, IntRange iron, IntRange copper, IntRange lead, IntRange silver, IntRange dawnstone) {
        add(output, input, CTUtil.toAspectRange(iron,copper,lead,silver,dawnstone));
    }

    public static void add(IItemStack output, @NotNull IIngredient[] input, AspectRangeList aspectRange) {
        if(Config.OVERRIDE_ALCHEMY_TABLET)
            addInternal(output, input, aspectRange);
        else if(input instanceof IItemStack[] && !hasInvalidAspects(aspectRange))
            addInternalOld(output,(IItemStack[])input, aspectRange);
        else
            CraftTweakerAPI.logWarning("Alchemy recipe for "+output.getDisplayName()+" is invalid if alchemy tablet override is disabled.");
    }

    private static boolean hasInvalidAspects(AspectRangeList list) {
        return list.getMaxAspects().getAspects().stream().anyMatch(aspect -> !validAspects.contains(aspect));
    }

    private static void addInternal(IItemStack output, @NotNull IIngredient[] input, AspectRangeList aspects) {
        RecipeAlchemyTablet recipe = new RecipeAlchemyTablet(InputHelper.toStack(output), CTUtil.toIngredient(input[0]), Lists.newArrayList(CTUtil.toIngredient(input[1]),CTUtil.toIngredient(input[2]),CTUtil.toIngredient(input[3]),CTUtil.toIngredient(input[4])), aspects);
        CraftTweakerAPI.apply(new Add(recipe));
    }

    private static void addInternalOld(IItemStack output, @NotNull IItemStack[] input, AspectRangeList aspects) {
        AlchemyRecipe recipe = new AlchemyRecipe(aspects.getMin("iron"),aspects.getMax("iron"),
                aspects.getMin("dawnstone"),aspects.getMax("dawnstone"),
                aspects.getMin("copper"),aspects.getMax("copper"),
                aspects.getMin("silver"),aspects.getMax("silver"),
                aspects.getMin("lead"),aspects.getMax("lead"),
                InputHelper.toStack(input[0]),InputHelper.toStack(input[1]),InputHelper.toStack(input[2]),InputHelper.toStack(input[3]),InputHelper.toStack(input[4]),
                InputHelper.toStack(output));
        CraftTweakerAPI.apply(new AddOld(recipe));
    }

    @ZenMethod
    public static void remove(IItemStack output) {
        if(Config.OVERRIDE_ALCHEMY_TABLET)
            CraftTweakerAPI.apply(new Remove(InputHelper.toStack(output)));
        else
            CraftTweakerAPI.apply(new RemoveOld(InputHelper.toStack(output)));
    }

    private static List<AlchemyRecipe> getOldRecipesByOutput(ItemStack stack) {
       return RecipeRegistry.alchemyRecipes.stream().filter(recipe -> ItemStack.areItemsEqual(recipe.result, stack)).collect(Collectors.toCollection(ArrayList::new));
    }

    private static List<RecipeAlchemyTablet> getRecipesByOutput(ItemStack stack) {
        return CraftingRegistry.alchemyTabletRecipes.stream().filter(recipe -> ItemStack.areItemsEqual(recipe.output, stack)).collect(Collectors.toCollection(ArrayList::new));
    }

    public static class AddAspect implements IAction
    {
        String name;
        IIngredient item;

        public AddAspect(String name, IIngredient item) {
            this.name = name;
            this.item = item;
        }

        @Override
        public void apply() {
            AlchemyUtil.registerAspect(name,CTUtil.toIngredient(item));
        }

        @Override
        public String describe() {
            return "Adding custom aspect '"+name+"'";
        }
    }

    public static class Add extends BaseListAddition<RecipeAlchemyTablet>
    {
        public Add(RecipeAlchemyTablet recipe)
        {
            super("Alchemy", CraftingRegistry.alchemyTabletRecipes, Lists.newArrayList(recipe));
        }

        @Override
        protected String getRecipeInfo(RecipeAlchemyTablet recipe) {
            return recipe.toString();
        }
    }

    public static class Remove extends BaseListRemoval<RecipeAlchemyTablet>
    {
        protected Remove(ItemStack input) {
            super("Alchemy", CraftingRegistry.alchemyTabletRecipes, getRecipesByOutput(input));
        }

        @Override
        protected String getRecipeInfo(RecipeAlchemyTablet recipe) {
            return recipe.toString();
        }
    }

    public static class AddOld extends BaseListAddition<AlchemyRecipe>
    {
        public AddOld(AlchemyRecipe recipe) {
            super("Alchemy", RecipeRegistry.alchemyRecipes, Lists.newArrayList(recipe));
        }

        @Override
        protected String getRecipeInfo(AlchemyRecipe recipe) {
            return recipe.toString();
        }
    }

    public static class RemoveOld extends BaseListRemoval<AlchemyRecipe>
    {
        protected RemoveOld(ItemStack input) {
            super("Alchemy", RecipeRegistry.alchemyRecipes, getOldRecipesByOutput(input));
        }

        @Override
        protected String getRecipeInfo(AlchemyRecipe recipe) {
            return recipe.toString();
        }
    }
}
