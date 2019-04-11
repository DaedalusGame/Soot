package soot.particle;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import teamroots.embers.particle.IEmberParticle;

import java.awt.*;

public class ParticleAlchemyExplosion extends Particle implements IEmberParticle {
    Color mainColor;
    Color backColor;
    Color cubeColor;

    protected ParticleAlchemyExplosion(World worldIn, double posXIn, double posYIn, double posZIn, Color mainColor, Color backColor, Color cubeColor, float scale, int lifetime) {
        super(worldIn, posXIn, posYIn, posZIn);
        this.mainColor = mainColor;
        this.backColor = backColor;
        this.cubeColor = cubeColor;
        this.particleScale = scale;
        this.particleMaxAge = lifetime;

    }

    @Override
    public void onUpdate(){
        super.onUpdate();
        float lifeCoeff = (float)particleAge / particleMaxAge;
        float scale = particleScale;// * (1-lifeCoeff);
        if(particleAge <= 1) {
            for (int i = 0; i < 20; i++) {
                double dx = rand.nextDouble() - 0.5;
                double dy = rand.nextDouble() - 0.5;
                double dz = rand.nextDouble() - 0.5;
                double dist = (5 + rand.nextDouble() * 3) * scale;
                int lifetime = rand.nextInt(5) + 5;
                ParticleUtilSoot.spawnParticleCube(world, posX, posY, posZ, dx * dist / lifetime, dy * dist / lifetime, dz * dist / lifetime, cubeColor, 6.0f * scale, lifetime);
            }
        }
        /*if(particleAge <= 10) {
            for (int i = 0; i < 1; i++) {
                double dx = rand.nextDouble() - 0.5;
                double dy = rand.nextDouble() - 0.5;
                double dz = rand.nextDouble() - 0.5;
                double dist = (2 + rand.nextDouble() * 2) * scale;
                int lifetime = rand.nextInt(5) + 5;
                ParticleUtil.spawnParticleSpark(world, (float)(posX + dx * dist), (float)(posY + dy * dist), (float)(posZ + dz * dist), 0, 0, 0, mainColor.getRed()/255f, mainColor.getGreen()/255f, mainColor.getBlue()/255f, 20.0f * scale, lifetime);
                ParticleUtil.spawnParticleSpark(world, (float)(posX + dx * dist), (float)(posY + dy * dist), (float)(posZ + dz * dist), 0, 0, 0, mainColor.getRed()/255f, mainColor.getGreen()/255f, mainColor.getBlue()/255f, 20.0f * scale, lifetime);
                ParticleUtil.spawnParticleSpark(world, (float)(posX + dx * dist), (float)(posY + dy * dist), (float)(posZ + dz * dist), 0, 0, 0, mainColor.getRed()/255f, mainColor.getGreen()/255f, mainColor.getBlue()/255f, 20.0f * scale, lifetime);
                ParticleUtil.spawnParticleSpark(world, (float)(posX + dx * dist), (float)(posY + dy * dist), (float)(posZ + dz * dist), 0, 0, 0, mainColor.getRed()/255f, mainColor.getGreen()/255f, mainColor.getBlue()/255f, 20.0f * scale, lifetime);
            }
        }
        else
        {
            for (int i = 0; i < 4; i++) {
                double dx = -(rand.nextDouble() - 0.5);
                double dy = -(rand.nextDouble() - 0.5);
                double dz = -(rand.nextDouble() - 0.5);
                double dist = (4 + rand.nextDouble() * 3) * scale;
                int lifetime = rand.nextInt(40) + 20;
                ParticleUtil.spawnParticleVapor(world, (float)posX, (float)posY, (float)posZ, (float)(dx * dist / lifetime), (float)(dy * dist / lifetime), (float)(dz * dist / lifetime), mainColor.getRed()/255f, mainColor.getGreen()/255f, mainColor.getBlue()/255f, mainColor.getAlpha()/255f, 20.0f * scale, 10.0f * scale, lifetime);
                ParticleUtil.spawnParticleVapor(world, (float)posX, (float)posY, (float)posZ, (float)(dx * dist / lifetime), (float)(dy * dist / lifetime), (float)(dz * dist / lifetime), mainColor.getRed()/255f, mainColor.getGreen()/255f, mainColor.getBlue()/255f, mainColor.getAlpha()/255f, 15.0f * scale, 7.5f * scale, lifetime);
                ParticleUtil.spawnParticleVapor(world, (float)posX, (float)posY, (float)posZ, (float)(dx * dist / lifetime), (float)(dy * dist / lifetime), (float)(dz * dist / lifetime), mainColor.getRed()/255f, mainColor.getGreen()/255f, mainColor.getBlue()/255f, mainColor.getAlpha()/255f, 10.0f * scale, 5.0f * scale, lifetime);
            }
        }
        for (int i = 0; i < 4; i++) {
            double dx = rand.nextDouble() - 0.5;
            double dy = rand.nextDouble() - 0.5;
            double dz = rand.nextDouble() - 0.5;
            double dist = (6 + rand.nextDouble() * 4) * scale;
            int lifetime = rand.nextInt(20) + 5;
            ParticleUtil.spawnParticleVapor(world, (float)posX, (float)posY, (float)posZ, (float)(dx * dist / lifetime), (float)(dy * dist / lifetime), (float)(dz * dist / lifetime), backColor.getRed()/255f, backColor.getGreen()/255f, backColor.getBlue()/255f, backColor.getAlpha()/255f, 10.0f * scale, 20.0f * scale, lifetime);
        }*/
        for (int i = 0; i < 4; i++) {
            double dx = rand.nextDouble() - 0.5;
            double dy = rand.nextDouble() - 0.5;
            double dz = rand.nextDouble() - 0.5;
            double dist = (5 + rand.nextDouble() * 3) * scale;
            int lifetime = rand.nextInt(10) + 5;
            ParticleUtilSoot.spawnParticleCube(world, posX, posY, posZ, dx * dist / lifetime, dy * dist / lifetime, dz * dist / lifetime, cubeColor, 3.0f * scale, lifetime);
        }
        for(int i = 0; i < 1; i++) {
            double dx = rand.nextDouble() - 0.5;
            double dy = rand.nextDouble() - 0.5;
            double dz = rand.nextDouble() - 0.5;
            double dist = 5 * scale;
            ParticleUtilSoot.spawnLightning(world, posX, posY, posZ, posX + dx * dist, posY + dy * dist, posZ + dz * dist, 8, 0.6 * scale, mainColor, 2.0 * scale, 10);
        }
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
