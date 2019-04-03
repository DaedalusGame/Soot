package soot.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import teamroots.embers.EventManager;

import java.awt.*;

public class ItemElixir extends Item {
    public Color getColor(ItemStack stack) {
        float hue = (Math.abs(stack.hashCode()) % 1000) / 1000.0f;
        return Color.getHSBColor(hue,1.0f,1.0f);
    }

    @Override
    public boolean hasEffect(ItemStack stack) {
        return false;
    }

    @Override
    public int getRGBDurabilityForDisplay(ItemStack stack) {
        Color color = getColor(stack);
        float glow = (float) MathHelper.clampedLerp(1.0,2.0,0.5 + 0.5*Math.sin(EventManager.ticks * Math.PI * 2 / 100.0));
        return new Color(MathHelper.clamp(color.getRed() * glow / 255,0,1),MathHelper.clamp(color.getGreen() * glow / 255,0,1),MathHelper.clamp(color.getBlue() * glow / 255,0,1)).getRGB();
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        return true;
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack) {
        return 0.0;
    }
}
