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

    public static boolean GENERATE_SULFUR_ORE;

    public static void preInit(FMLPreInitializationEvent event)
    {
        configuration = new Configuration(event.getSuggestedConfigurationFile());
        configuration.load();

        DEBUG_MODE = loadPropBool("debug","Debug","Enables full stack traces when something goes wrong",false);

        TRADING_ANTIMONY = loadPropBool("tradingAntimony","Features","Allows trading signet of antimony with villagers instead of emeralds.",true);
        GOLEMS_TYRFING_WEAK = loadPropBool("golemsTyrfingWeak","Features","Golems take extra damage from the Tyrfing.",true);
        GOLEMS_POISON_IMMUNE = loadPropBool("golemsPoisonImmune","Features","Golems are immune to poison.",true);
        //MELTER_ORE_AMOUNT = loadPropInt("melterOreAmount","Features","How many mb of fluid are obtained per ore output in the melter. This is multiplied by the amount of output a melter would produce, so by default 144mb * 2 ingots.",144);
        //METALLURGICAL_DUST_COLLECT = loadPropBool("metallurgicalDustCollect","Features","Disabling this will disable filling in the default Metallurgical Dust compatibility. So you can do it all via CT.",true);
        //METALLURGICAL_DUST_IS_WHITELIST = loadPropBool("metallurgicalDustIsWhitelist","Features","Whether the blacklist is actually a whitelist.",false);
        //METALLURGICAL_DUST_BLACKLIST = loadPropStringSet("metallurgicalDustBlacklist","Features","Ores that should not be affected by metallurgical dust.",new String[]{"rftools:dimensional_shard_ore"});

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
