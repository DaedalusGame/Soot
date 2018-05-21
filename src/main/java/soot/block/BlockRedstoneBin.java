package soot.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import soot.tile.TileEntityRedstoneBin;
import soot.util.EmberUtil;
import teamroots.embers.block.BlockBin;

import javax.annotation.Nullable;

public class BlockRedstoneBin extends BlockBin {
    public static final PropertyBool OPEN = PropertyBool.create("open");

    public BlockRedstoneBin(Material material, String name) {
        super(material, name, false);
        EmberUtil.overrideRegistryLocation(this, name);
        setDefaultState(getDefaultState().withProperty(OPEN, false));
    }

    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        if (!worldIn.isRemote) {
            boolean powered = worldIn.isBlockPowered(pos);
            boolean open = state.getValue(OPEN);

            if (open != powered) {
                worldIn.setBlockState(pos, state.withProperty(OPEN, powered), 2);
                this.playSound(worldIn, pos, powered);
            }
        }
    }

    protected void playSound(World worldIn, BlockPos pos, boolean powered) {
        if (powered)
            worldIn.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BLOCK_IRON_TRAPDOOR_OPEN, SoundCategory.BLOCKS, 1.0F, worldIn.rand.nextFloat() * 0.1F + 0.9F);
        else
            worldIn.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BLOCK_IRON_TRAPDOOR_CLOSE, SoundCategory.BLOCKS, 1.0F, worldIn.rand.nextFloat() * 0.1F + 0.9F);
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityRedstoneBin();
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(OPEN, (meta & 1) == 1);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(OPEN) ? 1 : 0;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, OPEN);
    }
}
