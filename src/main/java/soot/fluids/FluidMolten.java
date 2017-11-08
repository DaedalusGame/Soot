package soot.fluids;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;

public class FluidMolten extends Fluid {
    public FluidMolten(String fluidName, ResourceLocation still, ResourceLocation flowing) {
        super(fluidName, still, flowing);
        this.setViscosity(6000);
        this.setDensity(2000);
        this.setLuminosity(15);
        this.setTemperature(900);
    }
}
