package soot.compat.crafttweaker;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemCondition;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraft.item.ItemStack;
import stanhebben.zenscript.annotations.ZenExpansion;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethod;
import stanhebben.zenscript.annotations.ZenSetter;
import teamroots.embers.util.ItemModUtil;

public class CraftTweaker {
    @ZenExpansion("crafttweaker.item.IIngredient")
    @ZenRegister
    public static class IngredientExtensions {
        @ZenMethod
        public IIngredient anyHeat(IIngredient ingredient)
        {
            return ingredient.only(iItemStack -> {
                ItemStack stack = CraftTweakerMC.getItemStack(iItemStack);
                return ItemModUtil.hasHeat(stack);
            });
        }

        @ZenMethod
        public IIngredient onlyHeatAtLeast(IIngredient ingredient, float threshold)
        {
            return ingredient.only(iItemStack -> {
                ItemStack stack = CraftTweakerMC.getItemStack(iItemStack);
                return ItemModUtil.getHeat(stack) >= threshold;
            });
        }

        @ZenMethod
        public IIngredient onlyHeatAtMost(IIngredient ingredient, float threshold)
        {
            return ingredient.only(iItemStack -> {
                ItemStack stack = CraftTweakerMC.getItemStack(iItemStack);
                return ItemModUtil.getHeat(stack) <= threshold;
            });
        }

        @ZenMethod
        public IIngredient onlyHeatLevelAtLeast(IIngredient ingredient, int threshold)
        {
            return ingredient.only(iItemStack -> {
                ItemStack stack = CraftTweakerMC.getItemStack(iItemStack);
                return ItemModUtil.getLevel(stack) >= threshold;
            });
        }

        @ZenMethod
        public IIngredient onlyHeatLevelAtMost(IIngredient ingredient, int threshold)
        {
            return ingredient.only(iItemStack -> {
                ItemStack stack = CraftTweakerMC.getItemStack(iItemStack);
                return ItemModUtil.getLevel(stack) <= threshold;
            });
        }

        @ZenMethod
        public IIngredient onlyWithModifier(IIngredient ingredient, String modifier)
        {
            return ingredient.only(iItemStack -> {
                ItemStack stack = CraftTweakerMC.getItemStack(iItemStack);
                return ItemModUtil.hasModifier(stack, modifier);
            });
        }

        @ZenMethod
        public IIngredient onlyWithModifierLevelAtLeast(IIngredient ingredient, String modifier, int threshold)
        {
            return ingredient.only(iItemStack -> {
                ItemStack stack = CraftTweakerMC.getItemStack(iItemStack);
                return ItemModUtil.getModifierLevel(stack, modifier) >= threshold;
            });
        }

        @ZenMethod
        public IIngredient onlyWithModifierLevelAtMost(IIngredient ingredient, String modifier, int threshold)
        {
            return ingredient.only(iItemStack -> {
                ItemStack stack = CraftTweakerMC.getItemStack(iItemStack);
                return ItemModUtil.getModifierLevel(stack, modifier) >= threshold;
            });
        }

        @ZenMethod
        public IIngredient onlyIfModifierValid(IIngredient ingredient, IItemStack modifier)
        {
            return ingredient.only(iItemStack -> {
                ItemStack stack = CraftTweakerMC.getItemStack(iItemStack);
                ItemStack modifierStack = CraftTweakerMC.getItemStack(modifier);
                return ItemModUtil.isModValid(stack, modifierStack);
            });
        }
    }

    @ZenExpansion("crafttweaker.item.IItemStack")
    @ZenRegister
    public static class ItemStackExtensions {
        @ZenGetter("hasHeat")
        public boolean hasHeat(IItemStack itemStack) {
            ItemStack stack = (ItemStack) itemStack.getInternal();
            return ItemModUtil.hasHeat(stack);
        }

        @ZenGetter("heat")
        public float getHeat(IItemStack itemStack) {
            ItemStack stack = (ItemStack) itemStack.getInternal();
            return ItemModUtil.getHeat(stack);
        }

        @ZenSetter("heat")
        public void setHeat(IItemStack itemStack, float heat) {
            ItemStack stack = (ItemStack) itemStack.getInternal();
            ItemModUtil.setHeat(stack, heat);
        }

        @ZenGetter("maxHeat")
        public float getMaxHeat(IItemStack itemStack) {
            ItemStack stack = (ItemStack) itemStack.getInternal();
            return ItemModUtil.getMaxHeat(stack);
        }

        @ZenGetter("heatLevel")
        public int getHeatLevel(IItemStack itemStack) {
            ItemStack stack = (ItemStack) itemStack.getInternal();
            return ItemModUtil.getLevel(stack);
        }

        @ZenSetter("heatLevel")
        public void getHeatLevel(IItemStack itemStack, int level) {
            ItemStack stack = (ItemStack) itemStack.getInternal();
            ItemModUtil.setLevel(stack, level);
        }

        @ZenMethod
        public void addModifier(IItemStack itemStack, IItemStack modifier) {
            ItemStack stack = (ItemStack) itemStack.getInternal();
            ItemStack modifierStack = CraftTweakerMC.getItemStack(modifier);
            ItemModUtil.addModifier(stack,modifierStack);
        }

        @ZenMethod
        public boolean isModifierValid(IItemStack itemStack, IItemStack modifier) {
            ItemStack stack = (ItemStack) itemStack.getInternal();
            ItemStack modifierStack = CraftTweakerMC.getItemStack(modifier);
            return ItemModUtil.isModValid(stack,modifierStack);
        }

        @ZenMethod
        public boolean hasModifier(IItemStack itemStack, String modifier) {
            ItemStack stack = (ItemStack) itemStack.getInternal();
            return ItemModUtil.hasModifier(stack,modifier);
        }

        @ZenMethod
        public int getModifierLevel(IItemStack itemStack, String modifier) {
            ItemStack stack = (ItemStack) itemStack.getInternal();
            return ItemModUtil.getModifierLevel(stack,modifier);
        }

        @ZenMethod
        public void setModifierLevel(IItemStack itemStack, String modifier, int level) {
            ItemStack stack = (ItemStack) itemStack.getInternal();
            ItemModUtil.setModifierLevel(stack,modifier,level);
        }
    }
}
