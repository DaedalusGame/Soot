package soot.upgrade;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.MathHelper;
import soot.util.MiscUtil;
import teamroots.embers.api.event.HeatCoilVisualEvent;
import teamroots.embers.api.event.UpgradeEvent;
import teamroots.embers.tileentity.TileEntityHeatCoil;
import teamroots.embers.util.DefaultUpgradeProvider;

import java.awt.*;

public class UpgradeInsulation extends DefaultUpgradeProvider {
    public UpgradeInsulation(TileEntity tile) {
        super("insulation", tile);
    }

    @Override
    public int getLimit(TileEntity tile) {
        if(tile instanceof TileEntityHeatCoil)
            return 5;
        return 0;
    }

    @Override
    public double getOtherParameter(TileEntity tile, String type, double value) {
        if(type.equals("max_heat"))
            return value + 75.0;
        if(type.equals("cooling_speed"))
            return value * 0.7;
        return value;
    }

    @Override
    public double transformEmberConsumption(TileEntity tile, double ember) {
        return ember * 0.8;
    }

    @Override
    public void throwEvent(TileEntity tile, UpgradeEvent event) {
        if(event instanceof HeatCoilVisualEvent && tile instanceof TileEntityHeatCoil) {
            HeatCoilVisualEvent visualEvent = (HeatCoilVisualEvent) event;
            double heat = ((TileEntityHeatCoil) tile).heat;
            double overheat = heat - TileEntityHeatCoil.MAX_HEAT;
            visualEvent.setColor(MiscUtil.lerpColor(visualEvent.getColor(), new Color(192,255,128), MathHelper.clamp(overheat / 200, 0, 1)));
            visualEvent.setVerticalSpeed((float) MathHelper.clampedLerp(visualEvent.getVerticalSpeed(),Math.max(visualEvent.getVerticalSpeed(),0.03), overheat / 200));
        }
    }
}
