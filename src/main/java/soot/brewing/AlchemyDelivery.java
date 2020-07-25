package soot.brewing;

import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class AlchemyDelivery {
    private static List<AlchemyDelivery> REGISTRY = new ArrayList<>();

    double priority;

    public static AlchemyDelivery get(FluidStack stack) {
        for (AlchemyDelivery effect : REGISTRY) {
            if(effect.matches(stack))
                return effect;
        }
        return null;
    }

    public abstract boolean matches(FluidStack stack);

    public static void register(AlchemyDelivery delivery) {
        REGISTRY.add(delivery);
        REGISTRY.sort(AlchemyDelivery::compare);
    }

    private static int compare(AlchemyDelivery a, AlchemyDelivery b) {
        return Objects.compare(a.priority, b.priority, Double::compareTo);
    }
}
