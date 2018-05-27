package soot.block.overrides;

import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import soot.tile.overrides.TileEntityCrystalCellImproved;
import soot.util.EmberUtil;
import teamroots.embers.block.BlockCrystalCell;

import javax.annotation.Nullable;

public class BlockCrystalCellImproved extends BlockCrystalCell {
    public BlockCrystalCellImproved(Material material, String name, boolean addToTab) {
        super(material, name, addToTab);
        EmberUtil.overrideRegistryLocation(this,name);
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityCrystalCellImproved();
    }
}
