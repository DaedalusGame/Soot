package soot.util;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

public class ResettingFluidTank extends FluidTank {
    public ResettingFluidTank(int capacity) {
        super(capacity);
    }

    @Override
    public int fillInternal(FluidStack resource, boolean doFill) {
        promptReset();
        return super.fillInternal(resource, doFill);
    }

    @Override
    protected void onContentsChanged() {
        super.onContentsChanged();
        promptReset();
    }

    private void promptReset() {
        if(getFluidAmount() <= 0)
            this.setFluid(null);
    }
}
