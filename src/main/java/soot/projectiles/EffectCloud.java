package soot.projectiles;

import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import soot.entity.EntityCustomCloud;
import teamroots.embers.api.projectile.IProjectileEffect;
import teamroots.embers.api.projectile.IProjectilePreset;

public class EffectCloud implements IProjectileEffect {
    interface IGenerator {
        EntityCustomCloud generate(World world, Vec3d pos);
    }

    IGenerator generator;

    public EffectCloud(IGenerator generator) {
        this.generator = generator;
    }

    @Override
    public void onHit(World world, RayTraceResult raytrace, IProjectilePreset projectile) {
        onFizzle(world,raytrace.hitVec,projectile);
    }

    @Override
    public void onFizzle(World world, Vec3d pos, IProjectilePreset projectile) {
        EntityCustomCloud cloud = generator.generate(world,pos);
        world.spawnEntity(cloud);
    }
}
