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
import soot.block.BlockEmberBurst;
import soot.block.BlockEmberFunnel;
import soot.tile.TileEntityEmberBurst;
import soot.tile.TileEntityEmberBurstRenderer;
import soot.tile.TileEntityEmberFunnel;

import java.util.ArrayList;

public class Registry {
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
    }

    public static void registerBlockModels()
    {
        for (Block block : BLOCKS) {
            Soot.proxy.registerBlockModel(block);
        }
    }

    public static void registerItemModels()
    {
        for (Item item : ITEMS) {
            Soot.proxy.registerItemModel(item);
        }
    }

    public static void registerBlock(String id,Block block, ItemBlock itemBlock)
    {
        block.setRegistryName(Soot.MODID,id);
        block.setUnlocalizedName(id);
        BLOCKS.add(block);
        registerItem(id,itemBlock);
    }

    public static void registerItem(String id,Item item)
    {
        item.setRegistryName(Soot.MODID,id);
        item.setUnlocalizedName(id);
        ITEMS.add(item);
    }

    public static void registerTileEntities()
    {
        registerTileEntity(TileEntityEmberBurst.class);
        registerTileEntity(TileEntityEmberFunnel.class);
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
