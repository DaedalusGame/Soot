package soot.potion;

import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.common.MinecraftForge;

import java.awt.*;
import java.util.Random;

public class PotionTipsy extends PotionBase {
    public PotionTipsy() {
        super(true, new Color(90,70,20).getRGB());
        setPotionName("effect.tipsy");
        setIconIndex(5,0);
    }

    @Override
    public boolean isReady(int duration, int amplifier) {
        return duration % 10 == 0; //Every half second, have a % chance to drop item from hand
    }

    @Override
    public void performEffect(EntityLivingBase entity, int amplifier) {
        Random random = entity.getRNG();

        if(random.nextInt(100) < amplifier)
        {

        }
    }
}
