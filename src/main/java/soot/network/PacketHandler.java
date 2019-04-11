package soot.network;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import soot.Soot;
import soot.network.message.*;


public class PacketHandler {
        public static SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(Soot.MODID);

        private static int id = 0;

        public static void registerMessages(){
            INSTANCE.registerMessage(MessageInspirationFX.MessageHolder.class, MessageInspirationFX.class,id++, Side.CLIENT);
            INSTANCE.registerMessage(MessageMuseSpawnFX.MessageHolder.class, MessageMuseSpawnFX.class,id++, Side.CLIENT);
            INSTANCE.registerMessage(MessageEitrFX.MessageHolder.class, MessageEitrFX.class,id++, Side.CLIENT);
            INSTANCE.registerMessage(MessageMixerFailFX.MessageHolder.class, MessageMixerFailFX.class,id++, Side.CLIENT);
            //INSTANCE.registerMessage(MessageWitchBurnFX.MessageHolder.class, MessageWitchBurnFX.class,id++, Side.CLIENT);
            INSTANCE.registerMessage(MessageMixerBlastFX.MessageHolder.class, MessageMixerBlastFX.class,id++, Side.CLIENT);
            INSTANCE.registerMessage(MessageAlchemyBlastFX.MessageHolder.class, MessageAlchemyBlastFX.class,id++, Side.CLIENT);
            INSTANCE.registerMessage(MessageAlchemyRingFX.MessageHolder.class, MessageAlchemyRingFX.class,id++, Side.CLIENT);
            INSTANCE.registerMessage(MessageFireBlastFX.MessageHolder.class, MessageFireBlastFX.class,id++, Side.CLIENT);
            INSTANCE.registerMessage(MessageGauntletActivate.MessageHolder.class, MessageGauntletActivate.class,id++, Side.SERVER);
            INSTANCE.registerMessage(MessageGauntletRotate.MessageHolder.class, MessageGauntletRotate.class,id++, Side.SERVER);
            INSTANCE.registerMessage(MessageGauntletDodge.MessageHolder.class, MessageGauntletDodge.class,id++, Side.SERVER);
        }
    }

