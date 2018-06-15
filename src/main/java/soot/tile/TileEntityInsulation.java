package soot.tile;

import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import soot.block.BlockInsulation;
import soot.upgrade.UpgradeInsulation;
import teamroots.embers.api.capabilities.EmbersCapabilities;

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
        return (capability == EmbersCapabilities.UPGRADE_PROVIDER_CAPABILITY && getFacing().getOpposite() == facing) || super.hasCapability(capability, facing);
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == EmbersCapabilities.UPGRADE_PROVIDER_CAPABILITY && getFacing().getOpposite() == facing)
            return (T) upgrade;
        return super.getCapability(capability, facing);
    }
}
