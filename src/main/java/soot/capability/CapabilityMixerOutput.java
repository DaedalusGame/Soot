package soot.capability;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import java.util.Arrays;
import soot.Soot;
import teamroots.embers.tileentity.TileEntityMixerBottom;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;

public class CapabilityMixerOutput implements IFluidHandler, ICapabilityProvider {
    public static final ResourceLocation MIXER_OUTPUT = new ResourceLocation(Soot.MODID, "mixer_output");

    TileEntityMixerBottom tile;
    IFluidTankProperties[] properties;

    public CapabilityMixerOutput(TileEntityMixerBottom tile)
    {
        this.tile = tile;
    }

    @Override
    public IFluidTankProperties[] getTankProperties() {
        if(properties == null) { //Fingers crossed
            ArrayList<IFluidTankProperties> importedProperties = new ArrayList<>();

            if (tile != null)
                for (EnumFacing facing : EnumFacing.HORIZONTALS) {
                    if (tile.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, facing)) {
                        IFluidHandler capability = tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, facing);
                        IFluidTankProperties[] faceProperties = capability.getTankProperties();
                        importedProperties.addAll(Arrays.asList(faceProperties));
                    }
                }

            properties = importedProperties.toArray(new IFluidTankProperties[importedProperties.size()]);
        }

        return properties;
    }

    @Override
    public int fill(FluidStack resource, boolean doFill) {
        return 0;
    }

    @Nullable
    @Override
    public FluidStack drain(FluidStack resource, boolean doDrain) {
        FluidStack drained = null;

        if(tile != null)
        for (EnumFacing facing : EnumFacing.HORIZONTALS) {
            if(tile.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY,facing))
            {
                IFluidHandler capability = tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY,facing);
                drained = capability.drain(resource,doDrain);
                if(drained != null)
                    break;
            }
        }

        return drained;
    }

    @Nullable
    @Override
    public FluidStack drain(int maxDrain, boolean doDrain) {
        FluidStack drained = null;

        if(tile != null)
            for (EnumFacing facing : EnumFacing.HORIZONTALS) {
                if(tile.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY,facing))
                {
                    IFluidHandler capability = tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY,facing);
                    drained = capability.drain(maxDrain,doDrain);
                    if(drained != null)
                        break;
                }
            }

        return drained;
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY;
    }

    @Nullable
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        return hasCapability(capability,facing) ? (T)this : null;
    }
}
