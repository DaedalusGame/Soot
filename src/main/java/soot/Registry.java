package soot;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import soot.block.BlockDawnstoneAnvilImproved;
import soot.block.BlockEmberBurst;
import soot.block.BlockEmberFunnel;
import soot.block.BlockMixerImproved;
import soot.item.ItemBlockMeta;
import soot.tile.*;
import teamroots.embers.RegistryManager;
import teamroots.embers.block.BlockSeed;

import java.util.ArrayList;

public class Registry {
    private static ArrayList<Block> MODELLED_BLOCKS = new ArrayList<>();
    private static ArrayList<Item> MODELLED_ITEMS = new ArrayList<>();
    private static ArrayList<Block> BLOCKS = new ArrayList<>();
    private static ArrayList<Item> ITEMS = new ArrayList<>();

    public static void preInit()
    {
        MinecraftForge.EVENT_BUS.register(Registry.class);
        registerBlocks();
        registerTileEntities();
    }

    public static void registerBlocks()
    {
        BlockEmberBurst emberBurst = new BlockEmberBurst(Material.ROCK);
        BlockEmberFunnel emberFunnel = new BlockEmberFunnel(Material.ROCK);
        registerBlock("ember_burst", emberBurst, new ItemBlock(emberBurst));
        registerBlock("ember_funnel", emberFunnel, new ItemBlock(emberFunnel));

        BlockMixerImproved mixerImproved = (BlockMixerImproved) new BlockMixerImproved(Material.ROCK,"mixer",true).setIsFullCube(false).setIsOpaqueCube(false).setHarvestProperties("pickaxe", 0).setHardness(1.0F);
        BlockDawnstoneAnvilImproved dawnstoneAnvilImproved = (BlockDawnstoneAnvilImproved) new BlockDawnstoneAnvilImproved(Material.ROCK,"dawnstone_anvil",true).setHarvestProperties("pickaxe", 1).setIsFullCube(false).setIsOpaqueCube(false).setHardness(1.6f).setLightOpacity(0);
        BlockSeed seed = (BlockSeed) RegistryManager.seed;
        Item seedImprovedItem = new ItemBlockMeta(seed).setRegistryName(seed.getRegistryName());
        seed.itemBlock = seedImprovedItem;
        registerBlock(mixerImproved,false);
        registerBlock(dawnstoneAnvilImproved,false);
        registerItem(mixerImproved.getItemBlock(),false);
        registerItem(dawnstoneAnvilImproved.getItemBlock(),false);
        registerItem(seedImprovedItem,false);
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

        registerTileEntity(TileEntityMixerBottomImproved.class);
        registerTileEntity(TileEntityDawnstoneAnvilImproved.class);
    }

    public static void registerTESRs()
    {
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityEmberBurst.class,new TileEntityEmberBurstRenderer());
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

    private static void registerTileEntity(Class<? extends TileEntity> tile)
    {
        GameRegistry.registerTileEntity(tile,tile.getSimpleName().toLowerCase());
    }
}
