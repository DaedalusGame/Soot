package soot.tile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import org.lwjgl.opengl.GL11;
import teamroots.embers.util.RenderUtil;

public class TileEntityDecanterTopRenderer extends TileEntitySpecialRenderer<TileEntityDecanterTop> {
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

        double fillCoeff = 0.5;
        double fill = MathHelper.clampedLerp(0.25,1.0,fillCoeff);
        RenderUtil.renderFluidCuboid(new FluidStack(FluidRegistry.WATER,1),tile.getPos(),0.25+ 0.0625/2,0.25,0.25+ 0.0625/2,0.75- 0.0625/2,fill,0.75- 0.0625/2);

        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
        RenderHelper.enableStandardItemLighting();
    }
}
