package soot;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod.EventBusSubscriber(modid = Soot.MODID)
public class SoundEvents {
    @GameRegistry.ObjectHolder("soot:block.embers.alchemy.fail")
    public static SoundEvent ALCHEMY_FAIL;
    @GameRegistry.ObjectHolder("soot:block.embers.alchemy.success")
    public static SoundEvent ALCHEMY_SUCCESS;
    @GameRegistry.ObjectHolder("soot:block.embers.alchemy.loop")
    public static SoundEvent ALCHEMY_LOOP;
    @GameRegistry.ObjectHolder("soot:block.embers.alchemy.start")
    public static SoundEvent ALCHEMY_START;
    @GameRegistry.ObjectHolder("soot:block.embers.pedestal.loop")
    public static SoundEvent PEDESTAL_LOOP;
    @GameRegistry.ObjectHolder("soot:block.embers.beam_cannon.fire")
    public static SoundEvent BEAM_CANNON_FIRE;
    @GameRegistry.ObjectHolder("soot:block.embers.crystalcell")
    public static SoundEvent CRYSTAL_CELL;
    @GameRegistry.ObjectHolder("soot:block.embers.activator")
    public static SoundEvent ACTIVATOR;


    @SubscribeEvent
    public static void registerSounds(RegistryEvent.Register<SoundEvent> event) {
        event.getRegistry().register(registerSound("block.embers.alchemy.fail"));
        event.getRegistry().register(registerSound("block.embers.alchemy.success"));
        event.getRegistry().register(registerSound("block.embers.alchemy.loop"));
        event.getRegistry().register(registerSound("block.embers.alchemy.start"));
        event.getRegistry().register(registerSound("block.embers.pedestal.loop"));
        event.getRegistry().register(registerSound("block.embers.beam_cannon.fire"));
        event.getRegistry().register(registerSound("block.embers.crystalcell"));
        event.getRegistry().register(registerSound("block.embers.activator"));
    }

    public static SoundEvent registerSound(String soundName) {
        ResourceLocation soundID = new ResourceLocation(Soot.MODID, soundName);
        return new SoundEvent(soundID).setRegistryName(soundID);
    }
}
