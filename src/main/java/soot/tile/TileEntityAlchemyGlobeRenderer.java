package soot.tile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import soot.Soot;
import teamroots.embers.util.RenderUtil;
import teamroots.embers.util.StructUV;

public class TileEntityAlchemyGlobeRenderer extends TileEntitySpecialRenderer<TileEntityAlchemyGlobe> {
    public static final ResourceLocation BUBBLE_TEXTURE = new ResourceLocation(Soot.MODID + ":textures/blocks/bubble.png");
    public static final StructUV[] BUBBLE_UV = new StructUV[] {new StructUV(0,0,1,1),new StructUV(0,0,1,1),new StructUV(0,0,1,1),new StructUV(0,0,1,1),new StructUV(0,0,1,1),new StructUV(0,0,1,1)};

    @Override
    public void render(TileEntityAlchemyGlobe tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        RenderManager renderManager = Minecraft.getMinecraft().getRenderManager();
        //float eyeHeight = Minecraft.getMinecraft().getRenderViewEntity().getEyeHeight();
        //Vec3d offset = new Vec3d(x,y,z);
        //Vec3d offsetNormalized = offset.normalize();
        float playerLookX = (renderManager.options.thirdPersonView == 2 ? 1 : -1) * renderManager.playerViewX;
        float playerLookY = renderManager.playerViewY;

        //double pitch = Math.toRadians(playerLookX);
        //double yaw = Math.toRadians(playerLookY);
        //double lookx = -Math.sin(yaw) * Math.cos(pitch);
        //double looky = Math.sin(pitch);
        //double lookz = Math.cos(yaw) * Math.cos(pitch);
        //Vec3d playerLookVec = new Vec3d(lookx,looky,lookz).normalize();

        GlStateManager.pushMatrix();

        GlStateManager.translate(x + 0.5, y + 0.5, z + 0.5);
        //GlStateManager.translate(offsetNormalized.x * 2, offsetNormalized.y * 2 + eyeHeight, offsetNormalized.z * 2);
        GlStateManager.rotate(-playerLookY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-playerLookX, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);


        Minecraft.getMinecraft().renderEngine.bindTexture(BUBBLE_TEXTURE);
        GlStateManager.disableCull();
        GlStateManager.disableLighting();
        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
        GlStateManager.depthFunc(GL11.GL_LEQUAL);
        GlStateManager.alphaFunc(GL11.GL_ALWAYS, 0);
        GlStateManager.depthMask(false);
        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buffer = tess.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL);
        RenderUtil.addBox(buffer, 0.5, 0.5, 0.0, -0.5, -0.5, -0.0, BUBBLE_UV, new int[]{0,0,0,0,0,0});
        tess.draw();
        GlStateManager.popMatrix();
        GlStateManager.depthMask(true);
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.disableBlend();
    }
}
