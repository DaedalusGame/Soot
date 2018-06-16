package soot.block.overrides;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import soot.tile.overrides.TileEntityAlchemyPedestalImproved;
import soot.util.EmberUtil;
import soot.util.IMigrateable;
import soot.util.MigrationUtil;
import teamroots.embers.RegistryManager;
import teamroots.embers.block.BlockAlchemyPedestal;
import teamroots.embers.item.ItemTinkerHammer;

import javax.annotation.Nullable;

public class BlockAlchemyPedestalImproved extends BlockAlchemyPedestal implements IMigrateable {
    public BlockAlchemyPedestalImproved(Material material, String name, boolean addToTab) {
        super(material, name, addToTab);
        EmberUtil.overrideRegistryLocation(this,name);
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return meta == 1 ? new TileEntityAlchemyPedestalImproved() : null;
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float x, float y, float z) {
        ItemStack heldItem = player.getHeldItem(hand);
        if (heldItem.getItem() instanceof ItemTinkerHammer && state.getBlock() == this && state.getValue(isTop)){
            MigrationUtil.migrateBlock(world,pos);
            return true;
        }
        return super.onBlockActivated(world, pos, state, player, hand, facing, x, y, z);
    }

    @Override
    public IBlockState getReplacementState(IBlockState state) {
        return RegistryManager.alchemy_pedestal.getDefaultState().withProperty(isTop,state.getValue(isTop));
    }
}
