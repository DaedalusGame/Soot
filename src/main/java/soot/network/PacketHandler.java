package soot.network;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import soot.Soot;
import soot.network.message.MessageEitrFX;
import soot.network.message.MessageInspirationFX;
import soot.network.message.MessageMixerFailFX;
import soot.network.message.MessageMuseSpawnFX;


public class PacketHandler {
        public static SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(Soot.MODID);

        private static int id = 0;

        public static void registerMessages(){
            INSTANCE.registerMessage(MessageInspirationFX.MessageHolder.class, MessageInspirationFX.class,id++, Side.CLIENT);
            INSTANCE.registerMessage(MessageMuseSpawnFX.MessageHolder.class, MessageMuseSpawnFX.class,id++, Side.CLIENT);
            INSTANCE.registerMessage(MessageEitrFX.MessageHolder.class, MessageEitrFX.class,id++, Side.CLIENT);
            INSTANCE.registerMessage(MessageMixerFailFX.MessageHolder.class, MessageMixerFailFX.class,id++, Side.CLIENT);
        }
    }

