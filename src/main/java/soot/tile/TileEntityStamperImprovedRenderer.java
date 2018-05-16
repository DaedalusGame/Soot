package soot.tile;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import teamroots.embers.Embers;
import teamroots.embers.block.BlockStamper;
import teamroots.embers.util.RenderUtil;
import teamroots.embers.util.StructBox;
import teamroots.embers.util.StructUV;

public class TileEntityStamperImprovedRenderer extends TileEntitySpecialRenderer<TileEntityStamperImproved> {
    public ResourceLocation texture = new ResourceLocation(Embers.MODID + ":textures/blocks/stamp_top.png");
    public StructBox stampX = new StructBox(0,0.25,0.25,1.0,0.75,0.75,new StructUV[]{new StructUV(0,0,8,16,32,32),new StructUV(0,0,8,16,32,32),new StructUV(16,0,32,8,32,32),new StructUV(16,0,32,8,32,32),new StructUV(8,0,16,8,32,32),new StructUV(8,0,16,8,32,32)});
    public StructBox stampY = new StructBox(0.25,0,0.25,0.75,1.0,0.75,new StructUV[]{new StructUV(8,0,16,8,32,32),new StructUV(8,0,16,8,32,32),new StructUV(0,0,8,16,32,32),new StructUV(0,0,8,16,32,32),new StructUV(0,0,8,16,32,32),new StructUV(0,0,8,16,32,32)});
    public StructBox stampZ = new StructBox(0.25,0.25,0,0.75,0.75,1.0,new StructUV[]{new StructUV(16,0,32,8,32,32),new StructUV(16,0,32,8,32,32),new StructUV(8,0,16,8,32,32),new StructUV(8,0,16,8,32,32),new StructUV(16,0,32,8,32,32),new StructUV(16,0,32,8,32,32)});

    @Override
    public void render(TileEntityStamperImproved te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        super.render(te, x, y, z, partialTicks, destroyStage, alpha);
        IBlockState state = te.getWorld().getBlockState(te.getPos());
        if (state.getBlock() instanceof BlockStamper){
            Minecraft.getMinecraft().renderEngine.bindTexture(texture);
            GlStateManager.disableCull();
            Tessellator tess = Tessellator.getInstance();
            BufferBuilder buffer = tess.getBuffer();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL);
            float offX = 0;
            float offY = 0;
            float offZ = 0;
            boolean powered = te.powered;
            boolean prevPowered = te.prevPowered;
            if (powered){
                if (state.getValue(BlockStamper.facing) == EnumFacing.EAST){
                    offX = 1;
                }
                if (state.getValue(BlockStamper.facing) == EnumFacing.WEST){
                    offX = -1;
                }
                if (state.getValue(BlockStamper.facing) == EnumFacing.NORTH){
                    offZ = -1;
                }
                if (state.getValue(BlockStamper.facing) == EnumFacing.SOUTH){
                    offZ = 1;
                }
                if (state.getValue(BlockStamper.facing) == EnumFacing.UP){
                    offY = 1;
                }
                if (state.getValue(BlockStamper.facing) == EnumFacing.DOWN){
                    offY = -1;
                }
            }
            float magnitude = 0;
            if (!powered){
                if (prevPowered){
                    magnitude = 1.0f-partialTicks;
                }
            }
            else if (powered){
                if (!prevPowered){
                    magnitude = partialTicks;
                }
                else {
                    magnitude = 1;
                }
            }
            offX *= magnitude;
            offY *= magnitude;
            offZ *= magnitude;
            if (state.getValue(BlockStamper.facing) == EnumFacing.EAST || state.getValue(BlockStamper.facing) == EnumFacing.WEST){
                RenderUtil.addBox(buffer, stampX.x1+x-0.0001+offX, stampX.y1+y, stampX.z1+z, stampX.x2+x+0.0001+offX, stampX.y2+y, stampX.z2+z, stampX.textures, new int[]{1,1,1,1,1,1});
            }
            if (state.getValue(BlockStamper.facing) == EnumFacing.UP || state.getValue(BlockStamper.facing) == EnumFacing.DOWN){
                RenderUtil.addBox(buffer, stampY.x1+x, stampY.y1-0.0001+y+offY, stampY.z1+z, stampY.x2+x, stampY.y2+y+0.0001+offY, stampY.z2+z, stampY.textures, new int[]{1,1,1,1,1,1});
            }
            if (state.getValue(BlockStamper.facing) == EnumFacing.NORTH || state.getValue(BlockStamper.facing) == EnumFacing.SOUTH){
                RenderUtil.addBox(buffer, stampZ.x1+x, stampZ.y1+y, stampZ.z1-0.0001+z+offZ, stampZ.x2+x, stampZ.y2+y, stampZ.z2+0.0001+z+offZ, stampZ.textures, new int[]{1,1,1,1,1,1});
            }

            tess.draw();
            ItemStack stamp = te.stamp.getStackInSlot(0);
            if (!stamp.isEmpty()){
                GlStateManager.pushMatrix();
                GlStateManager.translate(x, y, z);
                Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
                //GlStateManager.translate(0, -0.5F-0.03125f-1.0F, 0);
                GlStateManager.translate(0, -0.5F-0.03125F-magnitude*1F, 0);
                GlStateManager.translate(0.5F, 0.5F, 0.5F);
                GlStateManager.scale(0.7F,1.0F,0.7F);
                GlStateManager.rotate(90F, 1F, 0F, 0F);

                Minecraft.getMinecraft().getRenderItem().renderItem(stamp, ItemCameraTransforms.TransformType.NONE);
                GlStateManager.popMatrix();
            }
        }
    }
}
