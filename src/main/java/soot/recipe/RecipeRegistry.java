package soot.recipe;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import soot.Registry;
import soot.util.AspectList;
import soot.util.AspectList.AspectRangeList;
import teamroots.embers.ConfigManager;
import teamroots.embers.RegistryManager;
import teamroots.embers.item.EnumStampType;
import teamroots.embers.recipe.FluidMixingRecipe;
import teamroots.embers.recipe.ItemMeltingOreRecipe;
import teamroots.embers.recipe.ItemMeltingRecipe;
import teamroots.embers.recipe.ItemStampingRecipe;

import java.util.ArrayList;
import java.util.List;

public class RecipeRegistry {
    public static ArrayList<RecipeDawnstoneAnvil> dawnstoneAnvilRecipes = new ArrayList<>();
    public static ArrayList<RecipeAlchemicalMixer> alchemicalMixingRecipes = new ArrayList<>();

    public static void preInit()
    {
        MinecraftForge.EVENT_BUS.register(RecipeRegistry.class);
    }

    @SubscribeEvent
    public static void registerRecipes(RegistryEvent.Register<IRecipe> event)
    {
        teamroots.embers.recipe.RecipeRegistry.meltingRecipes.add(new ItemMeltingRecipe(new ItemStack(Items.SUGAR),new FluidStack(Registry.MOLTEN_SUGAR,100),false,false));

        ArrayList<Fluid> leveledMetals = new ArrayList<>();
        leveledMetals.add(RegistryManager.fluid_molten_lead);
        if(ConfigManager.enableTin) //Tin sometimes doesn't exist.
            leveledMetals.add(RegistryManager.fluid_molten_tin);
        leveledMetals.add(RegistryManager.fluid_molten_iron);
        leveledMetals.add(RegistryManager.fluid_molten_copper);
        leveledMetals.add(RegistryManager.fluid_molten_silver);
        leveledMetals.add(RegistryManager.fluid_molten_gold);
        //Nickel and Aluminium are mundane materials

        for(int i = 0; i < leveledMetals.size()-1; i++)
        {
            int e = i+1;
            FluidStack currentLevel = new FluidStack(leveledMetals.get(i),4); //TODO: Adjust this and add alchemical slurry as an input (redstone is either arsenic, copper or mercury)
            FluidStack nextLevel = new FluidStack(leveledMetals.get(e),4);
            AspectRangeList aspectRange = new AspectRangeList(AspectList.createStandard(0, 0, 0, 0, (e*e) * 4), AspectList.createStandard(0, 0, 0, 0, (e*e) * 8)); //Recipe gets harder the higher level it is
            alchemicalMixingRecipes.add(new RecipeAlchemicalMixer(new FluidStack[]{currentLevel}, nextLevel, aspectRange));
        }

        alchemicalMixingRecipes.add(new RecipeAlchemicalMixer(new FluidStack[]{new FluidStack(RegistryManager.fluid_molten_lead,8),new FluidStack(Registry.MOLTEN_SUGAR,4)}, new FluidStack(Registry.MOLTEN_ANTIMONY,12), new AspectRangeList(AspectList.createStandard(0, 16, 0, 16, 0), AspectList.createStandard(0, 32, 0, 24, 0))));
        teamroots.embers.recipe.RecipeRegistry.stampingRecipes.add(new ItemStampingRecipe(new ItemStack(RegistryManager.shard_ember),new FluidStack(Registry.MOLTEN_ANTIMONY,144), EnumStampType.TYPE_BAR, new ItemStack(Registry.SIGNET_ANTIMONY), false, false));
    }

    public static RecipeAlchemicalMixer getAlchemicalMixingRecipe(ArrayList<FluidStack> fluids)
    {
        for (RecipeAlchemicalMixer recipe: alchemicalMixingRecipes) {
            if(recipe.matches(fluids))
                return recipe;
        }

        return null;
    }

    public static RecipeDawnstoneAnvil getDawnstoneAnvilRecipe(ItemStack bottom, ItemStack top)
    {
        for (RecipeDawnstoneAnvil recipe: dawnstoneAnvilRecipes) {
            if(recipe.matches(bottom,top))
                return recipe;
        }

        return null;
    }
}
