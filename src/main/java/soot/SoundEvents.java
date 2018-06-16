package soot;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod.EventBusSubscriber(modid = Soot.MODID)
public class SoundEvents {
    @GameRegistry.ObjectHolder("soot:block.embers.still.loop")
    public static SoundEvent STILL_LOOP;
    @GameRegistry.ObjectHolder("soot:block.embers.still.slow")
    public static SoundEvent STILL_SLOW;
    @GameRegistry.ObjectHolder("soot:block.embers.still.fast")
    public static SoundEvent STILL_FAST;
    @GameRegistry.ObjectHolder("soot:block.embers.alchemical_mixer.waste")
    public static SoundEvent ALCHEMICAL_MIXER_WASTE;
    @GameRegistry.ObjectHolder("soot:block.sulfur_ore.vent")
    public static SoundEvent SULFUR_VENT;
    @GameRegistry.ObjectHolder("soot:inspiration.start")
    public static SoundEvent INSPIRATION_START;
    @GameRegistry.ObjectHolder("soot:inspiration.end")
    public static SoundEvent INSPIRATION_END;
    @GameRegistry.ObjectHolder("soot:inspiration.refresh")
    public static SoundEvent INSPIRATION_REFRESH;
    @GameRegistry.ObjectHolder("soot:inspiration.loop")
    public static SoundEvent INSPIRATION_LOOP;
    @GameRegistry.ObjectHolder("soot:inspiration.muse_appear")
    public static SoundEvent INSPIRATION_MUSE_APPEAR;

    @SubscribeEvent
    public static void registerSounds(RegistryEvent.Register<SoundEvent> event) {
        event.getRegistry().register(registerSound("block.embers.still.loop"));
        event.getRegistry().register(registerSound("block.embers.still.slow"));
        event.getRegistry().register(registerSound("block.embers.still.fast"));
        event.getRegistry().register(registerSound("block.embers.alchemical_mixer.waste"));
        event.getRegistry().register(registerSound("block.sulfur_ore.vent"));
        event.getRegistry().register(registerSound("inspiration.start"));
        event.getRegistry().register(registerSound("inspiration.end"));
        event.getRegistry().register(registerSound("inspiration.refresh"));
        event.getRegistry().register(registerSound("inspiration.loop"));
        event.getRegistry().register(registerSound("inspiration.muse_appear"));
    }

    public static SoundEvent registerSound(String soundName) {
        ResourceLocation soundID = new ResourceLocation(Soot.MODID, soundName);
        return new SoundEvent(soundID).setRegistryName(soundID);
    }
}
