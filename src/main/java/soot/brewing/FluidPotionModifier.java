package soot.brewing;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.fluids.Fluid;

import java.util.ArrayList;
import java.util.List;

public class FluidPotionModifier extends FluidModifier {
    Potion potion;
    int maxStack;

    public FluidPotionModifier(String name, float defaultValue, EnumType type, EffectType effectType, Potion potion, int maxStack) {
        super(name, defaultValue, type, effectType);
        this.potion = potion;
        this.maxStack = maxStack;
    }

    @Override
    public void providePotionEffects(EntityLivingBase target, ArrayList<CaskManager.CaskPotionEffect> effects, NBTTagCompound compound, Fluid fluid) {
        float value = getOrDefault(compound, fluid);
        effects.add(new CaskManager.CaskPotionEffect(new PotionEffect(potion, (int) value),maxStack));
    }

    @Override
    public void providePotionEffects(List<PotionEffect> effects, NBTTagCompound compound, Fluid fluid) {
        float value = getOrDefault(compound, fluid);
        effects.add(new PotionEffect(potion, (int) value));
    }
}
