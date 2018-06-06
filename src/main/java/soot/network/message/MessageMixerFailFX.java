package soot.network.message;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import teamroots.embers.particle.ParticleUtil;
import teamroots.embers.util.Misc;

import java.util.Random;

public class MessageMixerFailFX implements IMessage {
    double x, y, z;
    EnumFacing facing;

    public MessageMixerFailFX() {
    }

    public MessageMixerFailFX(double x, double y, double z, EnumFacing facing) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.facing = facing;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        x = buf.readDouble();
        y = buf.readDouble();
        z = buf.readDouble();
        facing = EnumFacing.getFront(buf.readInt());
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeDouble(x);
        buf.writeDouble(y);
        buf.writeDouble(z);
        buf.writeInt(facing.getIndex());
    }

    public static class MessageHolder implements IMessageHandler<MessageMixerFailFX, IMessage> {
        @SideOnly(Side.CLIENT)
        @Override
        public IMessage onMessage(final MessageMixerFailFX message, final MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                World world = Minecraft.getMinecraft().world;
                EnumFacing facing = message.facing;
                Random random = world.rand;
                for(int i = 0; i < 20; i++) {
                    float speed = 0.125f * random.nextFloat();
                    float spread = 0.025f*3;
                    float vx = facing.getFrontOffsetX() * speed + spread * (random.nextFloat() - 0.5f);
                    float vy = facing.getFrontOffsetY() * speed + spread * (random.nextFloat() - 0.5f);
                    float vz = facing.getFrontOffsetZ() * speed + spread * (random.nextFloat() - 0.5f);
                    ParticleUtil.spawnParticleSmoke(world, (float) message.x, (float) message.y, (float) message.z, vx, vy, vz, 32, 32, 32, 0.5f, 2.0f + Misc.random.nextFloat(), 24);
                }
            });
            return null;
        }
    }
}
