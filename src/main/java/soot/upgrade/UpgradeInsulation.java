package soot.upgrade;

import net.minecraft.tileentity.TileEntity;
import soot.capability.CapabilityUpgradeProvider;
import soot.tile.TileEntityHeatCoilImproved;

public class UpgradeInsulation extends CapabilityUpgradeProvider {
    public UpgradeInsulation(TileEntity tile) {
        super("insulation", tile);
    }

    @Override
    public double getOtherParameter(TileEntity tile, String type, double value) {
        if(type.equals(TileEntityHeatCoilImproved.TAG_MAX_HEAT))
            return value + 75.0;
        if(type.equals(TileEntityHeatCoilImproved.TAG_COOLING_SPEED))
            return value * 0.7;
        return value;
    }
}
