package soot;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.potion.Potion;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import soot.block.*;
import soot.fluids.FluidMolten;
import soot.item.ItemBlockMeta;
import soot.potion.PotionAle;
import soot.tile.*;
import teamroots.embers.RegistryManager;
import teamroots.embers.block.BlockSeed;

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

    @GameRegistry.ObjectHolder("soot:ale")
    public static Potion POTION_ALE;

    public static Fluid BOILING_WORT;
    public static Fluid BOILING_POTATO_JUICE;
    public static Fluid ALE;
    public static Fluid VODKA;
    public static Fluid MOLTEN_ANTIMONY;
    public static Fluid MOLTEN_SUGAR;

    public static void preInit()
    {
        MinecraftForge.EVENT_BUS.register(Registry.class);
        registerBlocks();
        registerTileEntities();
        registerPotions();
        registerFluids();
    }

    public static void registerBlocks()
    {
        BlockEmberBurst emberBurst = new BlockEmberBurst(Material.ROCK);
        BlockEmberFunnel emberFunnel = new BlockEmberFunnel(Material.ROCK);
        registerBlock("ember_burst", emberBurst, new ItemBlock(emberBurst));
        registerBlock("ember_funnel", emberFunnel, new ItemBlock(emberFunnel));

        BlockAlchemyGlobe alchemyGlobe = new BlockAlchemyGlobe(Material.ROCK);
        registerBlock("alchemy_globe", alchemyGlobe, new ItemBlock(alchemyGlobe));

        BlockMixerImproved mixerImproved = (BlockMixerImproved) new BlockMixerImproved(Material.ROCK,"mixer",true).setIsFullCube(false).setIsOpaqueCube(false).setHarvestProperties("pickaxe", 0).setHardness(1.0F);
        BlockDawnstoneAnvilImproved dawnstoneAnvilImproved = (BlockDawnstoneAnvilImproved) new BlockDawnstoneAnvilImproved(Material.ROCK,"dawnstone_anvil",true).setHarvestProperties("pickaxe", 1).setIsFullCube(false).setIsOpaqueCube(false).setHardness(1.6f).setLightOpacity(0);
        registerBlock(mixerImproved,false);
        registerBlock(dawnstoneAnvilImproved,false);
        registerItem(mixerImproved.getItemBlock(),false);
        registerItem(dawnstoneAnvilImproved.getItemBlock(),false);

        registerItem("signet_antimony",new Item());
    }

    public static void registerFluids()
    {
        //For creating alcohol. All made in Melter, so very hot.
        FluidRegistry.registerFluid(BOILING_WORT = new Fluid("boiling_wort",new ResourceLocation(Soot.MODID,"blocks/wort"),new ResourceLocation(Soot.MODID,"blocks/wort_flowing")).setTemperature(500));
        FluidRegistry.registerFluid(BOILING_POTATO_JUICE = new Fluid("boiling_potato_juice",new ResourceLocation(Soot.MODID,"blocks/potato_juice"),new ResourceLocation(Soot.MODID,"blocks/potato_juice_flowing")).setTemperature(500));
        //Alcohol itself. Cold.
        FluidRegistry.registerFluid(ALE = new Fluid("ale",new ResourceLocation(Soot.MODID,"blocks/ale"),new ResourceLocation(Soot.MODID,"blocks/ale_flowing")));
        FluidRegistry.registerFluid(VODKA = new Fluid("vodka",new ResourceLocation(Soot.MODID,"blocks/vodka"),new ResourceLocation(Soot.MODID,"blocks/vodka_flowing")));
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

    public static void registerPotions()
    {
        //POTION_ALE = new PotionAle().setRegistryName(Soot.MODID,"ale");
    }

    public static void registerTileEntities()
    {
        registerTileEntity(TileEntityEmberBurst.class);
        registerTileEntity(TileEntityEmberFunnel.class);

        registerTileEntity(TileEntityAlchemyGlobe.class);

        registerTileEntity(TileEntityMixerBottomImproved.class);
        registerTileEntity(TileEntityDawnstoneAnvilImproved.class);
    }

    public static void registerTESRs()
    {
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityEmberBurst.class, new TileEntityEmberBurstRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityAlchemyGlobe.class, new TileEntityAlchemyGlobeRenderer());
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
    }

    private static void registerTileEntity(Class<? extends TileEntity> tile)
    {
        GameRegistry.registerTileEntity(tile,tile.getSimpleName().toLowerCase());
    }
}
