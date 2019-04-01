package soot.network.message;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import soot.particle.ParticleUtilSoot;
import teamroots.embers.particle.ParticleUtil;

import java.awt.*;
import java.util.Random;

public class MessageMixerBlastFX implements IMessage {
    double x, y, z;
    int lightning;
    double radius;

    public MessageMixerBlastFX() {
    }

    public MessageMixerBlastFX(double x, double y, double z, int lightning, double radius) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.lightning = lightning;
        this.radius = radius;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        x = buf.readDouble();
        y = buf.readDouble();
        z = buf.readDouble();
        lightning = buf.readInt();
        radius = buf.readDouble();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeDouble(x);
        buf.writeDouble(y);
        buf.writeDouble(z);
        buf.writeInt(lightning);
        buf.writeDouble(radius);
    }

    public static class MessageHolder implements IMessageHandler<MessageMixerBlastFX, IMessage> {
        @SideOnly(Side.CLIENT)
        @Override
        public IMessage onMessage(final MessageMixerBlastFX message, final MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                World world = Minecraft.getMinecraft().world;
                Random random = world.rand;
                Color color = new Color(181,90,255);
                float ox = (float) message.x;
                float oy = (float) message.y;
                float oz = (float) message.z;
                for (int i = 0; i < 120; i++) {
                    double yaw = random.nextFloat() * Math.PI * 2;
                    double pitch = random.nextFloat() * Math.PI * 2;
                    float dx = (float) (Math.sin(yaw) * Math.cos(pitch));
                    float dy = (float) (Math.sin(pitch));
                    float dz = (float) (Math.cos(yaw) * Math.cos(pitch));
                    int lifetime = 20;
                    float dist = (float) message.radius;
                    ParticleUtil.spawnParticleSpark(world, ox,oy,oz,dx * dist / lifetime,dy * dist / lifetime,dz * dist / lifetime,color.getRed(),color.getGreen(),color.getBlue(),2,lifetime);
                }
                for (int i = 0; i < message.lightning; i++) {
                    double yaw = random.nextFloat() * Math.PI * 2;
                    double pitch = random.nextFloat() * Math.PI * 2;
                    float dx = (float) (Math.sin(yaw) * Math.cos(pitch));
                    float dy = (float) (Math.sin(pitch));
                    float dz = (float) (Math.cos(yaw) * Math.cos(pitch));
                    double dist = 16;
                    double sx = ox + dx * 0.6;
                    double sy = oy + dy * 0.6;
                    double sz = oz + dz * 0.6;
                    if (world.collidesWithAnyBlock(new AxisAlignedBB(sx, sy, sz, sx, sy, sz)))
                        continue;
                    double ex = ox + dx * dist;
                    double ey = oy + dy * dist;
                    double ez = oz + dz * dist;
                    RayTraceResult raytraceresult = world.rayTraceBlocks(new Vec3d(sx, sy, sz), new Vec3d(ex, ey, ez), true, true, false);
                    if (raytraceresult != null && raytraceresult.hitVec != null) {
                        ex = raytraceresult.hitVec.x;
                        ey = raytraceresult.hitVec.y;
                        ez = raytraceresult.hitVec.z;
                    }
                    ParticleUtilSoot.spawnLightning(world, ox, oy, oz, ex, ey, ez, 16, 1.0, color, 2.5, 30);
                }
            });
            return null;
        }
    }
}
