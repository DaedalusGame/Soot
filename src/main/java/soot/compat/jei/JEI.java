package soot.compat.jei;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.api.recipe.IRecipeWrapperFactory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import soot.compat.jei.category.DawnstoneAnvilCategory;
import soot.compat.jei.wrapper.DawnstoneAnvilWrapper;
import soot.recipe.RecipeDawnstoneAnvil;
import soot.recipe.RecipeRegistry;
import teamroots.embers.RegistryManager;
import teamroots.embers.util.Misc;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@mezz.jei.api.JEIPlugin
public class JEI implements IModPlugin {
    @Override
    public void registerCategories(IRecipeCategoryRegistration registry) {
        IGuiHelper guiHelper = registry.getJeiHelpers().getGuiHelper();
        registry.addRecipeCategories(new DawnstoneAnvilCategory(guiHelper));
    }

    @Override
    public void register(IModRegistry registry) {
        registry.addRecipes(RecipeRegistry.dawnstoneAnvilRecipes, DawnstoneAnvilCategory.UID);
        registry.addRecipes(generateDawnstoneAnvilRecipes(), DawnstoneAnvilCategory.UID);
        
        registry.handleRecipes(RecipeDawnstoneAnvil.class, DawnstoneAnvilWrapper::new, DawnstoneAnvilCategory.UID);
        
        registry.addRecipeCatalyst(new ItemStack(RegistryManager.dawnstone_anvil), DawnstoneAnvilCategory.UID);
    }
    
    private List<RecipeDawnstoneAnvil> generateDawnstoneAnvilRecipes()
    {
        //I'm basicly sorry for all of this. Please forgive me.
        ArrayList<RecipeDawnstoneAnvil> repairRecipes = new ArrayList<>();
        ArrayList<RecipeDawnstoneAnvil> destroyRecipes = new ArrayList<>();

        for (Item item : Item.REGISTRY) {
            ItemStack stack = item.getDefaultInstance();
            boolean isRepairable = item.getIsRepairable(stack, Misc.getRepairItem(stack));
            boolean materiaAllowed = item.isRepairable();
            if(isRepairable || materiaAllowed)
            {
                ArrayList<ItemStack> repairMaterials = new ArrayList<>();
                repairMaterials.add(Misc.getRepairItem(stack));
                if(materiaAllowed)
                    repairMaterials.add(new ItemStack(RegistryManager.isolated_materia));
                ItemStack[] repairMaterialsArray = repairMaterials.toArray(new ItemStack[repairMaterials.size()]);
                repairRecipes.add(new RecipeDawnstoneAnvil(new ItemStack[]{stack.copy()}, Ingredient.fromStacks(makeDamaged(stack)),Ingredient.fromStacks(repairMaterialsArray)));
                if(Misc.getResourceCount(stack) != -1) {
                    ItemStack material = Misc.getRepairItem(stack).copy();
                    material.setCount(Misc.getResourceCount(stack));
                    destroyRecipes.add(new RecipeDawnstoneAnvil(new ItemStack[]{material}, Ingredient.fromStacks(makeDamaged(stack)), Ingredient.EMPTY));
                }
            }
        }

        return Stream.concat(repairRecipes.stream(),destroyRecipes.stream()).collect(Collectors.toList());
    }

    private ItemStack makeDamaged(ItemStack stack)
    {
        ItemStack damagedStack = stack.copy();
        damagedStack.setItemDamage(stack.getMaxDamage() / 2);
        return damagedStack;
    }
}
