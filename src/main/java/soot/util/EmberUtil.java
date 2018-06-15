package soot.util;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.registries.IForgeRegistryEntry;
import soot.Soot;

public class EmberUtil {
    public static void overrideRegistryLocation(IForgeRegistryEntry.Impl forgeRegistryEntry, String name) {
        try {
            ReflectionHelper.findField(IForgeRegistryEntry.Impl.class,"registryName").set(forgeRegistryEntry,new ResourceLocation(Soot.MODID,name));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
