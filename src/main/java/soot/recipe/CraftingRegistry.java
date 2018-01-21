package soot.recipe;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import soot.Registry;
import soot.util.AspectList;
import soot.util.AspectList.AspectRangeList;
import teamroots.embers.ConfigManager;
import teamroots.embers.RegistryManager;
import teamroots.embers.item.EnumStampType;
import teamroots.embers.recipe.AlchemyRecipe;
import teamroots.embers.recipe.ItemMeltingRecipe;
import teamroots.embers.recipe.ItemStampingRecipe;
import teamroots.embers.recipe.RecipeRegistry;

import java.util.ArrayList;

public class CraftingRegistry {
    public static ArrayList<RecipeDawnstoneAnvil> dawnstoneAnvilRecipes = new ArrayList<>();
    public static ArrayList<RecipeAlchemicalMixer> alchemicalMixingRecipes = new ArrayList<>();

    public static void preInit()
    {
        MinecraftForge.EVENT_BUS.register(CraftingRegistry.class);
    }

    @SubscribeEvent
    public static void registerRecipes(RegistryEvent.Register<IRecipe> event)
    {
        RecipeRegistry.alchemyRecipes.add(new AlchemyRecipe(0,0,16,32,0,0,0,0,32,64,new ItemStack(Blocks.GLASS),new ItemStack(RegistryManager.ingot_lead),new ItemStack(RegistryManager.aspectus_lead),new ItemStack(RegistryManager.ingot_lead),new ItemStack(RegistryManager.archaic_circuit),new ItemStack(Registry.ALCHEMY_GLOBE)));
        RecipeRegistry.meltingRecipes.add(new ItemMeltingRecipe(new ItemStack(Items.SUGAR),FluidRegistry.getFluidStack("sugar",16),false,false)); //Nugget size -> you can combine sugar and lead into antimony without remainder and 1000 sugar store nicely in a fluid vessel

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

        alchemicalMixingRecipes.add(new RecipeAlchemicalMixer(new FluidStack[]{new FluidStack(RegistryManager.fluid_molten_lead,8),FluidRegistry.getFluidStack("sugar",4)}, FluidRegistry.getFluidStack("antimony",12), new AspectRangeList(AspectList.createStandard(0, 16, 0, 16, 0), AspectList.createStandard(0, 32, 0, 24, 0))));
        RecipeRegistry.stampingRecipes.add(new ItemStampingRecipe(new ItemStack(RegistryManager.shard_ember),FluidRegistry.getFluidStack("antimony",144), EnumStampType.TYPE_BAR, new ItemStack(Registry.SIGNET_ANTIMONY), false, false));
        RecipeRegistry.stampingRecipes.add(new ItemStampingRecipe(ItemStack.EMPTY, FluidRegistry.getFluidStack("antimony",144), EnumStampType.TYPE_BAR, new ItemStack(Registry.INGOT_ANTIMONY), false, false));
        RecipeRegistry.meltingRecipes.add(new ItemMeltingRecipe(new ItemStack(Registry.INGOT_ANTIMONY),FluidRegistry.getFluidStack("antimony",144),false,false));
    }

    public static RecipeAlchemicalMixer getAlchemicalMixingRecipe(ArrayList<FluidStack> fluids)
    {
        RecipeAlchemicalMixer matchedRecipe = null;

        for (RecipeAlchemicalMixer recipe: alchemicalMixingRecipes) {
            if(recipe.matches(fluids) && (matchedRecipe == null || recipe.inputs.size() > matchedRecipe.inputs.size()))
                matchedRecipe = recipe;
        }

        return matchedRecipe;
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
