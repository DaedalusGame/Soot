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
import soot.Config;
import soot.Registry;
import soot.Soot;
import soot.brewing.*;
import soot.brewing.deliverytypes.DeliveryBlast;
import soot.compat.jei.ExtraRecipeInfo;
import soot.item.ItemEssence;
import soot.recipe.breweffects.*;
import soot.tile.TileEntityStillBase;
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
import java.util.HashMap;
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
        Ingredient ingotAntimony = new OreIngredient("ingotAntimony");
        Ingredient ingotLead = new OreIngredient("ingotLead");
        Ingredient ingotNickel = new OreIngredient("ingotNickel");
        Ingredient aspectDawnstone = Ingredient.fromItem(RegistryManager.aspectus_dawnstone);
        Ingredient blankGlass = new OreIngredient("blockGlassColorless");
        Ingredient fluidPipe = Ingredient.fromStacks(new ItemStack(RegistryManager.pipe));
        Ingredient accessor = Ingredient.fromStacks(new ItemStack(RegistryManager.mech_accessor));
        Ingredient distillationPipe = Ingredient.fromStacks(new ItemStack(Registry.DISTILLATION_PIPE));
        Ingredient leadPickaxe = new IngredientSpecial(stack -> {
            Item item = stack.getItem();
            return item instanceof ItemTool && item.getToolClasses(stack).contains("pickaxe") && ((ItemTool) item).getToolMaterialName().toLowerCase().contains("lead");
        });
        RecipeRegistry.alchemyRecipes.add(new AlchemyRecipe(new AspectList.AspectRangeList().setRange("dawnstone",8,16),blankGlass, Lists.newArrayList(aspectDawnstone), Registry.ESSENCE.getStack(EssenceType.NULL,32)));
        RecipeRegistry.alchemyRecipes.add(new AlchemyRecipe(new AspectList.AspectRangeList().setRange("copper",16,32).setRange("lead",32,64),blankGlass, Lists.newArrayList(ingotLead, Ingredient.fromItem(RegistryManager.aspectus_lead), ingotLead, Ingredient.fromItem(RegistryManager.archaic_circuit)),new ItemStack(Registry.ALCHEMY_GLOBE)));
        RecipeRegistry.alchemyRecipes.add(new AlchemyRecipe(new AspectList.AspectRangeList().setRange("silver",24,56).setRange("iron",32,64),distillationPipe, Lists.newArrayList(fluidPipe, blankGlass, fluidPipe, accessor),new ItemStack(Registry.DECANTER)));
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
        
        if(Config.RENAME_STAMP)
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
        OreDictionary.registerOre("dustSugar", new ItemStack(Items.SUGAR));

        initAlcoholRecipes();
    }

    private static void initAlcoholRecipes() {
        //Primary modifiers
        FluidUtil.registerModifier(new FluidPotionModifier("ale", 0, EnumType.PRIMARY, EffectType.POSITIVE, Registry.POTION_ALE, 4) {
            @Override
            public EssenceStack toEssence(float amount) {
                if(amount > 0)
                    return new EssenceStack(EssenceType.GLASS, (int)Math.ceil(amount / 400));
                return super.toEssence(amount);
            }
        }.setFormatType("name_only"));
        FluidUtil.registerModifier(new FluidPotionModifier("stoutness", 0, EnumType.PRIMARY, EffectType.POSITIVE, Registry.POTION_STOUTNESS, 4) {
            @Override
            public EssenceStack toEssence(float amount) {
                return super.toEssence(amount);
            }
        }.setFormatType("name_only"));
        FluidUtil.registerModifier(new FluidPotionModifier("inner_fire", 0, EnumType.PRIMARY, EffectType.POSITIVE, Registry.POTION_INNER_FIRE, 2) {
            @Override
            public EssenceStack toEssence(float amount) {
                return super.toEssence(amount);
            }
        }.setFormatType("name_only"));
        FluidUtil.registerModifier(new FluidPotionModifier("inspiration", 0, EnumType.PRIMARY, EffectType.POSITIVE, Registry.POTION_INSPIRATION, 3) {
            @Override
            public EssenceStack toEssence(float amount) {
                return super.toEssence(amount);
            }
        }.setFormatType("name_only"));
        FluidUtil.registerModifier(new FluidPotionModifier("fire_lung", 0, EnumType.PRIMARY, EffectType.POSITIVE, Registry.POTION_FIRE_LUNG, 2) {
            @Override
            public EssenceStack toEssence(float amount) {
                return super.toEssence(amount);
            }
        }.setFormatType("name_only"));
        FluidUtil.registerModifier(new FluidPotionModifier("snowpoff", 0, EnumType.PRIMARY, EffectType.POSITIVE, Registry.POTION_SNOWPOFF, 3) {
            @Override
            public EssenceStack toEssence(float amount) {
                return super.toEssence(amount);
            }
        }.setFormatType("name_only"));
        //Secondary modifiers
        FluidUtil.registerModifier(new FluidPotionModifier("lifedrinker", 0, EnumType.SECONDARY, EffectType.POSITIVE, Registry.POTION_LIFEDRINKER, 0) {
            @Override
            public EssenceStack toEssence(float amount) {
                if(amount > 0)
                    return new EssenceStack(EssenceType.LIFEDRINKER, (int)Math.ceil(amount / 400));
                return super.toEssence(amount);
            }
        }.setFormatType("name_only"));
        FluidUtil.registerModifier(new FluidPotionModifier("steadfast", 0, EnumType.SECONDARY, EffectType.POSITIVE, Registry.POTION_STEADFAST, 0) {
            @Override
            public EssenceStack toEssence(float amount) {
                if(amount > 0)
                    return new EssenceStack(EssenceType.SPEED, (int)Math.ceil(amount / 400));
                return super.toEssence(amount);
            }
        }.setFormatType("name_only"));
        FluidUtil.registerModifier(new FluidPotionModifier("experience_boost", 0, EnumType.SECONDARY, EffectType.POSITIVE, Registry.POTION_EXPERIENCE_BOOST, 0) {
            @Override
            public EssenceStack toEssence(float amount) {
                if(amount > 0)
                    return new EssenceStack(EssenceType.EXPERIENCE, (int)Math.ceil(amount / 400));
                return super.toEssence(amount);
            }
        }.setFormatType("name_only"));
        FluidUtil.registerModifier(new FluidPotionModifier("glass", 0, EnumType.SECONDARY, EffectType.NEGATIVE, Registry.POTION_GLASS, 9) {
            @Override
            public EssenceStack toEssence(float amount) {
                if(amount > 0)
                    return new EssenceStack(EssenceType.GLASS, (int)Math.ceil(amount / 400));
                return super.toEssence(amount);
            }
        }.setFormatType("name_only"));
        FluidUtil.registerModifier(new FluidPotionModifier("resistance", 0, EnumType.SECONDARY, EffectType.POSITIVE, MobEffects.RESISTANCE, 2) {
            @Override
            public EssenceStack toEssence(float amount) {
                return super.toEssence(amount);
            }
        }.setFormatType("name_only"));
        FluidUtil.registerModifier(new FluidPotionModifier("speed", 0, EnumType.SECONDARY, EffectType.POSITIVE, MobEffects.SPEED, 3) {
            @Override
            public EssenceStack toEssence(float amount) {
                if(amount > 0)
                    return new EssenceStack(EssenceType.SPEED, (int)Math.ceil(amount / 400));
                return super.toEssence(amount);
            }
        }.setFormatType("name_only"));
        FluidUtil.registerModifier(new FluidPotionModifier("slow", 0, EnumType.SECONDARY, EffectType.NEGATIVE, MobEffects.SLOWNESS, 3) {
            @Override
            public EssenceStack toEssence(float amount) {
                if(amount > 0)
                    return new EssenceStack(EssenceType.SLOWNESS, (int)Math.ceil(amount / 400));
                return super.toEssence(amount);
            }
        }.setFormatType("name_only"));
        FluidUtil.registerModifier(new FluidPotionModifier("regeneration", 0, EnumType.SECONDARY, EffectType.POSITIVE, MobEffects.REGENERATION, 3) {
            @Override
            public EssenceStack toEssence(float amount) {
                if(amount > 0)
                    return new EssenceStack(EssenceType.REGENERATION, (int)Math.ceil(amount / 400));
                return super.toEssence(amount);
            }
        }.setFormatType("name_only"));
        //Tertiary modifiers
        FluidUtil.registerModifier(new FluidModifier("viscosity", 1000, EnumType.TERTIARY, EffectType.NEGATIVE) {
            @Override
            public String getFormattedText(NBTTagCompound compound, Fluid fluid) {
                float value = getOrDefault(compound, fluid);
                if (value > 1000)
                    return I18n.format("distilling.modifier.dial.slower_chugging", value);
                else
                    return I18n.format("distilling.modifier.dial.faster_chugging", value);
            }

            @Override
            public EssenceStack toEssence(float amount) {
                if(amount > 1000)
                    return new EssenceStack(EssenceType.SLOWNESS, (int)Math.ceil((amount - 1000) / 200));
                return super.toEssence(amount);
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

            @Override
            public EssenceStack toEssence(float amount) {
                if(amount > 0)
                    return new EssenceStack(EssenceType.REGENERATION, (int)Math.ceil(amount / 2));
                if(amount < 0)
                    return new EssenceStack(EssenceType.DEATH, (int)Math.ceil(amount / 10));
                return super.toEssence(amount);
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

            @Override
            public EssenceStack toEssence(float amount) {
                if(amount > 200)
                    return new EssenceStack(EssenceType.WITHER, (int)Math.ceil((amount - 200) / 20));
                if(amount > 0)
                    return new EssenceStack(EssenceType.POISON,(int)Math.ceil(amount / 20));
                return EssenceStack.EMPTY;
            }
        });
        FluidUtil.registerModifier(new FluidModifier("sweetness", 0, EnumType.TERTIARY, EffectType.POSITIVE) {
            @Override
            public void applyEffect(EntityLivingBase target, NBTTagCompound compound, Fluid fluid) {
                float value = getOrDefault(compound, fluid);
                if (value >= 50)
                    target.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, (int) value * 5));
            }

            @Override
            public EssenceStack toEssence(float amount) {
                if(amount >= 50)
                    return new EssenceStack(EssenceType.REGENERATION,(int)Math.ceil((amount - 50) / 10));
                if(amount > 0)
                    return new EssenceStack(EssenceType.SWEET,(int)Math.ceil(amount / 20));
                return EssenceStack.EMPTY;
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

            @Override
            public EssenceStack toEssence(float amount) {
                if(amount > 500)
                    return new EssenceStack(EssenceType.FIRE,(int)Math.ceil((amount - 500) / 20));
                if(amount < 250)
                    return new EssenceStack(EssenceType.ICE,(int)Math.ceil((250 - amount) / 20));
                return EssenceStack.EMPTY;
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
        FluidUtil.setDefaultValue(ale, "ale", 1200);
        FluidUtil.setDefaultValue(ale, "volume", 20);
        FluidUtil.setDefaultValue(ale, "fuel", 400);

        Fluid inner_fire = FluidRegistry.getFluid("inner_fire");
        FluidUtil.setDefaultValue(inner_fire, "inner_fire", 1000);
        FluidUtil.setDefaultValue(inner_fire, "heat", 600);
        FluidUtil.setDefaultValue(inner_fire, "volume", 10);
        FluidUtil.setDefaultValue(inner_fire, "fuel", 1600);

        Fluid umber_ale = FluidRegistry.getFluid("umber_ale");

        Fluid vodka = FluidRegistry.getFluid("vodka");
        FluidUtil.setDefaultValue(vodka, "stoutness", 1600);
        FluidUtil.setDefaultValue(vodka, "volume", 30);
        FluidUtil.setDefaultValue(vodka, "fuel", 1200);

        Fluid snowpoff = FluidRegistry.getFluid("snowpoff");
        FluidUtil.setDefaultValue(snowpoff, "snowpoff", 1000);
        FluidUtil.setDefaultValue(snowpoff, "heat", 200);
        FluidUtil.setDefaultValue(snowpoff, "volume", 20);
        FluidUtil.setDefaultValue(snowpoff, "fuel", -2000);

        Fluid absinthe = FluidRegistry.getFluid("absinthe");
        FluidUtil.setDefaultValue(absinthe, "inspiration", 400);
        FluidUtil.setDefaultValue(absinthe, "toxicity", 50);
        FluidUtil.setDefaultValue(absinthe, "volume", 50);
        FluidUtil.setDefaultValue(absinthe, "fuel", 800);

        Fluid methanol = FluidRegistry.getFluid("methanol");
        FluidUtil.setDefaultValue(methanol, "fire_lung", 200);
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

        stillRecipes.add(new RecipeStill(getRL("brew_ale"), new FluidStack(boiling_wort, 1), Ingredient.EMPTY, 0, new FluidStack(ale, 1))
                .setEssence(Lists.newArrayList(new EssenceStack(EssenceType.GLASS, 1))));
        stillRecipes.add(new RecipeStill(getRL("brew_vodka"),new FluidStack(boiling_potato_juice, 3), Ingredient.EMPTY, 0, new FluidStack(vodka, 2))
                .setEssence(Lists.newArrayList(new EssenceStack(EssenceType.VILE, 1))));
        stillRecipes.add(new RecipeStill(getRL("brew_snowpoff"),new FluidStack(vodka, 1), Ingredient.fromItem(Items.SNOWBALL), 1, new FluidStack(snowpoff, 1))
                .setEssence(Lists.newArrayList(new EssenceStack(EssenceType.ICE, 5))));
        stillRecipes.add(new RecipeStill(getRL("brew_absinthe"),new FluidStack(boiling_verdigris, 1), Ingredient.fromItem(Items.SUGAR), 0, new FluidStack(absinthe, 1))
                .setEssence(Lists.newArrayList(new EssenceStack(EssenceType.POISON, 5), new EssenceStack(EssenceType.EXPERIENCE, 1))));
        stillRecipes.add(new RecipeStill(getRL("brew_methanol"),null, new OreIngredient("logWood"), 1, new FluidStack(methanol, 1))
                .setEssence(Lists.newArrayList(new EssenceStack(EssenceType.FIRE, 3), new EssenceStack(EssenceType.POISON, 1))));

        stillRecipes.add(new RecipeStill(getRL("extract_lava"),new FluidStack(FluidRegistry.LAVA, 3), Ingredient.EMPTY, 1, new FluidStack(FluidRegistry.LAVA, 1))
                .setEssence(Lists.newArrayList(new EssenceStack(EssenceType.FIRE, 10))));
        stillRecipes.add(new RecipeStill(getRL("extract_iron"), new FluidStack(RegistryManager.fluid_molten_iron, 3), Ingredient.EMPTY, 1, new FluidStack(RegistryManager.fluid_molten_iron, 2))
                .setEssence(Lists.newArrayList(new EssenceStack(EssenceType.EXTRACT, 15))));

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
        stillCatalysts.add(new CatalystInfo(new OreIngredient("dustSugar"), 100));
        stillCatalysts.add(new CatalystInfo(Ingredient.fromItem(Items.SNOWBALL), 250));
        stillCatalysts.add(new CatalystInfo(new OreIngredient("logWood"), 750));
        stillCatalysts.add(new CatalystInfo(Ingredient.fromItem(Registry.ESSENCE), ItemEssence.CAPACITY));
        stillCatalysts.add(new CatalystInfo(new OreIngredient("dustSulfur"), 100));
        stillCatalysts.add(new CatalystInfoSulfur());

        stillRecipes.add(new RecipeStillDoubleDistillation(getRL("modify_double_distill"), allAlcohols, Ingredient.EMPTY, 0) {
            @Override
            public int getInputConsumed() {
                return 3;
            }
        }
            .addEffect(new EffectAdd("concentration", 10, 120, false))
            .addEffect(new EffectMultiply("concentration", 1.8f, 0, 120, false))
            .addEffect(new EffectMultiply("volume", 1.1f, false))
            .addEffect(new EffectLoss(3, 2))
        );
        stillRecipes.add(new RecipeStillModifier(getRL("modify_lifedrinker"), allAlcohols, Ingredient.fromItem(Items.GHAST_TEAR), 1)
                .addEffect(new EffectInfo("lifedrinker"))
                .addEffect(new EffectAdd("lifedrinker", 600, 18000, true))
                .addEffect(new EffectMultiply("lifedrinker", 1.6f, Float.NEGATIVE_INFINITY, 18000, true))
                .addEffect(new EffectAdd("toxicity", 10, false))
        );
        stillRecipes.add(new RecipeStillModifier(getRL("modify_steadfast"), allAlcohols, Ingredient.fromItem(Items.RABBIT_FOOT), 1)
                .addEffect(new EffectInfo("steadfast"))
                .addEffect(new EffectAdd("steadfast", 600, 18000, true))
                .addEffect(new EffectMultiply("steadfast", 1.6f, Float.NEGATIVE_INFINITY, 18000, true))
        );
        stillRecipes.add(new RecipeStillModifier(getRL("modify_experience_boost"), allDrinks, Ingredient.fromItem(Items.EGG), 1)
                .addEffect(new EffectInfo("experience_boost"))
                .addEffect(new EffectAdd("experience_boost", 600, 18000, true))
                .addEffect(new EffectMultiply("experience_boost", 1.6f, Float.NEGATIVE_INFINITY, 18000, true))
                .addEffect(new EffectAdd("toxicity", 10, 50, false))
        );
        stillRecipes.add(new RecipeStillModifier(getRL("modify_duration_bonus"), allAlcohols, new OreIngredient("dustRedstone"), 1)
                .addEffect(new EffectAdd("duration", 0.5f, 2.5f, false))
                .addEffect(new EffectAdd("toxicity", 5, false))
        );
        stillRecipes.add(new RecipeStillModifier(getRL("modify_sweetness_bonus"), allDrinks, new OreIngredient("dustSugar"), 1)
                .addEffect(new EffectAdd("sweetness",15,80,false))
        );
        stillRecipes.add(new RecipeStillModifier(getRL("modify_purify"), allDrinks, new OreIngredient("dustPrismarine"), 1)
                .addEffect(new EffectMultiply("toxicity",0.8f, 0, Float.POSITIVE_INFINITY,false))
                .addEffect(new EffectAdd("toxicity",-20,0,false))
        );
        stillRecipes.add(new RecipeStillModifier(getRL("modify_taint"), allDrinks, Ingredient.fromItem(Items.FERMENTED_SPIDER_EYE), 1) {
            @Override
            public void modifyOutput(TileEntityStillBase tile, FluidStack output) {
                NBTTagCompound compound = FluidUtil.createModifiers(output);
                for(String modifier : FluidUtil.SORTED_MODIFIER_KEYS) {
                    EffectType effectType = FluidUtil.getEffectType(modifier);
                    if(effectType == EffectType.NEUTRAL)
                        continue;
                    float defaultValue = FluidUtil.getDefault(modifier);
                    float value = getModifierOrDefault(modifier, compound, output);
                    if ((value > defaultValue && effectType == EffectType.POSITIVE) || (value < defaultValue && effectType == EffectType.NEGATIVE)) {
                        compound.setFloat(modifier, defaultValue);
                    }
                }
            }

            @Override
            public void modifyTooltip(List<String> tooltip) {
                super.modifyTooltip(tooltip);
                addModifier(tooltip, "erase_positive", false);
            }
        });
        stillRecipes.add(new RecipeStillModifier(getRL("modify_heal"), allDrinks, new OreIngredient("cropNetherWart"), 1)
                .addEffect(new EffectAdd("health",4,false))
                .addEffect(new EffectAdd("hunger",-3,false))
        );
        stillRecipes.add(new RecipeStillModifier(getRL("modify_cool"), allDrinks, Ingredient.fromStacks(new ItemStack(Blocks.ICE)), 1)
                .addEffect(new EffectMultiply("heat",0.5f, 200, Float.POSITIVE_INFINITY,false))
        );
        stillRecipes.add(new RecipeStillModifierFood(getRL("soup_potato"), allSoups, new OreIngredient("cropPotato"), 1, 2, 0.3f));
        stillRecipes.add(new RecipeStillModifierFood(getRL("soup_carrot"), allSoups, new OreIngredient("cropCarrot"), 1, 1, 0.2f));
        stillRecipes.add(new RecipeStillModifierFood(getRL("soup_wheat"), allSoups, new OreIngredient("cropWheat"), 1, 4, 0.6f)
                .addEffect(new EffectInfo("thick_soup", TextFormatting.BLUE))
                .addEffect(new EffectAdd("viscosity", 1000, true))
        );
        stillRecipes.add(new RecipeStillModifier(getRL("essence_sweet"), allDrinks, Ingredient.fromStacks(Registry.ESSENCE.getStack(EssenceType.SWEET)), 1)
                .addEffect(new EffectAdd("sweetness",20,false))
        );
        stillRecipes.add(new RecipeStillModifier(getRL("essence_poison"), allDrinks, Ingredient.fromStacks(Registry.ESSENCE.getStack(EssenceType.POISON)), 1)
                .addEffect(new EffectAdd("toxicity",20,false))
        );
        stillRecipes.add(new RecipeStillModifier(getRL("essence_wither"), allDrinks, Ingredient.fromStacks(Registry.ESSENCE.getStack(EssenceType.WITHER)), 1)
                .addEffect(new EffectInfo("wither", TextFormatting.RED))
                .addEffect(new EffectMax("toxicity",200,false))
                .addEffect(new EffectAdd("toxicity",50,false))
        );
        stillRecipes.add(new RecipeStillModifier(getRL("essence_death"), allDrinks, Ingredient.fromStacks(Registry.ESSENCE.getStack(EssenceType.DEATH)), 1)
                .addEffect(new EffectInfo("death", TextFormatting.RED))
                .addEffect(new EffectAdd("health",-10,false))
                .addEffect(new EffectMin("health",-20, true))
        );
        stillRecipes.add(new RecipeStillModifier(getRL("essence_vile"), allDrinks, Ingredient.fromStacks(Registry.ESSENCE.getStack(EssenceType.VILE)), 1) {
            @Override
            public void modifyOutput(TileEntityStillBase tile, FluidStack output) {
                NBTTagCompound compound = FluidUtil.createModifiers(output);
                float health = getModifierOrDefault("health", compound, output);
                float toxicity = getModifierOrDefault("toxicity", compound, output);
                float toConvert = Math.max(0,Math.min(toxicity,20));
                compound.setFloat("health", health - toConvert / 2);
                compound.setFloat("toxicity", toxicity - toConvert);
            }

            @Override
            public void modifyTooltip(List<String> tooltip) {
                super.modifyTooltip(tooltip);
                addModifier(tooltip, "vile", false);
            }
        });
        stillRecipes.add(new RecipeStillModifier(getRL("essence_fire"), allDrinks, Ingredient.fromStacks(Registry.ESSENCE.getStack(EssenceType.FIRE)), 1)
                .addEffect(new EffectAdd("heat", 40, false))
                .addEffect(new EffectAdd("fuel", 500, false))
        );
        stillRecipes.add(new RecipeStillModifier(getRL("essence_ice"), allDrinks, Ingredient.fromStacks(Registry.ESSENCE.getStack(EssenceType.ICE)), 1)
                .addEffect(new EffectAdd("heat", -20, 0, false))
        );
        stillRecipes.add(new RecipeStillModifier(getRL("essence_lifedrinker"), allDrinks, Ingredient.fromStacks(Registry.ESSENCE.getStack(EssenceType.LIFEDRINKER)), 1)
                .addEffect(new EffectInfo("lifedrinker"))
                .addEffect(new EffectAdd("lifedrinker", 800, true))
        );
        stillRecipes.add(new RecipeStillModifier(getRL("essence_glass"), allDrinks, Ingredient.fromStacks(Registry.ESSENCE.getStack(EssenceType.GLASS)), 1)
                .addEffect(new EffectInfo("glass"))
                .addEffect(new EffectAdd("glass", 800, true))
        );
        stillRecipes.add(new RecipeStillModifier(getRL("essence_speed"), allDrinks, Ingredient.fromStacks(Registry.ESSENCE.getStack(EssenceType.SPEED)), 1)
                .addEffect(new EffectInfo("speed"))
                .addEffect(new EffectAdd("speed", 800, true))
        );
        stillRecipes.add(new RecipeStillModifier(getRL("essence_slowness"), allDrinks, Ingredient.fromStacks(Registry.ESSENCE.getStack(EssenceType.SLOWNESS)), 1)
                .addEffect(new EffectInfo("slow"))
                .addEffect(new EffectAdd("slow", 800, true))
        );
        stillRecipes.add(new RecipeStillModifier(getRL("essence_regeneration"), allDrinks, Ingredient.fromStacks(Registry.ESSENCE.getStack(EssenceType.REGENERATION)), 1)
                .addEffect(new EffectInfo("regeneration"))
                .addEffect(new EffectAdd("regeneration", 800, true))
        );
        stillRecipes.add(new RecipeStillModifier(getRL("essence_experience"), allDrinks, Ingredient.fromStacks(Registry.ESSENCE.getStack(EssenceType.EXPERIENCE)), 1)
                .addEffect(new EffectInfo("experience_boost"))
                .addEffect(new EffectAdd("experience_boost", 800, true))
        );
        stillRecipes.add(new RecipeStillModifier(getRL("essence_extract"), allDrinks, Ingredient.fromStacks(Registry.ESSENCE.getStack(EssenceType.EXTRACT)), 1) {
            @Override
            public void modifyOutput(TileEntityStillBase tile, FluidStack output) {
                NBTTagCompound compound = FluidUtil.createModifiers(output);
                String minModifier = getSmallestModifier(output, compound);
                if(minModifier != null)
                    compound.setFloat(minModifier, FluidUtil.getDefault(minModifier));
            }

            @Override
            public List<EssenceStack> getEssenceOutput(TileEntityStillBase tile, FluidStack input, ItemStack catalyst) {
                NBTTagCompound compound = FluidUtil.createModifiers(input);
                String minModifier = getSmallestModifier(input, compound);
                if(minModifier != null) {
                    float value = getModifierOrDefault(minModifier, compound, input);
                    return Lists.newArrayList(FluidUtil.modifierToEssence(minModifier, value));
                }
                return Lists.newArrayList();
            }

            @Override
            public List<ExtraRecipeInfo> getExtraInfo() {
                return Lists.newArrayList(new ExtraRecipeInfo(Lists.newArrayList(Registry.ESSENCE.getStack(EssenceType.CHAOS))) {
                    @Override
                    public void modifyTooltip(List<String> strings) {
                        strings.clear();
                        strings.add(Translator.translateToLocalFormatted("distilling.effect.decanter"));
                        strings.add(Translator.translateToLocalFormatted("distilling.effect.decanter.desc"));
                        strings.add(Translator.translateToLocalFormatted("distilling.effect.decanter.extract"));
                    }
                });
            }

            @Override
            public void modifyTooltip(List<String> tooltip) {
                super.modifyTooltip(tooltip);
                addModifier(tooltip, "extract", true);
            }

            private String getSmallestModifier(FluidStack output, NBTTagCompound compound) {
                float minValue = Float.POSITIVE_INFINITY;
                String minModifier = null;
                for(String modifier : FluidUtil.SORTED_MODIFIER_KEYS) {
                    if(FluidUtil.getType(modifier) == EnumType.PRIMARY)
                        continue;
                    float value = getModifierOrDefault(modifier, compound, output) - FluidUtil.getDefault(modifier);
                    EssenceStack essence = FluidUtil.modifierToEssence(modifier, value);
                    if(!essence.isEmpty() && value != 0 && Math.abs(value) < minValue) {
                        minModifier = modifier;
                        minValue = Math.abs(value);
                    }
                }
                return minModifier;
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

    private static ResourceLocation getRL(String name) {
        return new ResourceLocation(Soot.MODID, name);
    }
}
