package soot.potion;

import net.minecraft.entity.SharedMonsterAttributes;
import soot.util.Attributes;

import java.awt.*;

public class PotionAle extends PotionBase {
    public PotionAle() {
        super(false, new Color(125,78,24).getRGB());
        setPotionName("effect.ale");
        setIconIndex(0,0);
        setBeneficial();
        registerPotionAttributeModifier(SharedMonsterAttributes.ATTACK_DAMAGE,"6795bd8a-4239-454e-81a1-2d69b1316d66",0.8,2);
        registerPotionAttributeModifier(Attributes.PHYSICAL_DAMAGE_RATE,"caa69b2b-ac4f-42b6-887b-a35d2fcea3c9",0.8,2);
    }
}
