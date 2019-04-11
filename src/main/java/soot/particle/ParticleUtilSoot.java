package soot.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import teamroots.embers.Embers;
import teamroots.embers.particle.ParticleUtil;
import teamroots.embers.proxy.ClientProxy;

import java.awt.*;
import java.util.function.Function;

public class ParticleUtilSoot {
    public static void spawnParticleSolidGlow(World world, float x, float y, float z, float vx, float vy, float vz, float r, float g, float b, float a, float scale, int lifetime) {
        if (Embers.proxy instanceof ClientProxy) {
            ParticleUtil.counter += ParticleUtil.random.nextInt(3);
            if (ParticleUtil.counter % (Minecraft.getMinecraft().gameSettings.particleSetting == 0 ? 1 : 2 * Minecraft.getMinecraft().gameSettings.particleSetting) == 0) {
                ClientProxy.particleRenderer.addParticle(new ParticleSolidGlow(world, x, y, z, vx, vy, vz, r, g, b, a, scale, lifetime));
            }
        }
    }

    public static void spawnParticleCube(World world, double x, double y, double z, double vx, double vy, double vz, Color color, float scale, int lifetime) {
        if (Embers.proxy instanceof ClientProxy) {
            ParticleUtil.counter += ParticleUtil.random.nextInt(3);
            if (ParticleUtil.counter % (Minecraft.getMinecraft().gameSettings.particleSetting == 0 ? 1 : 2 * Minecraft.getMinecraft().gameSettings.particleSetting) == 0) {
                ClientProxy.particleRenderer.addParticle(new ParticleCube(world, x, y, z, vx, vy, vz, color, scale, lifetime));
            }
        }
    }

    public static void spawnParticleCrystal(Entity anchor, double x, double y, double z, double vx, double vy, double vz, float yaw, float pitch, Color color, float scale, int lifetime) {
        if (Embers.proxy instanceof ClientProxy) {
            ParticleUtil.counter += ParticleUtil.random.nextInt(3);
            if (ParticleUtil.counter % (Minecraft.getMinecraft().gameSettings.particleSetting == 0 ? 1 : 2 * Minecraft.getMinecraft().gameSettings.particleSetting) == 0) {
                ClientProxy.particleRenderer.addParticle(new ParticleCrystal(anchor, x, y, z, vx, vy, vz, yaw, pitch, color, scale, lifetime));
            }
        }
    }

    public static void spawnAlchemyExplosion(World world, double x, double y, double z, Color mainColor, Color backColor, Color cubeColor, float scale, int lifetime)
    {
        if (Embers.proxy instanceof ClientProxy) {
            ClientProxy.particleRenderer.addParticle(new ParticleAlchemyExplosion(world, x, y, z, mainColor, backColor, cubeColor, scale, lifetime));
        }
    }

    public static void spawnFireBlast(World world, double x, double y, double z, Color color, float scale, int lifetime)
    {
        if (Embers.proxy instanceof ClientProxy) {
            ClientProxy.particleRenderer.addParticle(new ParticleFireBlast(world, x, y, z, color, scale, lifetime));
        }
    }

    public static void spawnCrystalStrike(Entity anchor, double x, double y, double z, Function<Double, Vec3d> emitPos, Function<Double, Vec3d> emitAngle, Function<Double, Color> emitColor, Function<Double, Float> emitScale, int lifetime)
    {
        if (Embers.proxy instanceof ClientProxy) {
            ClientProxy.particleRenderer.addParticle(new ParticleCrystalStrike(anchor, x, y, z, emitPos, emitAngle, emitColor, emitScale, lifetime));
        }
    }

    public static void spawnLightning(World world, double x1, double y1, double z1, double x2, double y2, double z2, int segments, double wildness, Color color, double thickness, int lifetime)
    {
        double prevx = x1;
        double prevy = y1;
        double prevz = z1;

        for(int i = 1; i <= segments; i++)
        {
            double coeff = (double)i / segments;
            double wildCoeff = Math.sin(Math.PI * coeff) * wildness;
            double currx = MathHelper.clampedLerp(x1, x2, coeff) + (ParticleUtil.random.nextDouble() - 0.5) * 2 * wildCoeff;
            double curry = MathHelper.clampedLerp(y1, y2, coeff) + (ParticleUtil.random.nextDouble() - 0.5) * 2 * wildCoeff;
            double currz = MathHelper.clampedLerp(z1, z2, coeff) + (ParticleUtil.random.nextDouble() - 0.5) * 2 * wildCoeff;
            spawnSpark(world,prevx,prevy,prevz,currx,curry,currz, color, thickness, lifetime);
            prevx = currx;
            prevy = curry;
            prevz = currz;
        }
    }

    private static void spawnSpark(World world, double x1, double y1, double z1, double x2, double y2, double z2, Color color, double thickness, int lifetime)
    {
        double dx = x2 - x1;
        double dy = y2 - y1;
        double dz = z2 - z1;

        double dist = Math.sqrt(dx*dx+dy*dy+dz*dz);
        double segments = Math.ceil(dist) * 10;
        for (int i = 0; i <= segments; i++) {
            double coeff = i / segments;
            double sparkx = MathHelper.clampedLerp(x1,x2,coeff);
            double sparky = MathHelper.clampedLerp(y1,y2,coeff);
            double sparkz = MathHelper.clampedLerp(z1,z2,coeff);
            ParticleUtil.spawnParticleGlow(world, (float)sparkx, (float)sparky, (float)sparkz, 0, 0, 0, color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()/255f, (float) thickness, lifetime);
        }
    }

    public static void spawnCubeRing(World world, double x, double y, double z, Color mainColor, int segments, double dist) {
        for(int i = 0; i< segments; i++) {
            double angle = (double)i / segments * Math.PI * 2;
            double dx = Math.sin(angle);
            double dy = 0;
            double dz = Math.cos(angle);
            int lifetime = 5;
            double velocity = dist / lifetime;
            spawnParticleCube(world,x,y,z,dx*velocity,dy*velocity,dz*velocity,mainColor, 0.1f ,lifetime);
        }
    }
}
