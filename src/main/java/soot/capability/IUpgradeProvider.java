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
    default float getSpeed(TileEntity tile) {
        return 0.0f;
    }

    default float getEmberFuelEfficiency(TileEntity tile) {
        return 0.0f;
    }

    default float getEmberProductEfficiency(TileEntity tile) {
        return 0.0f;
    }

    //Called if machine is working
    //If this returns true, this call replaces the usual work the machine would do.
    default boolean doWork(TileEntity tile, List<IUpgradeProvider> upgrades) {
        return false;
    }

    //Called when machine would output items, allows modification of what items the machine outputs.
    default void transformOutput(TileEntity tile, List<ItemStack> outputs)
    {
        //NOOP
    }

    //Called when machine would output fluid, allows modification of what fluid it will output.
    default FluidStack transformOutput(TileEntity tile, FluidStack output)
    {
        return output;
    }

    default <T> T getOtherParameter(TileEntity tile, String type, T value) { return value; }
}
