package soot.itemmod;

import com.google.common.collect.Lists;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import soot.Registry;
import soot.handler.WitchburnHandler;
import teamroots.embers.api.event.EmberProjectileEvent;
import teamroots.embers.api.event.ItemVisualEvent;
import teamroots.embers.api.itemmod.ItemModUtil;
import teamroots.embers.api.itemmod.ModifierProjectileBase;
import teamroots.embers.api.projectile.*;

import java.util.ListIterator;

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
                    projectile.setColor(WitchburnHandler.COLOR);
                    projectile.setEffect(adjustEffect(projectile.getEffect(),level));
                }
        }
    }

    @SubscribeEvent
    public void onItemEffect(ItemVisualEvent event) {
        ItemStack stack = event.getItem();
        if(!stack.isEmpty() && ItemModUtil.hasHeat(stack)) {
            int level = ItemModUtil.getModifierLevel(stack, Registry.WITCHBURN);
            if(level > 0)
                event.setColor(WitchburnHandler.COLOR);
        }
    }

    private IProjectileEffect adjustEffect(IProjectileEffect effect, int level) {
        if (effect instanceof EffectArea) {
            EffectArea areaEffect = (EffectArea) effect;
            areaEffect.setEffect(adjustEffect(areaEffect.getEffect(),level));
            return areaEffect;
        } else if (effect instanceof EffectMulti) {
            ((EffectMulti) effect).addEffect(new EffectWitchburn(100 * level));
            return effect;
        } else {
            if(effect instanceof EffectDamage)
                ((EffectDamage) effect).setFire(0);
            EffectMulti multiEffect = new EffectMulti(Lists.newArrayList(effect));
            return adjustEffect(multiEffect,level);
        }
    }

}
