package soot.util;

import net.minecraft.potion.PotionEffect;
import net.minecraftforge.fluids.Fluid;

import java.util.ArrayList;

public class CaskManager {
    public static ArrayList<CaskLiquid> liquids = new ArrayList<>();

    public static class CaskLiquid {
        public Fluid fluid;
        public int color;
        public ArrayList<PotionEffect> effects = new ArrayList<>();
    }
}
