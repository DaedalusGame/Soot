package soot.block;

import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import soot.tile.TileEntityAlchemyTabletImproved;
import soot.util.EmberUtil;
import teamroots.embers.block.BlockAlchemyTablet;

import javax.annotation.Nullable;

public class BlockAlchemyTabletImproved extends BlockAlchemyTablet {
    public BlockAlchemyTabletImproved(Material material, String name, boolean addToTab) {
        super(material, name, addToTab);
        EmberUtil.overrideRegistryLocation(this,name);
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityAlchemyTabletImproved();
    }
}
