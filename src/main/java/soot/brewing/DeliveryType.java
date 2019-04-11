package soot.brewing;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fluids.FluidStack;

public abstract class DeliveryType {
    public interface IGenerator {
        DeliveryType generate(ItemStack gauntlet, ItemStack elixir, EntityLivingBase user, FluidStack fluid);
    }

    protected EntityLivingBase user;
    protected FluidStack fluidStack;

    public DeliveryType(EntityLivingBase user, FluidStack fluidStack) {
        this.user = user;
        this.fluidStack = fluidStack;
    }

    public abstract void apply(BlockPos pos, EnumFacing facing);

    public abstract void apply(Vec3d dir);
}
