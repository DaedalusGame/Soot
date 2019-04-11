package soot.projectiles;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import soot.brewing.CaskManager;
import teamroots.embers.api.projectile.IProjectileEffect;
import teamroots.embers.api.projectile.IProjectilePreset;

public class EffectBrew implements IProjectileEffect {
    FluidStack fluid;

    public EffectBrew(FluidStack fluid) {
        this.fluid = fluid;
    }

    @Override
    public void onEntityImpact(Entity entity, IProjectilePreset projectile) {
        CaskManager.CaskLiquid liquid = CaskManager.getFromFluid(fluid);
        if(liquid != null && entity instanceof EntityLivingBase)
            liquid.applyEffects((EntityLivingBase) entity,projectile.getEntity(),projectile.getShooter(),fluid);
    }

    @Override
    public void onBlockImpact(World world, BlockPos pos, EnumFacing side, IProjectilePreset projectile) {
        CaskManager.CaskLiquid liquid = CaskManager.getFromFluid(fluid);
        if(liquid != null)
            liquid.applyEffects(world,pos,side,projectile.getEntity(),projectile.getShooter(),fluid);
    }
}
