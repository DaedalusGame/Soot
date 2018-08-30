package soot.itemmod;

import com.google.common.collect.Lists;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import soot.Registry;
import teamroots.embers.api.EmbersAPI;
import teamroots.embers.api.event.EmberProjectileEvent;
import teamroots.embers.api.itemmod.ItemModUtil;
import teamroots.embers.api.projectile.*;
import teamroots.embers.itemmod.ModifierProjectileBase;

import java.awt.*;
import java.util.ListIterator;
import java.util.Random;

public class ModifierWitchburn extends ModifierProjectileBase {
    public ModifierWitchburn() {
        super("witchburn", 8.0, true);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onProjectileFire(EmberProjectileEvent event) {
        ListIterator<IProjectilePreset> projectiles = event.getProjectiles().listIterator();

        ItemStack weapon = event.getStack();
        if(!weapon.isEmpty() && ItemModUtil.hasHeat(weapon)) {
            int level = ItemModUtil.getModifierLevel(weapon, Registry.WITCHBURN);
            if(level > 0)
                while (projectiles.hasNext()) {
                    IProjectilePreset projectile = projectiles.next();
                    projectile.setColor(new Color(64,255,16));
                    projectile.setEffect(adjustEffect(projectile.getEffect()));
                }
        }
    }

    private IProjectileEffect adjustEffect(IProjectileEffect effect) {
        if (effect instanceof EffectArea) {
            EffectArea areaEffect = (EffectArea) effect;
            areaEffect.setEffect(adjustEffect(areaEffect.getEffect()));
            return areaEffect;
        } else if (effect instanceof EffectMulti) {
            ((EffectMulti) effect).addEffect(new EffectPotion(new PotionEffect(Registry.POTION_WITCHBURN,200,0,false,false)));
            return effect;
        } else {
            if(effect instanceof EffectDamage)
                ((EffectDamage) effect).setFire(0);
            EffectMulti multiEffect = new EffectMulti(Lists.newArrayList(effect));
            return adjustEffect(multiEffect);
        }
    }
}
