package soot.block.overrides;

import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import soot.tile.overrides.TileEntityHeatCoilImproved;
import soot.util.EmberUtil;
import teamroots.embers.block.BlockHeatCoil;

import javax.annotation.Nullable;

public class BlockHeatCoilImproved extends BlockHeatCoil {
    public BlockHeatCoilImproved(Material material, String name, boolean addToTab) {
        super(material, name, addToTab);
        EmberUtil.overrideRegistryLocation(this,name);
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityHeatCoilImproved();
    }
}
