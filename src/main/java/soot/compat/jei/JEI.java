package soot.compat.jei;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLLog;
import soot.Config;
import soot.Registry;
import soot.Soot;
import soot.compat.jei.category.AlchemicalMixerCategory;
import soot.compat.jei.category.AlchemyTabletCategory;
import soot.compat.jei.category.DawnstoneAnvilCategory;
import soot.compat.jei.category.StillCategory;
import soot.compat.jei.wrapper.*;
import soot.recipe.*;
import teamroots.embers.RegistryManager;
import teamroots.embers.compat.jei.StampRecipeCategory;
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
        registry.addRecipes(CraftingRegistry.stamperRecipes, "embers.stamp"); //REEEEE
        
        registry.handleRecipes(RecipeDawnstoneAnvil.class, DawnstoneAnvilWrapper::new, DawnstoneAnvilCategory.UID);
        registry.handleRecipes(RecipeAlchemicalMixer.class, AlchemicalMixerWrapper::new, AlchemicalMixerCategory.UID);
        registry.handleRecipes(RecipeAlchemyTablet.class, recipe -> new AlchemyTabletWrapper(helpers,recipe), AlchemyTabletCategory.UID);
        registry.handleRecipes(RecipeStill.class, StillWrapper::new, StillCategory.UID);
        registry.handleRecipes(RecipeStamper.class, StamperWrapper::new, "embers.stamp");
        
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
                if(Config.DEBUG_MODE)
                    e.printStackTrace();
                else
                    Soot.log.info("Cannot find repair material for "+stack.getDisplayName());
            }
            boolean isRepairable = !repairItem.isEmpty() && item.getIsRepairable(stack, repairItem);
            boolean materiaAllowed = item.isRepairable();
            if(isRepairable || materiaAllowed)
            {
                ArrayList<ItemStack> repairMaterials = new ArrayList<>();
                repairMaterials.add(repairItem.copy());
                if(materiaAllowed)
                    repairMaterials.add(new ItemStack(RegistryManager.isolated_materia));
                repairMaterials.removeIf(repairStack -> CraftingRegistry.isDawnstoneAnvilRecipeBlacklisted(stack,repairStack));
                if(!repairMaterials.isEmpty()) {
                    ItemStack[] repairMaterialsArray = repairMaterials.toArray(new ItemStack[repairMaterials.size()]);
                    repairRecipes.add(new RecipeDawnstoneAnvil(new ItemStack[]{stack.copy()}, Ingredient.fromStacks(makeDamaged(stack)), Ingredient.fromStacks(repairMaterialsArray)));
                }
                if(Misc.getResourceCount(stack) != -1 && !CraftingRegistry.isDawnstoneAnvilRecipeBlacklisted(stack,ItemStack.EMPTY)) {
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
