package soot.compat.jei;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import net.minecraft.item.ItemStack;
import soot.Registry;
import soot.compat.jei.category.AlchemicalMixerCategory;
import soot.compat.jei.category.DawnstoneAnvilCategory;
import soot.compat.jei.category.StillCategory;
import soot.compat.jei.wrapper.*;
import soot.recipe.*;
import teamroots.embers.RegistryManager;
import teamroots.embers.recipe.DawnstoneAnvilRecipe;
import teamroots.embers.recipe.RecipeRegistry;

@mezz.jei.api.JEIPlugin
public class JEI implements IModPlugin {
    @Override
    public void registerCategories(IRecipeCategoryRegistration registry) {
        IGuiHelper guiHelper = registry.getJeiHelpers().getGuiHelper();
        registry.addRecipeCategories(new DawnstoneAnvilCategory(guiHelper), new AlchemicalMixerCategory(guiHelper), new StillCategory(guiHelper));
    }

    @Override
    public void register(IModRegistry registry) {
        IJeiHelpers helpers = registry.getJeiHelpers();

        registry.addRecipes(RecipeRegistry.dawnstoneAnvilRecipes, DawnstoneAnvilCategory.UID);
        //registry.addRecipes(generateDawnstoneAnvilRecipes(), DawnstoneAnvilCategory.UID);
        registry.addRecipes(CraftingRegistry.alchemicalMixingRecipes, AlchemicalMixerCategory.UID);
        registry.addRecipes(CraftingRegistry.stillRecipes, StillCategory.UID);
        
        registry.handleRecipes(DawnstoneAnvilRecipe.class, DawnstoneAnvilWrapper::new, DawnstoneAnvilCategory.UID);
        registry.handleRecipes(RecipeAlchemicalMixer.class, AlchemicalMixerWrapper::new, AlchemicalMixerCategory.UID);
        registry.handleRecipes(RecipeStill.class, StillWrapper::new, StillCategory.UID);
        
        registry.addRecipeCatalyst(new ItemStack(RegistryManager.dawnstone_anvil), DawnstoneAnvilCategory.UID);
        registry.addRecipeCatalyst(new ItemStack(RegistryManager.mixer), AlchemicalMixerCategory.UID);
        registry.addRecipeCatalyst(new ItemStack(Registry.ALCHEMY_GLOBE), AlchemicalMixerCategory.UID);
        registry.addRecipeCatalyst(new ItemStack(Registry.STILL), StillCategory.UID);
    }
}
