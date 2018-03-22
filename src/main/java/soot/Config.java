package soot;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class Config {
    static Configuration configuration;

    public static boolean TRADING_ANTIMONY;
    public static boolean GOLEMS_TYRFING_WEAK;
    public static boolean GOLEMS_POISON_IMMUNE;
    public static boolean HEARTHCOIL_SMELTING;
    public static boolean ASH_FIRST;
    public static boolean MIGRATE_ALCHEMY_RECIPES;

    public static boolean OVERRIDE_DAWNSTONE_ANVIL;
    public static boolean OVERRIDE_HEARTH_COIL;
    public static boolean OVERRIDE_MIXER;
    public static boolean OVERRIDE_ALCHEMY_TABLET;
    public static boolean FIX_MATH_ERROR_A;
    public static boolean FIX_MATH_ERROR_B;
    public static boolean OVERRIDE_MECH_ACCESSOR;

    public static void preInit(FMLPreInitializationEvent event)
    {
        configuration = new Configuration(event.getSuggestedConfigurationFile());
        configuration.load();

        OVERRIDE_MECH_ACCESSOR = loadPropBool("mechAccessor","Overrides","Overrides the Mech Accessor",true);
        OVERRIDE_DAWNSTONE_ANVIL = loadPropBool("dawnstoneAnvil","Overrides","Overrides the Dawnstone Anvil",true);
        OVERRIDE_HEARTH_COIL = loadPropBool("hearthCoil","Overrides","Overrides the Hearth Coil",true);
        OVERRIDE_MIXER = loadPropBool("mixer","Overrides","Overrides the Mixer Centrifuge",true);
        OVERRIDE_ALCHEMY_TABLET = loadPropBool("alchemyTablet","Overrides","Overrides the Exchange Tablet",true);

        TRADING_ANTIMONY = loadPropBool("tradingAntimony","Features","Allows trading signet of antimony with villagers instead of emeralds.",true);
        GOLEMS_TYRFING_WEAK = loadPropBool("golemsTyrfingWeak","Features","Golems take extra damage from the Tyrfing.",true);
        GOLEMS_POISON_IMMUNE = loadPropBool("golemsPoisonImmune","Features","Golems are immune to poison.",true);
        HEARTHCOIL_SMELTING = loadPropBool("hearthCoilSmelting","Features","(requires override) The Hearthcoil can smelt items normally smeltable in a furnace.",true);
        ASH_FIRST = loadPropBool("ashFirst","Features","Ash is removed before the aspect from pedestals.",true);
        MIGRATE_ALCHEMY_RECIPES = loadPropBool("migrateAlchemyRecipes","Features","Disabling this will clear all alchemy recipes if the alchemy tablet override is enabled.",true);
        FIX_MATH_ERROR_A = loadPropBool("fixMathErrorA","Features","If two aspect ranges are the same size on an alchemy recipe, they will no longer have the same exact value.",true);
        FIX_MATH_ERROR_B = loadPropBool("fixMathErrorB","Features","If two aspect ranges are the same size on two alchemy recipes, they will no longer have the same exact value.",true);

        if (configuration.hasChanged())
        {
            configuration.save();
        }
    }

    public static boolean loadPropBool(String propName, String category, String desc, boolean default_) {
        Property prop = configuration.get(category, propName, default_);
        prop.setComment(desc);

        return prop.getBoolean(default_);
    }
}
