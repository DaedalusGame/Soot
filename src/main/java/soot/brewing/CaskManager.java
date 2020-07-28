package soot.brewing;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import soot.brewing.deliverytypes.DeliveryBlast;
import soot.util.FluidUtil;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CaskManager {
    public static ArrayList<CaskLiquid> liquids = new ArrayList<>();
    public static Map<String,DeliveryType.IGenerator> deliveryTypes = new HashMap<>();

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
        //public ArrayList<CaskPotionEffect> effects = new ArrayList<>();

        public CaskLiquid(Fluid fluid, int model, int color)
        {
            this.fluid = fluid;
            this.model = model;
            this.color = color;
        }

        /*public CaskLiquid addEffect(PotionEffect effect, int maxstack)
        {
            effects.add(new CaskPotionEffect(effect,maxstack));
            return this;
        }

        public List<PotionEffect> getEffects()
        {
            return effects.stream().map(x -> x.potionEffect).collect(Collectors.toCollection(ArrayList::new));
        }*/

        public void applyEffects(World world, BlockPos pos, EnumFacing side, Entity source, Entity indirectsource, FluidStack fluid) {

        }

        public void applyEffects(EntityLivingBase target, Entity source, Entity indirectsource, FluidStack fluid) {
            ArrayList<CaskPotionEffect> effects = new ArrayList<>();
            Fluid internal = fluid != null ? fluid.getFluid() : null;
            NBTTagCompound compound = FluidUtil.getModifiers(fluid);
            for (String key : FluidUtil.getModifierKeys(fluid)) {
                FluidModifier modifier = FluidUtil.MODIFIERS.get(key);
                if(modifier != null) {
                    modifier.applyEffect(target, compound, internal);
                    modifier.providePotionEffects(target, effects, compound, internal);
                }
            }

            float duration_modifier = FluidUtil.getModifier(compound, internal, "duration");
            boolean concentrated = FluidUtil.getModifier(compound, internal,"concentration") >= 100;
            boolean showParticles = FluidUtil.getModifier(compound, internal,"concentration") >= 50;

            for (CaskPotionEffect effect : effects) {
                PotionEffect potioneffect = effect.potionEffect;
                PotionEffect currentStack = target.getActivePotionEffect(potioneffect.getPotion());
                int concentration_bonus = concentrated ? 1 : 0;
                if (potioneffect.getPotion().isInstant()) {
                    potioneffect.getPotion().affectEntity(source, indirectsource, target, potioneffect.getAmplifier() + concentration_bonus, 1.0D);
                } else {
                    int amplifier = potioneffect.getAmplifier();
                    int duration = (int)(potioneffect.getDuration() * duration_modifier);
                    if(currentStack != null)
                    {
                        amplifier = Math.min(amplifier + currentStack.getAmplifier() + 1,effect.maxStack + concentration_bonus);
                        if(amplifier != currentStack.getAmplifier())
                            duration += currentStack.getDuration();
                    }
                    PotionEffect newStack = new PotionEffect(potioneffect.getPotion(),duration,amplifier,false, showParticles); //TODO: curative item?? alchemical hangover cure???
                    target.addPotionEffect(newStack);
                }
            }
        }
    }

    public static DeliveryType.IGenerator getDeliveryType(FluidStack fluidStack) {
        NBTTagCompound compound = FluidUtil.getModifiers(fluidStack);
        Fluid fluid = fluidStack != null ? fluidStack.getFluid() : null;
        FluidUtil.getModifier(compound,fluid,"");
        for (Map.Entry<String,DeliveryType.IGenerator> entry : deliveryTypes.entrySet()) {
            if(FluidUtil.getModifier(compound,fluid,entry.getKey()) > 0)
                return entry.getValue();
        }
        return (gauntlet, elixir, user, fluid1) -> new DeliveryBlast(user, fluid1,8.0,3.0);
    }

    @Nullable
    public static CaskLiquid getFromFluid(FluidStack fluidStack) {
        if(fluidStack != null)
            return getFromFluid(fluidStack.getFluid());

        return null;
    }

    @Nullable
    public static CaskLiquid getFromFluid(Fluid fluid) {
        for (CaskLiquid liquid : liquids) {
            if(liquid.fluid.equals(fluid))
                return liquid;
        }

        return null;
    }

    public static void register(String deliveryType, DeliveryType.IGenerator generator) {
        deliveryTypes.put(deliveryType,generator);
    }

    public static void register(CaskLiquid liquid) {
        liquids.add(liquid);
    }
}
