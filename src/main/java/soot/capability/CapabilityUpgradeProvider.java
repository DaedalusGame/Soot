package soot.capability;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

public class CapabilityUpgradeProvider implements IUpgradeProvider {
    protected final String id;
    protected final TileEntity tile;

    @CapabilityInject(IUpgradeProvider.class)
    public static final Capability<IUpgradeProvider> UPGRADE_PROVIDER_CAPABILITY = null;

    public CapabilityUpgradeProvider(String id,TileEntity tile)
    {
        this.id = id;
        this.tile = tile;
    }

    @Override
    public String getUpgradeId() {
        return id;
    }
}
