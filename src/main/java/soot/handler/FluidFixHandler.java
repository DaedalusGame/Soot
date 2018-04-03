package soot.handler;

import com.google.common.collect.BiMap;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import teamroots.embers.util.FluidTextureUtil;

public class FluidFixHandler {
    @SideOnly(Side.CLIENT)
    public static void onStitch(TextureStitchEvent.Post event)
    {
        BiMap<String,Fluid> masterFluidReference = ReflectionHelper.getPrivateValue(FluidRegistry.class,null,"masterFluidReference");
        TextureMap map = event.getMap();
        for (Fluid fluid : masterFluidReference.values()) {
            FluidTextureUtil.stillTextures.put(fluid, map.getAtlasSprite(fluid.getStill().toString()));
        }
    }
}
