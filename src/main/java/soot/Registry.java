package soot;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import soot.block.*;
import soot.block.overrides.*;
import soot.capability.CapabilityUpgradeProvider;
import soot.capability.IUpgradeProvider;
import soot.entity.EntityFireCloud;
import soot.entity.EntityMuse;
import soot.entity.EntitySnowpoff;
import soot.fluids.FluidBooze;
import soot.fluids.FluidMolten;
import soot.item.ItemBlockSlab;
import soot.item.ItemMetallurgicDust;
import soot.item.ItemMug;
import soot.item.ItemStill;
import soot.potion.*;
import soot.tile.*;
import soot.tile.overrides.*;
import soot.tile.overrides.TileEntityEmberBoreImproved.BoreOutput;
import soot.tile.overrides.TileEntityEmberBoreImproved.WeightedItemStack;
import soot.upgrade.UpgradeCatalyticPlug;
import soot.util.CaskManager;
import soot.util.CaskManager.CaskLiquid;
import soot.util.HeatManager;
import soot.util.Nope;
import soot.util.OreTransmutationManager;
import teamroots.embers.Embers;
import teamroots.embers.RegistryManager;
import teamroots.embers.tileentity.*;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class Registry {
    private static ArrayList<Block> MODELLED_BLOCKS = new ArrayList<>();
    private static ArrayList<Item> MODELLED_ITEMS = new ArrayList<>();
    private static ArrayList<Block> BLOCKS = new ArrayList<>();
    private static ArrayList<Item> ITEMS = new ArrayList<>();

    @GameRegistry.ObjectHolder("soot:alchemy_globe")
    public static BlockAlchemyGlobe ALCHEMY_GLOBE;
    @GameRegistry.ObjectHolder("soot:still")
    public static BlockStill STILL;
    @GameRegistry.ObjectHolder("soot:catalytic_plug")
    public static BlockCatalyticPlug CATALYTIC_PLUG;

    @GameRegistry.ObjectHolder("soot:heat_coil")
    public static BlockHeatCoilImproved HEAT_COIL_OVERRIDE;

    @GameRegistry.ObjectHolder("soot:caminite_clay")
    public static Block CAMINITE_CLAY;
    @GameRegistry.ObjectHolder("soot:caminite_large_tile")
    public static Block CAMINITE_LARGE_TILE;

    @GameRegistry.ObjectHolder("soot:signet_antimony")
    public static Item SIGNET_ANTIMONY;
    @GameRegistry.ObjectHolder("soot:ingot_antimony")
    public static Item INGOT_ANTIMONY;
    @GameRegistry.ObjectHolder("soot:ember_grit")
    public static Item EMBER_GRIT;
    @GameRegistry.ObjectHolder("soot:mug")
    public static ItemMug MUG;
    @GameRegistry.ObjectHolder("soot:metallurgic_dust")
    public static Item METALLURGIC_DUST;

    @GameRegistry.ObjectHolder("soot:eitr")
    public static ItemSword EITR;

    @GameRegistry.ObjectHolder("soot:ale")
    public static Potion POTION_ALE;
    @GameRegistry.ObjectHolder("soot:stoutness")
    public static Potion POTION_STOUTNESS;
    @GameRegistry.ObjectHolder("soot:inner_fire")
    public static Potion POTION_INNER_FIRE;
    @GameRegistry.ObjectHolder("soot:fire_lung")
    public static Potion POTION_FIRE_LUNG;
    @GameRegistry.ObjectHolder("soot:steadfast")
    public static Potion POTION_STEADFAST;
    @GameRegistry.ObjectHolder("soot:tipsy")
    public static Potion POTION_TIPSY;
    @GameRegistry.ObjectHolder("soot:lifedrinker")
    public static Potion POTION_LIFEDRINKER;
    @GameRegistry.ObjectHolder("soot:snowpoff")
    public static Potion POTION_SNOWPOFF;
    @GameRegistry.ObjectHolder("soot:inspiration")
    public static Potion POTION_INSPIRATION;

    public static Fluid BOILING_WORT;
    public static Fluid BOILING_POTATO_JUICE;
    public static Fluid BOILING_WORMWOOD;
    public static Fluid BOILING_BEETROOT_SOUP;
    public static Fluid ALE;
    public static Fluid VODKA;
    public static Fluid ABSINTHE;
    public static Fluid METHANOL;
    public static Fluid INNER_FIRE;
    public static Fluid UMBER_ALE;
    public static Fluid SNOWPOFF_VODKA;

    //Roots Integration?
    public static Fluid BOILING_ROOT_WATER;
    public static Fluid BOILING_AUBERGE;
    public static Fluid ROOT_BEER;
    public static Fluid AUBERGINE_LIQUOR;

    public static final String STONE = "stone";
    public static final String NETHER = "nether";
    public static final String END = "end";
    public static final String SAND = "sand";
    public static final String BETWEEN_STONE = "betweenlands";
    public static final String BETWEEN_PIT = "betweenlands_pit";
    public static final String BETWEEN_GEM = "betweenlands_gem";
    public static final HashMap<String,String> ALTERNATE_ORES = new HashMap<>();

    public static void preInit() {
        MinecraftForge.EVENT_BUS.register(Registry.class);
        registerBlocks();
        registerTileEntities();
        registerEntities();
        registerFluids();
        registerCapabilities();
        Soot.proxy.addResourceOverride(Embers.MODID,"models","item","pipe","json");
        Soot.proxy.addResourceOverride(Embers.MODID,"models","item","item_pipe","json");
    }

    public static void init() {
        registerCaskLiquids();
        registerAccessorTiles();

        BoreOutput defaultOutput = new BoreOutput(Sets.newHashSet(), Sets.newHashSet(), Lists.newArrayList(
                new WeightedItemStack(new ItemStack(RegistryManager.crystal_ember),1),
                new WeightedItemStack(new ItemStack(RegistryManager.shard_ember),3),
                new WeightedItemStack(new ItemStack(Registry.EMBER_GRIT),1)
        ));
        TileEntityEmberBoreImproved.setDefault(defaultOutput);

        HeatManager.register(RegistryManager.archaic_light,20);
        HeatManager.register(Blocks.FIRE,10);
        HeatManager.register(HEAT_COIL_OVERRIDE, (world, pos, state) -> {
            TileEntity tile = world.getTileEntity(pos);
            if(tile instanceof TileEntityHeatCoilImproved) {
                double heat = ((TileEntityHeatCoilImproved) tile).getHeat();
                return heat > TileEntityHeatCoilImproved.MAX_HEAT / 2 ? heat : 0;
            }
            return 0;
        });

        UpgradeCatalyticPlug.registerBlacklistedTile(TileEntityStillBase.class);
    }

    public static void postInit() {
        OreTransmutationManager.registerTransmutationSet(STONE,Blocks.STONE.getDefaultState());
        OreTransmutationManager.registerTransmutationSet(NETHER,Blocks.NETHERRACK.getDefaultState());
        OreTransmutationManager.registerTransmutationSet(END,Blocks.END_STONE.getDefaultState());
        OreTransmutationManager.registerTransmutationSet(SAND,Blocks.SAND.getDefaultState());
        OreTransmutationManager.registerTransmutationSet(BETWEEN_STONE,new ResourceLocation("thebetweenlands:betweenstone"),0);
        OreTransmutationManager.registerTransmutationSet(BETWEEN_PIT,new ResourceLocation("thebetweenlands:pitstone"),0);
        OreTransmutationManager.registerTransmutationSet(BETWEEN_GEM,new ResourceLocation("thebetweenlands:mud"),0);

        OreTransmutationManager.registerOre(STONE,Blocks.LIT_REDSTONE_ORE.getDefaultState()); //workaround for a vanilla issue

        ALTERNATE_ORES.put("minecraft:quartz_ore",NETHER);
        ALTERNATE_ORES.put("tconstruct:ore",NETHER);
        ALTERNATE_ORES.put("astralsorcery:blockcustomsandore",SAND);
        ALTERNATE_ORES.put("thebetweenlands:slimy_bone_ore",BETWEEN_STONE);
        ALTERNATE_ORES.put("thebetweenlands:sulfur_ore",BETWEEN_STONE);
        ALTERNATE_ORES.put("thebetweenlands:syrmorite_ore",BETWEEN_STONE);
        ALTERNATE_ORES.put("thebetweenlands:octine_ore",BETWEEN_STONE);
        ALTERNATE_ORES.put("thebetweenlands:valonite_ore",BETWEEN_PIT);
        ALTERNATE_ORES.put("thebetweenlands:scabyst_ore",BETWEEN_PIT);
        ALTERNATE_ORES.put("thebetweenlands:aqua_middle_gem_ore",BETWEEN_GEM);
        ALTERNATE_ORES.put("thebetweenlands:crimson_middle_gem_ore",BETWEEN_GEM);
        ALTERNATE_ORES.put("thebetweenlands:green_middle_gem_ore",BETWEEN_GEM);
        //How about this
        //You click the make pr button if you want more support

        gatherOreTransmutations();
    }

    public static void gatherOreTransmutations() {
        HashSet<String> existingTags = new HashSet<>();

        for (String orename : OreDictionary.getOreNames()) {
            if(orename == null || !orename.startsWith("ore"))
                continue;
            for (ItemStack stack : OreDictionary.getOres(orename,false)) {
                Item item = stack.getItem();
                if(!(item instanceof ItemBlock))
                    continue;
                ResourceLocation registryName = item.getRegistryName();
                if(registryName == null)
                    continue; //WEE WOO WEE WOO
                String entry = ALTERNATE_ORES.getOrDefault(registryName.toString(),STONE);
                String tag = entry+":"+orename;
                if(existingTags.contains(tag))
                    continue;
                if(stack.getMetadata() == OreDictionary.WILDCARD_VALUE)
                    OreTransmutationManager.registerOre(entry, registryName);
                else
                    OreTransmutationManager.registerOre(entry, registryName,stack.getMetadata());
                existingTags.add(tag);
            }
        }
    }

    public static void registerCaskLiquids() {
        BOILING_WORT = FluidRegistry.getFluid("boiling_wort");
        BOILING_POTATO_JUICE = FluidRegistry.getFluid("boiling_potato_juice");
        BOILING_WORMWOOD = FluidRegistry.getFluid("boiling_wormwood");
        BOILING_BEETROOT_SOUP = FluidRegistry.getFluid("boiling_beetroot_soup");

        ALE = FluidRegistry.getFluid("dwarven_ale");
        VODKA = FluidRegistry.getFluid("vodka");
        INNER_FIRE = FluidRegistry.getFluid("inner_fire");
        UMBER_ALE = FluidRegistry.getFluid("umber_ale");
        ABSINTHE = FluidRegistry.getFluid("absinthe");
        METHANOL = FluidRegistry.getFluid("methanol");
        SNOWPOFF_VODKA = FluidRegistry.getFluid("methanol");

        CaskManager.register(new CaskLiquid(BOILING_WORT, 1, 0xFF898516));
        CaskManager.register(new CaskLiquid(BOILING_POTATO_JUICE, 1, 0xFFECEAA7));
        CaskManager.register(new CaskLiquid(BOILING_WORMWOOD, 1, 0xFFAFFF8D).addEffect(new PotionEffect(MobEffects.POISON, 1200, 0), 2).addEffect(new PotionEffect(MobEffects.BLINDNESS, 1200, 0), 0));
        CaskManager.register(new CaskLiquid(BOILING_BEETROOT_SOUP, 1, 0xFFC62E00));

        CaskManager.register(new CaskLiquid(ALE, 2, 0xFFE1862C).addEffect(new PotionEffect(POTION_ALE, 1200, 0), 4));
        CaskManager.register(new CaskLiquid(VODKA, 1, 0xFFC8EFEF).addEffect(new PotionEffect(POTION_STOUTNESS, 1600, 0), 4));
        CaskManager.register(new CaskLiquid(INNER_FIRE, 2, 0xFFFF4D00).addEffect(new PotionEffect(POTION_INNER_FIRE, 1000, 0), 2));
        CaskManager.register(new CaskLiquid(UMBER_ALE, 2, 0xFF473216));
        CaskManager.register(new CaskLiquid(ABSINTHE, 1, 0xFF58FF2E).addEffect(new PotionEffect(POTION_INSPIRATION, 400, 0),3));
        CaskManager.register(new CaskLiquid(METHANOL, 1, 0xFF666633).addEffect(new PotionEffect(POTION_FIRE_LUNG, 200, 0), 2));
        CaskManager.register(new CaskLiquid(SNOWPOFF_VODKA, 2, 0xFFC3E6F7).addEffect(new PotionEffect(POTION_SNOWPOFF, 1000, 0), 3));
    }

    public static void registerAccessorTiles() {
        TileEntityMechAccessorImproved.registerAccessibleTile(TileEntityMechCore.class);
        TileEntityMechAccessorImproved.registerAccessibleTile(TileEntityMixerBottom.class);
        TileEntityMechAccessorImproved.registerAccessibleTile(TileEntityActivatorBottom.class);
        TileEntityMechAccessorImproved.registerAccessibleTile(TileEntityFurnaceBottom.class);
        TileEntityMechAccessorImproved.registerAccessibleTile(TileEntityBoilerBottom.class);
        TileEntityMechAccessorImproved.registerAccessibleTile(TileEntityReactor.class);
        TileEntityMechAccessorImproved.registerAccessibleTile(TileEntityStillBase.class);
    }

    public static void registerBlocks() {
        Nope.shutupForge(Registry::registerOverrides);

        BlockEmberBurst emberBurst = (BlockEmberBurst) new BlockEmberBurst(Material.ROCK).setCreativeTab(Soot.creativeTab);
        BlockEmberFunnel emberFunnel = (BlockEmberFunnel) new BlockEmberFunnel(Material.ROCK).setCreativeTab(Soot.creativeTab);
        registerBlock("ember_burst", emberBurst, new ItemBlock(emberBurst));
        registerBlock("ember_funnel", emberFunnel, new ItemBlock(emberFunnel));

        BlockRedstoneBin redstoneBin = (BlockRedstoneBin) new BlockRedstoneBin(Material.IRON,"redstone_bin").setIsFullCube(false).setIsOpaqueCube(false).setHarvestProperties("pickaxe",0).setHardness(1f).setCreativeTab(Soot.creativeTab);
        registerBlock("redstone_bin", redstoneBin, new ItemBlock(redstoneBin));

        BlockAlchemyGlobe alchemyGlobe = (BlockAlchemyGlobe) new BlockAlchemyGlobe(Material.ROCK).setHardness(1.6f).setLightOpacity(0).setCreativeTab(Soot.creativeTab);
        registerBlock("alchemy_globe", alchemyGlobe, new ItemBlock(alchemyGlobe));
        BlockCatalyticPlug catalyticPlug = (BlockCatalyticPlug) new BlockCatalyticPlug(Material.IRON).setHardness(1.6f).setLightOpacity(0).setCreativeTab(Soot.creativeTab);
        registerBlock("catalytic_plug", catalyticPlug, new ItemBlock(catalyticPlug));
        BlockInsulation insulation = (BlockInsulation) new BlockInsulation(Material.ROCK).setHardness(1.6f).setLightOpacity(1).setCreativeTab(Soot.creativeTab);
        registerBlock("insulation", insulation, new ItemBlock(insulation));
        BlockDistillationPipe distillationPipe = (BlockDistillationPipe) new BlockDistillationPipe(Material.IRON).setHardness(1.6f).setLightOpacity(1).setCreativeTab(Soot.creativeTab);
        registerBlock("distillation_pipe", distillationPipe, new ItemBlock(distillationPipe));

        BlockAlchemyGauge alchemyGauge = (BlockAlchemyGauge) new BlockAlchemyGauge(Material.IRON).setHardness(1.6f).setLightOpacity(0).setCreativeTab(Soot.creativeTab);
        registerBlock("alchemy_gauge", alchemyGauge, new ItemBlock(alchemyGauge));

        registerItem("signet_antimony", new Item().setCreativeTab(Soot.creativeTab));
        registerItem("ingot_antimony", new Item().setCreativeTab(Soot.creativeTab));
        registerItem("mug", new ItemMug().setCreativeTab(Soot.creativeTab));
        registerItem("metallurgic_dust", new ItemMetallurgicDust().setCreativeTab(Soot.creativeTab));
        registerItem("ember_grit", new Item().setCreativeTab(Soot.creativeTab));

        BlockStill still = (BlockStill) new BlockStill().setHardness(1.6f).setLightOpacity(0).setCreativeTab(Soot.creativeTab);
        registerBlock("still", still, new ItemStill(still));

        Block caminiteClay = new Block(Material.CLAY, MapColor.WHITE_STAINED_HARDENED_CLAY).setHardness(1.6f).setCreativeTab(Soot.creativeTab);
        registerBlock("caminite_clay",caminiteClay,new ItemBlock(caminiteClay));

        Block caminiteTiles = new Block(Material.ROCK, MapColor.WHITE_STAINED_HARDENED_CLAY).setHardness(1.6f).setCreativeTab(Soot.creativeTab);
        Block caminiteTilesSlab = new BlockModSlab(Material.ROCK, MapColor.WHITE_STAINED_HARDENED_CLAY).setHardness(1.6f).setCreativeTab(Soot.creativeTab);
        Block caminiteTilesStairs = new BlockModStairs(caminiteTiles.getDefaultState()).setHardness(1.6f).setCreativeTab(Soot.creativeTab);
        registerBlock("caminite_tiles",caminiteTiles,new ItemBlock(caminiteTiles));
        registerBlock("caminite_tiles_slab",caminiteTilesSlab,new ItemBlockSlab(caminiteTilesSlab,caminiteTiles));
        registerBlock("caminite_tiles_stairs",caminiteTilesStairs,new ItemBlock(caminiteTilesStairs));

        Block caminiteLargeTile = new Block(Material.ROCK, MapColor.WHITE_STAINED_HARDENED_CLAY).setHardness(1.6f).setCreativeTab(Soot.creativeTab);
        Block caminiteLargeTileSlab = new BlockModSlab(Material.ROCK, MapColor.WHITE_STAINED_HARDENED_CLAY).setHardness(1.6f).setCreativeTab(Soot.creativeTab);
        Block caminiteLargeTileStairs = new BlockModStairs(caminiteLargeTile.getDefaultState()).setHardness(1.6f).setCreativeTab(Soot.creativeTab);
        registerBlock("caminite_large_tile",caminiteLargeTile,new ItemBlock(caminiteLargeTile));
        registerBlock("caminite_large_tile_slab",caminiteLargeTileSlab,new ItemBlockSlab(caminiteLargeTileSlab,caminiteLargeTile));
        registerBlock("caminite_large_tile_stairs",caminiteLargeTileStairs,new ItemBlock(caminiteLargeTileStairs));

        Block archaicBigBrick = new Block(Material.ROCK, MapColor.BROWN_STAINED_HARDENED_CLAY).setHardness(1.6f).setCreativeTab(Soot.creativeTab);
        Block archaicBigBrickSlab = new BlockModSlab(Material.ROCK, MapColor.BROWN_STAINED_HARDENED_CLAY).setHardness(1.6f).setCreativeTab(Soot.creativeTab);
        Block archaicBigBrickStairs = new BlockModStairs(archaicBigBrick.getDefaultState()).setHardness(1.6f).setCreativeTab(Soot.creativeTab);
        registerBlock("archaic_big_bricks",archaicBigBrick,new ItemBlock(archaicBigBrick));
        registerBlock("archaic_big_bricks_slab",archaicBigBrickSlab,new ItemBlockSlab(archaicBigBrickSlab,archaicBigBrick));
        registerBlock("archaic_big_bricks_stairs",archaicBigBrickStairs,new ItemBlock(archaicBigBrickStairs));

        Block sealedPlanksSlab = new BlockModSlab(Material.WOOD, MapColor.BROWN_STAINED_HARDENED_CLAY).setHardness(1.6f).setCreativeTab(Soot.creativeTab);
        Block sealedPlanksStairs = new BlockModStairs(RegistryManager.sealed_planks.getDefaultState()).setHardness(1.6f).setCreativeTab(Soot.creativeTab);
        registerBlock("sealed_planks_slab",sealedPlanksSlab,new ItemBlockSlab(sealedPlanksSlab, RegistryManager.sealed_planks));
        registerBlock("sealed_planks_stairs",sealedPlanksStairs,new ItemBlock(sealedPlanksStairs));

        Block sealedTile = new Block(Material.WOOD, MapColor.BROWN_STAINED_HARDENED_CLAY).setHardness(1.6f).setCreativeTab(Soot.creativeTab);
        Block sealedTileSlab = new BlockModSlab(Material.WOOD, MapColor.BROWN_STAINED_HARDENED_CLAY).setHardness(1.6f).setCreativeTab(Soot.creativeTab);
        Block sealedTileStairs = new BlockModStairs(sealedTile.getDefaultState()).setHardness(1.6f).setCreativeTab(Soot.creativeTab);
        registerBlock("sealed_tile",sealedTile,new ItemBlock(sealedTile));
        registerBlock("sealed_tile_slab",sealedTileSlab,new ItemBlockSlab(sealedTileSlab,sealedTile));
        registerBlock("sealed_tile_stairs",sealedTileStairs,new ItemBlock(sealedTileStairs));

        Block sealedKeg = new BlockPillar(Material.WOOD, MapColor.BROWN_STAINED_HARDENED_CLAY).setHardness(1.6f).setCreativeTab(Soot.creativeTab);
        Block sealedPillar = new BlockPillar(Material.WOOD, MapColor.BROWN_STAINED_HARDENED_CLAY).setHardness(1.6f).setCreativeTab(Soot.creativeTab);
        registerBlock("sealed_keg",sealedKeg,new ItemBlock(sealedKeg));
        registerBlock("sealed_pillar",sealedPillar,new ItemBlock(sealedPillar));

        Block wroughtTile = new Block(Material.IRON, MapColor.GRAY).setHardness(1.6f).setCreativeTab(Soot.creativeTab);
        Block wroughtPlatform = new Block(Material.IRON, MapColor.GRAY).setHardness(1.6f).setCreativeTab(Soot.creativeTab);
        Block wroughtPlatformSlab = new BlockModSlab(Material.IRON, MapColor.GRAY).setHardness(1.6f).setCreativeTab(Soot.creativeTab);
        registerBlock("wrought_tile",wroughtTile,new ItemBlock(wroughtTile));
        registerBlock("wrought_platform",wroughtPlatform,new ItemBlock(wroughtPlatform));
        registerBlock("wrought_platform_slab",wroughtPlatformSlab,new ItemBlockSlab(wroughtPlatformSlab,wroughtPlatform));
    }

    public static void registerOverrides() {
        if (Config.OVERRIDE_STAMPER) {
            BlockStamperImproved stamperImproved = (BlockStamperImproved) new BlockStamperImproved(Material.ROCK, "stamper", true).setIsFullCube(false).setIsOpaqueCube(false).setHarvestProperties("pickaxe", 0).setHardness(1.0F);
            registerBlock(stamperImproved, false);
            registerItem(stamperImproved.getItemBlock(), false);
        }
        if (Config.OVERRIDE_MIXER) {
            BlockMixerImproved mixerImproved = (BlockMixerImproved) new BlockMixerImproved(Material.ROCK, "mixer", true).setIsFullCube(false).setIsOpaqueCube(false).setHarvestProperties("pickaxe", 0).setHardness(1.0F);
            registerBlock(mixerImproved, false);
            registerItem(mixerImproved.getItemBlock(), false);
        }
        if (Config.OVERRIDE_DAWNSTONE_ANVIL) {
            BlockDawnstoneAnvilImproved dawnstoneAnvilImproved = (BlockDawnstoneAnvilImproved) new BlockDawnstoneAnvilImproved(Material.ROCK, "dawnstone_anvil", true).setHarvestProperties("pickaxe", 1).setIsFullCube(false).setIsOpaqueCube(false).setHardness(1.6f).setLightOpacity(0);
            registerBlock(dawnstoneAnvilImproved, false);
            registerItem(dawnstoneAnvilImproved.getItemBlock(), false);
        }
        if (Config.OVERRIDE_ALCHEMY_TABLET) {
            BlockAlchemyTabletImproved alchemyTabletImproved = (BlockAlchemyTabletImproved) new BlockAlchemyTabletImproved(Material.ROCK, "alchemy_tablet", true).setIsFullCube(false).setIsOpaqueCube(false).setHarvestProperties("pickaxe", 0).setHardness(1.6F);
            registerBlock(alchemyTabletImproved, false);
            registerItem(alchemyTabletImproved.getItemBlock(), false);
        }
        if (Config.OVERRIDE_ALCHEMY_PEDESTAL) {
            BlockAlchemyPedestalImproved alchemyPedestalImproved = (BlockAlchemyPedestalImproved) new BlockAlchemyPedestalImproved(Material.ROCK, "alchemy_pedestal", true).setIsFullCube(false).setIsOpaqueCube(false).setHarvestProperties("pickaxe", 0).setHardness(1.6F);
            registerBlock(alchemyPedestalImproved, false);
            registerItem(alchemyPedestalImproved.getItemBlock(), false);
        }
        if (Config.OVERRIDE_HEARTH_COIL) {
            BlockHeatCoilImproved heatCoilImproved = (BlockHeatCoilImproved) new BlockHeatCoilImproved(Material.ROCK, "heat_coil", true).setIsFullCube(false).setIsOpaqueCube(false).setHarvestProperties("pickaxe", 0).setHardness(1.0F);
            registerBlock(heatCoilImproved, false);
            registerItem(heatCoilImproved.getItemBlock(), false);
        }
        if (Config.OVERRIDE_MECH_ACCESSOR) {
            BlockMechAccessorImproved accessorImproved = (BlockMechAccessorImproved) new BlockMechAccessorImproved(Material.ROCK, "mech_accessor", true).setIsFullCube(false).setIsOpaqueCube(false).setHarvestProperties("pickaxe", 0).setHardness(1.0F);
            registerBlock(accessorImproved, false);
            registerItem(accessorImproved.getItemBlock(), false);
        }
    }

    public static void registerFluids() {
        //For creating alcohol. All made in Melter, so very hot.
        FluidRegistry.registerFluid(new FluidBooze("boiling_wort", new ResourceLocation(Soot.MODID, "blocks/wort"), new ResourceLocation(Soot.MODID, "blocks/wort_flowing")).setTemperature(500));
        FluidRegistry.registerFluid(new FluidBooze("boiling_potato_juice", new ResourceLocation(Soot.MODID, "blocks/potato_juice"), new ResourceLocation(Soot.MODID, "blocks/potato_juice_flowing")).setTemperature(500));
        FluidRegistry.registerFluid(new FluidBooze("boiling_wormwood", new ResourceLocation(Soot.MODID, "blocks/verdigris"), new ResourceLocation(Soot.MODID, "blocks/verdigris_flowing")).setTemperature(500));
        FluidRegistry.registerFluid(new FluidBooze("boiling_beetroot_soup", new ResourceLocation(Soot.MODID, "blocks/beetsoup"), new ResourceLocation(Soot.MODID, "blocks/beetsoup_flowing")).setTemperature(500));
        //Alcohol itself. Cold.
        FluidRegistry.registerFluid(new FluidBooze("dwarven_ale", new ResourceLocation(Soot.MODID, "blocks/ale"), new ResourceLocation(Soot.MODID, "blocks/ale_flowing")));
        FluidRegistry.registerFluid(new FluidBooze("vodka", new ResourceLocation(Soot.MODID, "blocks/vodka"), new ResourceLocation(Soot.MODID, "blocks/vodka_flowing")));
        FluidRegistry.registerFluid(new FluidBooze("inner_fire", new ResourceLocation(Soot.MODID, "blocks/inner_fire"), new ResourceLocation(Soot.MODID, "blocks/inner_fire_flowing")));
        FluidRegistry.registerFluid(new FluidBooze("umber_ale", new ResourceLocation(Soot.MODID, "blocks/umber_ale"), new ResourceLocation(Soot.MODID, "blocks/umber_ale_flowing")));
        FluidRegistry.registerFluid(new FluidBooze("methanol", new ResourceLocation(Soot.MODID, "blocks/methanol"), new ResourceLocation(Soot.MODID, "blocks/methanol_flowing")));
        FluidRegistry.registerFluid(new FluidBooze("absinthe", new ResourceLocation(Soot.MODID, "blocks/absinthe"), new ResourceLocation(Soot.MODID, "blocks/absinthe_flowing")));
        FluidRegistry.registerFluid(new FluidBooze("snowpoff", new ResourceLocation(Soot.MODID, "blocks/snowpoff"), new ResourceLocation(Soot.MODID, "blocks/snowpoff_flowing")));
        //Alchemy Fluids
        registerFluid(new FluidMolten("antimony", new ResourceLocation(Soot.MODID, "blocks/molten_antimony"), new ResourceLocation(Soot.MODID, "blocks/molten_antimony_flowing")), true);
        registerFluid(new FluidMolten("sugar", new ResourceLocation(Soot.MODID, "blocks/molten_sugar"), new ResourceLocation(Soot.MODID, "blocks/molten_sugar_flowing")), true);
        registerFluid(new FluidMolten("alchemical_redstone", new ResourceLocation(Embers.MODID, "blocks/alchemic_slurry_still"), new ResourceLocation(Embers.MODID, "blocks/alchemic_slurry_flowing")), true);
    }

    private static void registerFluid(Fluid fluid, boolean withBucket) {
        FluidRegistry.registerFluid(fluid);
        if (withBucket)
            FluidRegistry.addBucketForFluid(fluid);
    }

    public static void registerBlockModels() {
        for (Block block : MODELLED_BLOCKS) {
            Soot.proxy.registerBlockModel(block);
        }
    }

    public static void registerItemModels() {
        for (Item item : MODELLED_ITEMS) {
            Soot.proxy.registerItemModel(item);
        }
    }

    public static void registerBlock(String id, Block block, ItemBlock itemBlock) {
        registerBlock(id, block);
        registerItem(id, itemBlock);
    }

    public static void registerBlock(String id, Block block) {
        if(block.getRegistryName() == null)
            block.setRegistryName(Soot.MODID, id);
        block.setUnlocalizedName(id);
        registerBlock(block, true);
    }

    public static void registerBlock(Block block, boolean hasmodel) {
        BLOCKS.add(block);
        if (hasmodel)
            MODELLED_BLOCKS.add(block);
    }

    public static void registerItem(String id, Item item) {
        item.setRegistryName(Soot.MODID, id);
        item.setUnlocalizedName(id);
        registerItem(item, true);
    }

    public static void registerItem(Item item, boolean hasmodel) {
        ITEMS.add(item);
        if (hasmodel)
            MODELLED_ITEMS.add(item);
    }

    public static void registerTileEntities() {
        registerTileEntity(TileEntityEmberBurst.class);
        registerTileEntity(TileEntityEmberFunnel.class);

        registerTileEntity(TileEntityRedstoneBin.class);

        registerTileEntity(TileEntityStillBase.class);
        registerTileEntity(TileEntityStillTip.class);

        registerTileEntity(TileEntityStamperImproved.class);
        registerTileEntity(TileEntityMixerBottomImproved.class);
        registerTileEntity(TileEntityDawnstoneAnvilImproved.class);
        registerTileEntity(TileEntityHeatCoilImproved.class);
        registerTileEntity(TileEntityAlchemyTabletImproved.class);
        registerTileEntity(TileEntityAlchemyPedestalImproved.class);
        registerTileEntity(TileEntityMechAccessorImproved.class);

        registerTileEntity(TileEntityAlchemyGlobe.class);
        registerTileEntity(TileEntityInsulation.class);
        registerTileEntity(TileEntityCatalyticPlug.class);
        registerTileEntity(TileEntityDistillationPipe.class);
    }

    private static void registerEntities() {
        EntityRegistry.registerModEntity(new ResourceLocation(Soot.MODID, "firecloud"), EntityFireCloud.class, "firecloud", 0, Soot.instance, 80, 1, true);
        EntityRegistry.registerModEntity(new ResourceLocation(Soot.MODID, "snowpoff"), EntitySnowpoff.class, "snowpoff", 1, Soot.instance, 80, 1, true);
        EntityRegistry.registerModEntity(new ResourceLocation(Soot.MODID, "muse"), EntityMuse.class, "muse", 2, Soot.instance, 80, 1, true);
    }

    public static void registerCapabilities() {
        CapabilityManager.INSTANCE.register(IUpgradeProvider.class, new Capability.IStorage<IUpgradeProvider>() {
            @Nullable
            @Override
            public NBTBase writeNBT(Capability<IUpgradeProvider> capability, IUpgradeProvider instance, EnumFacing side) {
                return null;
            }

            @Override
            public void readNBT(Capability<IUpgradeProvider> capability, IUpgradeProvider instance, EnumFacing side, NBTBase nbt) {
                //NOOP
            }
        }, () -> {
            return new CapabilityUpgradeProvider("none", null);
        });
    }

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        for (Block block : BLOCKS) {
            event.getRegistry().register(block);
        }
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        for (Item item : ITEMS) {
            event.getRegistry().register(item);
        }
    }

    @SubscribeEvent
    public static void registerPotions(RegistryEvent.Register<Potion> event) {
        event.getRegistry().register(new PotionAle().setRegistryName(Soot.MODID, "ale"));
        event.getRegistry().register(new PotionTipsy().setRegistryName(Soot.MODID, "tipsy"));
        event.getRegistry().register(new PotionStoutness().setRegistryName(Soot.MODID, "stoutness"));
        event.getRegistry().register(new PotionInnerFire().setRegistryName(Soot.MODID, "inner_fire"));
        event.getRegistry().register(new PotionFireLung().setRegistryName(Soot.MODID, "fire_lung"));
        event.getRegistry().register(new PotionSteadfast().setRegistryName(Soot.MODID, "steadfast"));
        event.getRegistry().register(new PotionLifedrinker().setRegistryName(Soot.MODID, "lifedrinker"));
        event.getRegistry().register(new PotionSnowpoff().setRegistryName(Soot.MODID, "snowpoff"));
        event.getRegistry().register(new PotionInspiration().setRegistryName(Soot.MODID, "inspiration"));
    }

    private static void registerTileEntity(Class<? extends TileEntity> tile) {
        GameRegistry.registerTileEntity(tile, tile.getSimpleName().toLowerCase());
    }
}
