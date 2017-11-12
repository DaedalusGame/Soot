package soot.util;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.fluids.Fluid;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CaskManager {
    public static ArrayList<CaskLiquid> liquids = new ArrayList<>();

    public static class CaskPotionEffect
    {
        public PotionEffect potionEffect;
        public int maxStack;

        public CaskPotionEffect(PotionEffect effect, int maxStack) {
            this.potionEffect = effect;
            this.maxStack = maxStack;
        }
    }

    public static class CaskLiquid {
        public Fluid fluid;
        public int model;
        public int color;
        public ArrayList<CaskPotionEffect> effects = new ArrayList<>();
        public IBlockState caskState;

        public CaskLiquid(Fluid fluid, int model, int color)
        {
            this.fluid = fluid;
            this.model = model;
            this.color = color;
        }

        public CaskLiquid setCaskState(IBlockState state)
        {
            caskState = state;
            return this;
        }

        public CaskLiquid addEffect(PotionEffect effect, int maxstack)
        {
            effects.add(new CaskPotionEffect(effect,maxstack));
            return this;
        }

        public List<PotionEffect> getEffects()
        {
            return effects.stream().map(x -> x.potionEffect).collect(Collectors.toCollection(ArrayList::new));
        }

        public void applyEffects(EntityLivingBase target, EntityLivingBase source, EntityLivingBase indirectsource) {
            for (CaskPotionEffect effect : effects)
            {
                PotionEffect potioneffect = effect.potionEffect;
                PotionEffect currentStack = target.getActivePotionEffect(potioneffect.getPotion());
                if (potioneffect.getPotion().isInstant())
                {
                    potioneffect.getPotion().affectEntity(source, indirectsource, target, potioneffect.getAmplifier(), 1.0D);
                }
                else
                {
                    int amplifier = currentStack.getAmplifier();
                    int duration = currentStack.getDuration();
                    if(currentStack != null)
                    {
                        amplifier = Math.min(amplifier + currentStack.getAmplifier() + 1,effect.maxStack);
                        if(amplifier != currentStack.getAmplifier())
                            duration += currentStack.getDuration();
                    }
                    PotionEffect newStack = new PotionEffect(potioneffect.getPotion(),duration,amplifier,false,false); //TODO: curative item?? alchemical hangover cure???
                    target.addPotionEffect(newStack);
                }
            }
        }
    }

    @Nullable
    public static CaskLiquid getFromCask(IBlockState state)
    {
        for (CaskLiquid liquid : liquids) {
            if(liquid.caskState.equals(state))
                return liquid;
        }

        return null;
    }

    @Nullable
    public static CaskLiquid getFromFluid(Fluid fluid)
    {
        for (CaskLiquid liquid : liquids) {
            if(liquid.fluid.equals(fluid))
                return liquid;
        }

        return null;
    }

    public static void register(CaskLiquid liquid)
    {
        liquids.add(liquid);
    }
}
