package soot.potion;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import soot.util.Attributes;

import java.awt.*;

public class PotionStoutness extends PotionBase {
    public PotionStoutness() {
        super(false, new Color(240,255,128).getRGB());
        setPotionName("effect.stoutness");
        setIconIndex(2,0);
        setBeneficial();
        registerPotionAttributeModifier(SharedMonsterAttributes.ATTACK_DAMAGE,"328b29cb-9d51-4e1f-b92b-9ed06a0ebe4f",-0.1,2);
        registerPotionAttributeModifier(Attributes.PHYSICAL_DAMAGE_RATE,"1fa43086-4054-45ce-923c-261fc4c026d3",-0.1,2);
    }

    @Override
    public void performEffect(EntityLivingBase entityLivingBaseIn, int amplifier) {
        super.performEffect(entityLivingBaseIn, amplifier);
        //Increase Alcohol Resistance over time (We obviously don't want that)
    }
}
