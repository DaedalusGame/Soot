package soot.brewing;

import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AlchemyEffect {
    private static List<AlchemyEffect> REGISTRY = new ArrayList<>();

    double priority;

    public static AlchemyEffect get(FluidStack stack) {
        for (AlchemyEffect effect : REGISTRY) {
            if(effect.matches(stack))
                return effect;
        }
        return null;
    }

    private boolean matches(FluidStack stack) {
        return false;
    }

    public static void register(AlchemyEffect effect) {
        REGISTRY.add(effect);
        REGISTRY.sort(AlchemyEffect::compare);
    }

    private static int compare(AlchemyEffect a, AlchemyEffect b) {
        return Objects.compare(a.priority, b.priority, Double::compareTo);
    }
}
