package soot;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class Config {
    static Configuration configuration;

    public static boolean TRADING_ANTIMONY;
    public static boolean GOLEMS_TYRFING_WEAK;
    public static boolean GOLEMS_POISON_IMMUNE;

    public static void preInit(FMLPreInitializationEvent event)
    {
        configuration = new Configuration(event.getSuggestedConfigurationFile());
        configuration.load();

        TRADING_ANTIMONY = loadPropBool("tradingAntimony","Features","Allows trading signet of antimony with villagers instead of emeralds.",true);
        GOLEMS_TYRFING_WEAK = loadPropBool("golemsTyrfingWeak","Features","Golems take extra damage from the Tyrfing.",true);
        GOLEMS_POISON_IMMUNE = loadPropBool("golemsPoisonImmune","Features","Golems are immune to poison.",true);

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
