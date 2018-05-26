package soot.util;

import com.google.common.hash.Hashing;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.util.math.MathHelper;
import teamroots.embers.EventManager;
import teamroots.embers.util.EmberGenUtil;
import teamroots.embers.util.NoiseGenUtil;

import java.awt.*;
import java.util.Arrays;
import java.util.Random;

public class RenderUtil {
    public static void renderWavyEmberLine(BufferBuilder b, double x1, double y1, double x2, double y2, double thickness, Color color){
        double angleRads = Math.atan2(y2-y1, x2-x1);
        double orthoX = Math.cos(angleRads+(Math.PI/2.0));
        double orthoY = Math.sin(angleRads+(Math.PI/2.0));
        //Random seedRandom = new Random(Arrays.hashCode(new double[] {x1,y1,x2,y2}));
        int segments = 10;
        for (int i = 0; i <= segments; i ++){
            float coeff = (float)i / (float)segments;
            double thickCoeff = Math.min(1.0, 1.4f* MathHelper.sqrt(2.0f*(0.5f-Math.abs((coeff-0.5f)))));
            double tx = x1*(1.0f-coeff) + x2*coeff;
            double ty = NoiseGenUtil.interpolate((float)y1, (float)y2, coeff);
            float tick = Minecraft.getMinecraft().getRenderPartialTicks()+ EventManager.ticks;
            int offX = (int)(6f*tick);
            int offZ = (int)(6f*tick);
            float sine = (float)Math.sin(coeff*Math.PI*2.0f + 0.25f*(tick)) + 0.25f*(float)Math.sin(coeff*Math.PI*3.47f + 0.25f*(tick));
            //sine = (seedRandom.nextFloat() - 0.5f) * 4;
            float sineOff = (4.0f + (float)thickness)/3.0f;
            float minusDensity = EmberGenUtil.getEmberDensity(1, offX+(int)(tx-thickness*orthoX*thickCoeff), offZ+(int)(ty-thickness*orthoY*thickCoeff));
            float plusDensity = EmberGenUtil.getEmberDensity(1, offX+(int)(tx-thickness*orthoX*thickCoeff), offZ+(int)(ty-thickness*orthoY*thickCoeff));
            float red = color.getRed() / 255f;
            float green = color.getGreen() / 255f;
            float blue = color.getBlue() / 255f;
            b.pos(tx-thickness*(0.5f+minusDensity)*orthoX*thickCoeff-thickCoeff*orthoX*sine*sineOff, ty-thickness*(0.5f+minusDensity)*orthoY*thickCoeff-thickCoeff*orthoY*sine*sineOff, 0).color(red, green, blue, (float)Math.pow(0.5f*(float)Math.max(0,thickCoeff-0.4f)*minusDensity,1)).endVertex();
            b.pos(tx+thickness*(0.5f+plusDensity)*orthoX*thickCoeff-thickCoeff*orthoX*sine*sineOff, ty+thickness*(0.5f+plusDensity)*orthoY*thickCoeff-thickCoeff*orthoY*sine*sineOff, 0).color(red, green, blue, (float)Math.pow(0.5f*(float)Math.max(0,thickCoeff-0.4f)*plusDensity,1)).endVertex();
        }
    }
}
