package soot.network.message;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.EnumHand;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import soot.item.ItemAlchemyGauntlet;

public class MessageGauntletRotate implements IMessage {
    EnumHand hand;

    public MessageGauntletRotate() {
    }

    public MessageGauntletRotate(EnumHand hand) {
        this.hand = hand;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        PacketBuffer buffer = new PacketBuffer(buf);
        hand = buffer.readEnumValue(EnumHand.class);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        PacketBuffer buffer = new PacketBuffer(buf);
        buffer.writeEnumValue(hand);
    }

    public static class MessageHolder implements IMessageHandler<MessageGauntletRotate, IMessage> {
        @Override
        public IMessage onMessage(final MessageGauntletRotate message, final MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().player;
            WorldServer world = player.getServerWorld();
            world.addScheduledTask(() -> {
                ItemStack held = player.getHeldItem(message.hand);
                if(held.getItem() instanceof ItemAlchemyGauntlet) {
                    ItemAlchemyGauntlet gauntlet = (ItemAlchemyGauntlet) held.getItem();
                    gauntlet.rotate(held,player,message.hand);
                }
            });
            return null;
        }
    }
}
