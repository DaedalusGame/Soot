package soot.potion;

import net.minecraftforge.common.MinecraftForge;

import java.awt.*;

public class PotionSnowpoff extends PotionBase {
    public PotionSnowpoff() {
        super(false, new Color(240,255,255).getRGB());
        setPotionName("Snowpoff");
        setIconIndex(4,0);
        setBeneficial();
        MinecraftForge.EVENT_BUS.register(this);
    }
}
