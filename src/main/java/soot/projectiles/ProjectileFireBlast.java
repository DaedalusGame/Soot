package soot.projectiles;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import soot.network.PacketHandler;
import soot.network.message.MessageFireBlastFX;
import teamroots.embers.api.projectile.IProjectileEffect;
import teamroots.embers.api.projectile.IProjectilePreset;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.ArrayList;
import java.util.ListIterator;

public class ProjectileFireBlast implements IProjectilePreset {
    static class Active {
        public World world;
        public ProjectileFireBlast preset;
        public int ticks;
        public double counterTicks;
        public int counter;

        public Active(World world, ProjectileFireBlast preset) {
            this.world = world;
            this.preset = preset;
            this.counterTicks = preset.counterLifetime; //Trigger on the first tick
        }

        public void update() {
            ticks++;
            counterTicks++;
            if(counterTicks > preset.counterLifetime) {
                counter++;
                counterTicks %= 1;
                emit();
            }
        }

        public void emit() {
            Vec3d pos = preset.pos.addVector(preset.velocity.x * ticks,preset.velocity.y * ticks,preset.velocity.z * ticks);
            PacketHandler.INSTANCE.sendToDimension(new MessageFireBlastFX(pos.x,pos.y,pos.z,preset.color,(float)preset.radius,7),world.provider.getDimension());
            AxisAlignedBB box = new AxisAlignedBB(pos,pos);
            box = box.grow(preset.radius);
            for (Entity entity : world.getEntitiesWithinAABB(EntityLivingBase.class,box, e -> e != preset.getShooter() && pos.squareDistanceTo(e.getPositionVector()) < preset.radius*preset.radius)) {
                preset.effect.onEntityImpact(entity,preset);
            }
            if(!isAlive())
                preset.effect.onFizzle(world,pos,preset);
        }

        public boolean isAlive() {
            return counter < preset.counter;
        }
    }

    static ArrayList<Active> activeBlasts = new ArrayList<>();

    @SubscribeEvent
    public static void updateBlasts(TickEvent.WorldTickEvent event) {
        if (event.phase != TickEvent.Phase.START || event.side != Side.SERVER)
            return;
        ListIterator<Active> iterator = activeBlasts.listIterator();
        while(iterator.hasNext()) {
            Active blast = iterator.next();
            if (blast.world == event.world)
                blast.update();
            if (!blast.isAlive())
                iterator.remove();
        }
    }

    Vec3d pos;
    Vec3d velocity;
    IProjectileEffect effect;
    Entity shooter;
    Color color;
    double counterLifetime;
    int counter;
    double radius;

    public ProjectileFireBlast(Entity shooter, Vec3d start, Vec3d stop, IProjectileEffect effect, Color color, int times, int lifetime, double radius) {
        this.shooter = shooter;
        this.pos = start;
        this.velocity = stop.subtract(start).scale(1.0/lifetime);
        this.effect = effect;
        this.color = color;
        this.counter = times;
        this.counterLifetime = (double)lifetime / times;
        this.radius = radius;
    }

    @Override
    public Vec3d getPos() {
        return pos;
    }

    @Override
    public Vec3d getVelocity() {
        return velocity;
    }

    @Override
    public Color getColor() {
        return color;
    }

    @Override
    public IProjectileEffect getEffect() {
        return effect;
    }

    @Nullable
    @Override
    public Entity getEntity() {
        return null;
    }

    @Nullable
    @Override
    public Entity getShooter() {
        return shooter;
    }

    @Override
    public void setPos(Vec3d pos) {
        this.pos = pos;
    }

    @Override
    public void setVelocity(Vec3d velocity) {
        this.velocity = velocity;
    }

    @Override
    public void setColor(Color color) {
        this.color = color;
    }

    @Override
    public void setEffect(IProjectileEffect effect) {
        this.effect = effect;
    }

    @Override
    public void shoot(World world) {
        if(!world.isRemote)
            activeBlasts.add(new Active(world,this));
    }
}
