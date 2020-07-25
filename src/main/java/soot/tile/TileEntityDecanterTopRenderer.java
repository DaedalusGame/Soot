package soot.tile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.CapabilityItemHandler;
import org.lwjgl.opengl.GL11;
import soot.Soot;
import soot.brewing.EssenceStack;
import teamroots.embers.Embers;
import teamroots.embers.tileentity.IItemPipeConnectable;
import teamroots.embers.util.EnumPipeConnection;
import teamroots.embers.util.PipeRenderUtil;
import teamroots.embers.util.RenderUtil;

import java.awt.*;

public class TileEntityDecanterTopRenderer extends TileEntitySpecialRenderer<TileEntityDecanterTop> {
    public ResourceLocation texture = new ResourceLocation(Embers.MODID + ":textures/blocks/item_pipe_tex.png");

    public static final ResourceLocation ESSENCE_STILL = new ResourceLocation(Soot.MODID,"blocks/brew_essence");
    public static final ResourceLocation ESSENCE_FLOWING = new ResourceLocation(Soot.MODID,"blocks/brew_essence_flowing");

    @Override
    public void render(TileEntityDecanterTop tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        super.render(tile, x, y, z, partialTicks, destroyStage, alpha);

        GlStateManager.pushMatrix();

        RenderHelper.disableStandardItemLighting();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        if (Minecraft.isAmbientOcclusionEnabled()) {
            GL11.glShadeModel(GL11.GL_SMOOTH);
        } else {
            GL11.glShadeModel(GL11.GL_FLAT);
        }

        GlStateManager.translate(x, y, z);

        EssenceStack stack = tile.currentEssence;

        if(!stack.isEmpty()) {
            double fillCoeff = (double) stack.getAmount() / tile.getCapacity();
            double fill = MathHelper.clampedLerp(0.25, 1.0, fillCoeff);
            Color color = stack.getEssence().getFillColor();
            renderEssenceCuboid(tile.getPos(), 0.25 + 0.0625 / 2, 0.25, 0.25 + 0.0625 / 2, 0.75 - 0.0625 / 2, fill, 0.75 - 0.0625 / 2, color.getRGB());
        }

        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
        RenderHelper.enableStandardItemLighting();

        GlStateManager.disableCull();

        Minecraft.getMinecraft().renderEngine.bindTexture(texture);
        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buffer = tess.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL);

        for (EnumFacing facing : EnumFacing.HORIZONTALS) {
            if(shouldRenderLip(tile, facing))
                PipeRenderUtil.addPipeLip(buffer, x, y, z, facing);
        }

        tess.draw();

        GlStateManager.enableCull();
    }

    private EnumPipeConnection getPipeConnection(World world, BlockPos pos, EnumFacing facing) {
        TileEntity tile = world.getTileEntity(pos.offset(facing));
        if (tile instanceof IItemPipeConnectable) {
            return ((IItemPipeConnectable) tile).getConnection(facing.getOpposite());
        } else if (tile != null && tile.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing.getOpposite())) {
            return EnumPipeConnection.BLOCK;
        }
        return EnumPipeConnection.NONE;
    }

    private boolean shouldRenderLip(TileEntityDecanterTop pipe, EnumFacing facing) {
        EnumPipeConnection connection = getPipeConnection(pipe.getWorld(), pipe.getPos(), facing);
        return connection == EnumPipeConnection.PIPE || connection == EnumPipeConnection.BLOCK || connection == EnumPipeConnection.LEVER;
    }

    public static void renderEssenceCuboid(BlockPos pos, double x1, double y1, double z1, double x2, double y2, double z2, int color) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder renderer = tessellator.getBuffer();
        renderer.begin(7, DefaultVertexFormats.BLOCK);
        Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        int luminosity = 15;
        int brightness = Minecraft.getMinecraft().world.getCombinedLight(pos, luminosity);
        TextureAtlasSprite still = Minecraft.getMinecraft().getTextureMapBlocks().getTextureExtry(ESSENCE_STILL.toString());
        TextureAtlasSprite flowing = Minecraft.getMinecraft().getTextureMapBlocks().getTextureExtry(ESSENCE_FLOWING.toString());
        RenderUtil.putTexturedQuad(renderer, still, x1, y1, z1, x2 - x1, y2 - y1, z2 - z1, EnumFacing.DOWN, color, brightness, false);
        RenderUtil.putTexturedQuad(renderer, flowing, x1, y1, z1, x2 - x1, y2 - y1, z2 - z1, EnumFacing.NORTH, color, brightness, true);
        RenderUtil.putTexturedQuad(renderer, flowing, x1, y1, z1, x2 - x1, y2 - y1, z2 - z1, EnumFacing.EAST, color, brightness, true);
        RenderUtil.putTexturedQuad(renderer, flowing, x1, y1, z1, x2 - x1, y2 - y1, z2 - z1, EnumFacing.SOUTH, color, brightness, true);
        RenderUtil.putTexturedQuad(renderer, flowing, x1, y1, z1, x2 - x1, y2 - y1, z2 - z1, EnumFacing.WEST, color, brightness, true);
        RenderUtil.putTexturedQuad(renderer, still, x1, y1, z1, x2 - x1, y2 - y1, z2 - z1, EnumFacing.UP, color, brightness, false);
        tessellator.draw();
    }
}
