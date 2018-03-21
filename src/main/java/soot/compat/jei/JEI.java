package soot.compat.jei;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import soot.Registry;
import soot.compat.jei.category.AlchemicalMixerCategory;
import soot.compat.jei.category.AlchemyTabletCategory;
import soot.compat.jei.category.DawnstoneAnvilCategory;
import soot.compat.jei.category.StillCategory;
import soot.compat.jei.wrapper.AlchemicalMixerWrapper;
import soot.compat.jei.wrapper.AlchemyTabletWrapper;
import soot.compat.jei.wrapper.DawnstoneAnvilWrapper;
import soot.compat.jei.wrapper.StillWrapper;
import soot.recipe.*;
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
        registry.addRecipeCategories(new DawnstoneAnvilCategory(guiHelper), new AlchemicalMixerCategory(guiHelper), new AlchemyTabletCategory(guiHelper), new StillCategory(guiHelper));
    }

    @Override
    public void register(IModRegistry registry) {
        IJeiHelpers helpers = registry.getJeiHelpers();

        registry.addRecipes(CraftingRegistry.dawnstoneAnvilRecipes, DawnstoneAnvilCategory.UID);
        registry.addRecipes(generateDawnstoneAnvilRecipes(), DawnstoneAnvilCategory.UID);
        registry.addRecipes(CraftingRegistry.alchemicalMixingRecipes, AlchemicalMixerCategory.UID);
        registry.addRecipes(CraftingRegistry.alchemyTabletRecipes, AlchemyTabletCategory.UID);
        registry.addRecipes(CraftingRegistry.stillRecipes, StillCategory.UID);
        
        registry.handleRecipes(RecipeDawnstoneAnvil.class, DawnstoneAnvilWrapper::new, DawnstoneAnvilCategory.UID);
        registry.handleRecipes(RecipeAlchemicalMixer.class, AlchemicalMixerWrapper::new, AlchemicalMixerCategory.UID);
        registry.handleRecipes(RecipeAlchemyTablet.class, recipe -> new AlchemyTabletWrapper(helpers,recipe), AlchemyTabletCategory.UID);
        registry.handleRecipes(RecipeStill.class, StillWrapper::new, StillCategory.UID);
        
        registry.addRecipeCatalyst(new ItemStack(RegistryManager.dawnstone_anvil), DawnstoneAnvilCategory.UID);
        registry.addRecipeCatalyst(new ItemStack(RegistryManager.mixer), AlchemicalMixerCategory.UID);
        registry.addRecipeCatalyst(new ItemStack(Registry.ALCHEMY_GLOBE), AlchemicalMixerCategory.UID);
        registry.addRecipeCatalyst(new ItemStack(RegistryManager.alchemy_tablet), AlchemyTabletCategory.UID);
        registry.addRecipeCatalyst(new ItemStack(Registry.STILL), StillCategory.UID);
    }
    
    private List<RecipeDawnstoneAnvil> generateDawnstoneAnvilRecipes()
    {
        //I'm basicly sorry for all of this. Please forgive me.
        ArrayList<RecipeDawnstoneAnvil> repairRecipes = new ArrayList<>();
        ArrayList<RecipeDawnstoneAnvil> destroyRecipes = new ArrayList<>();

        for (Item item : Item.REGISTRY) {
            ItemStack stack = item.getDefaultInstance();
            ItemStack repairItem = ItemStack.EMPTY;
            try {
                repairItem = Misc.getRepairItem(stack);
            }
            catch (Exception e) { //Gotta catch em all
                e.printStackTrace();
            }
            boolean isRepairable = item.getIsRepairable(stack, repairItem);
            boolean materiaAllowed = item.isRepairable();
            if(isRepairable || materiaAllowed)
            {
                ArrayList<ItemStack> repairMaterials = new ArrayList<>();
                repairMaterials.add(repairItem.copy());
                if(materiaAllowed)
                    repairMaterials.add(new ItemStack(RegistryManager.isolated_materia));
                ItemStack[] repairMaterialsArray = repairMaterials.toArray(new ItemStack[repairMaterials.size()]);
                repairRecipes.add(new RecipeDawnstoneAnvil(new ItemStack[]{stack.copy()}, Ingredient.fromStacks(makeDamaged(stack)),Ingredient.fromStacks(repairMaterialsArray)));
                if(Misc.getResourceCount(stack) != -1) {
                    ItemStack material = repairItem.copy();
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
