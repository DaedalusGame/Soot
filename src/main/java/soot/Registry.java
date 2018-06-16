package soot;

import crafttweaker.CraftTweakerAPI;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import soot.block.*;
import soot.block.overrides.*;
import soot.entity.EntityFireCloud;
import soot.entity.EntityMuse;
import soot.entity.EntitySnowpoff;
import soot.fluids.FluidBooze;
import soot.fluids.FluidMolten;
import soot.item.*;
import soot.potion.*;
import soot.tile.*;
import soot.tile.overrides.*;
import soot.util.CaskManager;
import soot.util.CaskManager.CaskLiquid;
import soot.util.HeatManager;
import soot.util.Nope;
import teamroots.embers.Embers;
import teamroots.embers.RegistryManager;
import teamroots.embers.research.ResearchBase;
import teamroots.embers.research.ResearchCategory;
import teamroots.embers.research.ResearchManager;
import teamroots.embers.tileentity.*;
import teamroots.embers.upgrade.UpgradeCatalyticPlug;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

public class Registry {
    public static ArrayList<Block> MODELLED_BLOCKS = new ArrayList<>();
    public static ArrayList<Item> MODELLED_ITEMS = new ArrayList<>();
    public static ArrayList<Block> BLOCKS = new ArrayList<>();
    public static ArrayList<Item> ITEMS = new ArrayList<>();
    public static ArrayList<Fluid> FLUIDS = new ArrayList<>();
    private static ArrayList<Runnable> WRITEBACKS = new ArrayList<>();

    public static Item.ToolMaterial EITR_TOOL_MATERIAL = EnumHelper.addToolMaterial(Soot.MODID+":eitr", 2, 512, 7.5f, 0.0f, 24);

    @GameRegistry.ObjectHolder("soot:alchemy_globe")
    public static BlockAlchemyGlobe ALCHEMY_GLOBE;
    @GameRegistry.ObjectHolder("soot:still")
    public static BlockStill STILL;
    @GameRegistry.ObjectHolder("soot:alchemy_gauge")
    public static BlockAlchemyGauge ALCHEMY_GAUGE;
    @GameRegistry.ObjectHolder("soot:ember_burst")
    public static BlockEmberBurst EMBER_BURST;
    @GameRegistry.ObjectHolder("soot:distillation_pipe")
    public static BlockDistillationPipe DISTILLATION_PIPE;
    @GameRegistry.ObjectHolder("soot:insulation")
    public static BlockInsulation INSULATION;

    @GameRegistry.ObjectHolder("soot:redstone_bin")
    public static BlockRedstoneBin REDSTONE_BIN;
    @GameRegistry.ObjectHolder("soot:scale")
    public static BlockScale SCALE;

    @GameRegistry.ObjectHolder("soot:sulfur_ore")
    public static BlockSulfurOre SULFUR_ORE;
    @GameRegistry.ObjectHolder("soot:caminite_clay")
    public static Block CAMINITE_CLAY;
    @GameRegistry.ObjectHolder("soot:caminite_large_tile")
    public static Block CAMINITE_LARGE_TILE;

    @GameRegistry.ObjectHolder("soot:signet_antimony")
    public static Item SIGNET_ANTIMONY;
    @GameRegistry.ObjectHolder("soot:ingot_antimony")
    public static Item INGOT_ANTIMONY;
    @GameRegistry.ObjectHolder("soot:mug")
    public static ItemMug MUG;
    @GameRegistry.ObjectHolder("soot:stamp_text_raw")
    public static Item STAMP_TEXT_RAW;
    @GameRegistry.ObjectHolder("soot:stamp_text")
    public static Item STAMP_TEXT;
    @GameRegistry.ObjectHolder("soot:stamp_nugget_raw")
    public static Item STAMP_NUGGET_RAW;
    @GameRegistry.ObjectHolder("soot:stamp_nugget")
    public static Item STAMP_NUGGET;
    @GameRegistry.ObjectHolder("soot:sulfur")
    public static Item SULFUR;
    @GameRegistry.ObjectHolder("soot:sulfur_clump")
    public static ItemSulfurClump SULFUR_CLUMP;

    @GameRegistry.ObjectHolder("soot:eitr")
    public static ItemEitr EITR;

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
    }

    public static void init() {
        registerCaskLiquids();
        registerAccessorTiles();

        HeatManager.register(RegistryManager.archaic_light,20);
        HeatManager.register(Blocks.FIRE,10);
        HeatManager.register(RegistryManager.heat_coil, (world, pos, state) -> {
            TileEntity tile = world.getTileEntity(pos);
            if(tile instanceof TileEntityHeatCoil) {
                double heat = ((TileEntityHeatCoil) tile).heat;
                return heat > TileEntityHeatCoil.MAX_HEAT / 2 ? heat : 0;
            }
            return 0;
        });

        EITR_TOOL_MATERIAL.setRepairItem(new ItemStack(SULFUR));

        UpgradeCatalyticPlug.registerBlacklistedTile(TileEntityStillBase.class);
    }

    public static void postInit() {
        initResearches();
    }


    public static void initResearches() {
        ResearchCategory categoryWorld = null;
        ResearchCategory categoryMechanisms = null;
        ResearchCategory categoryMetallurgy = null;
        ResearchCategory categoryAlchemy = null;
        ResearchCategory categorySmithing = null;
        ResearchCategory categoryBrewing = new ResearchCategory("brewing",new ResourceLocation(Soot.MODID,"textures/gui/codex_index.png"), 192.0, 16.0);

        for(ResearchCategory category : ResearchManager.researches) {
            if(category.name.equals("world"))
                categoryWorld = category;
            if(category.name.equals("mechanisms"))
                categoryMechanisms = category;
            if(category.name.equals("metallurgy"))
                categoryMetallurgy = category;
            if(category.name.equals("alchemy"))
                categoryAlchemy = category;
            if(category.name.equals("smithing"))
                categorySmithing = category;
        }
        ResearchManager.researches.add(categoryBrewing);
        categoryWorld.addResearch(new ResearchBase("sulfur",new ItemStack(SULFUR_CLUMP),12.0,0.0));
        categoryWorld.addResearch(new ResearchBase("redstone_bin",new ItemStack(REDSTONE_BIN),12.0,5.0));
        categoryMechanisms.addResearch(new ResearchBase("insulation",new ItemStack(INSULATION), 12.0D, 0.0D).addAncestor(ResearchManager.hearth_coil));
        categoryMetallurgy.addResearch(new ResearchBase("advanced_emitters",new ItemStack(EMBER_BURST),0.0D, 4.0D));
        categoryMetallurgy.addResearch(new ResearchBase("scale",new ItemStack(SCALE),12.0D, 5.0D).addAncestor(ResearchManager.alchemy));
        categoryAlchemy.addResearch(new ResearchBase("eitr",new ItemStack(EITR), 4.0, 4.0).addAncestor(ResearchManager.waste));
        ResearchBase still = new ResearchBase("still", new ItemStack(STILL), 6.0D, 4.0D);
        categoryBrewing.addResearch(still);
        categoryBrewing.addResearch(new ResearchBase("distillation_pipe",new ItemStack(DISTILLATION_PIPE), 8.0D, 7.0D).addAncestor(still));
        categoryBrewing.addResearch(new ResearchBase("still_fuel",new ItemStack(RegistryManager.archaic_light), 6.0D, 7.0D).addAncestor(still));
        categoryBrewing.addResearch(new ResearchBase("alchemy_dial",new ItemStack(ALCHEMY_GAUGE), 7.0D, 2.0D).addAncestor(still));
        Random random = new Random();
        CaskLiquid liquid = CaskManager.liquids.get(random.nextInt(CaskManager.liquids.size()));
        ResearchBase drinks = new ResearchBase("drinks", MUG.getFilled(liquid), 4.0D, 7.0D);
        categoryBrewing.addResearch(drinks);
        categoryBrewing.addResearch(new ResearchBase("rename",new ItemStack(Items.SIGN), 1.0D, 7.0D).addAncestor(drinks));
        categoryBrewing.addResearch(new ResearchBase("lifedrinker",new ItemStack(Items.GHAST_TEAR), 11.0D, 7.0D).addAncestor(still));
        categoryBrewing.addResearch(new ResearchBase("steadfast",new ItemStack(Items.GOLDEN_CARROT), 0.0D, 5.0D).addAncestor(still));
        categoryBrewing.addResearch(new ResearchBase("duration",new ItemStack(Items.REDSTONE), 0.0D, 3.0D).addAncestor(still));
        categoryBrewing.addResearch(new ResearchBase("purification",new ItemStack(Items.PRISMARINE_CRYSTALS), 1.0D, 1.0D).addAncestor(still));
        categoryBrewing.addResearch(new ResearchBase("healing",new ItemStack(Items.NETHER_WART), 11.0D, 1.0D).addAncestor(still));
        categoryBrewing.addResearch(new ResearchBase("cooling",new ItemStack(Blocks.ICE), 12.0D, 3.0D).addAncestor(still));
        categoryBrewing.addResearch(new ResearchBase("stew",new ItemStack(Items.POTATO), 12.0D, 5.0D).addAncestor(still));
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
        SNOWPOFF_VODKA = FluidRegistry.getFluid("snowpoff");

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
        TileEntityMechAccessorImproved.registerAccessibleTile(TileEntityStillBase.class);
    }

    public static void registerBlocks() {
        Nope.shutupForge(Registry::registerOverrides);

        BlockSulfurOre sulfurOre = (BlockSulfurOre) new BlockSulfurOre(Material.ROCK).setHardness(1.6f).setCreativeTab(Soot.creativeTab);
        registerBlock("sulfur_ore", sulfurOre, new ItemBlock(sulfurOre));

        BlockEmberBurst emberBurst = (BlockEmberBurst) new BlockEmberBurst(Material.ROCK).setCreativeTab(Soot.creativeTab);
        BlockEmberFunnel emberFunnel = (BlockEmberFunnel) new BlockEmberFunnel(Material.ROCK).setCreativeTab(Soot.creativeTab);
        registerBlock("ember_burst", emberBurst, new ItemBlock(emberBurst));
        registerBlock("ember_funnel", emberFunnel, new ItemBlock(emberFunnel));

        BlockRedstoneBin redstoneBin = (BlockRedstoneBin) new BlockRedstoneBin(Material.IRON,"redstone_bin").setIsFullCube(false).setIsOpaqueCube(false).setHarvestProperties("pickaxe",0).setHardness(1f).setCreativeTab(Soot.creativeTab);
        registerBlock("redstone_bin", redstoneBin, new ItemBlock(redstoneBin));
        BlockScale scale = (BlockScale) new BlockScale(Material.IRON).setHardness(1.6f).setLightOpacity(0).setCreativeTab(Soot.creativeTab);
        registerBlock("scale", scale, new ItemBlock(scale));

        BlockAlchemyGlobe alchemyGlobe = (BlockAlchemyGlobe) new BlockAlchemyGlobe(Material.ROCK).setHardness(1.6f).setLightOpacity(0).setCreativeTab(Soot.creativeTab);
        registerBlock("alchemy_globe", alchemyGlobe, new ItemBlock(alchemyGlobe));
        BlockInsulation insulation = (BlockInsulation) new BlockInsulation(Material.ROCK).setHardness(1.6f).setLightOpacity(1).setCreativeTab(Soot.creativeTab);
        registerBlock("insulation", insulation, new ItemBlock(insulation));
        BlockDistillationPipe distillationPipe = (BlockDistillationPipe) new BlockDistillationPipe(Material.IRON).setHardness(1.6f).setLightOpacity(1).setCreativeTab(Soot.creativeTab);
        registerBlock("distillation_pipe", distillationPipe, new ItemBlock(distillationPipe));

        BlockAlchemyGauge alchemyGauge = (BlockAlchemyGauge) new BlockAlchemyGauge(Material.IRON).setHardness(1.6f).setLightOpacity(0).setCreativeTab(Soot.creativeTab);
        registerBlock("alchemy_gauge", alchemyGauge, new ItemBlock(alchemyGauge));

        registerItem("signet_antimony", new Item().setCreativeTab(Soot.creativeTab));
        registerItem("ingot_antimony", new Item().setCreativeTab(Soot.creativeTab));
        registerItem("mug", new ItemMug().setCreativeTab(Soot.creativeTab));
        registerItem("stamp_text_raw", new Item().setCreativeTab(Soot.creativeTab));
        registerItem("stamp_text", new Item().setCreativeTab(Soot.creativeTab));
        registerItem("stamp_nugget_raw", new Item().setCreativeTab(Soot.creativeTab));
        registerItem("stamp_nugget", new Item().setCreativeTab(Soot.creativeTab));
        registerItem("sulfur", new Item().setCreativeTab(Soot.creativeTab));
        registerItem("sulfur_clump", new ItemSulfurClump().setCreativeTab(Soot.creativeTab));
        registerItem("eitr", new ItemEitr(EITR_TOOL_MATERIAL).setCreativeTab(Soot.creativeTab));

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

        Block archaicTileSlab = new BlockModSlab(Material.ROCK, MapColor.BROWN_STAINED_HARDENED_CLAY).setHardness(1.6f).setCreativeTab(Soot.creativeTab);
        Block archaicTileStairs = new BlockModStairs(RegistryManager.archaic_tile.getDefaultState()).setHardness(1.6f).setCreativeTab(Soot.creativeTab);
        registerBlock("archaic_tile_slab",archaicTileSlab,new ItemBlockSlab(archaicTileSlab, RegistryManager.archaic_tile));
        registerBlock("archaic_tile_stairs",archaicTileStairs,new ItemBlock(archaicTileStairs));

        Block archaicBigBrick = new Block(Material.ROCK, MapColor.BROWN_STAINED_HARDENED_CLAY).setHardness(1.6f).setCreativeTab(Soot.creativeTab);
        Block archaicBigBrickSlab = new BlockModSlab(Material.ROCK, MapColor.BROWN_STAINED_HARDENED_CLAY).setHardness(1.6f).setCreativeTab(Soot.creativeTab);
        Block archaicBigBrickStairs = new BlockModStairs(archaicBigBrick.getDefaultState()).setHardness(1.6f).setCreativeTab(Soot.creativeTab);
        registerBlock("archaic_big_bricks",archaicBigBrick,new ItemBlock(archaicBigBrick));
        registerBlock("archaic_big_bricks_slab",archaicBigBrickSlab,new ItemBlockSlab(archaicBigBrickSlab,archaicBigBrick));
        registerBlock("archaic_big_bricks_stairs",archaicBigBrickStairs,new ItemBlock(archaicBigBrickStairs));

        Block archaicBrickSlab = new BlockModSlab(Material.ROCK, MapColor.BROWN_STAINED_HARDENED_CLAY).setHardness(1.6f).setCreativeTab(Soot.creativeTab);
        Block archaicBrickStairs = new BlockModStairs(RegistryManager.archaic_bricks.getDefaultState()).setHardness(1.6f).setCreativeTab(Soot.creativeTab);
        registerBlock("archaic_bricks_slab",archaicBrickSlab,new ItemBlockSlab(archaicBrickSlab, RegistryManager.archaic_bricks));
        registerBlock("archaic_bricks_stairs",archaicBrickStairs,new ItemBlock(archaicBrickStairs));

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
        if (Config.OVERRIDE_BORE) {
            BlockEmberBoreImproved boreImproved = (BlockEmberBoreImproved) new BlockEmberBoreImproved(Material.ROCK, "ember_bore", true).setIsFullCube(false).setIsOpaqueCube(false).setHarvestProperties("pickaxe", 0).setHardness(1.0F);
            registerBlock(boreImproved, false);
        }
        if (Config.OVERRIDE_STAMPER) {
            BlockStamperImproved stamperImproved = (BlockStamperImproved) new BlockStamperImproved(Material.ROCK, "stamper", true).setIsFullCube(false).setIsOpaqueCube(false).setHarvestProperties("pickaxe", 0).setHardness(1.0F);
            registerBlock(stamperImproved, false);
        }
        if (Config.OVERRIDE_MIXER) {
            BlockMixerImproved mixerImproved = (BlockMixerImproved) new BlockMixerImproved(Material.ROCK, "mixer", true).setIsFullCube(false).setIsOpaqueCube(false).setHarvestProperties("pickaxe", 0).setHardness(1.0F);
            registerBlock(mixerImproved, false);
        }
        if (Config.OVERRIDE_DAWNSTONE_ANVIL) {
            BlockDawnstoneAnvilImproved dawnstoneAnvilImproved = (BlockDawnstoneAnvilImproved) new BlockDawnstoneAnvilImproved(Material.ROCK, "dawnstone_anvil", true).setHarvestProperties("pickaxe", 1).setIsFullCube(false).setIsOpaqueCube(false).setHardness(1.6f).setLightOpacity(0);
            registerBlock(dawnstoneAnvilImproved, false);
        }
        if (Config.OVERRIDE_BEAM_CANNON) {
            BlockBeamCannonImproved beamCannonImproved = (BlockBeamCannonImproved) new BlockBeamCannonImproved(Material.ROCK, "beam_cannon", true).setIsFullCube(false).setIsOpaqueCube(false).setHarvestProperties("pickaxe", 0).setHardness(1.6F);
            registerBlock(beamCannonImproved, false);
        }
        if (Config.OVERRIDE_ALCHEMY_TABLET) {
            BlockAlchemyTabletImproved alchemyTabletImproved = (BlockAlchemyTabletImproved) new BlockAlchemyTabletImproved(Material.ROCK, "alchemy_tablet", true).setIsFullCube(false).setIsOpaqueCube(false).setHarvestProperties("pickaxe", 0).setHardness(1.6F);
            registerBlock(alchemyTabletImproved, false);
        }
        if (Config.OVERRIDE_ALCHEMY_PEDESTAL) {
            BlockAlchemyPedestalImproved alchemyPedestalImproved = (BlockAlchemyPedestalImproved) new BlockAlchemyPedestalImproved(Material.ROCK, "alchemy_pedestal", true).setIsFullCube(false).setIsOpaqueCube(false).setHarvestProperties("pickaxe", 0).setHardness(1.6F);
            registerBlock(alchemyPedestalImproved, false);
        }
        if (Config.OVERRIDE_HEARTH_COIL) {
            BlockHeatCoilImproved heatCoilImproved = (BlockHeatCoilImproved) new BlockHeatCoilImproved(Material.ROCK, "heat_coil", true).setIsFullCube(false).setIsOpaqueCube(false).setHarvestProperties("pickaxe", 0).setHardness(1.0F);
            registerBlock(heatCoilImproved, false);
        }
        if (Config.OVERRIDE_MECH_ACCESSOR) {
            BlockMechAccessorImproved accessorImproved = (BlockMechAccessorImproved) new BlockMechAccessorImproved(Material.ROCK, "mech_accessor", true).setIsFullCube(false).setIsOpaqueCube(false).setHarvestProperties("pickaxe", 0).setHardness(1.0F);
            registerBlock(accessorImproved, false);
        }
        if (Config.OVERRIDE_CRYSTAL_CELL) {
            BlockCrystalCellImproved crystalCellImproved = (BlockCrystalCellImproved) new BlockCrystalCellImproved(Material.ROCK, "crystal_cell", true).setIsFullCube(false).setIsOpaqueCube(false).setHarvestProperties("pickaxe", 0).setHardness(1.0F);
            registerBlock(crystalCellImproved, false);
        }
    }

    public static void registerFluids() {
        //For creating alcohol. All made in Melter, so very hot.
        registerFluid(new FluidBooze("boiling_wort", new ResourceLocation(Soot.MODID, "blocks/wort"), new ResourceLocation(Soot.MODID, "blocks/wort_flowing")).setTemperature(500), false);
        registerFluid(new FluidBooze("boiling_potato_juice", new ResourceLocation(Soot.MODID, "blocks/potato_juice"), new ResourceLocation(Soot.MODID, "blocks/potato_juice_flowing")).setTemperature(500), false);
        registerFluid(new FluidBooze("boiling_wormwood", new ResourceLocation(Soot.MODID, "blocks/verdigris"), new ResourceLocation(Soot.MODID, "blocks/verdigris_flowing")).setTemperature(500), false);
        registerFluid(new FluidBooze("boiling_beetroot_soup", new ResourceLocation(Soot.MODID, "blocks/beetsoup"), new ResourceLocation(Soot.MODID, "blocks/beetsoup_flowing")).setTemperature(500), false);
        //Alcohol itself. Cold.
        registerFluid(new FluidBooze("dwarven_ale", new ResourceLocation(Soot.MODID, "blocks/ale"), new ResourceLocation(Soot.MODID, "blocks/ale_flowing")), false);
        registerFluid(new FluidBooze("vodka", new ResourceLocation(Soot.MODID, "blocks/vodka"), new ResourceLocation(Soot.MODID, "blocks/vodka_flowing")), false);
        registerFluid(new FluidBooze("inner_fire", new ResourceLocation(Soot.MODID, "blocks/inner_fire"), new ResourceLocation(Soot.MODID, "blocks/inner_fire_flowing")), false);
        registerFluid(new FluidBooze("umber_ale", new ResourceLocation(Soot.MODID, "blocks/umber_ale"), new ResourceLocation(Soot.MODID, "blocks/umber_ale_flowing")), false);
        registerFluid(new FluidBooze("methanol", new ResourceLocation(Soot.MODID, "blocks/methanol"), new ResourceLocation(Soot.MODID, "blocks/methanol_flowing")), false);
        registerFluid(new FluidBooze("absinthe", new ResourceLocation(Soot.MODID, "blocks/absinthe"), new ResourceLocation(Soot.MODID, "blocks/absinthe_flowing")), false);
        registerFluid(new FluidBooze("snowpoff", new ResourceLocation(Soot.MODID, "blocks/snowpoff"), new ResourceLocation(Soot.MODID, "blocks/snowpoff_flowing")), false);
        //Alchemy Fluids
        registerFluid(new FluidMolten("antimony", new ResourceLocation(Soot.MODID, "blocks/molten_antimony"), new ResourceLocation(Soot.MODID, "blocks/molten_antimony_flowing")), true);
        registerFluid(new FluidMolten("sugar", new ResourceLocation(Soot.MODID, "blocks/molten_sugar"), new ResourceLocation(Soot.MODID, "blocks/molten_sugar_flowing")), true);
    }

    private static void registerFluid(Fluid fluid, boolean withBucket) {
        FluidRegistry.registerFluid(fluid);
        if (withBucket)
            FluidRegistry.addBucketForFluid(fluid);
        FLUIDS.add(fluid);
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
        registerTileEntity(TileEntitySulfurOre.class);

        registerTileEntity(TileEntityEmberBurst.class);
        registerTileEntity(TileEntityEmberFunnel.class);

        registerTileEntity(TileEntityRedstoneBin.class);
        registerTileEntity(TileEntityScale.class);

        registerTileEntity(TileEntityStillBase.class);
        registerTileEntity(TileEntityStillTip.class);

        registerTileEntity(TileEntityEmberBoreImproved.class);
        registerTileEntity(TileEntityStamperImproved.class);
        registerTileEntity(TileEntityMixerBottomImproved.class);
        registerTileEntity(TileEntityDawnstoneAnvilImproved.class);
        registerTileEntity(TileEntityHeatCoilImproved.class);
        registerTileEntity(TileEntityBeamCannonImproved.class);
        registerTileEntity(TileEntityAlchemyTabletImproved.class);
        registerTileEntity(TileEntityAlchemyPedestalImproved.class);
        registerTileEntity(TileEntityMechAccessorImproved.class);
        registerTileEntity(TileEntityCrystalCellImproved.class);

        registerTileEntity(TileEntityAlchemyGlobe.class);
        registerTileEntity(TileEntityInsulation.class);
        registerTileEntity(TileEntityDistillationPipe.class);
    }

    private static void registerEntities() {
        EntityRegistry.registerModEntity(new ResourceLocation(Soot.MODID, "firecloud"), EntityFireCloud.class, "firecloud", 0, Soot.instance, 80, 1, true);
        EntityRegistry.registerModEntity(new ResourceLocation(Soot.MODID, "snowpoff"), EntitySnowpoff.class, "snowpoff", 1, Soot.instance, 80, 1, true);
        EntityRegistry.registerModEntity(new ResourceLocation(Soot.MODID, "muse"), EntityMuse.class, "muse", 2, Soot.instance, 80, 1, true);
    }

    public static void registerCapabilities() {
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
        WRITEBACKS.forEach(Runnable::run); //Have to do this possibly. Another mod may depend on embers items.
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
