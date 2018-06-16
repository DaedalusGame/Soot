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
import soot.tile.overrides.TileEntityMixerBottomImproved;
import soot.util.EmberUtil;
import soot.util.IMigrateable;
import soot.util.MigrationUtil;
import teamroots.embers.RegistryManager;
import teamroots.embers.block.BlockMixer;
import teamroots.embers.item.ItemTinkerHammer;
import teamroots.embers.tileentity.TileEntityMixerTop;

import javax.annotation.Nullable;

public class BlockMixerImproved extends BlockMixer implements IMigrateable {
    public BlockMixerImproved(Material material, String name, boolean addToTab) {
        super(material, name, addToTab);
        EmberUtil.overrideRegistryLocation(this,name);
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float x, float y, float z) {
        ItemStack heldItem = player.getHeldItem(hand);
        if (heldItem.getItem() instanceof ItemTinkerHammer && state.getBlock() == this){
            MigrationUtil.migrateBlock(world,pos);
            return true;
        }
        return super.onBlockActivated(world, pos, state, player, hand, facing, x, y, z);
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        if(meta == 1)
            return new TileEntityMixerTop();
        else
            return new TileEntityMixerBottomImproved();
    }

    @Override
    public IBlockState getReplacementState(IBlockState state) {
        return RegistryManager.mixer.getDefaultState().withProperty(isTop,state.getValue(isTop));
    }
}
