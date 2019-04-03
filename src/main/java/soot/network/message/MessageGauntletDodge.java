package soot.network.message;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import soot.item.ItemAlchemyGauntlet;

public class MessageGauntletDodge implements IMessage {
    EnumHand hand;
    Vec3d visualPos;

    public MessageGauntletDodge() {
    }

    public MessageGauntletDodge(EnumHand hand, Vec3d visualPos) {
        this.hand = hand;
        this.visualPos = visualPos;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        PacketBuffer buffer = new PacketBuffer(buf);
        hand = buffer.readEnumValue(EnumHand.class);
        visualPos = new Vec3d(buffer.readDouble(),buffer.readDouble(),buffer.readDouble());
    }

    @Override
    public void toBytes(ByteBuf buf) {
        PacketBuffer buffer = new PacketBuffer(buf);
        buffer.writeEnumValue(hand);
        buffer.writeDouble(visualPos.x);
        buffer.writeDouble(visualPos.y);
        buffer.writeDouble(visualPos.z);
    }

    public static class MessageHolder implements IMessageHandler<MessageGauntletDodge, IMessage> {
        @Override
        public IMessage onMessage(final MessageGauntletDodge message, final MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().player;
            WorldServer world = player.getServerWorld();
            world.addScheduledTask(() -> {
                ItemStack held = player.getHeldItem(message.hand);
                if(held.getItem() instanceof ItemAlchemyGauntlet) {
                    ItemAlchemyGauntlet gauntlet = (ItemAlchemyGauntlet) held.getItem();
                    gauntlet.dodge(held,player,message.hand,message.visualPos);
                }
            });
            return null;
        }
    }
}
