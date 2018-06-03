package soot;

import com.google.common.collect.Sets;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.util.HashSet;

public class Config {
    static Configuration configuration;

    public static boolean DEBUG_MODE;

    public static boolean TRADING_ANTIMONY;
    public static boolean GOLEMS_TYRFING_WEAK;
    public static boolean GOLEMS_POISON_IMMUNE;
    public static boolean HEARTHCOIL_SMELTING;
    public static boolean ASH_FIRST;
    public static boolean MIGRATE_STAMPER_RECIPES;
    public static boolean MIGRATE_ALCHEMY_RECIPES;
    public static int MELTER_ORE_AMOUNT;
    public static boolean METALLURGICAL_DUST_COLLECT;
    public static boolean METALLURGICAL_DUST_IS_WHITELIST;
    public static HashSet<String> METALLURGICAL_DUST_BLACKLIST;

    public static boolean OVERRIDE_BORE;
    public static boolean OVERRIDE_DAWNSTONE_ANVIL;
    public static boolean OVERRIDE_HEARTH_COIL;
    public static boolean OVERRIDE_MIXER;
    public static boolean OVERRIDE_STAMPER;
    public static boolean OVERRIDE_BEAM_CANNON;
    public static boolean OVERRIDE_ALCHEMY_TABLET;
    public static boolean FIX_MATH_ERROR_A;
    public static boolean FIX_MATH_ERROR_B;
    public static boolean OVERRIDE_MECH_ACCESSOR;
    public static boolean OVERRIDE_ALCHEMY_PEDESTAL;
    public static boolean OVERRIDE_CRYSTAL_CELL;
    public static boolean OVERRIDE_CODEX;
    public static boolean EMBERS_CRAFTTWEAKER_SUPPORT;

    public static boolean GENERATE_SULFUR_ORE;

    public static void preInit(FMLPreInitializationEvent event)
    {
        configuration = new Configuration(event.getSuggestedConfigurationFile());
        configuration.load();

        DEBUG_MODE = loadPropBool("debug","Debug","Enables full stack traces when something goes wrong",false);

        OVERRIDE_BORE = loadPropBool("bore","Overrides","Overrides the Ember Bore",true);
        OVERRIDE_STAMPER = loadPropBool("stamper","Overrides","Overrides the Stamper",true);
        OVERRIDE_MECH_ACCESSOR = loadPropBool("mechAccessor","Overrides","Overrides the Mech Accessor",true);
        OVERRIDE_DAWNSTONE_ANVIL = loadPropBool("dawnstoneAnvil","Overrides","Overrides the Dawnstone Anvil",true);
        OVERRIDE_HEARTH_COIL = loadPropBool("hearthCoil","Overrides","Overrides the Hearth Coil",true);
        OVERRIDE_MIXER = loadPropBool("mixer","Overrides","Overrides the Mixer Centrifuge",true);
        OVERRIDE_BEAM_CANNON = loadPropBool("beamCannon","Overrides","Overrides the Beam Cannon",true);
        OVERRIDE_ALCHEMY_TABLET = loadPropBool("alchemyTablet","Overrides","Overrides the Exchange Tablet",true);
        OVERRIDE_ALCHEMY_PEDESTAL = loadPropBool("alchemyPedestal","Overrides","Overrides the Alchemy Pedestal",true);
        OVERRIDE_CRYSTAL_CELL = loadPropBool("crystalCell","Overrides","Overrides the Crystal Cell",true);
        OVERRIDE_CODEX = loadPropBool("codex","Overrides","Overrides the Codex gui",true);

        EMBERS_CRAFTTWEAKER_SUPPORT = loadPropBool("embersCraftTweaker","Features","Registers the Crafttweaker Support for Embers.",true);
        TRADING_ANTIMONY = loadPropBool("tradingAntimony","Features","Allows trading signet of antimony with villagers instead of emeralds.",true);
        GOLEMS_TYRFING_WEAK = loadPropBool("golemsTyrfingWeak","Features","Golems take extra damage from the Tyrfing.",true);
        GOLEMS_POISON_IMMUNE = loadPropBool("golemsPoisonImmune","Features","Golems are immune to poison.",true);
        HEARTHCOIL_SMELTING = loadPropBool("hearthCoilSmelting","Features","(requires override) The Hearthcoil can smelt items normally smeltable in a furnace.",true);
        ASH_FIRST = loadPropBool("ashFirst","Features","Ash is removed before the aspect from pedestals.",true);
        MIGRATE_STAMPER_RECIPES = loadPropBool("migrateStamperRecipes","Features","Disabling this will clear all stamper recipes if the stamper override is enabled.",true);
        MIGRATE_ALCHEMY_RECIPES = loadPropBool("migrateAlchemyRecipes","Features","Disabling this will clear all alchemy recipes if the alchemy tablet override is enabled.",true);
        FIX_MATH_ERROR_A = loadPropBool("fixMathErrorA","Features","If two aspect ranges are the same size on an alchemy recipe, they will no longer have the same exact value.",true);
        FIX_MATH_ERROR_B = loadPropBool("fixMathErrorB","Features","If two aspect ranges are the same size on two alchemy recipes, they will no longer have the same exact value.",true);
        MELTER_ORE_AMOUNT = loadPropInt("melterOreAmount","Features","How many mb of fluid are obtained per ore output in the melter. This is multiplied by the amount of output a melter would produce, so by default 144mb * 2 ingots.",144);
        METALLURGICAL_DUST_COLLECT = loadPropBool("metallurgicalDustCollect","Features","Disabling this will disable filling in the default Metallurgical Dust compatibility. So you can do it all via CT.",true);
        METALLURGICAL_DUST_IS_WHITELIST = loadPropBool("metallurgicalDustIsWhitelist","Features","Whether the blacklist is actually a whitelist.",false);
        METALLURGICAL_DUST_BLACKLIST = loadPropStringSet("metallurgicalDustBlacklist","Features","Ores that should not be affected by metallurgical dust.",new String[]{"rftools:dimensional_shard_ore"});

        GENERATE_SULFUR_ORE = loadPropBool("sulfurOre","Generation","Whether sulfur ore generates in new chunks.",true);

        if (configuration.hasChanged()) {
            configuration.save();
        }
    }

    public static boolean loadPropBool(String propName, String category, String desc, boolean default_) {
        Property prop = configuration.get(category, propName, default_);
        prop.setComment(desc);

        return prop.getBoolean(default_);
    }

    public static int loadPropInt(String propName, String category, String desc, int default_) {
        Property prop = configuration.get(category, propName, default_);
        prop.setComment(desc);

        return prop.getInt(default_);
    }

    public static String[] loadPropStringList(String propName, String category, String desc, String[] default_) {
        Property prop = configuration.get(category, propName, default_);
        prop.setComment(desc);
        return prop.getStringList();
    }

    public static HashSet<String> loadPropStringSet(String propName, String category, String desc, String[] default_) {
        Property prop = configuration.get(category, propName, default_);
        prop.setComment(desc);
        return Sets.newHashSet(prop.getStringList());
    }
}
