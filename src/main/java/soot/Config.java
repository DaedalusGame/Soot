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
    public static boolean ASH_FIRST;
    public static boolean RENAME_STAMP;

    public static boolean OVERRIDE_BORE;
    public static boolean OVERRIDE_DAWNSTONE_ANVIL;
    public static boolean OVERRIDE_HEARTH_COIL;
    public static boolean OVERRIDE_MIXER;
    public static boolean OVERRIDE_STAMPER;
    public static boolean OVERRIDE_BEAM_CANNON;
    public static boolean OVERRIDE_ALCHEMY_TABLET;
    public static boolean OVERRIDE_MECH_ACCESSOR;
    public static boolean OVERRIDE_ALCHEMY_PEDESTAL;
    public static boolean OVERRIDE_CRYSTAL_CELL;

    //public static boolean GENERATE_SULFUR_ORE;

    public static HashSet<Integer> SULFUR_GREYLIST = new HashSet<>();
    public static boolean SULFUR_IS_WHITELIST;
    public static int SULFUR_MIN_Y;
    public static int SULFUR_MAX_Y;
    public static int SULFUR_PER_CHUNK;

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

        TRADING_ANTIMONY = loadPropBool("tradingAntimony","Features","Allows trading signet of antimony with villagers instead of emeralds.",true);
        GOLEMS_TYRFING_WEAK = loadPropBool("golemsTyrfingWeak","Features","Golems take extra damage from the Tyrfing.",true);
        GOLEMS_POISON_IMMUNE = loadPropBool("golemsPoisonImmune","Features","Golems are immune to poison.",true);
        RENAME_STAMP = loadPropBool("enableRenamingStamp","Features","Enable Renaming Recipe for stamper.",true);
        ASH_FIRST = loadPropBool("ashFirst","Features","Ash is removed before the aspect from pedestals.",true);
        //MELTER_ORE_AMOUNT = loadPropInt("melterOreAmount","Features","How many mb of fluid are obtained per ore output in the melter. This is multiplied by the amount of output a melter would produce, so by default 144mb * 2 ingots.",144);

        //GENERATE_SULFUR_ORE = loadPropBool("sulfurOre","Generation","Whether sulfur ore generates in new chunks.",true);

        for (String s : configuration.getStringList("sulfurBlacklist", "Ores", new String[]{"-1","1"}, "A list of all dimension IDs in which sulfur orespawn is prohibited. Sulfur ores will spawn in any dimension not on this list, but only in vanilla stone.")){
            SULFUR_GREYLIST.add(Integer.valueOf(s));
        }
        SULFUR_IS_WHITELIST = configuration.getBoolean("sulfurBlacklistIsWhitelist","Ores",false,"Whether the sulfur blacklist is a whitelist.");

        SULFUR_MIN_Y = configuration.getInt("sulfurMinY", "Ores", 0, 0, 255, "Minimum height over which sulfur ore will spawn.");
        SULFUR_MAX_Y = configuration.getInt("sulfurMaxY", "Ores", 32, 0, 255, "Maximum height under which sulfur ore will spawn.");
        SULFUR_PER_CHUNK = configuration.getInt("sulfurVeinsPerChunk", "Ores", 3, 0, Integer.MAX_VALUE, "Number of attempts to spawn copper ore the world generator will make for each chunk.");

        if (configuration.hasChanged()) {
            configuration.save();
        }
    }

    public static boolean isSulfurEnabled(int dimension) {
        return !(SULFUR_GREYLIST.contains(dimension) != SULFUR_IS_WHITELIST || SULFUR_GREYLIST.contains(dimension));
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
