package soot.network.message;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import soot.particle.ParticleUtilSoot;

import java.awt.*;

public class MessageAlchemyRingFX implements IMessage {
    double x, y, z;
    Color mainColor;
    int segments;
    double distance;
    boolean withCube;

    public MessageAlchemyRingFX() {
    }

    public MessageAlchemyRingFX(double x, double y, double z, Color mainColor, int segments, double distance, boolean withCube) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.mainColor = mainColor;
        this.segments = segments;
        this.distance = distance;
        this.withCube = withCube;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        x = buf.readDouble();
        y = buf.readDouble();
        z = buf.readDouble();
        mainColor = new Color(buf.readInt(),true);
        segments = buf.readInt();
        distance = buf.readDouble();
        withCube = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeDouble(x);
        buf.writeDouble(y);
        buf.writeDouble(z);
        buf.writeInt(mainColor.getRGB());
        buf.writeInt(segments);
        buf.writeDouble(distance);
        buf.writeBoolean(withCube);
    }

    public static class MessageHolder implements IMessageHandler<MessageAlchemyRingFX, IMessage> {
        @SideOnly(Side.CLIENT)
        @Override
        public IMessage onMessage(final MessageAlchemyRingFX message, final MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                World world = Minecraft.getMinecraft().world;
                ParticleUtilSoot.spawnCubeRing(world,message.x,message.y,message.z,message.mainColor,message.segments,message.distance);
                if(message.withCube)
                    ParticleUtilSoot.spawnParticleCube(world, message.x,message.y,message.z, 0, 0, 0, message.mainColor, 2.0f, 10);
            });
            return null;
        }
    }
}
