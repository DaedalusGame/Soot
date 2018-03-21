package soot.potion;

import net.minecraftforge.common.MinecraftForge;

import java.awt.*;

public class PotionInspiration extends PotionBase {
    public PotionInspiration() {
        super(false, new Color(64,255,32).getRGB());
        setPotionName("effect.inspiration");
        setIconIndex(5,0);
        setBeneficial();
        MinecraftForge.EVENT_BUS.register(this);
    }
}
