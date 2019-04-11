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
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
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
import soot.Registry;
import soot.brewing.deliverytypes.DeliveryBlast;
import soot.tile.TileEntityStillBase;
import soot.brewing.CaskManager;
import soot.brewing.FluidModifier;
import soot.brewing.FluidModifier.EffectType;
import soot.brewing.FluidModifier.EnumType;
import soot.util.FluidUtil;
import soot.util.MiscUtil;
import teamroots.embers.ConfigManager;
import teamroots.embers.Embers;
import teamroots.embers.RegistryManager;
import teamroots.embers.api.alchemy.AspectList;
import teamroots.embers.recipe.*;
import teamroots.embers.util.IngredientSpecial;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class CraftingRegistry {
    public static HashSet<ResourceLocation> REMOVE_RECIPE_BY_RL = new HashSet<>();

    public static ArrayList<RecipeAlchemicalMixer> alchemicalMixingRecipes = new ArrayList<>();
    public static ArrayList<RecipeStill> stillRecipes = new ArrayList<>();
    public static ArrayList<CatalystInfo> stillCatalysts = new ArrayList<>();

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

    @SubscribeEvent
    public static void registerRecipes(RegistryEvent.Register<IRecipe> event) {
        OreIngredient ingotAntimony = new OreIngredient("ingotAntimony");
        OreIngredient ingotLead = new OreIngredient("ingotLead");
        OreIngredient ingotNickel = new OreIngredient("ingotNickel");
        OreIngredient blankGlass = new OreIngredient("blockGlassColorless");
        Ingredient leadPickaxe = new IngredientSpecial(stack -> {
            Item item = stack.getItem();
            return item instanceof ItemTool && item.getToolClasses(stack).contains("pickaxe") && ((ItemTool) item).getToolMaterialName().toLowerCase().contains("lead");
        });
        RecipeRegistry.alchemyRecipes.add(new AlchemyRecipe(new AspectList.AspectRangeList().setRange("copper",16,32).setRange("lead",32,64),blankGlass, Lists.newArrayList(ingotLead, Ingredient.fromItem(RegistryManager.aspectus_lead), ingotLead, Ingredient.fromItem(RegistryManager.archaic_circuit)),new ItemStack(Registry.ALCHEMY_GLOBE)));
        RecipeRegistry.alchemyRecipes.add(new AlchemyRecipe(new AspectList.AspectRangeList().setRange("iron",64,96).setRange("lead",64,96),leadPickaxe, Lists.newArrayList(ingotAntimony, Ingredient.fromItem(Registry.SULFUR_CLUMP), ingotAntimony, Ingredient.fromItem(Registry.SIGNET_ANTIMONY)),new ItemStack(Registry.EITR)));
        RecipeRegistry.alchemyRecipes.add(new AlchemyRecipe(new AspectList.AspectRangeList().setRange("copper",16,32).setRange("iron",32,48).setRange("lead",32,48),Ingredient.fromItem(RegistryManager.jet_augment), Lists.newArrayList(ingotNickel, Ingredient.fromItem(Registry.SULFUR_CLUMP), ingotNickel, ingotNickel),new ItemStack(Registry.WITCH_FIRE)));

        removeRecipe(new ResourceLocation(Embers.MODID, "archaic_bricks_2")); //Remove conflicting recipe
        removeRecipe(new ResourceLocation(Embers.MODID, "plate_caminite_raw"));

        FurnaceRecipes.instance().addSmeltingRecipe(new ItemStack(Registry.STAMP_NUGGET_RAW), new ItemStack(Registry.STAMP_NUGGET), 0.1f);
        FurnaceRecipes.instance().addSmeltingRecipe(new ItemStack(Registry.STAMP_TEXT_RAW), new ItemStack(Registry.STAMP_TEXT), 0.1f);
        FurnaceRecipes.instance().addSmeltingRecipe(new ItemStack(Registry.CAMINITE_CLAY), new ItemStack(Registry.CAMINITE_LARGE_TILE), 0.1f);

        int nuggetSize = 16;

        RecipeRegistry.stampingRecipes.add(new ItemStampingRecipe(Ingredient.EMPTY, FluidRegistry.getFluidStack("iron", nuggetSize), Ingredient.fromItem(Registry.STAMP_NUGGET), new ItemStack(Items.IRON_NUGGET)));
        RecipeRegistry.stampingRecipes.add(new ItemStampingRecipe(Ingredient.EMPTY, FluidRegistry.getFluidStack("gold", nuggetSize), Ingredient.fromItem(Registry.STAMP_NUGGET), new ItemStack(Items.GOLD_NUGGET)));
        RecipeRegistry.stampingRecipes.add(new ItemStampingRecipe(Ingredient.EMPTY, FluidRegistry.getFluidStack("copper", nuggetSize), Ingredient.fromItem(Registry.STAMP_NUGGET), new ItemStack(RegistryManager.nugget_copper)));
        RecipeRegistry.stampingRecipes.add(new ItemStampingRecipe(Ingredient.EMPTY, FluidRegistry.getFluidStack("dawnstone", nuggetSize), Ingredient.fromItem(Registry.STAMP_NUGGET), new ItemStack(RegistryManager.nugget_dawnstone)));
        RecipeRegistry.stampingRecipes.add(new ItemStampingRecipe(Ingredient.EMPTY, FluidRegistry.getFluidStack("lead", nuggetSize), Ingredient.fromItem(Registry.STAMP_NUGGET),new ItemStack(RegistryManager.nugget_lead)));
        RecipeRegistry.stampingRecipes.add(new ItemStampingRecipe(Ingredient.EMPTY, FluidRegistry.getFluidStack("silver", nuggetSize), Ingredient.fromItem(Registry.STAMP_NUGGET),new ItemStack(RegistryManager.nugget_silver)));
        if (ConfigManager.enableTin)
            RecipeRegistry.stampingRecipes.add(new ItemStampingRecipe(Ingredient.EMPTY, FluidRegistry.getFluidStack("tin", nuggetSize), Ingredient.fromItem(Registry.STAMP_NUGGET),new ItemStack(RegistryManager.nugget_tin)));
        if (ConfigManager.enableAluminum)
            RecipeRegistry.stampingRecipes.add(new ItemStampingRecipe(Ingredient.EMPTY, FluidRegistry.getFluidStack("aluminum", nuggetSize), Ingredient.fromItem(Registry.STAMP_NUGGET), new ItemStack(RegistryManager.nugget_aluminum)));
        if (ConfigManager.enableBronze)
            RecipeRegistry.stampingRecipes.add(new ItemStampingRecipe(Ingredient.EMPTY, FluidRegistry.getFluidStack("bronze", nuggetSize), Ingredient.fromItem(Registry.STAMP_NUGGET),new ItemStack(RegistryManager.nugget_bronze)));
        if (ConfigManager.enableNickel)
            RecipeRegistry.stampingRecipes.add(new ItemStampingRecipe(Ingredient.EMPTY, FluidRegistry.getFluidStack("nickel", nuggetSize), Ingredient.fromItem(Registry.STAMP_NUGGET),new ItemStack(RegistryManager.nugget_nickel)));
        if (ConfigManager.enableElectrum)
            RecipeRegistry.stampingRecipes.add(new ItemStampingRecipe(Ingredient.EMPTY, FluidRegistry.getFluidStack("electrum", nuggetSize), Ingredient.fromItem(Registry.STAMP_NUGGET),new ItemStack(RegistryManager.nugget_electrum)));

        RecipeRegistry.stampingRecipes.add(new ItemLiverStampingRecipe());
        RecipeRegistry.stampingRecipes.add(new ItemRenameStampingRecipe());

        RecipeRegistry.meltingRecipes.add(new ItemMeltingRecipe(Ingredient.fromItem(Items.SUGAR), FluidRegistry.getFluidStack("sugar", 16))); //Nugget size -> you can combine sugar and lead into antimony without remainder and 1000 sugar store nicely in a fluid vessel

        ArrayList<Fluid> leveledMetals = new ArrayList<>();
        leveledMetals.add(FluidRegistry.getFluid("lead"));
        if (ConfigManager.enableTin) //Tin sometimes doesn't exist.
            leveledMetals.add(FluidRegistry.getFluid("tin"));
        leveledMetals.add(FluidRegistry.getFluid("iron"));
        leveledMetals.add(FluidRegistry.getFluid("copper"));
        leveledMetals.add(FluidRegistry.getFluid("silver"));
        leveledMetals.add(FluidRegistry.getFluid("gold"));
        //Nickel and Aluminium are mundane materials

        for (int i = 0; i < leveledMetals.size() - 1; i++) {
            int e = i + 1;
            FluidStack currentLevel = new FluidStack(leveledMetals.get(i), 4);
            FluidStack nextLevel = new FluidStack(leveledMetals.get(e), 4);
            addAlchemicalMixingRecipe(nextLevel, new FluidStack[]{currentLevel, FluidRegistry.getFluidStack("alchemical_redstone", 3)}, new AspectList.AspectRangeList(AspectList.createStandard(0, 0, 0, 0, (e * e) * 4), AspectList.createStandard(0, 0, 0, 0, (e * e) * 8)));
        }

        addAlchemicalMixingRecipe(FluidRegistry.getFluidStack("antimony", 12), new FluidStack[]{FluidRegistry.getFluidStack("lead", 8), FluidRegistry.getFluidStack("sugar", 4)}, new AspectList.AspectRangeList(AspectList.createStandard(0, 16, 0, 16, 0), AspectList.createStandard(0, 32, 0, 24, 0)));
        RecipeRegistry.stampingRecipes.add(new ItemStampingRecipe(Ingredient.fromItem(RegistryManager.shard_ember), FluidRegistry.getFluidStack("antimony", 144), Ingredient.fromItem(RegistryManager.stamp_bar),new ItemStack(Registry.SIGNET_ANTIMONY)));
        RecipeRegistry.stampingRecipes.add(new ItemStampingRecipe(Ingredient.EMPTY, FluidRegistry.getFluidStack("antimony", 144), Ingredient.fromItem(RegistryManager.stamp_bar),new ItemStack(Registry.INGOT_ANTIMONY)));

        RecipeRegistry.meltingRecipes.add(new ItemMeltingRecipe(new OreIngredient("ingotAntimony"), FluidRegistry.getFluidStack("antimony", 144)));

        OreDictionary.registerOre("ingotAntimony", new ItemStack(Registry.INGOT_ANTIMONY));
        OreDictionary.registerOre("dustSulfur", new ItemStack(Registry.SULFUR));

        initAlcoholRecipes();
    }

    private static void initAlcoholRecipes() {
        FluidUtil.registerModifier(new FluidModifier("ale", 0, EnumType.PRIMARY, EffectType.POSITIVE) {
            @Override
            public void providePotionEffects(EntityLivingBase target, ArrayList<CaskManager.CaskPotionEffect> effects, NBTTagCompound compound, Fluid fluid) {
                float value = getOrDefault(compound, fluid);
                effects.add(new CaskManager.CaskPotionEffect(new PotionEffect(Registry.POTION_ALE, (int) value),4));
            }
        }.setFormatType("name_only"));
        FluidUtil.registerModifier(new FluidModifier("lifedrinker", 0, EnumType.SECONDARY, EffectType.POSITIVE) {
            @Override
            public void providePotionEffects(EntityLivingBase target, ArrayList<CaskManager.CaskPotionEffect> effects, NBTTagCompound compound, Fluid fluid) {
                float value = getOrDefault(compound, fluid);
                effects.add(new CaskManager.CaskPotionEffect(new PotionEffect(Registry.POTION_LIFEDRINKER, (int) value),0));
            }
        }.setFormatType("name_only"));
        FluidUtil.registerModifier(new FluidModifier("steadfast", 0, EnumType.SECONDARY, EffectType.POSITIVE) {
            @Override
            public void providePotionEffects(EntityLivingBase target, ArrayList<CaskManager.CaskPotionEffect> effects, NBTTagCompound compound, Fluid fluid) {
                float value = getOrDefault(compound, fluid);
                effects.add(new CaskManager.CaskPotionEffect(new PotionEffect(Registry.POTION_STEADFAST, (int) value),0));
            }
        }.setFormatType("name_only"));
        FluidUtil.registerModifier(new FluidModifier("viscosity", 1000, EnumType.TERTIARY, EffectType.NEGATIVE) {
            @Override
            public String getFormattedText(NBTTagCompound compound, Fluid fluid) {
                float value = getOrDefault(compound, fluid);
                if (value > 1000)
                    return I18n.format("distilling.modifier.dial.slower_chugging", value);
                else
                    return I18n.format("distilling.modifier.dial.faster_chugging", value);
            }
        });
        FluidUtil.registerModifier(new FluidModifier("light", 0, EnumType.TERTIARY, EffectType.NEUTRAL).setFormatType("name_only"));
        FluidUtil.registerModifier(new FluidModifier("health", 0, EnumType.TERTIARY, EffectType.POSITIVE) {
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
                int value = (int) Math.ceil(getOrDefault(compound, fluid) / 2);
                DecimalFormat format = Embers.proxy.getDecimalFormat("embers.decimal_format.distilling.health");
                return I18n.format("distilling.modifier.dial.health", getLocalizedName(), format.format(value));
            }
        });
        FluidUtil.registerModifier(new FluidModifier("hunger", 0, EnumType.TERTIARY, EffectType.POSITIVE) {
            @Override
            public void applyEffect(EntityLivingBase target, NBTTagCompound compound, Fluid fluid) {
                if (target instanceof EntityPlayer) {
                    int hunger = (int) compound.getFloat("hunger");
                    float saturation = compound.getFloat("saturation");
                    ((EntityPlayer) target).getFoodStats().addStats(hunger, saturation);
                }
            }

            @Override
            public String getFormattedText(NBTTagCompound compound, Fluid fluid) {
                int value = (int) Math.ceil(getOrDefault(compound, fluid) / 2);
                DecimalFormat format = Embers.proxy.getDecimalFormat("embers.decimal_format.distilling.hunger");
                return I18n.format("distilling.modifier.dial.hunger", getLocalizedName(), format.format(value));
            }
        });
        FluidUtil.registerModifier(new FluidModifier("saturation", 0, EnumType.TERTIARY, EffectType.POSITIVE).setFormatType(null)); //Saturation is a classic hidden value
        FluidUtil.registerModifier(new FluidModifier("toxicity", 0, EnumType.TERTIARY, EffectType.NEGATIVE) {
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
        FluidUtil.registerModifier(new FluidModifier("heat", 300, EnumType.TERTIARY, EffectType.NEUTRAL) {
            @Override
            public void applyEffect(EntityLivingBase target, NBTTagCompound compound, Fluid fluid) {
                float value = getOrDefault(compound, fluid);
                if (value > 400) { //Scalding
                    MiscUtil.damageWithoutInvulnerability(target, new DamageSource("scalding"), 2.0f);
                    target.playSound(SoundEvents.ENTITY_PLAYER_HURT_ON_FIRE,1.0f,1.0f);
                    if(target instanceof EntityPlayer) {
                        ((EntityPlayer) target).sendStatusMessage(new TextComponentTranslation("message.scalding"),true);
                    }
                }
                if (value > 500) //Burning hot
                    target.setFire((int) ((value - 500) / 10));
            }
        });
        FluidUtil.registerModifier(new FluidModifier("volume", 0, EnumType.TERTIARY, EffectType.NEGATIVE) {
            @Override
            public void applyEffect(EntityLivingBase target, NBTTagCompound compound, Fluid fluid) {
                float value = getOrDefault(compound, fluid);
                if (target.getRNG().nextDouble() * 100 < value)
                    target.addPotionEffect(new PotionEffect(Registry.POTION_TIPSY, (int) value * 20));
            }
        }.setFormatType("percent"));
        FluidUtil.registerModifier(new FluidModifier("concentration", 0, EnumType.TERTIARY, EffectType.POSITIVE).setFormatType("percent"));
        FluidUtil.registerModifier(new FluidModifier("duration", 1, EnumType.TERTIARY, EffectType.POSITIVE).setFormatType("multiplier"));
        FluidUtil.registerModifier(new FluidModifier("fuel", 0, EnumType.TERTIARY, EffectType.NEUTRAL) {
            @Override
            public String getFormattedText(NBTTagCompound compound, Fluid fluid) {
                int burntime = (int) getOrDefault(compound, fluid);
                if (burntime > 0)
                    return super.getFormattedText(compound, fluid);
                else
                    return I18n.format("distilling.modifier.dial.fire_retardant", burntime);
            }
        });

        FluidUtil.registerModifier(new FluidModifier("alchemy_blast", 0, EnumType.TERTIARY, EffectType.POSITIVE).setFormatType("name_only"));
        FluidUtil.registerModifier(new FluidModifier("alchemy_blast_radius", 8, EnumType.TERTIARY, EffectType.POSITIVE));
        CaskManager.register("alchemy_blast", (gauntlet, elixir, user, fluid) -> {
            float radius = FluidUtil.getModifier(fluid,"alchemy_blast_radius");
            return new DeliveryBlast(user,fluid,8.0,radius);
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

        RecipeRegistry.meltingRecipes.add(new ItemMeltingRecipe(new OreIngredient("cropWheat"), new FluidStack(boiling_wort, 100)));
        RecipeRegistry.meltingRecipes.add(new ItemMeltingRecipe(new OreIngredient("cropPotato"), new FluidStack(boiling_potato_juice, 50)));
        RecipeRegistry.meltingRecipes.add(new ItemMeltingRecipe(Ingredient.fromItem(Items.BEETROOT), new FluidStack(boiling_beetroot_soup, 50)));
        RecipeRegistry.meltingRecipes.add(new ItemMeltingRecipe(Ingredient.fromStacks(smallFern), new FluidStack(boiling_verdigris, 50)));
        RecipeRegistry.meltingRecipes.add(new ItemMeltingRecipe(Ingredient.fromStacks(bigFern), new FluidStack(boiling_verdigris, 100)));
        RecipeRegistry.mixingRecipes.add(new FluidMixingRecipe(new FluidStack[]{new FluidStack(ale, 4), FluidRegistry.getFluidStack("lava", 1)}, new FluidStack(inner_fire, 4)));
        //TODO: Umber Ale requires clockwork arcana

        stillRecipes.add(new RecipeStill(new FluidStack(boiling_wort, 1), Ingredient.EMPTY, 0, new FluidStack(ale, 1)));
        stillRecipes.add(new RecipeStill(new FluidStack(boiling_potato_juice, 3), Ingredient.EMPTY, 0, new FluidStack(vodka, 2)));
        stillRecipes.add(new RecipeStill(new FluidStack(vodka, 1), Ingredient.fromItem(Items.SNOWBALL), 1, new FluidStack(snowpoff, 1)));
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
        stillCatalysts.add(new CatalystInfo(Ingredient.fromItem(Registry.ESSENCE), 1000));

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
        stillRecipes.add(new RecipeStillModifier(allDrinks, Ingredient.fromItem(Items.FERMENTED_SPIDER_EYE), 1) {
            @Override
            public void modifyOutput(TileEntityStillBase tile, FluidStack output) {
                NBTTagCompound compound = FluidUtil.createModifiers(output);

            }

            @Override
            public void modifyTooltip(List<String> tooltip) {
                super.modifyTooltip(tooltip);
                tooltip.add(tooltip.size() - 1, TextFormatting.RED + Translator.translateToLocalFormatted("distilling.effect.sub_percent", Translator.translateToLocal("distilling.modifier.toxicity.name"), 20));
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

    public static void addAlchemicalMixingRecipe(FluidStack output, FluidStack[] input, AspectList.AspectRangeList aspects) {
        aspects.fixMathematicalError();
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

    public static RecipeStill getStillRecipe(TileEntityStillBase tile, FluidStack stack, ItemStack catalyst) {
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
