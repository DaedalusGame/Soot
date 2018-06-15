package soot.handler;

import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import soot.Registry;

public class FluidStitchHandler {
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
