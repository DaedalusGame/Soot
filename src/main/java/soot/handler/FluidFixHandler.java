package soot.handler;

import com.google.common.collect.BiMap;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import soot.Registry;
import teamroots.embers.util.FluidTextureUtil;

import java.util.ArrayList;

public class FluidFixHandler {
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void onStitch(TextureStitchEvent.Post event) {
        try {
            BiMap<String,Fluid> masterFluidReference = ReflectionHelper.getPrivateValue(FluidRegistry.class,null,"masterFluidReference");
            TextureMap map = event.getMap();
            for (Fluid fluid : masterFluidReference.values()) {
                FluidTextureUtil.stillTextures.put(fluid, map.getAtlasSprite(fluid.getStill().toString()));
            }
        } catch (Exception ignore){} //Shut
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void onStitch(TextureStitchEvent.Pre event) {
        TextureMap map = event.getMap();
        for (Fluid fluid : Registry.FLUIDS) {
            map.registerSprite(fluid.getStill());
            map.registerSprite(fluid.getFlowing());
        }
    }
}
