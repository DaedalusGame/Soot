package soot.compat.crafttweaker;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.IAction;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.liquid.ILiquidStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.potions.IPotion;
import crafttweaker.mc1120.CraftTweaker;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import soot.brewing.EssenceStack;
import soot.brewing.FluidModifier;
import soot.brewing.FluidPotionModifier;
import soot.recipe.*;
import soot.util.FluidUtil;
import stanhebben.zenscript.annotations.Optional;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;
import teamroots.embers.compat.crafttweaker.CTUtil;

import java.util.ArrayList;
import java.util.List;

@ZenRegister
@ZenClass(Still.CLASS)
public class Still {
    public static final String NAME = "Still";
    public static final String CLASS = "mods.soot.Still";

    private static List<Fluid> toFluids(IIngredient ingredient) {
        List<Fluid> rList = new ArrayList<>();
        for (ILiquidStack liquidStack : ingredient.getLiquids()) {
            rList.add(CraftTweakerMC.getFluid(liquidStack.getDefinition()));
        }
        return rList;
    }

    private static int toFluidConsumption(IIngredient ingredient) {
        return ingredient.getAmount();
    }

    private static List<EssenceStack> toEssenceList(EssenceStackCT[] essences) {
        List<EssenceStack> rList = new ArrayList<>();
        for (EssenceStackCT essence : essences) {
            rList.add(essence.getInternal());
        }
        return rList;
    }

    @ZenMethod
    public static void add(String id, ILiquidStack output, ILiquidStack input, IIngredient catalyst, int catalystConsumed, @Optional EssenceStackCT[] essences) {
        RecipeStill recipe = new RecipeStill(new ResourceLocation(CraftTweaker.MODID, id), CraftTweakerMC.getLiquidStack(input), CTUtil.toIngredient(catalyst), catalystConsumed, CraftTweakerMC.getLiquidStack(output));
        if(essences != null)
            recipe.setEssence(toEssenceList(essences));
        CraftTweaker.LATE_ACTIONS.add(new AddRecipe(recipe));
    }

    @ZenMethod
    public static StillModifierBuilder addModifierRecipe(String id, IIngredient input, IIngredient catalyst, int catalystConsumed, @Optional EssenceStackCT[] essences) {
        int consumed = toFluidConsumption(input);
        RecipeStillModifier recipe = new RecipeStillModifier(new ResourceLocation(CraftTweaker.MODID, id), toFluids(input), CTUtil.toIngredient(catalyst), catalystConsumed) {
            @Override
            public int getInputConsumed() {
                return consumed;
            }
        };
        if(essences != null)
            recipe.setEssence(toEssenceList(essences));
        CraftTweaker.LATE_ACTIONS.add(new AddRecipe(recipe));
        return new StillModifierBuilder(recipe);
    }

    @ZenMethod
    public static StillModifierBuilder addFoodRecipe(String id, IIngredient input, IIngredient catalyst, int catalystConsumed, int hunger, float saturation, @Optional EssenceStackCT[] essences) {
        int consumed = toFluidConsumption(input);
        RecipeStillModifierFood recipe = new RecipeStillModifierFood(new ResourceLocation(CraftTweaker.MODID, id), toFluids(input),  CTUtil.toIngredient(catalyst), catalystConsumed, hunger, saturation) {
            @Override
            public int getInputConsumed() {
                return consumed;
            }
        };
        if(essences != null)
            recipe.setEssence(toEssenceList(essences));
        CraftTweaker.LATE_ACTIONS.add(new AddRecipe(recipe));
        return new StillModifierBuilder(recipe);
    }

    @ZenMethod
    public static StillModifierBuilder addDistillRecipe(String id, IIngredient input, IIngredient catalyst, int catalystConsumed, @Optional EssenceStackCT[] essences) {
        int consumed = toFluidConsumption(input);
        RecipeStillDoubleDistillation recipe = new RecipeStillDoubleDistillation(new ResourceLocation(CraftTweaker.MODID, id), toFluids(input),  CTUtil.toIngredient(catalyst), catalystConsumed) {
            @Override
            public int getInputConsumed() {
                return consumed;
            }
        };
        if(essences != null)
            recipe.setEssence(toEssenceList(essences));
        CraftTweaker.LATE_ACTIONS.add(new AddRecipe(recipe));
        return new StillModifierBuilder(recipe);
    }

    @ZenMethod
    public static void addCatalyst(IIngredient ingredient, int size) {
        CatalystInfo info = new CatalystInfo(CTUtil.toIngredient(ingredient), size);
        CraftTweakerAPI.apply(new AddCatalyst(info));
    }

    @ZenMethod
    public static void addPotionModifier(String name, boolean primary, IPotion effect, int maxStack) {
        FluidModifier.EffectType effectType = getEffectType(effect);
        FluidModifier.EnumType type = primary ? FluidModifier.EnumType.PRIMARY : FluidModifier.EnumType.SECONDARY;
        FluidModifier modifier = new FluidPotionModifier(name, 0, type, effectType, CraftTweakerMC.getPotion(effect), maxStack);
        CraftTweakerAPI.apply(new AddModifier(modifier));
    }

    private static FluidModifier.EffectType getEffectType(IPotion effect) {
        boolean good = effect.isBeneficial();
        boolean bad = effect.isBadEffect();
        FluidModifier.EffectType effectType = FluidModifier.EffectType.NEUTRAL;
        if(good && !bad)
            effectType = FluidModifier.EffectType.POSITIVE;
        else if(bad && !good)
            effectType = FluidModifier.EffectType.NEGATIVE;
        return effectType;
    }

    @ZenMethod
    public static void remove(String id) {
        CraftTweakerAPI.apply(new RemoveByName(new ResourceLocation(id)));
    }

    public static class AddRecipe implements IAction {
        RecipeStill recipe;

        public AddRecipe(RecipeStill recipe) {
            this.recipe = recipe;
        }

        @Override
        public void apply() {
            CraftingRegistry.stillRecipes.add(recipe);
        }

        @Override
        public String describe() {
            return String.format("Adding %s recipe: %s",NAME,recipe.toString());
        }
    }

    public static class AddCatalyst implements IAction {
        CatalystInfo catalystInfo;

        public AddCatalyst(CatalystInfo catalystInfo) {
            this.catalystInfo = catalystInfo;
        }

        @Override
        public void apply() {
            CraftingRegistry.stillCatalysts.add(catalystInfo);
        }

        @Override
        public String describe() {
            return String.format("Adding %s catalyst: %s",NAME,catalystInfo.toString());
        }
    }

    public static class AddModifier implements IAction {
        FluidModifier modifier;

        public AddModifier(FluidModifier modifier) {
            this.modifier = modifier;
        }

        @Override
        public void apply() {
            FluidUtil.registerModifier(modifier);
        }

        @Override
        public String describe() {
            return String.format("Adding modifier: %s",modifier.toString());
        }
    }

    public static class RemoveByName implements IAction {
        ResourceLocation id;

        public RemoveByName(ResourceLocation id) {
            this.id = id;
        }

        @Override
        public void apply() {
            CraftingRegistry.stillRecipes.removeIf(recipe -> recipe.id.equals(id));
        }

        @Override
        public String describe() {
            return String.format("Removing %s recipe: %s",NAME,id);
        }
    }
}
