package soot.compat.crafttweaker;

import crafttweaker.IAction;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.liquid.ILiquidDefinition;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.mc1120.CraftTweaker;
import net.minecraftforge.fluids.Fluid;
import soot.brewing.CaskManager;
import soot.util.FluidUtil;
import soot.util.MiscUtil;
import stanhebben.zenscript.annotations.ZenExpansion;
import stanhebben.zenscript.annotations.ZenMethod;

import java.awt.*;

@ZenExpansion("crafttweaker.liquid.ILiquidDefinition")
@ZenRegister
public class ExpansionFluid {
    @ZenMethod
    public static void setBrewingModifier(ILiquidDefinition liquidDefinition, String modifier, float value) {
        Fluid fluid = CraftTweakerMC.getFluid(liquidDefinition);
        if(fluid != null)
            CraftTweaker.LATE_ACTIONS.add(new SetBrewingModifier(fluid, modifier, value));
    }

    @ZenMethod
    public static void createMug(ILiquidDefinition liquidDefinition, int model, int[] rgb) {
        Fluid fluid = CraftTweakerMC.getFluid(liquidDefinition);
        Color color = MiscUtil.parseColor(rgb);
        if(fluid != null)
            CraftTweaker.LATE_ACTIONS.add(new SetMug(fluid, model, color));
    }

    public static class SetMug implements IAction {
        Fluid fluid;
        int model;
        Color color;

        public SetMug(Fluid fluid, int model, Color color) {
            this.fluid = fluid;
            this.model = model;
            this.color = color;
        }

        @Override
        public void apply() {
            CaskManager.register(new CaskManager.CaskLiquid(fluid, model, color.getRGB()));
        }

        @Override
        public String describe() {
            return null;
        }
    }

    public static class SetBrewingModifier implements IAction {
        Fluid fluid;
        String modifier;
        float value;

        public SetBrewingModifier(Fluid fluid, String modifier, float value) {
            this.fluid = fluid;
            this.modifier = modifier;
            this.value = value;
        }

        @Override
        public void apply() {
            FluidUtil.setDefaultValue(fluid, modifier, value);
        }

        @Override
        public String describe() {
            return String.format("Setting default modifier \"%s\" to %s for %s", modifier, value, fluid);
        }
    }
}