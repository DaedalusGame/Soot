package soot.potion;

import net.minecraft.entity.SharedMonsterAttributes;
import soot.util.Attributes;

import java.awt.*;

public class PotionGlass extends PotionBase {
    public PotionGlass() {
        super(true, new Color(255,255,255).getRGB());
        setPotionName("effect.glass");
        setIconIndex(3,1);
        registerPotionAttributeModifier(Attributes.PHYSICAL_DAMAGE_RATE,"f4308cd0-3866-47eb-a289-01b32880d6d1",0.8,2);
    }
}
