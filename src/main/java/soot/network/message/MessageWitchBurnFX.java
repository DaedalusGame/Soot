package soot.network.message;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import soot.Registry;
import soot.network.PacketHandler;
import soot.potion.PotionWitchBurn;

public class MessageWitchBurnFX implements IMessage {
    boolean active;
    int entityID;

    public MessageWitchBurnFX() {
    }

    public MessageWitchBurnFX(EntityLivingBase entity) {
        entityID = entity.getEntityId();
        active = entity.isPotionActive(Registry.POTION_WITCHBURN);
    }

    @Override
    public void fromBytes(ByteBuf byteBuf) {
        entityID = byteBuf.readInt();
        active = byteBuf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf byteBuf) {
        byteBuf.writeInt(entityID);
        byteBuf.writeBoolean(active);
    }

    public static class MessageHolder implements IMessageHandler<MessageWitchBurnFX, IMessage> {
        @SideOnly(Side.CLIENT)
        @Override
        public IMessage onMessage(final MessageWitchBurnFX message, final MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                World world = Minecraft.getMinecraft().world;
                Entity entity = world.getEntityByID(message.entityID);
                if(entity instanceof EntityLivingBase) {
                    PotionWitchBurn.appliedEntities.put((EntityLivingBase) entity,message.active);
                }
            });
            return null;
        }
    }
}
