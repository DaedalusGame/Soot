package soot.tile;

import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import soot.block.BlockDistillationPipe;
import soot.upgrade.UpgradeDistillationPipe;
import teamroots.embers.api.capabilities.EmbersCapabilities;

import javax.annotation.Nullable;

public class TileEntityDistillationPipe extends TileEntity {
    public UpgradeDistillationPipe upgrade;

    public TileEntityDistillationPipe() {
        upgrade = new UpgradeDistillationPipe(this);
    }

    public EnumFacing getFacing()
    {
        IBlockState state = world.getBlockState(pos);
        if(state.getBlock() instanceof BlockDistillationPipe)
            return state.getValue(BlockDistillationPipe.FACING);
        return null;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        return (capability == EmbersCapabilities.UPGRADE_PROVIDER_CAPABILITY && getFacing() == facing) || super.hasCapability(capability, facing);
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == EmbersCapabilities.UPGRADE_PROVIDER_CAPABILITY && getFacing() == facing)
            return (T) upgrade;
        return super.getCapability(capability, facing);
    }
}
