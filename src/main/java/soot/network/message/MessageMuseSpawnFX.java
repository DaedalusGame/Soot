package soot.network.message;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import soot.SoundEvents;
import teamroots.embers.particle.ParticleUtil;

import java.util.Random;

public class MessageMuseSpawnFX implements IMessage {
    double x, y, z;

    public MessageMuseSpawnFX() {
    }

    public MessageMuseSpawnFX(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        x = buf.readDouble();
        y = buf.readDouble();
        z = buf.readDouble();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeDouble(x);
        buf.writeDouble(y);
        buf.writeDouble(z);
    }

    public static class MessageHolder implements IMessageHandler<MessageMuseSpawnFX, IMessage> {
        @SideOnly(Side.CLIENT)
        @Override
        public IMessage onMessage(final MessageMuseSpawnFX message, final MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                World world = Minecraft.getMinecraft().world;
                Random random = world.rand;
                world.playSound(message.x,message.y,message.z, SoundEvents.INSPIRATION_MUSE_APPEAR, SoundCategory.HOSTILE, 1.0f, 1.0f, false);
                for (double i = 0; i < 24; i++) {
                    ParticleUtil.spawnParticleSpark(world, (float) message.x, (float) message.y, (float) message.z, 0.0125f * (random.nextFloat() - 0.5f), 0.0125f * (random.nextFloat() - 0.5f), 0.0125f * (random.nextFloat() - 0.5f), 64, 255, 64, 3.0f + random.nextFloat(), 36 + random.nextInt(24));
                }
            });
            return null;
        }
    }
}
