package soot.util;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.registries.IForgeRegistryEntry;
import soot.Soot;

public class EmberUtil {
    public static void overrideRegistryLocation(IForgeRegistryEntry.Impl forgeRegistryEntry, String name) {
        try {
            ObfuscationReflectionHelper.findField(IForgeRegistryEntry.Impl.class,"registryName").set(forgeRegistryEntry,new ResourceLocation(Soot.MODID,name));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
