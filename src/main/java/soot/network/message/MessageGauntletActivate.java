package soot.network.message;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import soot.item.ItemAlchemyGauntlet;

public class MessageGauntletActivate implements IMessage {
    enum Type {
        Block,
        Direction,
    }

    EnumHand hand;
    Type type;
    BlockPos pos;
    EnumFacing side;
    double hitX, hitY, hitZ;
    double faceX, faceY, faceZ;

    public MessageGauntletActivate() {
    }

    public MessageGauntletActivate(EnumHand hand, BlockPos pos, EnumFacing side, double hitX, double hitY, double hitZ) {
        this.type = Type.Block;
        this.hand = hand;
        this.pos = pos;
        this.side = side;
        this.hitX = hitX;
        this.hitY = hitY;
        this.hitZ = hitZ;
    }

    public MessageGauntletActivate(EnumHand hand, double faceX, double faceY, double faceZ) {
        this.type = Type.Direction;
        this.faceX = faceX;
        this.faceY = faceY;
        this.faceZ = faceZ;
        this.hand = hand;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        PacketBuffer buffer = new PacketBuffer(buf);
        hand = buffer.readEnumValue(EnumHand.class);
        type = buffer.readEnumValue(Type.class);
        switch(type) {
            case Block:
                pos = buffer.readBlockPos();
                side = buffer.readEnumValue(EnumFacing.class);
                hitX = buffer.readDouble();
                hitY = buffer.readDouble();
                hitZ = buffer.readDouble();
                break;
            case Direction:
                faceX = buffer.readDouble();
                faceY = buffer.readDouble();
                faceZ = buffer.readDouble();
                break;
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        PacketBuffer buffer = new PacketBuffer(buf);
        buffer.writeEnumValue(hand);
        buffer.writeEnumValue(type);
        switch(type) {
            case Block:
                buffer.writeBlockPos(pos);
                buffer.writeEnumValue(side);
                buffer.writeDouble(hitX);
                buffer.writeDouble(hitY);
                buffer.writeDouble(hitZ);
                break;
            case Direction:
                buffer.writeDouble(faceX);
                buffer.writeDouble(faceY);
                buffer.writeDouble(faceZ);
                break;
        }
    }

    public static class MessageHolder implements IMessageHandler<MessageGauntletActivate, IMessage> {
        @Override
        public IMessage onMessage(final MessageGauntletActivate message, final MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().player;
            WorldServer world = player.getServerWorld();
            world.addScheduledTask(() -> {
                ItemStack held = player.getHeldItem(message.hand);
                if(held.getItem() instanceof ItemAlchemyGauntlet) {
                    ItemAlchemyGauntlet gauntlet = (ItemAlchemyGauntlet) held.getItem();
                    switch (message.type) {
                        case Block:
                            gauntlet.activateBlock(held,player,message.hand,message.pos,message.side);
                            break;
                        case Direction:
                            gauntlet.activate(held,player,message.hand,new Vec3d(message.faceX,message.faceY,message.faceZ));
                            break;
                    }

                }
            });
            return null;
        }
    }
}
