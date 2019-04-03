package soot.upgrade;

import net.minecraft.tileentity.TileEntity;
import soot.block.BlockAlchemyGauge;
import teamroots.embers.Embers;
import teamroots.embers.api.event.DialInformationEvent;
import teamroots.embers.api.event.UpgradeEvent;
import teamroots.embers.util.DefaultUpgradeProvider;

public class UpgradeDecanter extends DefaultUpgradeProvider {
    public UpgradeDecanter(TileEntity tile) {
        super("decanter", tile);
    }

    @Override
    public void throwEvent(TileEntity tile, UpgradeEvent event) {
        if(event instanceof DialInformationEvent) {
            DialInformationEvent dialEvent = (DialInformationEvent) event;
            if(BlockAlchemyGauge.DIAL_TYPE.equals(dialEvent.getDialType())) {
                dialEvent.getInformation().add(Embers.proxy.formatLocalize("embers.tooltip.upgrade.decanter")); //Proxy this because it runs in shared code
            }
        }
    }
}
