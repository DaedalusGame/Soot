package soot.brewing.delivery;

import net.minecraftforge.fluids.FluidStack;
import soot.brewing.AlchemyDelivery;

public class DeliveryBlast extends AlchemyDelivery {
    @Override
    public boolean matches(FluidStack stack) {
        return true;
    }
}
