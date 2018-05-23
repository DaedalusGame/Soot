package soot.potion;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import soot.util.Attributes;

import java.awt.*;

public class PotionSteadfast extends PotionBase {
    public PotionSteadfast() {
        super(false, new Color(150,255,255).getRGB());
        setPotionName("effect.steadfast");
        setIconIndex(0,1);
        setBeneficial();
        registerPotionAttributeModifier(SharedMonsterAttributes.KNOCKBACK_RESISTANCE,"77ec8473-e919-466f-ac55-5bb81271e36d",-0.4,2);
        registerPotionAttributeModifier(SharedMonsterAttributes.MOVEMENT_SPEED,"2c2152e1-7aff-4dd5-8fb7-9f60e914ce33",0.4,2);
        registerPotionAttributeModifier(SharedMonsterAttributes.ATTACK_SPEED,"3c539200-6eb9-480e-8dff-2519a356b50e",0.3,2);
    }
}