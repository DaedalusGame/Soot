package soot.particle;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import soot.Soot;

import java.awt.*;

public class ParticleCrystal extends ParticleCube {
    public static final ResourceLocation texture = new ResourceLocation(Soot.MODID,"entity/particle_dawnstone");

    Entity anchor;

    public ParticleCrystal(Entity anchor, double x, double y, double z, double vx, double vy, double vz, float yaw, float pitch, Color color, float scale, int lifetime) {
        super(anchor.world, x, y, z, vx, vy, vz, color, scale, lifetime);
        this.anchor = anchor;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    @Override
    public int getFXLayer(){
        return 1;
    }

    @Override
    public void onUpdate(){
        if(anchor == null || !anchor.isEntityAlive() || anchor.world != world) {
            setExpired();
            return;
        }
        super.onUpdate();
        this.prevYaw = yaw;
        this.prevPitch = pitch;
        //yaw += 0.1f;
        //pitch += 0.1f;
    }

    @Override
    public boolean alive() {
        return anchor != null && anchor.isEntityAlive() && anchor.world == world && this.particleAge < this.particleMaxAge;
    }

    @Override
    public boolean isAdditive() {
        return true;
    }

    @Override
    public boolean renderThroughBlocks() {
        return false;
    }

    @Override
    public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
        float minU = this.particleTexture.getMinU();
        float maxU = this.particleTexture.getMaxU();
        float minV = this.particleTexture.getMinV();
        float maxV = this.particleTexture.getMaxV();
        double lifeCoeff = (particleAge + partialTicks) / particleMaxAge;
        float scale = 0.1F * (float)MathHelper.clampedLerp(scaleEnd,scaleStart,Math.sin(lifeCoeff*Math.PI));
        scale = (float) Math.sqrt(scale);

        float yaw = this.yaw + (this.yaw - this.prevYaw) * partialTicks;
        float pitch = this.pitch + (this.pitch - this.prevPitch) * partialTicks;

        double ex = anchor.lastTickPosX * (1 - partialTicks) + anchor.posX * (partialTicks);
        double ey = anchor.lastTickPosY * (1 - partialTicks) + anchor.posY * (partialTicks);
        double ez = anchor.lastTickPosZ * (1 - partialTicks) + anchor.posZ * (partialTicks);
        float x = (float) (ex + this.prevPosX + (this.posX - this.prevPosX) * (double) partialTicks - interpPosX);
        float y = (float) (ey + this.prevPosY + (this.posY - this.prevPosY) * (double) partialTicks - interpPosY);
        float z = (float) (ez + this.prevPosZ + (this.posZ - this.prevPosZ) * (double) partialTicks - interpPosZ);

        double sina = Math.sin(yaw);
        double cosa = Math.cos(yaw);
        double sinb = Math.sin(pitch);
        double cosb = Math.cos(pitch);

        Vec3d forward = new Vec3d(cosb, 0, sinb);
        Vec3d up = new Vec3d(sina * sinb, cosa, -sina * cosb);
        Vec3d right = new Vec3d(-cosa * sinb, sina, cosa * cosb);

        int lightmap = this.getBrightnessForRender(partialTicks);

        double length = scale * 8;
        Vec3d[] points = new Vec3d[]{
                new Vec3d(-scale, -scale, 0),
                new Vec3d(-scale, -scale, length),
                new Vec3d(-scale, scale, 0),
                new Vec3d(-scale, scale, length),
                new Vec3d(scale, -scale, 0),
                new Vec3d(scale, -scale, length),
                new Vec3d(scale, scale, 0),
                new Vec3d(scale, scale, length)
        };

        for (int i = 0; i < points.length; i++) {
            Vec3d v = points[i];
            points[i] = v.rotatePitch(pitch).rotateYaw(yaw).addVector(x,y,z);
            /*points[i] = new Vec3d(
                    x + forward.x * v.x + forward.y * v.y + forward.z * v.z,
                    y + up.x * v.x + up.y * v.y + up.z * v.z,
                    z + right.x * v.x + right.y * v.y+ right.z * v.z
            );*/
        }

        addBox(buffer, points[0], points[1], points[2], points[3], points[4], points[5], points[6], points[7], new Color(particleRed,particleGreen,particleBlue,particleAlpha), lightmap, minU, minV, maxU, maxV);
    }

    private static void addBox(BufferBuilder buffer, Vec3d a, Vec3d b, Vec3d c, Vec3d d, Vec3d e, Vec3d f, Vec3d g, Vec3d h, Color color, int lightmap, double minu, double minv, double maxu, double maxv) {
        int j = lightmap >> 16 & 65535;
        int k = lightmap & 65535;

        //BOTTOM FACE
        buffer.pos(a.x, a.y, a.z).tex(minu, minv).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).lightmap(j, k).endVertex();
        buffer.pos(b.x, b.y, b.z).tex(maxu, minv).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).lightmap(j, k).endVertex();
        buffer.pos(f.x, f.y, f.z).tex(maxu, maxv).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).lightmap(j, k).endVertex();
        buffer.pos(e.x, e.y, e.z).tex(minu, maxv).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).lightmap(j, k).endVertex();
        //TOP FACE
        buffer.pos(c.x, c.y, c.z).tex(minu, minv).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).lightmap(j, k).endVertex();
        buffer.pos(d.x, d.y, d.z).tex(maxu, minv).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).lightmap(j, k).endVertex();
        buffer.pos(h.x, h.y, h.z).tex(maxu, maxv).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).lightmap(j, k).endVertex();
        buffer.pos(g.x, g.y, g.z).tex(minu, maxv).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).lightmap(j, k).endVertex();
        //NORTH FACE
        buffer.pos(a.x, a.y, a.z).tex(minu, minv).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).lightmap(j, k).endVertex();
        buffer.pos(e.x, e.y, e.z).tex(maxu, minv).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).lightmap(j, k).endVertex();
        buffer.pos(g.x, g.y, g.z).tex(maxu, maxv).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).lightmap(j, k).endVertex();
        buffer.pos(c.x, c.y, c.z).tex(minu, maxv).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).lightmap(j, k).endVertex();
        //SOUTH FACE
        buffer.pos(b.x, b.y, b.z).tex(minu, minv).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).lightmap(j, k).endVertex();
        buffer.pos(f.x, f.y, f.z).tex(maxu, minv).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).lightmap(j, k).endVertex();
        buffer.pos(h.x, h.y, h.z).tex(maxu, maxv).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).lightmap(j, k).endVertex();
        buffer.pos(d.x, d.y, d.z).tex(minu, maxv).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).lightmap(j, k).endVertex();
        //WEST FACE
        buffer.pos(a.x, a.y, a.z).tex(minu, minv).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).lightmap(j, k).endVertex();
        buffer.pos(b.x, b.y, b.z).tex(maxu, minv).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).lightmap(j, k).endVertex();
        buffer.pos(d.x, d.y, d.z).tex(maxu, maxv).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).lightmap(j, k).endVertex();
        buffer.pos(c.x, c.y, c.z).tex(minu, maxv).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).lightmap(j, k).endVertex();
        //EAST FACE
        buffer.pos(e.x, e.y, e.z).tex(minu, minv).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).lightmap(j, k).endVertex();
        buffer.pos(f.x, f.y, f.z).tex(maxu, minv).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).lightmap(j, k).endVertex();
        buffer.pos(h.x, h.y, h.z).tex(maxu, maxv).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).lightmap(j, k).endVertex();
        buffer.pos(g.x, g.y, g.z).tex(minu, maxv).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).lightmap(j, k).endVertex();
    }
}
