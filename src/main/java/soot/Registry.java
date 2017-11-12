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
import net.minecraftforge.fml.common.registry.GameRegistry;
import soot.block.*;
import soot.capability.CapabilityUpgradeProvider;
import soot.capability.IUpgradeProvider;
import soot.fluids.FluidMolten;
import soot.item.ItemMug;
import soot.potion.PotionAle;
import soot.potion.PotionFireLung;
import soot.potion.PotionInnerFire;
import soot.potion.PotionStoutness;
import soot.tile.*;
import soot.util.CaskManager;
import soot.util.CaskManager.CaskLiquid;
import soot.util.Nope;

import javax.annotation.Nullable;
import java.util.ArrayList;

public class Registry {
    private static ArrayList<Block> MODELLED_BLOCKS = new ArrayList<>();
    private static ArrayList<Item> MODELLED_ITEMS = new ArrayList<>();
    private static ArrayList<Block> BLOCKS = new ArrayList<>();
    private static ArrayList<Item> ITEMS = new ArrayList<>();

    @GameRegistry.ObjectHolder("soot:alchemy_globe")
    public static BlockAlchemyGlobe ALCHEMY_GLOBE;

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

    //Roots Integration?
    public static Fluid BOILING_ROOT_WATER;
    public static Fluid BOILING_AUBERGE;
    public static Fluid ROOT_BEER;
    public static Fluid AUBERGINE_LIQUOR;

    //Alchemy
    public static Fluid MOLTEN_ANTIMONY;
    public static Fluid MOLTEN_SUGAR;

    public static void preInit()
    {
        MinecraftForge.EVENT_BUS.register(Registry.class);
        registerBlocks();
        registerTileEntities();
        registerFluids();
        registerCapabilities();
    }

    public static void init()
    {
        registerCaskLiquids();
    }

    public static void registerCaskLiquids() {
        CaskManager.register(new CaskLiquid(BOILING_WORT,1,0xFF898516));
        CaskManager.register(new CaskLiquid(BOILING_POTATO_JUICE,1,0xFFECEAA7));
        CaskManager.register(new CaskLiquid(BOILING_WORMWOOD,1,0xFFAFFF8D).addEffect(new PotionEffect(MobEffects.POISON,1200,0),2).addEffect(new PotionEffect(MobEffects.BLINDNESS,1200,0),0));
        CaskManager.register(new CaskLiquid(BOILING_BEETROOT_SOUP,1,0xFFC62E00));

        CaskManager.register(new CaskLiquid(ALE,2,0xFFE1862C).addEffect(new PotionEffect(POTION_ALE,1200,0),4));
        CaskManager.register(new CaskLiquid(VODKA,1,0xFFC8EFEF).addEffect(new PotionEffect(POTION_STOUTNESS,1600,0),4));
        CaskManager.register(new CaskLiquid(INNER_FIRE,2,0xFFFF4D00).addEffect(new PotionEffect(POTION_INNER_FIRE,1000,0),2));
        CaskManager.register(new CaskLiquid(UMBER_ALE,2,0xFF473216));
        CaskManager.register(new CaskLiquid(ABSINTHE,1,0xFF58FF2E));
        CaskManager.register(new CaskLiquid(METHANOL,1,0xFF666633).addEffect(new PotionEffect(POTION_FIRE_LUNG,200,0),2));
    }

    public static void registerBlocks()
    {
        Nope.shutupForge(Registry::registerOverrides);

        BlockEmberBurst emberBurst = (BlockEmberBurst) new BlockEmberBurst(Material.ROCK).setCreativeTab(Soot.creativeTab);
        BlockEmberFunnel emberFunnel = (BlockEmberFunnel) new BlockEmberFunnel(Material.ROCK).setCreativeTab(Soot.creativeTab);
        registerBlock("ember_burst", emberBurst, new ItemBlock(emberBurst));
        registerBlock("ember_funnel", emberFunnel, new ItemBlock(emberFunnel));

        BlockAlchemyGlobe alchemyGlobe = (BlockAlchemyGlobe) new BlockAlchemyGlobe(Material.ROCK).setCreativeTab(Soot.creativeTab);
        registerBlock("alchemy_globe", alchemyGlobe, new ItemBlock(alchemyGlobe));

        registerItem("signet_antimony",new Item().setCreativeTab(Soot.creativeTab));
        registerItem("mug",new ItemMug().setCreativeTab(Soot.creativeTab));
    }

    public static void registerOverrides()
    {
        BlockMixerImproved mixerImproved = (BlockMixerImproved) new BlockMixerImproved(Material.ROCK,"mixer",true).setIsFullCube(false).setIsOpaqueCube(false).setHarvestProperties("pickaxe", 0).setHardness(1.0F);
        registerBlock(mixerImproved,false);
        registerItem(mixerImproved.getItemBlock(),false);

        BlockDawnstoneAnvilImproved dawnstoneAnvilImproved = (BlockDawnstoneAnvilImproved) new BlockDawnstoneAnvilImproved(Material.ROCK,"dawnstone_anvil",true).setHarvestProperties("pickaxe", 1).setIsFullCube(false).setIsOpaqueCube(false).setHardness(1.6f).setLightOpacity(0);
        registerBlock(dawnstoneAnvilImproved,false);
        registerItem(dawnstoneAnvilImproved.getItemBlock(),false);
    }

    public static void registerFluids()
    {
        //For creating alcohol. All made in Melter, so very hot.
        FluidRegistry.registerFluid(BOILING_WORT = new Fluid("boiling_wort",new ResourceLocation(Soot.MODID,"blocks/wort"),new ResourceLocation(Soot.MODID,"blocks/wort_flowing")).setTemperature(500));
        FluidRegistry.registerFluid(BOILING_POTATO_JUICE = new Fluid("boiling_potato_juice",new ResourceLocation(Soot.MODID,"blocks/potato_juice"),new ResourceLocation(Soot.MODID,"blocks/potato_juice_flowing")).setTemperature(500));
        FluidRegistry.registerFluid(BOILING_WORMWOOD = new Fluid("boiling_wormwood",new ResourceLocation(Soot.MODID,"blocks/verdigris"),new ResourceLocation(Soot.MODID,"blocks/verdigris_flowing")).setTemperature(500));
        FluidRegistry.registerFluid(BOILING_BEETROOT_SOUP = new Fluid("boiling_beetroot_soup",new ResourceLocation(Soot.MODID,"blocks/beetroot_soup"),new ResourceLocation(Soot.MODID,"blocks/beetroot_soup_flowing")).setTemperature(500));
        //Alcohol itself. Cold.
        FluidRegistry.registerFluid(ALE = new Fluid("ale",new ResourceLocation(Soot.MODID,"blocks/ale"),new ResourceLocation(Soot.MODID,"blocks/ale_flowing")));
        FluidRegistry.registerFluid(VODKA = new Fluid("vodka",new ResourceLocation(Soot.MODID,"blocks/vodka"),new ResourceLocation(Soot.MODID,"blocks/vodka_flowing")));
        FluidRegistry.registerFluid(INNER_FIRE = new Fluid("inner_fire",new ResourceLocation(Soot.MODID,"blocks/inner_fire"),new ResourceLocation(Soot.MODID,"blocks/inner_fire_flowing")));
        FluidRegistry.registerFluid(UMBER_ALE = new Fluid("umber_ale",new ResourceLocation(Soot.MODID,"blocks/umber_ale"),new ResourceLocation(Soot.MODID,"blocks/umber_ale_flowing")));
        FluidRegistry.registerFluid(METHANOL = new Fluid("methanol",new ResourceLocation(Soot.MODID,"blocks/methanol"),new ResourceLocation(Soot.MODID,"blocks/methanol_flowing")));
        FluidRegistry.registerFluid(ABSINTHE = new Fluid("absinthe",new ResourceLocation(Soot.MODID,"blocks/absinthe"),new ResourceLocation(Soot.MODID,"blocks/absinthe_flowing")));
        //Alchemy Fluids
        FluidRegistry.registerFluid(MOLTEN_ANTIMONY = new FluidMolten("antimony",new ResourceLocation(Soot.MODID,"blocks/molten_antimony"),new ResourceLocation(Soot.MODID,"blocks/molten_antimony_flowing")));
        FluidRegistry.registerFluid(MOLTEN_SUGAR = new FluidMolten("sugar",new ResourceLocation(Soot.MODID,"blocks/molten_sugar"),new ResourceLocation(Soot.MODID,"blocks/molten_sugar_flowing")));
    }

    public static void registerBlockModels()
    {
        for (Block block : MODELLED_BLOCKS) {
            Soot.proxy.registerBlockModel(block);
        }
    }

    public static void registerItemModels()
    {
        for (Item item : MODELLED_ITEMS) {
            Soot.proxy.registerItemModel(item);
        }
    }

    public static void registerBlock(String id,Block block, ItemBlock itemBlock)
    {
        block.setRegistryName(Soot.MODID,id);
        block.setUnlocalizedName(id);
        registerBlock(block,true);
        registerItem(id,itemBlock);
    }

    public static void registerBlock(Block block, boolean hasmodel)
    {
        BLOCKS.add(block);
        if(hasmodel)
            MODELLED_BLOCKS.add(block);
    }

    public static void registerItem(String id,Item item)
    {
        item.setRegistryName(Soot.MODID,id);
        item.setUnlocalizedName(id);
        registerItem(item,true);
    }

    public static void registerItem(Item item, boolean hasmodel)
    {
        ITEMS.add(item);
        if(hasmodel)
            MODELLED_ITEMS.add(item);
    }

    public static void registerTileEntities()
    {
        registerTileEntity(TileEntityEmberBurst.class);
        registerTileEntity(TileEntityEmberFunnel.class);

        registerTileEntity(TileEntityAlchemyGlobe.class);

        registerTileEntity(TileEntityMixerBottomImproved.class);
        registerTileEntity(TileEntityDawnstoneAnvilImproved.class);
    }

    public static void registerCapabilities()
    {
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
            return new CapabilityUpgradeProvider("none",null);
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
        event.getRegistry().register(new PotionAle().setRegistryName(Soot.MODID,"ale"));
        event.getRegistry().register(new PotionStoutness().setRegistryName(Soot.MODID,"stoutness"));
        event.getRegistry().register(new PotionInnerFire().setRegistryName(Soot.MODID,"inner_fire"));
        event.getRegistry().register(new PotionFireLung().setRegistryName(Soot.MODID,"fire_lung"));
    }

    private static void registerTileEntity(Class<? extends TileEntity> tile)
    {
        GameRegistry.registerTileEntity(tile,tile.getSimpleName().toLowerCase());
    }
}
