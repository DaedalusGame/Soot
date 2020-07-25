package soot.brewing;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import soot.util.MiscUtil;
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
    public static final EssenceType FIRE = new EssenceType("fire") {
        @Override
        public Color getFillColor() {
            float sine = 0.5f*((float)Math.sin(Math.toRadians(4.0f*getTicks()))+1.0f);
            return new Color(128,32+(int)(64*sine),8);
        }

        @Override
        public Color getOverlayColor() {
            //float cosine = 0.5f*((float)Math.cos(Math.toRadians(4.0f*((float)EventManager.ticks + Minecraft.getMinecraft().getRenderPartialTicks())))+1.0f);
            return new Color(255,128,16);
        }
    };
    public static final EssenceType ICE = new EssenceType("ice",new Color(192,240,255,255),new Color(255,255,255,255));
    public static final EssenceType TOXIN = new EssenceType("toxin",new Color(120,0,240,255),new Color(180,0,255,255));
    public static final EssenceType VILE = new EssenceType("vile",new Color(128,32,0,255),new Color(192,48,0,255));
    public static final EssenceType DEATH = new EssenceType("death",new Color(64,64,64,255),new Color(0,0,0,0));
    public static final EssenceType SOUR = new EssenceType("sour",new Color(128,255,64,255),new Color(0,0,0,0));
    public static final EssenceType SWEET = new EssenceType("sweet",new Color(255,128,255,255),new Color(255,255,255,255));
    public static final EssenceType CLOUD = new EssenceType("cloud",new Color(0,64,64,255),new Color(128,128,255,255));
    public static final EssenceType LIFEDRINKER = new EssenceType("lifedrinker",new Color(128,32,0,255),new Color(0,0,0,0));
    public static final EssenceType SLOWNESS = new EssenceType("slowness",new Color(192,192,255,255),new Color(0,0,0,0));
    public static final EssenceType SPEED = new EssenceType("speed",new Color(128,128,255,255),new Color(255,255,255,255));
    public static final EssenceType INVERSION = new EssenceType("inversion") {
        @Override
        public Color getFillColor() {
            float sine = 0.5f*((float)Math.sin(Math.toRadians(4.0f*getTicks()))+1.0f);
            return MiscUtil.lerpColor(new Color(255,0,255,255),new Color(0,0,0,255),sine);
        }

        @Override
        public Color getOverlayColor() {
            float sine = 0.5f*((float)Math.sin(Math.toRadians(4.0f*getTicks()))+1.0f);
            return MiscUtil.lerpColor(new Color(255,0,255,255),new Color(0,0,0,255),1-sine);
        }
    };
    public static final EssenceType POISON = new EssenceType("poison",new Color(128,255,0,255),new Color(64,128,0,255));
    public static final EssenceType REGENERATION = new EssenceType("regeneration",new Color(255,128,255,255),new Color(0,0,0,0));
    public static final EssenceType WITHER = new EssenceType("wither",new Color(0,0,0,255),new Color(64,64,64,255));
    public static final EssenceType EXPERIENCE = new EssenceType("experience",new Color(128,255,0,255),new Color(255,255,0,255));
    public static final EssenceType HASTE = new EssenceType("haste",new Color(192,128,0,255),new Color(255,200,96,255));
    public static final EssenceType CHAOS = new EssenceType("chaos") {
        @Override
        public Color getFillColor() {
            return Color.getHSBColor(getTicks()*0.05f,1.0f,1.0f);
        }

        @Override
        public Color getOverlayColor() {
            return Color.getHSBColor((getTicks())*0.05f + 0.33f,1.0f,1.0f);
        }
    };
    public static final EssenceType PROJECTILE = new EssenceType("projectile",new Color(255,255,255,255),new Color(0,0,0,255));
    public static final EssenceType GLASS = new EssenceType("glass",new Color(255,255,255,255),new Color(0,0,0,0));
    public static final EssenceType EXTRACT = new EssenceType("extract",new Color(192,192,64,255),new Color(192,128,128,255));

    @SideOnly(Side.CLIENT)
    private static float getTicks() {
        return EventManager.ticks + Minecraft.getMinecraft().getRenderPartialTicks();
    }

    private String name;
    private Color fillColor;
    private Color overlayColor;

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
        return MiscUtil.lerpColor(fillColor,overlayColor,(double)overlayColor.getAlpha()/fillColor.getAlpha());
    }

    @Override
    public int compareTo(@Nonnull EssenceType other) {
        return name.compareTo(other.name);
    }
}
