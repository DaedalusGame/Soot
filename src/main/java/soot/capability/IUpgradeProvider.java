package soot.capability;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;

//This might be too comprehensive lol
public interface IUpgradeProvider {
    String getUpgradeId();

    //Determines the order in which upgrades apply
    default int getPriority() {
        return 0;
    }

    default int getLimit(TileEntity tile) {
        return Integer.MAX_VALUE;
    }

    //The speed modifier that this upgrade provides
    default double getSpeed(TileEntity tile, double speed) {
        return speed;
    }

    default double getEmberConsumption(TileEntity tile, double multiplier) {
        return multiplier;
    }

    default double getEmberProduction(TileEntity tile, double multiplier) {
        return multiplier;
    }

    //Called if machine is working
    //If this returns true, this call replaces the usual work the machine would do.
    default boolean doWork(TileEntity tile, List<IUpgradeProvider> upgrades) {
        return false;
    }

    //Called when machine would output items, allows modification of what items the machine outputs.
    default void transformOutput(TileEntity tile, List<ItemStack> outputs) {
        //NOOP
    }

    //Called when machine would output fluid, allows modification of what fluid it will output.
    default FluidStack transformOutput(TileEntity tile, FluidStack output)
    {
        return output;
    }

    default boolean getOtherParameter(TileEntity tile, String type, boolean value) { return value; }

    default double getOtherParameter(TileEntity tile, String type, double value) { return value; }

    default int getOtherParameter(TileEntity tile, String type, int value) { return value; }

    default String getOtherParameter(TileEntity tile, String type, String value) { return value; }

    default <T> T getOtherParameter(TileEntity tile, String type, T value) { return value; }
}
