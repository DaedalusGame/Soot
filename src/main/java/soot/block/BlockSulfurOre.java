package soot.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import soot.Registry;
import soot.SoundEvents;
import soot.tile.TileEntitySulfurOre;

import javax.annotation.Nullable;
import java.util.Random;

public class BlockSulfurOre extends Block {
    public static final PropertyBool ACTIVE = PropertyBool.create("active");

    public BlockSulfurOre(Material materialIn) {
        super(materialIn);
        this.setTickRandomly(true);
        this.setDefaultState(getDefaultState().withProperty(ACTIVE,false));
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        Random rand = world instanceof World ?((World)world).rand:new Random();
        drops.add(Registry.SULFUR_CLUMP.withSize(rand.nextInt(30)));
    }

    @Override
    public boolean canSilkHarvest(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
        return false;
    }

    public void activateVent(World worldIn, BlockPos pos, IBlockState state) {
        if(!state.getValue(ACTIVE)) {
            TileEntity tile = worldIn.getTileEntity(pos);
            if(tile instanceof TileEntitySulfurOre)
                ((TileEntitySulfurOre) tile).activate();
        }
    }

    @Override
    public void randomTick(World worldIn, BlockPos pos, IBlockState state, Random random) {
        activateVent(worldIn, pos, state);
    }

    @Override
    public void onEntityWalk(World worldIn, BlockPos pos, Entity entityIn) {
        IBlockState state = worldIn.getBlockState(pos);
        activateVent(worldIn, pos, state);
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        if(blockIn != this)
            activateVent(worldIn, pos, state);
    }

    @Override
    public boolean getUseNeighborBrightness(IBlockState state) {
        return true;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, ACTIVE);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(ACTIVE,meta > 0);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(ACTIVE) ? 1 : 0;
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileEntitySulfurOre();
    }
}
