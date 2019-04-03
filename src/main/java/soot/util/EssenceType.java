package soot.util;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import teamroots.embers.EventManager;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class EssenceType implements Comparable<EssenceType> {
    public static Map<String,EssenceType> TYPES = new HashMap<>();

    public static final EssenceType NULL = new EssenceType("null",Color.WHITE,new Color(1,1,1,0));
    public static final EssenceType FIERY = new EssenceType("fiery") {
        @Override
        public Color getFillColor() {
            float sine = 0.5f*((float)Math.sin(Math.toRadians(4.0f*((float) EventManager.ticks + Minecraft.getMinecraft().getRenderPartialTicks())))+1.0f);
            return new Color(128,32+(int)(64*sine),8);
        }

        @Override
        public Color getOverlayColor() {
            //float cosine = 0.5f*((float)Math.cos(Math.toRadians(4.0f*((float)EventManager.ticks + Minecraft.getMinecraft().getRenderPartialTicks())))+1.0f);
            return new Color(255,128,16);
        }
    };

    String name;
    Color fillColor;
    Color overlayColor;

    public static EssenceType getType(String name) {
        return TYPES.getOrDefault(name,NULL);
    }

    public static Collection<EssenceType> getAllTypes() {
        return TYPES.values().stream().sorted().collect(Collectors.toList());
    }

    public EssenceType(String name) {
        this.name = name;
        TYPES.put(name,this);
    }

    public EssenceType(String name, Color fillColor, Color overlayColor) {
        this(name);
        this.fillColor = fillColor;
        this.overlayColor = overlayColor;
    }

    public String getName() {
        return name;
    }

    @SideOnly(Side.CLIENT)
    public Color getFillColor() {
        return fillColor;
    }

    @SideOnly(Side.CLIENT)
    public Color getOverlayColor() {
        return overlayColor;
    }

    @Override
    public int compareTo(@Nonnull EssenceType other) {
        return name.compareTo(other.name);
    }
}
