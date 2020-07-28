package soot;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import soot.block.IBlockVariants;
import soot.entity.EntityCustomCloud;
import soot.entity.EntityMuse;
import soot.entity.EntityMuseRenderer;
import soot.tile.*;
import soot.util.*;
import teamroots.embers.tileentity.TileEntityBinRenderer;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ClientProxy implements IProxy {
    ArrayList<IBlockColored> COLOR_BLOCKS = new ArrayList<>();
    ArrayList<IItemColored> COLOR_ITEMS = new ArrayList<>();

    static ResourceProxy resourceProxy;

    static {
        resourceProxy = new ResourceProxy();
    }

    @Override
    public void registerResourcePack() {
        List<IResourcePack> packs = ObfuscationReflectionHelper.getPrivateValue(Minecraft.class, Minecraft.getMinecraft(),  "field_110449_ao");
        packs.add(resourceProxy);
    }

    @Override
    public EntityPlayer getMainPlayer() {
        return Minecraft.getMinecraft().player;
    }

    @Override
    public void addResourceOverride(String space, String dir, String file, String ext) {
        resourceProxy.addResource(space, dir, file, ext);
    }

    @Override
    public void addResourceOverride(String modid, String space, String dir, String file, String ext) {
        resourceProxy.addResource(space, modid, dir, file, ext);
    }

    @Override
    public void preInit() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void registerItemModel(Block block)
    {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), 0, new ModelResourceLocation(block.getRegistryName(), "inventory"));
    }

    @SubscribeEvent
    public void registerModels(ModelRegistryEvent event)
    {
        Registry.registerBlockModels();
        Registry.registerItemModels();

        RenderingRegistry.registerEntityRenderingHandler(EntityCustomCloud.class, manager -> new Render<EntityCustomCloud>(manager) {
            @Nullable
            @Override
            protected ResourceLocation getEntityTexture(EntityCustomCloud entity) {
                return null;
            }
        });
        RenderingRegistry.registerEntityRenderingHandler(EntityMuse.class, EntityMuseRenderer::new);
    }

    @Override
    public void init() {
        BlockColors blockColors = Minecraft.getMinecraft().getBlockColors();
        ItemColors itemColors = Minecraft.getMinecraft().getItemColors();

        for (IBlockColored block : COLOR_BLOCKS) {
            blockColors.registerBlockColorHandler(block::getColorMultiplier,(Block)block);
        }

        for (IItemColored item : COLOR_ITEMS) {
            itemColors.registerItemColorHandler(item::getColorFromItemstack,(Item)item);
        }

        registerTESRs();
    }

    @Override
    public void postInit() {

    }

    public static void registerTESRs()
    {
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityEmberBurst.class, new TileEntityEmberBurstRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityAlchemyGlobe.class, new TileEntityAlchemyGlobeRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityRedstoneBin.class, new TileEntityBinRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityStillTip.class, new TileEntityStillTipRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityDecanterTop.class, new TileEntityDecanterTopRenderer());
    }

    @Override
    public void registerBlockModel(Block block) {
        if(block instanceof IBlockColored)
            COLOR_BLOCKS.add((IBlockColored) block);
    }

    @Override
    public void registerItemModel(Item item) {
        if(item instanceof IItemColored)
            COLOR_ITEMS.add((IItemColored) item);
        if(item instanceof ItemBlock)
        {
            ItemBlock itemBlock = (ItemBlock) item;
            Block block = itemBlock.getBlock();
            ResourceLocation resloc = block.getRegistryName();
            if(block instanceof IBlockVariants) {
                for (IBlockState state : ((IBlockVariants) block).getValidStates()) {
                    ModelLoader.setCustomModelResourceLocation(item, block.getMetaFromState(state), new ModelResourceLocation(resloc, ((IBlockVariants) block).getBlockStateName(state)));
                }
            }
            else
                ModelLoader.setCustomModelResourceLocation(item,0,new ModelResourceLocation(resloc, "inventory"));
        }
        else
            ModelLoader.setCustomModelResourceLocation(item,0,new ModelResourceLocation(item.getRegistryName(), "inventory"));

    }
}
