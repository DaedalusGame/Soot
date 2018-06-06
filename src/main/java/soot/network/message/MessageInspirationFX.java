package soot.network.message;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import soot.SoundEvents;
import soot.util.GlobalSound;

public class MessageInspirationFX implements IMessage {
    Type type;

    public enum Type {
        Start,
        Stop,
        Refresh,
    }

    public MessageInspirationFX() {
    }

    public MessageInspirationFX(Type type) {
        this.type = type;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        type = Type.values()[buf.readInt()];
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(type.ordinal());
    }

    public static class MessageHolder implements IMessageHandler<MessageInspirationFX, IMessage> {
        @SideOnly(Side.CLIENT)
        @Override
        public IMessage onMessage(final MessageInspirationFX message, final MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                SoundHandler soundHandler = Minecraft.getMinecraft().getSoundHandler();
                switch (message.type) {
                    case Start:
                        soundHandler.playSound(new GlobalSound(SoundEvents.INSPIRATION_START, SoundCategory.AMBIENT, 1.0f, 1.0f));
                        break;
                    case Stop:
                        soundHandler.playSound(new GlobalSound(SoundEvents.INSPIRATION_END, SoundCategory.AMBIENT, 1.0f, 1.0f));
                        break;
                    case Refresh:
                        soundHandler.playSound(new GlobalSound(SoundEvents.INSPIRATION_REFRESH, SoundCategory.AMBIENT, 1.0f, 1.0f));
                        break;
                }
            });
            return null;
        }
    }
}
