package soot.recipe;

import com.google.common.collect.Lists;
import mezz.jei.util.Translator;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.client.resources.I18n;
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
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.OreIngredient;
import net.minecraftforge.registries.ForgeRegistry;
import soot.Config;
import soot.Registry;
import soot.tile.TileEntityStillBase;
import soot.util.*;
import soot.util.AspectList.AspectRangeList;
import teamroots.embers.ConfigManager;
import teamroots.embers.Embers;
import teamroots.embers.RegistryManager;
import teamroots.embers.item.EnumStampType;
import teamroots.embers.recipe.*;

import java.util.*;
import java.util.stream.Collectors;

public class CraftingRegistry {
    public static HashSet<ResourceLocation> REMOVE_RECIPE_BY_RL = new HashSet<>();

    public static ArrayList<RecipeAlchemyTablet> alchemyTabletRecipes = new ArrayList<>();
    public static ArrayList<RecipeDawnstoneAnvil> dawnstoneAnvilRecipes = new ArrayList<>();
    public static ArrayList<RecipeAlchemicalMixer> alchemicalMixingRecipes = new ArrayList<>();
    public static ArrayList<RecipeStamper> stamperRecipes = new ArrayList<>();
    public static ArrayList<RecipeHeatCoil> heatCoilRecipes = new ArrayList<>();
    public static ArrayList<RecipeStill> stillRecipes = new ArrayList<>();
    public static ArrayList<CatalystInfo> stillCatalysts = new ArrayList<>();

    public static ArrayList<RecipeDawnstoneAnvil> dawnstoneAnvilBlacklist = new ArrayList<>();

    public static HashMap<ItemStack, Ingredient> convertIngredient = new HashMap<>();

    public static void preInit() {
        MinecraftForge.EVENT_BUS.register(CraftingRegistry.class);
    }

    public static void removeRecipe(ResourceLocation resloc) {
        REMOVE_RECIPE_BY_RL.add(resloc);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void removeRecipes(RegistryEvent.Register<IRecipe> event) {
        ForgeRegistry<IRecipe> reg = (ForgeRegistry<IRecipe>) event.getRegistry();
        for (IRecipe recipe : reg) {
            REMOVE_RECIPE_BY_RL.stream().filter(loc -> loc.equals(recipe.getRegistryName())).forEach(loc -> reg.remove(recipe.getRegistryName()));
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void migrateRecipes(RegistryEvent.Register<IRecipe> event) {
        if (Config.MIGRATE_STAMPER_RECIPES && Config.OVERRIDE_STAMPER)
            migrateStamperRecipes();
        if (Config.MIGRATE_ALCHEMY_RECIPES && Config.OVERRIDE_ALCHEMY_TABLET)
            migrateAlchemyRecipes();
    }

    @SubscribeEvent
    public static void registerRecipes(RegistryEvent.Register<IRecipe> event) {
        convertIngredient.put(new ItemStack(Blocks.COBBLESTONE), new OreIngredient("cobblestone"));
        convertIngredient.put(new ItemStack(Blocks.SAND), new OreIngredient("sand"));
        convertIngredient.put(new ItemStack(Blocks.OBSIDIAN), new OreIngredient("obsidian"));
        convertIngredient.put(new ItemStack(Blocks.COAL_BLOCK), new OreIngredient("blockCoal"));
        convertIngredient.put(new ItemStack(Items.IRON_INGOT), new OreIngredient("ingotIron"));
        convertIngredient.put(new ItemStack(Items.GOLD_INGOT), new OreIngredient("ingotGold"));
        convertIngredient.put(new ItemStack(RegistryManager.ingot_lead), new OreIngredient("ingotLead"));
        convertIngredient.put(new ItemStack(RegistryManager.ingot_copper), new OreIngredient("ingotCopper"));
        convertIngredient.put(new ItemStack(RegistryManager.ingot_silver), new OreIngredient("ingotSilver"));
        convertIngredient.put(new ItemStack(RegistryManager.ingot_dawnstone), new OreIngredient("ingotDawnstone"));
        convertIngredient.put(new ItemStack(Items.DIAMOND), new OreIngredient("gemDiamond"));
        convertIngredient.put(new ItemStack(Items.QUARTZ), new OreIngredient("gemQuartz"));
        convertIngredient.put(new ItemStack(Items.DYE, 1, 4), new OreIngredient("gemLapis"));
        convertIngredient.put(new ItemStack(Items.PRISMARINE_SHARD), new OreIngredient("gemPrismarine"));
        convertIngredient.put(new ItemStack(Items.PRISMARINE_CRYSTALS), new OreIngredient("dustPrismarine"));
        convertIngredient.put(new ItemStack(Items.REDSTONE), new OreIngredient("dustRedstone"));
        convertIngredient.put(new ItemStack(Items.GUNPOWDER), new OreIngredient("gunpowder"));
        convertIngredient.put(new ItemStack(RegistryManager.dust_ash), new OreIngredient("dustAsh"));
        convertIngredient.put(new ItemStack(RegistryManager.plate_iron), new OreIngredient("plateIron"));
        convertIngredient.put(new ItemStack(RegistryManager.plate_copper), new OreIngredient("plateCopper"));
        convertIngredient.put(new ItemStack(RegistryManager.plate_dawnstone), new OreIngredient("plateDawnstone"));
        convertIngredient.put(new ItemStack(Items.STRING), new OreIngredient("string"));
        convertIngredient.put(new ItemStack(Items.ENDER_PEARL), new OreIngredient("enderpearl"));
        convertIngredient.put(new ItemStack(RegistryManager.sword_lead, 1, OreDictionary.WILDCARD_VALUE), new IngredientMaterialTool("sword", "lead"));

        OreIngredient ingotLead = new OreIngredient("ingotLead");
        OreIngredient ingotSilver = new OreIngredient("ingotSilver");
        OreIngredient blankGlass = new OreIngredient("blockGlassColorless");
        OreIngredient redstoneBlock = new OreIngredient("blockRedstone");
        OreIngredient glass = new OreIngredient("blockGlass");
        Ingredient fluidPipe = Ingredient.fromStacks(new ItemStack(RegistryManager.pipe));
        addAlchemyTabletRecipe(new ItemStack(Registry.ALCHEMY_GLOBE), blankGlass, ingotLead, Ingredient.fromItem(RegistryManager.aspectus_lead), ingotLead, Ingredient.fromItem(RegistryManager.archaic_circuit), new AspectRangeList(AspectList.createStandard(0, 0, 16, 0, 32), AspectList.createStandard(0, 0, 32, 0, 64)));
        addAlchemyTabletRecipe(new ItemStack(Registry.CATALYTIC_PLUG), ingotSilver, fluidPipe, glass, fluidPipe, redstoneBlock, new AspectRangeList(AspectList.createStandard(0, 20, 0, 32, 0), AspectList.createStandard(0, 30, 0, 64, 0)));
        addAlchemyTabletRecipe(new ItemStack(Registry.METALLURGIC_DUST), Ingredient.fromItem(RegistryManager.ancient_motive_core), Ingredient.fromItem(Registry.EMBER_GRIT), new OreIngredient("dustAsh"), new OreIngredient("dustRedstone"), Ingredient.EMPTY, new AspectRangeList(AspectList.createStandard(0, 0, 0, 0, 0), AspectList.createStandard(16, 16, 16, 16, 16)));

        removeRecipe(new ResourceLocation(Embers.MODID,"archaic_bricks_2")); //Remove conflicting recipe
        removeRecipe(new ResourceLocation(Embers.MODID,"plate_caminite_raw"));

        FurnaceRecipes.instance().addSmeltingRecipe(new ItemStack(Registry.CAMINITE_CLAY),new ItemStack(Registry.CAMINITE_LARGE_TILE),0.1f);

        RecipeRegistry.meltingRecipes.add(new ItemMeltingRecipe(new ItemStack(Items.SUGAR), FluidRegistry.getFluidStack("sugar", 16), false, false)); //Nugget size -> you can combine sugar and lead into antimony without remainder and 1000 sugar store nicely in a fluid vessel

        ArrayList<Fluid> leveledMetals = new ArrayList<>();
        leveledMetals.add(RegistryManager.fluid_molten_lead);
        if (ConfigManager.enableTin) //Tin sometimes doesn't exist.
            leveledMetals.add(RegistryManager.fluid_molten_tin);
        leveledMetals.add(RegistryManager.fluid_molten_iron);
        leveledMetals.add(RegistryManager.fluid_molten_copper);
        leveledMetals.add(RegistryManager.fluid_molten_silver);
        leveledMetals.add(RegistryManager.fluid_molten_gold);
        //Nickel and Aluminium are mundane materials

        for (int i = 0; i < leveledMetals.size() - 1; i++) {
            int e = i + 1;
            FluidStack currentLevel = new FluidStack(leveledMetals.get(i), 4);
            FluidStack nextLevel = new FluidStack(leveledMetals.get(e), 4);
            addAlchemicalMixingRecipe(nextLevel, new FluidStack[]{currentLevel, FluidRegistry.getFluidStack("alchemical_redstone", 3)}, new AspectRangeList(AspectList.createStandard(0, 0, 0, 0, (e * e) * 4), AspectList.createStandard(0, 0, 0, 0, (e * e) * 8)));
        }

        addAlchemicalMixingRecipe(FluidRegistry.getFluidStack("antimony", 12), new FluidStack[]{new FluidStack(RegistryManager.fluid_molten_lead, 8), FluidRegistry.getFluidStack("sugar", 4)}, new AspectRangeList(AspectList.createStandard(0, 16, 0, 16, 0), AspectList.createStandard(0, 32, 0, 24, 0)));
        RecipeRegistry.stampingRecipes.add(new ItemStampingRecipe(new ItemStack(RegistryManager.shard_ember), FluidRegistry.getFluidStack("antimony", 144), EnumStampType.TYPE_BAR, new ItemStack(Registry.SIGNET_ANTIMONY), false, false));
        RecipeRegistry.stampingRecipes.add(new ItemStampingRecipe(ItemStack.EMPTY, FluidRegistry.getFluidStack("antimony", 144), EnumStampType.TYPE_BAR, new ItemStack(Registry.INGOT_ANTIMONY), false, false));
        RecipeRegistry.meltingRecipes.add(new ItemMeltingRecipe(new ItemStack(Registry.INGOT_ANTIMONY), FluidRegistry.getFluidStack("antimony", 144), false, false));
        RecipeRegistry.meltingRecipes.add(new ItemMeltingRecipe(new ItemStack(Items.REDSTONE), FluidRegistry.getFluidStack("alchemical_redstone", 144), false, false));

        OreDictionary.registerOre("ingotAntimony", new ItemStack(Registry.INGOT_ANTIMONY));

        AlchemyUtil.registerAspect("iron", Ingredient.fromItem(RegistryManager.aspectus_iron));
        AlchemyUtil.registerAspect("copper", Ingredient.fromItem(RegistryManager.aspectus_copper));
        AlchemyUtil.registerAspect("dawnstone", Ingredient.fromItem(RegistryManager.aspectus_dawnstone));
        AlchemyUtil.registerAspect("lead", Ingredient.fromItem(RegistryManager.aspectus_lead));
        AlchemyUtil.registerAspect("silver", Ingredient.fromItem(RegistryManager.aspectus_silver));

        initAlcoholRecipes();

        //Catch-All Furnace recipe
        if (Config.HEARTHCOIL_SMELTING && Config.OVERRIDE_HEARTH_COIL) {
            heatCoilRecipes.add(new RecipeHeatCoil() {
                @Override
                public boolean matches(ItemStack stack) {
                    return !FurnaceRecipes.instance().getSmeltingResult(stack).isEmpty();
                }

                @Override
                public ItemStack getResult(TileEntity tile, ItemStack stack) {
                    return FurnaceRecipes.instance().getSmeltingResult(stack).copy();
                }
            });
        }
    }

    private static void initAlcoholRecipes() {
        FluidUtil.registerModifier(new FluidModifier("viscosity", 1000){
            @Override
            public String getFormattedText(NBTTagCompound compound, Fluid fluid) {
                float value = getOrDefault(compound, fluid);
                if(value > 1000)
                    return I18n.format("distilling.modifier.dial.slower_chugging",value);
                else
                    return I18n.format("distilling.modifier.dial.faster_chugging",value);
            }
        });
        FluidUtil.registerModifier(new FluidModifier("light", 0).setFormatType(FluidModifier.FormatType.NAME_ONLY));
        FluidUtil.registerModifier(new FluidModifier("health", 0) {
            @Override
            public void applyEffect(EntityLivingBase target, NBTTagCompound compound, Fluid fluid) {
                float value = getOrDefault(compound, fluid);
                if (value > 0)
                    target.heal(value);
                else if (value < 0)
                    MiscUtil.damageWithoutInvulnerability(target, new DamageSource("acid"), value);
            }

            @Override
            public String getFormattedText(NBTTagCompound compound, Fluid fluid) {
                int value = (int)Math.ceil(getOrDefault(compound, fluid) / 2);
                if(value >= 0)
                    return I18n.format("distilling.modifier.dial.add_health",value);
                else
                    return I18n.format("distilling.modifier.dial.sub_health",value);
            }
        });
        FluidUtil.registerModifier(new FluidModifier("hunger", 0) {
            @Override
            public void applyEffect(EntityLivingBase target, NBTTagCompound compound, Fluid fluid) {
                if (target instanceof EntityPlayer) {
                    int hunger = (int)compound.getFloat("hunger");
                    float saturation = compound.getFloat("saturation");
                    ((EntityPlayer) target).getFoodStats().addStats(hunger, saturation);
                }
            }

            @Override
            public String getFormattedText(NBTTagCompound compound, Fluid fluid) {
                int value = (int)Math.ceil(getOrDefault(compound, fluid) / 2);
                if(value >= 0)
                    return I18n.format("distilling.modifier.dial.add_hunger",value);
                else
                    return I18n.format("distilling.modifier.dial.sub_hunger",value);
            }
        });
        FluidUtil.registerModifier(new FluidModifier("saturation", 0).setFormatType(FluidModifier.FormatType.NONE)); //Saturation is a classic hidden value
        FluidUtil.registerModifier(new FluidModifier("toxicity", 0) {
            @Override
            public void applyEffect(EntityLivingBase target, NBTTagCompound compound, Fluid fluid) {
                float value = getOrDefault(compound, fluid);
                if (value > 0)
                    target.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, (int) value * 4));
                if (value >= 50)
                    target.addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, (int) value * 2));
                if (value >= 100)
                    target.addPotionEffect(new PotionEffect(MobEffects.POISON, (int) value - 50));
                if (value >= 200)
                    target.addPotionEffect(new PotionEffect(MobEffects.WITHER, (int) (value - 150)));
            }
        });
        FluidUtil.registerModifier(new FluidModifier("heat", 300) {
            @Override
            public void applyEffect(EntityLivingBase target, NBTTagCompound compound, Fluid fluid) {
                float value = getOrDefault(compound, fluid);
                if (value > 400) //Scalding
                {
                    MiscUtil.damageWithoutInvulnerability(target, new DamageSource("scalding"), 2.0f);
                    //TODO: Message
                }
                if (value > 500) //Burning hot
                    target.setFire((int) ((value - 500) / 10));
            }
        });
        FluidUtil.registerModifier(new FluidModifier("volume", 0) {
            @Override
            public void applyEffect(EntityLivingBase target, NBTTagCompound compound, Fluid fluid) {
                float value = getOrDefault(compound, fluid);
                if (target.getRNG().nextDouble()*100 < value)
                    target.addPotionEffect(new PotionEffect(Registry.POTION_TIPSY, (int) value * 20));
            }
        }.setFormatType(FluidModifier.FormatType.PERCENTAGE));
        FluidUtil.registerModifier(new FluidModifier("lifedrinker", 0) {
            @Override
            public void applyEffect(EntityLivingBase target, NBTTagCompound compound, Fluid fluid) {
                float value = getOrDefault(compound, fluid);
                target.addPotionEffect(new PotionEffect(Registry.POTION_LIFEDRINKER, (int) value));
            }
        }.setFormatType(FluidModifier.FormatType.NAME_ONLY));
        FluidUtil.registerModifier(new FluidModifier("steadfast", 0) {
            @Override
            public void applyEffect(EntityLivingBase target, NBTTagCompound compound, Fluid fluid) {
                float value = getOrDefault(compound, fluid);
                target.addPotionEffect(new PotionEffect(Registry.POTION_STEADFAST, (int) value));
            }
        }.setFormatType(FluidModifier.FormatType.NAME_ONLY));
        FluidUtil.registerModifier(new FluidModifier("concentration", 0).setFormatType(FluidModifier.FormatType.PERCENTAGE));
        FluidUtil.registerModifier(new FluidModifier("duration", 1).setFormatType(FluidModifier.FormatType.MULTIPLIER));
        FluidUtil.registerModifier(new FluidModifier("fuel", 0) {
            @Override
            public String getFormattedText(NBTTagCompound compound, Fluid fluid) {
                int burntime = (int)getOrDefault(compound, fluid);
                if(burntime > 0)
                    return super.getFormattedText(compound,fluid);
                else
                    return I18n.format("distilling.modifier.dial.fire_retardant",burntime);
            }
        });

        Fluid boiling_wort = FluidRegistry.getFluid("boiling_wort");
        Fluid boiling_potato_juice = FluidRegistry.getFluid("boiling_potato_juice");

        Fluid boiling_beetroot_soup = FluidRegistry.getFluid("boiling_beetroot_soup");
        FluidUtil.setDefaultValue(boiling_beetroot_soup, "hunger", 6);
        FluidUtil.setDefaultValue(boiling_beetroot_soup, "saturation", 0.6F);

        Fluid boiling_verdigris = FluidRegistry.getFluid("boiling_wormwood");
        FluidUtil.setDefaultValue(boiling_verdigris, "toxicity", 100);

        Fluid ale = FluidRegistry.getFluid("dwarven_ale");
        FluidUtil.setDefaultValue(ale, "volume", 20);
        FluidUtil.setDefaultValue(ale, "fuel", 400);

        Fluid inner_fire = FluidRegistry.getFluid("inner_fire");
        FluidUtil.setDefaultValue(inner_fire, "heat", 600);
        FluidUtil.setDefaultValue(inner_fire, "volume", 10);
        FluidUtil.setDefaultValue(inner_fire, "fuel", 1600);

        Fluid umber_ale = FluidRegistry.getFluid("umber_ale");

        Fluid vodka = FluidRegistry.getFluid("vodka");
        FluidUtil.setDefaultValue(vodka, "volume", 30);
        FluidUtil.setDefaultValue(vodka, "fuel", 1200);

        Fluid snowpoff = FluidRegistry.getFluid("snowpoff");
        FluidUtil.setDefaultValue(snowpoff, "heat", 200);
        FluidUtil.setDefaultValue(snowpoff, "volume", 20);
        FluidUtil.setDefaultValue(snowpoff, "fuel", -2000);

        Fluid absinthe = FluidRegistry.getFluid("absinthe");
        FluidUtil.setDefaultValue(absinthe, "toxicity", 50);
        FluidUtil.setDefaultValue(absinthe, "volume", 50);
        FluidUtil.setDefaultValue(absinthe, "fuel", 800);

        Fluid methanol = FluidRegistry.getFluid("methanol");
        FluidUtil.setDefaultValue(methanol, "toxicity", 10);
        FluidUtil.setDefaultValue(methanol, "fuel", 2400);

        ItemStack smallFern = new ItemStack(Blocks.TALLGRASS, 1, BlockTallGrass.EnumType.FERN.getMeta());
        ItemStack bigFern = new ItemStack(Blocks.DOUBLE_PLANT, 1, BlockDoublePlant.EnumPlantType.FERN.getMeta());

        RecipeRegistry.meltingOreRecipes.add(new ItemMeltingOreRecipe("cropWheat", new FluidStack(boiling_wort, 100)));
        RecipeRegistry.meltingOreRecipes.add(new ItemMeltingOreRecipe("cropPotato", new FluidStack(boiling_potato_juice, 50)));
        RecipeRegistry.meltingRecipes.add(new ItemMeltingRecipe(new ItemStack(Items.BEETROOT), new FluidStack(boiling_beetroot_soup, 50), false, false));
        RecipeRegistry.meltingRecipes.add(new ItemMeltingRecipe(smallFern, new FluidStack(boiling_verdigris, 50), true, false));
        RecipeRegistry.meltingRecipes.add(new ItemMeltingRecipe(bigFern, new FluidStack(boiling_verdigris, 100), true, false));
        RecipeRegistry.mixingRecipes.add(new FluidMixingRecipe(new FluidStack[]{new FluidStack(ale, 4), FluidRegistry.getFluidStack("lava", 1)}, new FluidStack(inner_fire, 4)));
        //TODO: Umber Ale requires clockwork arcana

        stillRecipes.add(new RecipeStill(new FluidStack(boiling_wort, 1), Ingredient.EMPTY, 0, new FluidStack(ale, 1)));
        stillRecipes.add(new RecipeStill(new FluidStack(boiling_potato_juice, 3), Ingredient.EMPTY, 0, new FluidStack(vodka, 2)));
        stillRecipes.add(new RecipeStill(new FluidStack(vodka,1),Ingredient.fromItem(Items.SNOWBALL),1,new FluidStack(snowpoff,1)));
        stillRecipes.add(new RecipeStill(new FluidStack(vodka,1),Ingredient.fromItem(Items.SNOWBALL),1,new FluidStack(snowpoff,1)));
        stillRecipes.add(new RecipeStill(new FluidStack(boiling_verdigris, 1), Ingredient.fromItem(Items.SUGAR), 0, new FluidStack(absinthe, 1)));
        stillRecipes.add(new RecipeStill(null, new OreIngredient("logWood"), 1, new FluidStack(methanol, 1)));

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

        stillCatalysts.add(new CatalystInfo(Ingredient.fromStacks(smallFern), 250));
        stillCatalysts.add(new CatalystInfo(Ingredient.fromStacks(bigFern), 500));
        stillCatalysts.add(new CatalystInfo(new OreIngredient("sugar"), 100));
        stillCatalysts.add(new CatalystInfo(Ingredient.fromItem(Items.SNOWBALL), 250));
        stillCatalysts.add(new CatalystInfo(new OreIngredient("logWood"), 750));

        stillRecipes.add(new RecipeStillDoubleDistillation(allAlcohols, Ingredient.EMPTY, 0));
        stillRecipes.add(new RecipeStillModifier(allAlcohols, Ingredient.fromItem(Items.GHAST_TEAR), 1) {
            @Override
            public void modifyOutput(TileEntityStillBase tile, FluidStack output) {
                NBTTagCompound compound = FluidUtil.createModifiers(output);
                float lifedrinker = getModifierOrDefault("lifedrinker", compound, output);
                float toxicity = getModifierOrDefault("toxicity", compound, output);
                compound.setFloat("toxicity", toxicity + 10);
                if (lifedrinker < 18000)
                    compound.setFloat("lifedrinker", Math.min((lifedrinker + 600) * 1.6f, 18000));
            }

            @Override
            public void modifyTooltip(List<String> tooltip) {
                super.modifyTooltip(tooltip);
                tooltip.add(tooltip.size() - 1, TextFormatting.GREEN + Translator.translateToLocal("distilling.effect.lifedrinker"));
                tooltip.add(tooltip.size() - 1, TextFormatting.RED + Translator.translateToLocalFormatted("distilling.effect.add", Translator.translateToLocal("distilling.modifier.toxicity.name"), 10));
            }
        });
        stillRecipes.add(new RecipeStillModifier(allAlcohols, Ingredient.fromItem(Items.GOLDEN_CARROT), 1) {
            @Override
            public void modifyOutput(TileEntityStillBase tile, FluidStack output) {
                NBTTagCompound compound = FluidUtil.createModifiers(output);
                float steadfast = getModifierOrDefault("steadfast", compound, output);
                if (steadfast < 18000)
                    compound.setFloat("steadfast", Math.min((steadfast + 600) * 1.6f, 18000));
            }

            @Override
            public void modifyTooltip(List<String> tooltip) {
                super.modifyTooltip(tooltip);
                tooltip.add(tooltip.size() - 1, TextFormatting.GREEN + Translator.translateToLocal("distilling.effect.steadfast"));
            }
        });
        stillRecipes.add(new RecipeStillModifier(allAlcohols, new OreIngredient("dustRedstone"), 1) {
            @Override
            public void modifyOutput(TileEntityStillBase tile, FluidStack output) {
                NBTTagCompound compound = FluidUtil.createModifiers(output);
                float duration = getModifierOrDefault("duration", compound, output);
                float toxicity = getModifierOrDefault("toxicity", compound, output);
                compound.setFloat("toxicity", toxicity + 5);
                if (duration < 2.5f)
                    compound.setFloat("duration", Math.min(duration + 0.5f, 2.5f));
            }

            @Override
            public void modifyTooltip(List<String> tooltip) {
                super.modifyTooltip(tooltip);
                tooltip.add(tooltip.size() - 1, TextFormatting.BLUE + Translator.translateToLocalFormatted("distilling.effect.add_percent", Translator.translateToLocal("distilling.modifier.duration.name"), 50));
                tooltip.add(tooltip.size() - 1, TextFormatting.RED + Translator.translateToLocalFormatted("distilling.effect.add", Translator.translateToLocal("distilling.modifier.toxicity.name"), 5));
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
            public void modifyOutput(TileEntityStillBase tile, FluidStack output) {
                NBTTagCompound compound = FluidUtil.createModifiers(output);
                float toxicity = getModifierOrDefault("toxicity", compound, output);
                if (toxicity > 0)
                    compound.setFloat("toxicity", Math.max(toxicity * 0.8f - 20, 0));
            }

            @Override
            public void modifyTooltip(List<String> tooltip) {
                super.modifyTooltip(tooltip);
                tooltip.add(tooltip.size() - 1, TextFormatting.BLUE + Translator.translateToLocalFormatted("distilling.effect.sub_percent", Translator.translateToLocal("distilling.modifier.toxicity.name"), 20));
                tooltip.add(tooltip.size() - 1, TextFormatting.BLUE + Translator.translateToLocalFormatted("distilling.effect.sub", Translator.translateToLocal("distilling.modifier.toxicity.name"), 20));
            }
        });
        stillRecipes.add(new RecipeStillModifier(allDrinks, new OreIngredient("cropNetherWart"), 1) {
            @Override
            public void modifyOutput(TileEntityStillBase tile, FluidStack output) {
                NBTTagCompound compound = FluidUtil.createModifiers(output);
                float health = getModifierOrDefault("health", compound, output);
                float hunger = getModifierOrDefault("hunger", compound, output);
                compound.setFloat("health", health + 4);
                compound.setFloat("hunger", hunger - 3);
            }

            @Override
            public void modifyTooltip(List<String> tooltip) {
                super.modifyTooltip(tooltip);
                tooltip.add(tooltip.size() - 1, TextFormatting.BLUE + Translator.translateToLocalFormatted("distilling.effect.add", Translator.translateToLocal("distilling.modifier.health.name"), 4));
                tooltip.add(tooltip.size() - 1, TextFormatting.BLUE + Translator.translateToLocalFormatted("distilling.effect.sub", Translator.translateToLocal("distilling.modifier.hunger.name"), 3));
            }
        });
        stillRecipes.add(new RecipeStillModifier(allDrinks, Ingredient.fromStacks(new ItemStack(Blocks.ICE)), 1) {
            @Override
            public void modifyOutput(TileEntityStillBase tile, FluidStack output) {
                NBTTagCompound compound = FluidUtil.createModifiers(output);
                float heat = getModifierOrDefault("heat", compound, output);
                if (heat > 200)
                    compound.setFloat("heat", Math.max(heat * 0.5f, 200));
            }

            @Override
            public void modifyTooltip(List<String> tooltip) {
                super.modifyTooltip(tooltip);
                tooltip.add(tooltip.size() - 1, TextFormatting.GREEN + Translator.translateToLocalFormatted("distilling.effect.sub_percent", Translator.translateToLocal("distilling.modifier.heat.name"), 50));
            }
        });
        stillRecipes.add(new RecipeStillModifierFood(allSoups, new OreIngredient("cropPotato"), 1, 2, 0.3f));
        stillRecipes.add(new RecipeStillModifierFood(allSoups, new OreIngredient("cropCarrot"), 1, 1, 0.2f));
        stillRecipes.add(new RecipeStillModifierFood(allSoups, new OreIngredient("cropWheat"), 1, 4, 0.6f) {
            @Override
            public void modifyOutput(TileEntityStillBase tile, FluidStack output) {
                super.modifyOutput(tile, output);
                NBTTagCompound compound = FluidUtil.createModifiers(output);
                float viscosity = getModifierOrDefault("viscosity", compound, output);
                compound.setFloat("viscosity", viscosity + 1000);
            }

            @Override
            public void modifyTooltip(List<String> tooltip) {
                super.modifyTooltip(tooltip);
                tooltip.add(tooltip.size() - 1, TextFormatting.GREEN + Translator.translateToLocal("distilling.effect.thick_soup"));
            }
        });
    }

    private static void convertStamperRecipe(ItemStampingRecipe recipe) {
        Ingredient stamp = Ingredient.EMPTY;
        switch (recipe.getStamp()) {
            case TYPE_FLAT:
                stamp = Ingredient.fromItem(RegistryManager.stamp_flat);
                break;
            case TYPE_BAR:
                stamp = Ingredient.fromItem(RegistryManager.stamp_bar);
                break;
            case TYPE_PLATE:
                stamp = Ingredient.fromItem(RegistryManager.stamp_plate);
                break;
        }
        addStamperRecipe(recipe.result, convertToIngredient(recipe.getStack()), recipe.getFluid(), stamp);
    }

    private static void convertAlchemyRecipe(AlchemyRecipe recipe) {
        AspectRangeList aspects = new AspectRangeList(
                AspectList.createStandard(recipe.ironAspectMin, recipe.dawnstoneAspectMin, recipe.copperAspectMin, recipe.silverAspectMin, recipe.leadAspectMin),
                AspectList.createStandard(recipe.ironAspectMin + recipe.ironAspectRange, recipe.dawnstoneAspectMin + recipe.dawnstoneAspectRange, recipe.copperAspectMin + recipe.copperAspectRange, recipe.silverAspectMin + recipe.copperAspectRange, recipe.leadAspectMin + recipe.leadAspectRange)
        );
        addAlchemyTabletRecipe(recipe.result, convertToIngredient(recipe.centerInput), recipe.inputs.stream().map(CraftingRegistry::convertToIngredient).collect(Collectors.toList()), aspects);
    }

    private static Ingredient convertToIngredient(ItemStack stack) {
        if (stack.isEmpty())
            return Ingredient.EMPTY;
        for (Map.Entry<ItemStack, Ingredient> entry : convertIngredient.entrySet())
            if (entry.getKey().isItemEqual(stack))
                return entry.getValue();
        return Ingredient.fromStacks(stack);
    }

    private static void migrateAlchemyRecipes() {
        RecipeRegistry.alchemyRecipes.forEach(CraftingRegistry::convertAlchemyRecipe);
        RecipeRegistry.alchemyRecipes.clear(); //Danny Deleto
    }

    private static void migrateStamperRecipes() {
        RecipeRegistry.stampingRecipes.forEach(CraftingRegistry::convertStamperRecipe);
        RecipeRegistry.stampingRecipes.clear(); //I'm the trash man
    }

    public static void addStamperRecipe(ItemStack output, Ingredient input, FluidStack fluid, Ingredient stamp) {
        stamperRecipes.add(new RecipeStamper(input, fluid, output, stamp));
    }

    public static void addAlchemyTabletRecipe(ItemStack output, Ingredient center, Ingredient east, Ingredient west, Ingredient north, Ingredient south, AspectRangeList aspects) {
        addAlchemyTabletRecipe(output, center, Lists.newArrayList(east, west, north, south), aspects);
    }

    public static void addAlchemyTabletRecipe(ItemStack output, Ingredient center, List<Ingredient> outside, AspectRangeList aspects) {
        if (Config.FIX_MATH_ERROR_A)
            aspects.fixMathematicalError();
        if (Config.FIX_MATH_ERROR_B)
            aspects.setSeedOffset(alchemyTabletRecipes.size());
        alchemyTabletRecipes.add(new RecipeAlchemyTablet(output, center, outside, aspects));
    }

    public static RecipeAlchemyTablet getAlchemyTabletRecipe(ItemStack center, List<ItemStack> outside) {
        RecipeAlchemyTablet matchedRecipe = null;

        for (RecipeAlchemyTablet recipe : alchemyTabletRecipes) {
            if (recipe.matches(center, outside) && (matchedRecipe == null || recipe.inputs.size() > matchedRecipe.inputs.size()))
                matchedRecipe = recipe;
        }

        return matchedRecipe;
    }

    public static void addAlchemicalMixingRecipe(FluidStack output, FluidStack[] input, AspectRangeList aspects) {
        if (Config.FIX_MATH_ERROR_A)
            aspects.fixMathematicalError();
        if (Config.FIX_MATH_ERROR_B)
            aspects.setSeedOffset(alchemicalMixingRecipes.size());
        alchemicalMixingRecipes.add(new RecipeAlchemicalMixer(input, output, aspects));
    }

    public static RecipeAlchemicalMixer getAlchemicalMixingRecipe(ArrayList<FluidStack> fluids) {
        RecipeAlchemicalMixer matchedRecipe = null;

        for (RecipeAlchemicalMixer recipe : alchemicalMixingRecipes) {
            if (recipe.matches(fluids) && (matchedRecipe == null || recipe.inputs.size() > matchedRecipe.inputs.size()))
                matchedRecipe = recipe;
        }

        return matchedRecipe;
    }

    public static RecipeDawnstoneAnvil getDawnstoneAnvilRecipe(ItemStack bottom, ItemStack top) {
        for (RecipeDawnstoneAnvil recipe : dawnstoneAnvilRecipes) {
            if (recipe.matches(bottom, top))
                return recipe;
        }

        return null;
    }

    public static boolean isDawnstoneAnvilRecipeBlacklisted(ItemStack bottom, ItemStack top) {
        for (RecipeDawnstoneAnvil recipe : dawnstoneAnvilBlacklist) {
            if (recipe.matches(bottom, top))
                return true;
        }

        return false;
    }

    public static RecipeHeatCoil getHeatCoilRecipe(ItemStack input) {
        RecipeHeatCoil matchedRecipe = null;

        for (RecipeHeatCoil recipe : heatCoilRecipes) {
            if (recipe.matches(input))
                matchedRecipe = recipe;
        }

        return matchedRecipe;
    }

    public static RecipeStamper getStamperRecipe(ItemStack input, FluidStack fluid, ItemStack stamp) {
        RecipeStamper matchedRecipe = null;

        for (RecipeStamper recipe : stamperRecipes) {
            if (recipe.matches(input, fluid, stamp))
                matchedRecipe = recipe;
        }

        return matchedRecipe;
    }

    public static RecipeStill getStillRecipe(TileEntityStillBase tile,FluidStack stack, ItemStack catalyst) {
        RecipeStill matchedRecipe = null;

        for (RecipeStill recipe : stillRecipes) {
            if (recipe.matches(tile, stack, catalyst) && (matchedRecipe == null || matchedRecipe.input == null || matchedRecipe.catalystInput.apply(ItemStack.EMPTY)))
                matchedRecipe = recipe;
        }

        return matchedRecipe;
    }

    public static int getStillCatalyst(ItemStack stack) {
        if (stack.isEmpty()) return 0;
        CatalystInfo matchedCatalyst = stillCatalysts.stream().filter(catalyst -> catalyst.matches(stack)).findFirst().orElse(null);
        return matchedCatalyst == null ? 1000 : matchedCatalyst.getAmount(stack);
    }
}
