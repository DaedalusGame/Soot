package soot.block;

import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import soot.tile.TileEntityAlchemyPedestalImproved;
import soot.util.EmberUtil;
import teamroots.embers.block.BlockAlchemyPedestal;

import javax.annotation.Nullable;

public class BlockAlchemyPedestalImproved extends BlockAlchemyPedestal {
    public BlockAlchemyPedestalImproved(Material material, String name, boolean addToTab) {
        super(material, name, addToTab);
        EmberUtil.overrideRegistryLocation(this,name);
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return meta == 1 ? new TileEntityAlchemyPedestalImproved() : null;
    }
}
