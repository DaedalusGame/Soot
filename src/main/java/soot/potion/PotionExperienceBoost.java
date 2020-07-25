package soot.potion;

import soot.util.Attributes;

import java.awt.*;

public class PotionExperienceBoost extends PotionBase {
    public PotionExperienceBoost() {
        super(false, new Color(128,255,16).getRGB());
        setPotionName("effect.experience_boost");
        setIconIndex(4,1);
        setBeneficial();
        registerPotionAttributeModifier(Attributes.EXPERIENCE_RATE,"c7689904-5ad0-404f-b348-37aeb47f4dc5",0.5,2);
    }
}
