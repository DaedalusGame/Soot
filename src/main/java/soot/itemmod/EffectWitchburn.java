package soot.itemmod;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import soot.handler.WitchburnHandler;
import teamroots.embers.api.projectile.IProjectileEffect;
import teamroots.embers.api.projectile.IProjectilePreset;

class EffectWitchburn implements IProjectileEffect {
    public int ticks;

    public EffectWitchburn(int ticks) {
        this.ticks = ticks;
    }

    public int getTicks() {
        return ticks;
    }

    public void setTicks(int ticks) {
        this.ticks = ticks;
    }

    @Override
    public void onEntityImpact(Entity entity, IProjectilePreset projectile) {
        if(entity instanceof EntityLivingBase) {
            WitchburnHandler.setWitchburn((EntityLivingBase) entity,ticks);
        }
    }
}
