package soot.upgrade;

import net.minecraft.tileentity.TileEntity;
import teamroots.embers.util.DefaultUpgradeProvider;

public class UpgradeInsulation extends DefaultUpgradeProvider {
    public UpgradeInsulation(TileEntity tile) {
        super("insulation", tile);
    }

    @Override
    public double getOtherParameter(TileEntity tile, String type, double value) {
        if(type.equals("max_heat"))
            return value + 75.0;
        if(type.equals("cooling_speed"))
            return value * 0.7;
        return value;
    }
}
