package soot.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import soot.tile.TileEntityMechAccessorImproved;
import soot.util.EmberUtil;
import teamroots.embers.block.BlockMechAccessor;

import javax.annotation.Nullable;

public class BlockMechAccessorImproved extends BlockMechAccessor {
    public BlockMechAccessorImproved(Material material, String name, boolean addToTab) {
        super(material, name, addToTab);
        EmberUtil.overrideRegistryLocation(this,name);
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        if(placer != null && placer.isSneaking())
            facing = facing.getOpposite();
        return getDefaultState().withProperty(BlockMechAccessor.facing, facing);
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityMechAccessorImproved();
    }
}
