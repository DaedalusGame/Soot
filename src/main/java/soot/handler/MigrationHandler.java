package soot.handler;

import com.google.common.collect.Sets;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import soot.Soot;
import soot.util.IMigrateable;
import teamroots.embers.block.IDial;
import teamroots.embers.item.ItemEmberGauge;
import teamroots.embers.util.EmberGenUtil;
import teamroots.embers.util.RenderUtil;
import teamroots.embers.world.EmberWorldData;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class MigrationHandler {
    static HashMap<String,String> migratedBlocks = new HashMap<>();
    static HashMap<String,String> migratedItems = new HashMap<>();
    static HashSet<String> removedBlocks = Sets.newHashSet();
    static HashSet<String> removedItems = Sets.newHashSet();

    public MigrationHandler() {
        migratedItems.put("soot:metallurgic_dust","embers:dust_metallurgic");
        migratedItems.put("soot:ember_grit","embers:dust_ember");
        migratedItems.put("soot:catalytic_plug","embers:catalytic_plug");
        migratedItems.put("soot:ember_funnel","embers:ember_funnel");

        migratedBlocks.put("soot:catalytic_plug","embers:catalytic_plug");
        migratedBlocks.put("soot:ember_funnel","embers:ember_funnel");
    }

    @SubscribeEvent
    public void missingBlockMappings(RegistryEvent.MissingMappings<Block> event) { //Thanks to KnightMiner for linking the relevant code for me to copy
        for(RegistryEvent.MissingMappings.Mapping<Block> entry : event.getAllMappings()) {
            String path = entry.key.toString();
            if(migratedBlocks.containsKey(path))
                entry.remap(Block.REGISTRY.getObject(new ResourceLocation(migratedBlocks.get(path))));
            if(removedBlocks.contains(path))
                entry.ignore();
        }
    }

    @SubscribeEvent
    public void missingItemMappings(RegistryEvent.MissingMappings<Item> event) {
        for(RegistryEvent.MissingMappings.Mapping<Item> entry : event.getAllMappings()) {
            String path = entry.key.toString();
            if(migratedItems.containsKey(path))
                entry.remap(Item.REGISTRY.getObject(new ResourceLocation(migratedItems.get(path))));
            if(removedItems.contains(path))
                entry.ignore();
        }
    }

    @SubscribeEvent
    public void missingSoundMappings(RegistryEvent.MissingMappings<SoundEvent> event) {
        for(RegistryEvent.MissingMappings.Mapping<SoundEvent> entry : event.getAllMappings()) {
            if(entry.key.getResourceDomain().equals(Soot.MODID)) {
                entry.ignore();
            }
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onGameOverlayRender(RenderGameOverlayEvent.Post e){
        EntityPlayer player = Minecraft.getMinecraft().player;

        int w = e.getResolution().getScaledWidth();
        int h = e.getResolution().getScaledHeight();

        int x = w/2;
        int y = h/2;

        World world = player.getEntityWorld();
        RayTraceResult result = player.rayTrace(6.0, e.getPartialTicks());

        if (result != null){
            if (result.typeOfHit == RayTraceResult.Type.BLOCK){
                IBlockState state = world.getBlockState(result.getBlockPos());
                if (state.getBlock() instanceof IMigrateable){
                    String text = I18n.translateToLocal("soot.tooltip.migrate");
                    Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(text, x-Minecraft.getMinecraft().fontRenderer.getStringWidth(text)/2, y+40, 0xFFFFFF);
                }
            }
        }
        GlStateManager.enableDepth();
    }
}
