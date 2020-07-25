package soot.tile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import org.lwjgl.opengl.GL11;
import teamroots.embers.Embers;
import teamroots.embers.tileentity.IItemPipeConnectable;
import teamroots.embers.util.*;

import java.awt.*;

public class TileEntityStillTipRenderer extends TileEntitySpecialRenderer<TileEntityStillTip> {
    public ResourceLocation texture = new ResourceLocation(Embers.MODID + ":textures/blocks/item_pipe_tex.png");

    public TileEntityStillTipRenderer(){
        super();
    }

    @Override
    public void render(TileEntityStillTip tile, double x, double y, double z, float partialTicks, int destroyStage, float tileAlpha){
            Minecraft.getMinecraft().renderEngine.bindTexture(texture);
            GlStateManager.disableCull();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            Tessellator tess = Tessellator.getInstance();
            BufferBuilder buffer = tess.getBuffer();
            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL);

            for (EnumFacing facing : EnumFacing.VALUES) {
                if(shouldRenderPipe(tile, facing))
                    PipeRenderUtil.addPipe(buffer, x, y, z, facing);
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

    private boolean shouldRenderLip(TileEntityStillTip pipe, EnumFacing facing) {
        EnumPipeConnection connection = getPipeConnection(pipe.getWorld(), pipe.getPos(), facing);
        return connection == EnumPipeConnection.BLOCK || connection == EnumPipeConnection.LEVER;
    }

    private boolean shouldRenderPipe(TileEntityStillTip pipe, EnumFacing facing) {
        EnumPipeConnection connection = getPipeConnection(pipe.getWorld(), pipe.getPos(), facing);
        return connection == EnumPipeConnection.PIPE || shouldRenderLip(pipe,facing);
    }
}
