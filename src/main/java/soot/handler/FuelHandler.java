package soot.handler;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.furnace.FurnaceFuelBurnTimeEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import soot.Registry;
import soot.util.FluidUtil;

public class FuelHandler {
    @SubscribeEvent
    public static void handleFuel(FurnaceFuelBurnTimeEvent event) {
        ItemStack stack = event.getItemStack();
        if(stack.getItem() == Registry.MUG) {
            FluidStack fluid = net.minecraftforge.fluids.FluidUtil.getFluidContained(stack);
            if(fluid == null) {
                event.setBurnTime(300);
                return;
            }

            NBTTagCompound compound = FluidUtil.getModifiers(fluid);
            double fuel = FluidUtil.getModifier(compound,fluid.getFluid(),"fuel");
            event.setBurnTime(Math.max((int)fuel+300,0));
        }
    }
}
