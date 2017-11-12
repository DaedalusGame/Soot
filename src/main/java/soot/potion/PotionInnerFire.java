package soot.potion;

import net.minecraftforge.common.MinecraftForge;

import java.awt.*;

public class PotionInnerFire extends PotionBase {
    public PotionInnerFire() {
        super(false, new Color(240,32,32).getRGB());
        setPotionName("InnerFire");
        setIconIndex(1,0);
        setBeneficial();
        MinecraftForge.EVENT_BUS.register(this);
    }
}
