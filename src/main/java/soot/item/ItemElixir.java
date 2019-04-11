package soot.item;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack;
import soot.brewing.DeliveryType;
import soot.brewing.CaskManager;
import soot.util.MiscUtil;
import teamroots.embers.EventManager;
import teamroots.embers.util.FluidColorHelper;

import javax.annotation.Nullable;
import java.awt.*;

public class ItemElixir extends Item {
    public static final int CAPACITY = 200;

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
        return new FluidHandlerItemStack(stack, getCapacity(stack));
    }

    public FluidStack getFluid(ItemStack stack) {
        return FluidUtil.getFluidContained(stack);
    }

    private boolean hasFluid(ItemStack stack) {
        return getFluid(stack) != null;
    }

    public int getCapacity(ItemStack stack) {
        return CAPACITY;
    }

    public Color getGlowColor(ItemStack stack) {
        Color firstColor = getColor(stack);
        Color secondColor;
        if(firstColor != null) {
            secondColor = MiscUtil.maxColor(firstColor);
        } else {
            firstColor = Color.MAGENTA;
            secondColor = Color.BLACK;
        }
        double glow = 0.5 + 0.5 * Math.sin(EventManager.ticks * Math.PI * 2 / 100.0);
        return MiscUtil.lerpColor(firstColor,secondColor, glow);
    }

    public Color getColor(ItemStack stack) {
        FluidStack fluid = getFluid(stack);
        return fluid != null ? new Color(FluidColorHelper.getColor(fluid)) : null;
    }

    @Override
    public boolean hasEffect(ItemStack stack) {
        return false;
    }

    public DeliveryType getDeliveryType(ItemStack gauntlet, ItemStack elixir,EntityLivingBase player) {
        FluidStack fluid = getFluid(elixir);
        DeliveryType.IGenerator generator = CaskManager.getDeliveryType(fluid);
        return generator.generate(gauntlet,elixir,player,fluid);
    }

    public void activateBlock(ItemStack gauntlet, ItemStack elixir, EntityLivingBase player, EnumHand hand, BlockPos pos, EnumFacing facing) {
        DeliveryType deliveryType = getDeliveryType(gauntlet,elixir,player);
        deliveryType.apply(pos,facing);
    }

    public void activate(ItemStack gauntlet, ItemStack elixir, EntityLivingBase player, EnumHand hand, Vec3d dir) {
        DeliveryType deliveryType = getDeliveryType(gauntlet,elixir,player);
        deliveryType.apply(dir);
    }

    public ItemStack getEmpty() {
        return new ItemStack(this);
    }

    public ItemStack getFilled(FluidStack fluid) {
        ItemStack filled = new ItemStack(this);
        IFluidHandlerItem handler = FluidUtil.getFluidHandler(filled);
        if(handler != null) {
            handler.fill(fluid, true);
            return handler.getContainer();
        }

        return ItemStack.EMPTY;
    }

    @Override
    public int getRGBDurabilityForDisplay(ItemStack stack) {
        return getGlowColor(stack).getRGB();
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        return hasFluid(stack);
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack) {
        return 0.0;
    }
}
