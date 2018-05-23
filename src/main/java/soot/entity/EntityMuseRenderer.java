package soot.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import soot.Soot;

public class EntityMuseRenderer extends RenderBiped<EntityMuse> {
    private static final ResourceLocation MUSE_TEXTURE = new ResourceLocation(Soot.MODID,"textures/entity/muse.png");

    public EntityMuseRenderer(RenderManager manager)
    {
        super(manager, new EntityMuseModel(), 0.3F);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityMuse entity)
    {
        return MUSE_TEXTURE;
    }

    /**
     * Renders the desired {@code T} type Entity.
     */
    @Override
    public void doRender(EntityMuse entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
        EntityPlayer player = Minecraft.getMinecraft().player;
        if(entity.isVisibleTo(player))
            super.doRender(entity, x, y, z, entityYaw, partialTicks);
        GlStateManager.disableBlend();
    }
}
