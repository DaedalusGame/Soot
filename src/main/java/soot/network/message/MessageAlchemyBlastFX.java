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

public class MessageAlchemyBlastFX implements IMessage {
    double x, y, z;
    Color mainColor;
    Color backColor;
    Color cubeColor;
    float scale;
    int lifetime;

    public MessageAlchemyBlastFX() {
    }

    public MessageAlchemyBlastFX(double x, double y, double z, Color mainColor, Color backColor, Color cubeColor, float scale, int lifetime) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.mainColor = mainColor;
        this.backColor = backColor;
        this.cubeColor = cubeColor;
        this.scale = scale;
        this.lifetime = lifetime;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        x = buf.readDouble();
        y = buf.readDouble();
        z = buf.readDouble();
        mainColor = new Color(buf.readInt(),true);
        backColor = new Color(buf.readInt(),true);
        cubeColor = new Color(buf.readInt(),true);
        scale = buf.readFloat();
        lifetime = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeDouble(x);
        buf.writeDouble(y);
        buf.writeDouble(z);
        buf.writeInt(mainColor.getRGB());
        buf.writeInt(backColor.getRGB());
        buf.writeInt(cubeColor.getRGB());
        buf.writeFloat(scale);
        buf.writeInt(lifetime);
    }

    public static class MessageHolder implements IMessageHandler<MessageAlchemyBlastFX, IMessage> {
        @SideOnly(Side.CLIENT)
        @Override
        public IMessage onMessage(final MessageAlchemyBlastFX message, final MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                World world = Minecraft.getMinecraft().world;
                ParticleUtilSoot.spawnAlchemyExplosion(world,message.x,message.y,message.z,message.mainColor,message.backColor,message.cubeColor,message.scale,message.lifetime);
            });
            return null;
        }
    }
}
