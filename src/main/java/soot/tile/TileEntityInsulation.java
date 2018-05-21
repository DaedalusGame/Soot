package soot.tile;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import soot.block.BlockCatalyticPlug;
import soot.block.BlockInsulation;
import soot.capability.CapabilityUpgradeProvider;
import soot.upgrade.UpgradeCatalyticPlug;
import soot.upgrade.UpgradeInsulation;
import teamroots.embers.EventManager;

import javax.annotation.Nullable;

public class TileEntityInsulation extends TileEntity {
    public UpgradeInsulation upgrade;

    public TileEntityInsulation() {
        upgrade = new UpgradeInsulation(this);
    }

    public EnumFacing getFacing()
    {
        IBlockState state = world.getBlockState(pos);
        if(state.getBlock() instanceof BlockInsulation)
            return state.getValue(BlockInsulation.FACING);
        return null;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        return (capability == CapabilityUpgradeProvider.UPGRADE_PROVIDER_CAPABILITY && getFacing().getOpposite() == facing) || super.hasCapability(capability, facing);
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityUpgradeProvider.UPGRADE_PROVIDER_CAPABILITY && getFacing().getOpposite() == facing)
            return (T) upgrade;
        return super.getCapability(capability, facing);
    }
}
