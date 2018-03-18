package soot.recipe;

import com.google.common.collect.Lists;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.OreIngredient;
import soot.Config;
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
import java.util.List;

public class CraftingRegistry {
    public static ArrayList<RecipeAlchemyTablet> alchemyTabletRecipes = new ArrayList<>();
    public static ArrayList<RecipeDawnstoneAnvil> dawnstoneAnvilRecipes = new ArrayList<>();
    public static ArrayList<RecipeAlchemicalMixer> alchemicalMixingRecipes = new ArrayList<>();
    public static ArrayList<RecipeHeatCoil> heatCoilRecipes = new ArrayList<>();

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
            FluidStack currentLevel = new FluidStack(leveledMetals.get(i),4);
            FluidStack nextLevel = new FluidStack(leveledMetals.get(e),4);
            AspectRangeList aspectRange = new AspectRangeList(AspectList.createStandard(0, 0, 0, 0, (e*e) * 4), AspectList.createStandard(0, 0, 0, 0, (e*e) * 8)); //Recipe gets harder the higher level it is
            alchemicalMixingRecipes.add(new RecipeAlchemicalMixer(new FluidStack[]{currentLevel, FluidRegistry.getFluidStack("alchemical_redstone",3)}, nextLevel, aspectRange));
        }

        alchemicalMixingRecipes.add(new RecipeAlchemicalMixer(new FluidStack[]{new FluidStack(RegistryManager.fluid_molten_lead,8),FluidRegistry.getFluidStack("sugar",4)}, FluidRegistry.getFluidStack("antimony",12), new AspectRangeList(AspectList.createStandard(0, 16, 0, 16, 0), AspectList.createStandard(0, 32, 0, 24, 0))));
        RecipeRegistry.stampingRecipes.add(new ItemStampingRecipe(new ItemStack(RegistryManager.shard_ember),FluidRegistry.getFluidStack("antimony",144), EnumStampType.TYPE_BAR, new ItemStack(Registry.SIGNET_ANTIMONY), false, false));
        RecipeRegistry.stampingRecipes.add(new ItemStampingRecipe(ItemStack.EMPTY, FluidRegistry.getFluidStack("antimony",144), EnumStampType.TYPE_BAR, new ItemStack(Registry.INGOT_ANTIMONY), false, false));
        RecipeRegistry.meltingRecipes.add(new ItemMeltingRecipe(new ItemStack(Registry.INGOT_ANTIMONY),FluidRegistry.getFluidStack("antimony",144),false,false));
        RecipeRegistry.meltingRecipes.add(new ItemMeltingRecipe(new ItemStack(Items.REDSTONE),FluidRegistry.getFluidStack("alchemical_redstone",144),false,false));

        OreDictionary.registerOre("ingotAntimony",new ItemStack(Registry.INGOT_ANTIMONY));

        migrateAlchemyRecipes();

        //Catch-All Furnace recipe
        if(Config.HEARTHCOIL_SMELTING && Config.OVERRIDE_ALCHEMY_TABLET) {
            heatCoilRecipes.add(new RecipeHeatCoil() {
                @Override
                public boolean matches(ItemStack stack) {
                    return !FurnaceRecipes.instance().getSmeltingResult(stack).isEmpty();
                }

                @Override
                public ItemStack getResult(World world, TileEntity tile, ItemStack stack) {
                    return FurnaceRecipes.instance().getSmeltingResult(stack);
                }
            });
        }
    }

    private static void migrateAlchemyRecipes()
    {
        Ingredient copper = new OreIngredient("ingotCopper");
        Ingredient emberShard = Ingredient.fromItem(RegistryManager.shard_ember);
        Ingredient quartz = new OreIngredient("gemQuartz");
        addAlchemyTabletRecipe(new ItemStack(RegistryManager.seed, 1, 2), quartz, copper, copper, emberShard, emberShard, new AspectRangeList(AspectList.createStandard(48,0,48,0,0),AspectList.createStandard(64,0,64,0,0)));
        Ingredient silver = new OreIngredient("ingotSilver");
        addAlchemyTabletRecipe(new ItemStack(RegistryManager.seed, 1, 4), quartz, silver, silver, emberShard, emberShard, new AspectRangeList(AspectList.createStandard(48,0,0,48,0),AspectList.createStandard(64,0,0,64,0)));
        Ingredient lead = new OreIngredient("ingotLead");
        addAlchemyTabletRecipe(new ItemStack(RegistryManager.seed, 1, 3), quartz, lead, lead, emberShard, emberShard, new AspectRangeList(AspectList.createStandard(48,0,0,0,48),AspectList.createStandard(64,0,0,0,64)));

        Ingredient gold = new OreIngredient("ingotGold");
        addAlchemyTabletRecipe(new ItemStack(RegistryManager.seed, 1, 1), quartz, gold, gold, emberShard, emberShard, new AspectRangeList(AspectList.createStandard(48,48,0,0,0),AspectList.createStandard(64,64,0,0,0)));

        Ingredient iron = new OreIngredient("ingotIron");
        addAlchemyTabletRecipe(new ItemStack(RegistryManager.seed, 1, 0), quartz, iron, iron, emberShard, emberShard, new AspectRangeList(AspectList.createStandard(96,0,0,0,0),AspectList.createStandard(128,0,0,0,0)));

        Ingredient ash = new OreIngredient("dustAsh");
        Ingredient string = new OreIngredient("string");
        Ingredient wool = Ingredient.fromStacks(new ItemStack(Blocks.WOOL,1,OreDictionary.WILDCARD_VALUE));
        addAlchemyTabletRecipe(new ItemStack(RegistryManager.ashen_cloth, 2), wool, ash, ash, string, string, new AspectRangeList(AspectList.createStandard(12,0,0,0,12),AspectList.createStandard(24,0,0,0,24)));
        Ingredient dawnstone = new OreIngredient("ingotDawnstone");
        Ingredient coal = Ingredient.fromItem(Items.COAL);
        Ingredient diamond = new OreIngredient("gemDiamond");
        addAlchemyTabletRecipe(new ItemStack(RegistryManager.inflictor_gem, 1), diamond, dawnstone, coal, coal, coal, new AspectRangeList(AspectList.createStandard(0,32,0,0,24),AspectList.createStandard(0,48,0,0,40)));

        Ingredient gunpowder = new OreIngredient("gunpowder");
        addAlchemyTabletRecipe(new ItemStack(RegistryManager.glimmer_shard, 1), quartz, gunpowder, gunpowder, emberShard, emberShard, new AspectRangeList(AspectList.createStandard(0,64,0,0,0),AspectList.createStandard(0,80,0,0,0)));

        Ingredient clay = Ingredient.fromItem(Items.CLAY_BALL);
        Ingredient lapis = new OreIngredient("gemLapis");
        Ingredient empty = Ingredient.EMPTY;
        addAlchemyTabletRecipe(new ItemStack(RegistryManager.isolated_materia, 4), iron, quartz, clay, lapis, empty, new AspectRangeList(AspectList.createStandard(24,0,0,0,0),AspectList.createStandard(36,0,0,0,0)));

        Ingredient bonemeal = Ingredient.fromStacks(new ItemStack(Items.DYE, 1, 15));
        addAlchemyTabletRecipe(new ItemStack(RegistryManager.adhesive, 6), clay, bonemeal, bonemeal, empty, empty, new AspectRangeList(AspectList.createStandard(12,0,0,0,0),AspectList.createStandard(18,0,0,0,0)));

        Ingredient redstone = new OreIngredient("dustRedstone");
        Ingredient cobblestone = new OreIngredient("cobblestone");
        addAlchemyTabletRecipe(new ItemStack(Blocks.NETHERRACK, 2), redstone, ash, ash, cobblestone, cobblestone, new AspectRangeList(AspectList.createStandard(0,0,0,0,8),AspectList.createStandard(0,0,0,0,16)));

        Ingredient sand = new OreIngredient("sand");
        addAlchemyTabletRecipe(new ItemStack(Blocks.SOUL_SAND, 4), ash, sand, sand, sand, sand, new AspectRangeList(AspectList.createStandard(0,0,8,0,0),AspectList.createStandard(0,0,16,0,0)));

        Ingredient leadSword = Ingredient.fromItem(RegistryManager.sword_lead);
        Ingredient obsidian = new OreIngredient("obsidian");
        Ingredient coalBlock = new OreIngredient("blockCoal");
        addAlchemyTabletRecipe(new ItemStack(RegistryManager.tyrfing, 1), leadSword, coalBlock, obsidian, lead, lead, new AspectRangeList(AspectList.createStandard(0,0,0,64,64),AspectList.createStandard(0,0,0,96,96)));
        Ingredient emberCrystal = Ingredient.fromItem(RegistryManager.crystal_ember);
        addAlchemyTabletRecipe( new ItemStack(RegistryManager.ember_cluster, 1),  emberCrystal, gunpowder, emberShard, emberShard, emberShard, new AspectRangeList(AspectList.createStandard(0,24,24,0,0),AspectList.createStandard(0,48,48,0,0)));
        Ingredient motiveCore = Ingredient.fromItem(RegistryManager.ancient_motive_core);
        Ingredient emberCluster = Ingredient.fromItem(RegistryManager.ember_cluster);
        Ingredient copperPlate = new OreIngredient("plateCopper");
        addAlchemyTabletRecipe(new ItemStack(RegistryManager.wildfire_core, 1), motiveCore, dawnstone, emberCluster, dawnstone, copperPlate, new AspectRangeList(AspectList.createStandard(32,0,0,24,0),AspectList.createStandard(48,0,0,32,0)));
        Ingredient archaicBrick = Ingredient.fromItem(RegistryManager.archaic_brick);
        Ingredient soulSand = Ingredient.fromStacks(new ItemStack(Blocks.SOUL_SAND,1,OreDictionary.WILDCARD_VALUE));
        addAlchemyTabletRecipe(new ItemStack(RegistryManager.archaic_brick, 5), archaicBrick, soulSand, soulSand, clay, clay, new AspectRangeList(AspectList.createStandard(0,4,0,0,0),AspectList.createStandard(0,8,0,0,0)));
        addAlchemyTabletRecipe(new ItemStack(RegistryManager.ancient_motive_core), emberShard, archaicBrick, archaicBrick, archaicBrick, archaicBrick, new AspectRangeList(AspectList.createStandard(0,24,0,0,0),AspectList.createStandard(0,32,0,0,0)));
        Ingredient ironPlate = new OreIngredient("plateIron");

        addAlchemyTabletRecipe(new ItemStack(RegistryManager.blasting_core, 1), gunpowder, ironPlate, ironPlate, ironPlate, copper, new AspectRangeList(AspectList.createStandard(0,0,16,0,0),AspectList.createStandard(0,0,24,0,0)));
        Ingredient archaicCircuit = Ingredient.fromItem(RegistryManager.archaic_circuit);
        addAlchemyTabletRecipe(new ItemStack(RegistryManager.eldritch_insignia, 1), archaicCircuit, archaicBrick, coal, archaicBrick, coal, new AspectRangeList(AspectList.createStandard(0,16,0,0,48),AspectList.createStandard(0,32,0,0,72)));

        addAlchemyTabletRecipe(new ItemStack(RegistryManager.intelligent_apparatus, 1), copperPlate, archaicCircuit, copper, archaicCircuit, copper, new AspectRangeList(AspectList.createStandard(0,0,24,0,40),AspectList.createStandard(0,0,48,0,64)));
        Ingredient dawnstonePlate = new OreIngredient("plateDawnstone");
        addAlchemyTabletRecipe(new ItemStack(RegistryManager.flame_barrier, 1) , emberCrystal, dawnstonePlate, dawnstonePlate, dawnstonePlate, silver, new AspectRangeList(AspectList.createStandard(0,16,0,16,0),AspectList.createStandard(0,32,0,32,0)));

        RecipeRegistry.alchemyRecipes.clear(); //Danny Deleto
    }

    public static void addAlchemyTabletRecipe(ItemStack output, Ingredient center, Ingredient east, Ingredient west, Ingredient north, Ingredient south, AspectRangeList aspects)
    {
        alchemyTabletRecipes.add(new RecipeAlchemyTablet(output, center, Lists.newArrayList(east,west,north,south), aspects));
    }

    public static RecipeAlchemyTablet getAlchemyTabletRecipe(ItemStack center, List<ItemStack> outside)
    {
        RecipeAlchemyTablet matchedRecipe = null;

        for (RecipeAlchemyTablet recipe: alchemyTabletRecipes) {
            if(recipe.matches(center,outside) && (matchedRecipe == null || recipe.inputs.size() > matchedRecipe.inputs.size()))
                matchedRecipe = recipe;
        }

        return matchedRecipe;
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

    public static RecipeHeatCoil getHeatCoilRecipe(ItemStack input)
    {
        RecipeHeatCoil matchedRecipe = null;

        for (RecipeHeatCoil recipe : heatCoilRecipes) {
            if(recipe.matches(input))
                matchedRecipe = recipe;
        }

        return matchedRecipe;
    }
}
