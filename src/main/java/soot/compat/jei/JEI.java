package soot.compat.jei;

import mezz.jei.api.*;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import net.minecraft.item.ItemStack;
import soot.Registry;
import soot.compat.jei.category.AlchemicalMixerCategory;
import soot.compat.jei.category.StillCategory;
import soot.compat.jei.wrapper.*;
import soot.recipe.*;
import teamroots.embers.RegistryManager;
import teamroots.embers.compat.jei.EmbersJEIPlugin;

@mezz.jei.api.JEIPlugin
public class JEI implements IModPlugin {
    @Override
    public void registerItemSubtypes(ISubtypeRegistry subtypeRegistry) {
        subtypeRegistry.useNbtForSubtypes(Registry.ESSENCE);
        subtypeRegistry.useNbtForSubtypes(Registry.MUG);
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registry) {
        IGuiHelper guiHelper = registry.getJeiHelpers().getGuiHelper();
        registry.addRecipeCategories(new AlchemicalMixerCategory(guiHelper), new StillCategory(guiHelper));
    }

    @Override
    public void register(IModRegistry registry) {
        IJeiHelpers helpers = registry.getJeiHelpers();

        registry.addRecipes(EmbersJEIPlugin.expandRecipes(CraftingRegistry.alchemicalMixingRecipes), AlchemicalMixerCategory.UID);
        registry.addRecipes(EmbersJEIPlugin.expandRecipes(CraftingRegistry.stillRecipes), StillCategory.UID);

        registry.handleRecipes(RecipeAlchemicalMixer.class, AlchemicalMixerWrapper::new, AlchemicalMixerCategory.UID);
        registry.handleRecipes(RecipeStill.class, StillWrapper::new, StillCategory.UID);

        registry.addRecipeCatalyst(new ItemStack(RegistryManager.mixer), AlchemicalMixerCategory.UID);
        registry.addRecipeCatalyst(new ItemStack(Registry.ALCHEMY_GLOBE), AlchemicalMixerCategory.UID);
        registry.addRecipeCatalyst(new ItemStack(Registry.STILL), StillCategory.UID);
    }
}
