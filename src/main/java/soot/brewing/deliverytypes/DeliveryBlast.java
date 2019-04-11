package soot.brewing.deliverytypes;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import soot.brewing.DeliveryType;
import soot.network.PacketHandler;
import soot.network.message.MessageAlchemyBlastFX;
import soot.util.Attributes;
import soot.brewing.CaskManager;
import teamroots.embers.util.FluidColorHelper;

import java.awt.*;

public class DeliveryBlast extends DeliveryType {
    double radius;
    double blastRadius;

    public DeliveryBlast(EntityLivingBase user, FluidStack fluidStack, double radius, double blastRadius) {
        super(user, fluidStack);
        this.radius = radius;
        this.blastRadius = blastRadius;
    }

    @Override
    public void apply(BlockPos pos, EnumFacing facing) {
        apply(user.getLookVec());
    }

    @Override
    public void apply(Vec3d dir) {
        World world = user.world;
        Color mainColor = new Color(FluidColorHelper.getColor(fluidStack));
        AxisAlignedBB aabb = new AxisAlignedBB(user.getPosition());
        aabb = aabb.grow(radius,radius,radius);
        CaskManager.CaskLiquid liquid = CaskManager.getFromFluid(fluidStack);
        if(liquid != null)
        for(Entity entity : world.getEntitiesInAABBexcluding(user, aabb, Attributes::isAttracted)) {
            AxisAlignedBB entityBox = entity.getEntityBoundingBox();
            Vec3d center = entityBox.getCenter();
            AxisAlignedBB blastBox = new AxisAlignedBB(center,center).grow(blastRadius);
            for(Entity blastEntity : world.getEntitiesInAABBexcluding(user, blastBox, e -> e instanceof EntityLivingBase)) {
                liquid.applyEffects((EntityLivingBase) blastEntity,user,user,fluidStack);
            }
            PacketHandler.INSTANCE.sendToAllTracking(new MessageAlchemyBlastFX(center.x,center.y,center.z,mainColor,mainColor,mainColor, (float) blastRadius,10),entity);
            Attributes.resetAttraction(entity,1);
        }
    }
}
