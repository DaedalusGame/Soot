package soot.tile;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import soot.block.BlockAlchemyGauge;
import teamroots.embers.tileentity.TileEntityBaseGauge;

public class TileEntityAlchemyGauge extends TileEntityBaseGauge {
    @Override
    public int calculateComparatorValue(TileEntity tileEntity, EnumFacing enumFacing) {
        return 0;
    }

    @Override
    public String getDialType() {
        return BlockAlchemyGauge.DIAL_TYPE;
    }
}
