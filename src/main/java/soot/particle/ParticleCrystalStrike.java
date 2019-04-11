package soot.particle;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import teamroots.embers.particle.IEmberParticle;

import java.awt.*;
import java.util.function.Function;

public class ParticleCrystalStrike extends Particle implements IEmberParticle {
    private Function<Double, Vec3d> emitPos;
    private Function<Double, Vec3d> emitAngle;
    private Function<Double, Float> emitScale;
    private Function<Double, Color> emitColor;

    Entity anchor;

    protected ParticleCrystalStrike(Entity anchor, double x, double y, double z, Function<Double, Vec3d> emitPos, Function<Double, Vec3d> emitAngle, Function<Double, Color> emitColor, Function<Double, Float> emitScale, int lifetime) {
        super(anchor.world,x,y,z);
        this.anchor = anchor;
        this.emitPos = emitPos;
        this.emitAngle = emitAngle;
        this.emitScale = emitScale;
        this.emitColor = emitColor;
        this.particleMaxAge = lifetime;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        double lifeCoeff = (double)particleAge / particleMaxAge;
        Vec3d angle = emitAngle.apply(lifeCoeff);
        Vec3d offset = emitPos.apply(lifeCoeff);
        ParticleUtilSoot.spawnParticleCrystal(anchor,posX+offset.x,posY+offset.y,posZ+offset.z,0,0,0,(float)angle.x,(float)angle.y,emitColor.apply(lifeCoeff),emitScale.apply(lifeCoeff),20);
    }

    @Override
    public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
        //NOOP
    }

    @Override
    public boolean alive() {
        return this.particleAge < this.particleMaxAge;
    }


    @Override
    public boolean isAdditive() {
        return false;
    }

    @Override
    public boolean renderThroughBlocks() {
        return false;
    }
}
