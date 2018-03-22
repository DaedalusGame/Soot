package soot;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
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
import soot.block.*;
import soot.capability.CapabilityUpgradeProvider;
import soot.capability.IUpgradeProvider;
import soot.entity.EntityFireCloud;
import soot.fluids.FluidBooze;
import soot.fluids.FluidMolten;
import soot.item.ItemMug;
import soot.item.ItemStill;
import soot.potion.*;
import soot.tile.*;
import soot.util.CaskManager;
import soot.util.CaskManager.CaskLiquid;
import soot.util.Nope;
import teamroots.embers.Embers;
import teamroots.embers.block.BlockHeatCoil;
import teamroots.embers.tileentity.*;

import javax.annotation.Nullable;
import java.util.ArrayList;

public class Registry {
    private static ArrayList<Block> MODELLED_BLOCKS = new ArrayList<>();
    private static ArrayList<Item> MODELLED_ITEMS = new ArrayList<>();
    private static ArrayList<Block> BLOCKS = new ArrayList<>();
    private static ArrayList<Item> ITEMS = new ArrayList<>();

    @GameRegistry.ObjectHolder("soot:alchemy_globe")
    public static BlockAlchemyGlobe ALCHEMY_GLOBE;
    @GameRegistry.ObjectHolder("soot:still")
    public static BlockStill STILL;

    @GameRegistry.ObjectHolder("soot:signet_antimony")
    public static Item SIGNET_ANTIMONY;
    @GameRegistry.ObjectHolder("soot:ingot_antimony")
    public static Item INGOT_ANTIMONY;

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
    @GameRegistry.ObjectHolder("soot:lifedrinker")
    public static Potion POTION_LIFEDRINKER;

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

    //Alchemy
    public static Fluid MOLTEN_ANTIMONY;
    public static Fluid MOLTEN_SUGAR;
    public static Fluid MOLTEN_REDSTONE;

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
    }

    public static void registerCaskLiquids() {
        BOILING_WORT = FluidRegistry.getFluid("boiling_wort");
        BOILING_POTATO_JUICE = FluidRegistry.getFluid("boiling_potato_juice");
        BOILING_WORMWOOD = FluidRegistry.getFluid("boiling_wormwood");
        BOILING_BEETROOT_SOUP = FluidRegistry.getFluid("boiling_beetroot_soup");

        ALE = FluidRegistry.getFluid("ale");
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
        CaskManager.register(new CaskLiquid(ABSINTHE, 1, 0xFF58FF2E));
        CaskManager.register(new CaskLiquid(METHANOL, 1, 0xFF666633).addEffect(new PotionEffect(POTION_FIRE_LUNG, 200, 0), 2));
        CaskManager.register(new CaskLiquid(SNOWPOFF_VODKA, 2, 0xFFC3E6F7));
    }

    public static void registerAccessorTiles() {
        TileEntityMechAccessorImproved.registerAccessibleTile(TileEntityMechCore.class);
        TileEntityMechAccessorImproved.registerAccessibleTile(TileEntityMixerBottom.class);
        TileEntityMechAccessorImproved.registerAccessibleTile(TileEntityActivatorBottom.class);
        TileEntityMechAccessorImproved.registerAccessibleTile(TileEntityFurnaceBottom.class);
        TileEntityMechAccessorImproved.registerAccessibleTile(TileEntityBoilerBottom.class);
    }

    public static void registerBlocks() {
        Nope.shutupForge(Registry::registerOverrides);

        BlockEmberBurst emberBurst = (BlockEmberBurst) new BlockEmberBurst(Material.ROCK).setCreativeTab(Soot.creativeTab);
        BlockEmberFunnel emberFunnel = (BlockEmberFunnel) new BlockEmberFunnel(Material.ROCK).setCreativeTab(Soot.creativeTab);
        registerBlock("ember_burst", emberBurst, new ItemBlock(emberBurst));
        registerBlock("ember_funnel", emberFunnel, new ItemBlock(emberFunnel));

        BlockAlchemyGlobe alchemyGlobe = (BlockAlchemyGlobe) new BlockAlchemyGlobe(Material.ROCK).setCreativeTab(Soot.creativeTab);
        registerBlock("alchemy_globe", alchemyGlobe, new ItemBlock(alchemyGlobe));

        registerItem("signet_antimony", new Item().setCreativeTab(Soot.creativeTab));
        registerItem("ingot_antimony", new Item().setCreativeTab(Soot.creativeTab));
        registerItem("mug", new ItemMug().setCreativeTab(Soot.creativeTab));

        BlockStill still = (BlockStill) new BlockStill().setHardness(1.6f).setLightOpacity(0).setCreativeTab(Soot.creativeTab);
        registerBlock("still", still, new ItemStill(still));
    }

    public static void registerOverrides() {
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
        FluidRegistry.registerFluid(new FluidBooze("ale", new ResourceLocation(Soot.MODID, "blocks/ale"), new ResourceLocation(Soot.MODID, "blocks/ale_flowing")));
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
        block.setRegistryName(Soot.MODID, id);
        block.setUnlocalizedName(id);
        registerBlock(block, true);
        registerItem(id, itemBlock);
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

        registerTileEntity(TileEntityAlchemyGlobe.class);
        registerTileEntity(TileEntityStillBase.class);
        registerTileEntity(TileEntityStillTip.class);

        registerTileEntity(TileEntityMixerBottomImproved.class);
        registerTileEntity(TileEntityDawnstoneAnvilImproved.class);
        registerTileEntity(TileEntityHeatCoilImproved.class);
        registerTileEntity(TileEntityAlchemyTabletImproved.class);
        registerTileEntity(TileEntityMechAccessorImproved.class);
    }

    private static void registerEntities() {
        EntityRegistry.registerModEntity(new ResourceLocation(Soot.MODID, "fireCloud"), EntityFireCloud.class, "fireCloud", 0, Soot.instance, 80, 1, true);
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
        event.getRegistry().register(new PotionStoutness().setRegistryName(Soot.MODID, "stoutness"));
        event.getRegistry().register(new PotionInnerFire().setRegistryName(Soot.MODID, "inner_fire"));
        event.getRegistry().register(new PotionFireLung().setRegistryName(Soot.MODID, "fire_lung"));
        event.getRegistry().register(new PotionSteadfast().setRegistryName(Soot.MODID, "steadfast"));
        event.getRegistry().register(new PotionLifedrinker().setRegistryName(Soot.MODID, "lifedrinker"));
    }

    private static void registerTileEntity(Class<? extends TileEntity> tile) {
        GameRegistry.registerTileEntity(tile, tile.getSimpleName().toLowerCase());
    }
}
