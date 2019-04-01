package soot.particle;

import net.minecraft.world.World;
import teamroots.embers.particle.ParticleGlow;

public class ParticleSolidGlow extends ParticleGlow {
    public ParticleSolidGlow(World worldIn, double x, double y, double z, double vx, double vy, double vz, float r, float g, float b, float a, float scale, int lifetime) {
        super(worldIn, x, y, z, vx, vy, vz, r, g, b, a, scale, lifetime);
    }

    @Override
    public boolean isAdditive() {
        return false;
    }
}
