package soot.recipe.breweffects;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;
import soot.brewing.FluidModifier;
import soot.util.FluidUtil;

import java.util.List;

public class EffectMin implements IBrewEffect {
    String modifier;
    float amount;
    boolean hidden;

    public EffectMin(String modifier, float amount, boolean hidden) {
        this.modifier = modifier;
        this.amount = amount;
        this.hidden = hidden;
    }

    private FluidModifier.EffectType getEffectType() {
        return FluidUtil.getEffectType(modifier);
    }

    @Override
    public void modify(FluidStack output, NBTTagCompound compound) {
        float value = getModifierOrDefault(modifier, compound, output);
        compound.setFloat(modifier, Math.min(value, amount));
    }

    @Override
    public void modifyTooltip(List<String> tooltip) {
        //NOOP
    }
}
