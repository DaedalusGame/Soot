package soot.recipe;

import com.google.common.collect.Lists;
import mezz.jei.util.Translator;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.TextFormatting;
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
import soot.tile.TileEntityStillBase;
import soot.util.*;
import soot.util.AspectList.AspectRangeList;
import teamroots.embers.ConfigManager;
import teamroots.embers.RegistryManager;
import teamroots.embers.item.EnumStampType;
import teamroots.embers.recipe.*;

import java.util.ArrayList;
import java.util.List;

public class CraftingRegistry {
    public static ArrayList<RecipeAlchemyTablet> alchemyTabletRecipes = new ArrayList<>();
    public static ArrayList<RecipeDawnstoneAnvil> dawnstoneAnvilRecipes = new ArrayList<>();
    public static ArrayList<RecipeAlchemicalMixer> alchemicalMixingRecipes = new ArrayList<>();
    public static ArrayList<RecipeHeatCoil> heatCoilRecipes = new ArrayList<>();
    public static ArrayList<RecipeStill> stillRecipes = new ArrayList<>();
    public static ArrayList<CatalystInfo> stillCatalysts = new ArrayList<>();

    public static void preInit()
    {
        MinecraftForge.EVENT_BUS.register(CraftingRegistry.class);
    }

    @SubscribeEvent
    public static void registerRecipes(RegistryEvent.Register<IRecipe> event)
    {
        OreIngredient ingotLead = new OreIngredient("ingotLead");
        addAlchemyTabletRecipe(new ItemStack(Registry.ALCHEMY_GLOBE),new OreIngredient("blockGlassColorless"), ingotLead,Ingredient.fromItem(RegistryManager.aspectus_lead), ingotLead,Ingredient.fromItem(RegistryManager.archaic_circuit),new AspectRangeList(AspectList.createStandard(0,0,16,0,32),AspectList.createStandard(0,0,32,0,64)));
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

        initAlcoholRecipes();

        //Catch-All Furnace recipe
        if(Config.HEARTHCOIL_SMELTING && Config.OVERRIDE_HEARTH_COIL) {
            heatCoilRecipes.add(new RecipeHeatCoil() {
                @Override
                public boolean matches(ItemStack stack) {
                    return !FurnaceRecipes.instance().getSmeltingResult(stack).isEmpty();
                }

                @Override
                public ItemStack getResult(World world, TileEntity tile, ItemStack stack) {
                    return FurnaceRecipes.instance().getSmeltingResult(stack).copy();
                }
            });
        }
    }

    private static void initAlcoholRecipes()
    {
        FluidUtil.registerModifier(new FluidModifier("viscosity",1000));
        FluidUtil.registerModifier(new FluidModifier("light",0));
        FluidUtil.registerModifier(new FluidModifier("health",0){
            @Override
            public void applyEffect(EntityLivingBase target, NBTTagCompound compound, Fluid fluid) {
                float value = getOrDefault(compound, fluid);
                if(value > 0)
                    target.heal(value);
                else if(value < 0)
                    MiscUtil.damageWithoutInvulnerability(target,new DamageSource("acid"),value);
            }
        });
        FluidUtil.registerModifier(new FluidModifier("hunger",0){
            @Override
            public void applyEffect(EntityLivingBase target, NBTTagCompound compound, Fluid fluid) {
                if(target instanceof EntityPlayer) {
                    int hunger = (int) compound.getFloat("hunger");
                    float saturation = compound.getFloat("saturation");
                    ((EntityPlayer) target).getFoodStats().addStats(hunger,saturation);
                }
            }
        });
        FluidUtil.registerModifier(new FluidModifier("saturation",0));
        FluidUtil.registerModifier(new FluidModifier("toxicity",0) {
            @Override
            public void applyEffect(EntityLivingBase target, NBTTagCompound compound, Fluid fluid) {
                float value = getOrDefault(compound, fluid);
                if(value > 0)
                    target.addPotionEffect(new PotionEffect(MobEffects.NAUSEA,(int)value * 4));
                if(value >= 50)
                    target.addPotionEffect(new PotionEffect(MobEffects.BLINDNESS,(int)value * 2));
                if(value >= 100)
                    target.addPotionEffect(new PotionEffect(MobEffects.POISON,(int)value - 50));
                if(value >= 200)
                    target.addPotionEffect(new PotionEffect(MobEffects.WITHER,(int)(value - 150)));
            }
        });
        FluidUtil.registerModifier(new FluidModifier("heat",300) {
            @Override
            public void applyEffect(EntityLivingBase target, NBTTagCompound compound, Fluid fluid) {
                float value = getOrDefault(compound, fluid);
                if(value > 400) //Scalding
                {
                    MiscUtil.damageWithoutInvulnerability(target,new DamageSource("scalding"),2.0f);
                    //TODO: Message
                }
                if(value > 500) //Burning hot
                    target.setFire((int)((value - 500) / 10));
            }
        });
        FluidUtil.registerModifier(new FluidModifier("lifedrinker",0) {
            @Override
            public void applyEffect(EntityLivingBase target, NBTTagCompound compound, Fluid fluid) {
                float value = getOrDefault(compound, fluid);
                target.addPotionEffect(new PotionEffect(Registry.POTION_LIFEDRINKER,(int)value));
            }
        });
        FluidUtil.registerModifier(new FluidModifier("concentration",0));
        FluidUtil.registerModifier(new FluidModifier("duration",1));

        Fluid boiling_wort = FluidRegistry.getFluid("boiling_wort");
        Fluid boiling_potato_juice = FluidRegistry.getFluid("boiling_potato_juice");

        Fluid boiling_beetroot_soup = FluidRegistry.getFluid("boiling_beetroot_soup");
        FluidUtil.setDefaultValue(boiling_beetroot_soup,"hunger",6);
        FluidUtil.setDefaultValue(boiling_beetroot_soup,"saturation",0.6F);

        Fluid boiling_verdigris = FluidRegistry.getFluid("boiling_wormwood");
        FluidUtil.setDefaultValue(boiling_verdigris,"toxicity",100);

        Fluid ale = FluidRegistry.getFluid("ale");

        Fluid inner_fire = FluidRegistry.getFluid("inner_fire");
        FluidUtil.setDefaultValue(inner_fire,"heat",600);

        Fluid umber_ale = FluidRegistry.getFluid("umber_ale");
        Fluid vodka = FluidRegistry.getFluid("vodka");

        Fluid snowpoff = FluidRegistry.getFluid("snowpoff");
        FluidUtil.setDefaultValue(snowpoff,"heat",200);
        Fluid absinthe = FluidRegistry.getFluid("absinthe");
        FluidUtil.setDefaultValue(absinthe,"toxicity",50);

        Fluid methanol = FluidRegistry.getFluid("methanol");
        FluidUtil.setDefaultValue(methanol,"toxicity",10);

        RecipeRegistry.meltingOreRecipes.add(new ItemMeltingOreRecipe("cropWheat",new FluidStack(boiling_wort,100)));
        RecipeRegistry.meltingOreRecipes.add(new ItemMeltingOreRecipe("cropPotato",new FluidStack(boiling_potato_juice,50)));
        RecipeRegistry.meltingRecipes.add(new ItemMeltingRecipe(new ItemStack(Items.BEETROOT),new FluidStack(boiling_beetroot_soup,50),false,false));
        RecipeRegistry.mixingRecipes.add(new FluidMixingRecipe(new FluidStack[]{new FluidStack(ale,4),FluidRegistry.getFluidStack("lava",1)},new FluidStack(inner_fire,4)));
        //TODO: Umber Ale requires clockwork arcana

        stillRecipes.add(new RecipeStill(new FluidStack(boiling_wort,1),Ingredient.EMPTY,0,new FluidStack(ale,1)));
        stillRecipes.add(new RecipeStill(new FluidStack(boiling_potato_juice,3),Ingredient.EMPTY,0,new FluidStack(vodka,2)));
        //stillRecipes.add(new RecipeStill(new FluidStack(vodka,1),Ingredient.fromItem(Items.SNOWBALL),1,new FluidStack(snowpoff,1)));
        stillRecipes.add(new RecipeStill(null,new OreIngredient("logWood"),1,new FluidStack(methanol,1)));

        ArrayList<Fluid> allSoups = new ArrayList<>();
        allSoups.add(boiling_beetroot_soup);
        ArrayList<Fluid> allAlcohols = new ArrayList<>();
        allAlcohols.add(ale);
        allAlcohols.add(vodka);
        allAlcohols.add(inner_fire);
        allAlcohols.add(umber_ale);
        allAlcohols.add(methanol);
        allAlcohols.add(absinthe);
        allAlcohols.add(snowpoff);
        ArrayList<Fluid> allDrinks = new ArrayList<>(allAlcohols);
        allDrinks.addAll(allSoups);
        allDrinks.add(boiling_verdigris);

        stillCatalysts.add(new CatalystInfo(new OreIngredient("sugar"),100));
        stillCatalysts.add(new CatalystInfo(Ingredient.fromItem(Items.SNOWBALL),250));
        stillCatalysts.add(new CatalystInfo(new OreIngredient("logWood"),750));

        stillRecipes.add(new RecipeStillModifier(allAlcohols, Ingredient.EMPTY, 0) {
            @Override
            public void modifyOutput(World world, TileEntityStillBase tile, FluidStack output) {
                NBTTagCompound compound = FluidUtil.createModifiers(output);
                output.amount = 2;
                float concentration = getModifierOrDefault("concentration",compound,output);
                if(concentration < 120)
                    compound.setFloat("concentration",Math.min((concentration + 10)*1.8f,120));
            }

            @Override
            public void modifyTooltip(List<String> tooltip) {
                super.modifyTooltip(tooltip);
                tooltip.add(tooltip.size()-1,TextFormatting.BLUE+ Translator.translateToLocalFormatted("distilling.effect.add",Translator.translateToLocal("distilling.modifier.concentration.name"),10));
                tooltip.add(tooltip.size()-1,TextFormatting.BLUE+ Translator.translateToLocalFormatted("distilling.effect.add_percent",Translator.translateToLocal("distilling.modifier.concentration.name"),80));
                tooltip.add(tooltip.size()-1,TextFormatting.RED+ Translator.translateToLocalFormatted("distilling.effect.loss",33));
            }
        });
        stillRecipes.add(new RecipeStillModifier(allAlcohols, Ingredient.fromItem(Items.GHAST_TEAR), 1) {
            @Override
            public void modifyOutput(World world, TileEntityStillBase tile, FluidStack output) {
                NBTTagCompound compound = FluidUtil.createModifiers(output);
                float lifedrinker = getModifierOrDefault("lifedrinker",compound,output);
                float toxicity = getModifierOrDefault("toxicity",compound,output);
                compound.setFloat("toxicity",toxicity + 10);
                if(lifedrinker < 18000)
                    compound.setFloat("lifedrinker",Math.min((lifedrinker + 600) * 1.6f,18000));
            }
            @Override
            public void modifyTooltip(List<String> tooltip) {
                super.modifyTooltip(tooltip);
                tooltip.add(tooltip.size()-1,TextFormatting.GREEN+ Translator.translateToLocal("distilling.effect.lifedrinker"));
                tooltip.add(tooltip.size()-1,TextFormatting.RED+ Translator.translateToLocalFormatted("distilling.effect.add",Translator.translateToLocal("distilling.modifier.toxicity.name"),10));
            }
        });
        stillRecipes.add(new RecipeStillModifier(allAlcohols, new OreIngredient("dustRedstone"), 1) {
            @Override
            public void modifyOutput(World world, TileEntityStillBase tile, FluidStack output) {
                NBTTagCompound compound = FluidUtil.createModifiers(output);
                float duration = getModifierOrDefault("duration",compound,output);
                float toxicity = getModifierOrDefault("toxicity",compound,output);
                compound.setFloat("toxicity",toxicity + 5);
                if(duration < 2.5f)
                    compound.setFloat("duration",Math.min(duration + 0.5f,2.5f));
            }
            @Override
            public void modifyTooltip(List<String> tooltip) {
                super.modifyTooltip(tooltip);
                tooltip.add(tooltip.size()-1,TextFormatting.BLUE+ Translator.translateToLocalFormatted("distilling.effect.add_percent",Translator.translateToLocal("distilling.modifier.duration.name"),50));
                tooltip.add(tooltip.size()-1,TextFormatting.RED+ Translator.translateToLocalFormatted("distilling.effect.add",Translator.translateToLocal("distilling.modifier.toxicity.name"),5));
            }
        });
        /*stillRecipes.add(new RecipeStillModifier(allDrinks, new OreIngredient("sugar"), 1) {
            @Override
            public void modifyOutput(World world, TileEntityStillBase tile, FluidStack output) {
                NBTTagCompound compound = FluidUtil.createModifiers(output);
                float sweetness = getModifierOrDefault("sweetness",compound,output);
                if(sweetness < 80)
                    compound.setFloat("sweetness",Math.min(sweetness+15,80));
            }
        });*/ //TODO: Use for sweetness
        stillRecipes.add(new RecipeStillModifier(allDrinks, new OreIngredient("dustPrismarine"), 1) {
            @Override
            public void modifyOutput(World world, TileEntityStillBase tile, FluidStack output) {
                NBTTagCompound compound = FluidUtil.createModifiers(output);
                float toxicity = getModifierOrDefault("toxicity",compound,output);
                if(toxicity > 0)
                    compound.setFloat("toxicity",Math.max(toxicity * 0.8f - 20,0));
            }
            @Override
            public void modifyTooltip(List<String> tooltip) {
                super.modifyTooltip(tooltip);
                tooltip.add(tooltip.size()-1,TextFormatting.BLUE+Translator.translateToLocalFormatted("distilling.effect.sub_percent",Translator.translateToLocal("distilling.modifier.toxicity.name"),20));
                tooltip.add(tooltip.size()-1,TextFormatting.BLUE+Translator.translateToLocalFormatted("distilling.effect.sub",Translator.translateToLocal("distilling.modifier.toxicity.name"),20));
            }
        });
        stillRecipes.add(new RecipeStillModifier(allDrinks, new OreIngredient("cropNetherWart"), 1) {
            @Override
            public void modifyOutput(World world, TileEntityStillBase tile, FluidStack output) {
                NBTTagCompound compound = FluidUtil.createModifiers(output);
                float health = getModifierOrDefault("health",compound,output);
                float hunger = getModifierOrDefault("hunger",compound,output);
                compound.setFloat("health",health + 4);
                compound.setFloat("hunger",hunger - 3);
            }
            @Override
            public void modifyTooltip(List<String> tooltip) {
                super.modifyTooltip(tooltip);
                tooltip.add(tooltip.size()-1,TextFormatting.BLUE+Translator.translateToLocalFormatted("distilling.effect.add",Translator.translateToLocal("distilling.modifier.health.name"),4));
                tooltip.add(tooltip.size()-1,TextFormatting.BLUE+Translator.translateToLocalFormatted("distilling.effect.sub",Translator.translateToLocal("distilling.modifier.hunger.name"),3));
            }
        });
        stillRecipes.add(new RecipeStillModifier(allDrinks, Ingredient.fromStacks(new ItemStack(Blocks.ICE)), 1) {
            @Override
            public void modifyOutput(World world, TileEntityStillBase tile, FluidStack output) {
                NBTTagCompound compound = FluidUtil.createModifiers(output);
                float heat = getModifierOrDefault("heat",compound,output);
                if(heat > 200)
                    compound.setFloat("heat",Math.max(heat * 0.5f,200));
            }
            @Override
            public void modifyTooltip(List<String> tooltip) {
                super.modifyTooltip(tooltip);
                tooltip.add(tooltip.size()-1,TextFormatting.GREEN+Translator.translateToLocalFormatted("distilling.effect.sub_percent",Translator.translateToLocal("distilling.modifier.heat.name"),50));
            }
        });
        stillRecipes.add(new RecipeStillModifierFood(allSoups, new OreIngredient("cropPotato"), 1, 2, 0.3f));
        stillRecipes.add(new RecipeStillModifierFood(allSoups, new OreIngredient("cropCarrot"), 1, 1, 0.2f));
        stillRecipes.add(new RecipeStillModifierFood(allSoups, new OreIngredient("cropWheat"), 1, 4, 0.6f) {
            @Override
            public void modifyOutput(World world, TileEntityStillBase tile, FluidStack output) {
                NBTTagCompound compound = FluidUtil.createModifiers(output);
                float viscosity = getModifierOrDefault("viscosity",compound,output);
                compound.setFloat("viscosity",viscosity+1000);
            }
            @Override
            public void modifyTooltip(List<String> tooltip) {
                super.modifyTooltip(tooltip);
                tooltip.add(tooltip.size()-1,TextFormatting.GREEN+Translator.translateToLocal("distilling.effect.thick_soup"));
            }
        });
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

        Ingredient leadSword = new IngredientMaterialTool("sword","lead");
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

    public static RecipeStill getStillRecipe(FluidStack stack, ItemStack catalyst)
    {
        RecipeStill matchedRecipe = null;

        for (RecipeStill recipe : stillRecipes) {
            if(recipe.matches(stack,catalyst) && (matchedRecipe == null || matchedRecipe.input == null || matchedRecipe.catalystInput.apply(ItemStack.EMPTY)))
                matchedRecipe = recipe;
        }

        return matchedRecipe;
    }

    public static int getStillCatalyst(ItemStack stack) {
        if(stack.isEmpty()) return 0;
        CatalystInfo matchedCatalyst = stillCatalysts.stream().filter(catalyst -> catalyst.matches(stack)).findFirst().orElse(null);
        return matchedCatalyst == null ? 1000 : matchedCatalyst.getAmount(stack);
    }
}
