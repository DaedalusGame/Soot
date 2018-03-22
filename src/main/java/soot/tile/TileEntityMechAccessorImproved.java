package soot.tile;

import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import teamroots.embers.block.BlockMechAccessor;
import teamroots.embers.tileentity.TileEntityMechAccessor;

import java.util.ArrayList;
import java.util.HashSet;

public class TileEntityMechAccessorImproved extends TileEntityMechAccessor {
    static HashSet<Class<? extends TileEntity>> ACCESSIBLE_TILES = new HashSet<>();

    public static void registerAccessibleTile(Class<? extends TileEntity> type)
    {
        ACCESSIBLE_TILES.add(type);
    }

    public static boolean canAccess(TileEntity tile)
    {
        Class<? extends TileEntity> tileClass = tile.getClass();
        return  ACCESSIBLE_TILES.stream().anyMatch(type -> type.isAssignableFrom(tileClass));
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        IBlockState state = world.getBlockState(pos);
        if(state.getBlock() instanceof BlockMechAccessor)
        {
            EnumFacing accessFace = state.getValue(BlockMechAccessor.facing).getOpposite();
            TileEntity tile = world.getTileEntity(pos.offset(accessFace));
            return tile != null && canAccess(tile) && tile.hasCapability(capability, accessFace);
        }
        return false;
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        IBlockState state = world.getBlockState(pos);
        if(state.getBlock() instanceof BlockMechAccessor)
        {
            EnumFacing accessFace = state.getValue(BlockMechAccessor.facing).getOpposite();
            TileEntity tile = world.getTileEntity(pos.offset(accessFace));
            return tile != null && canAccess(tile) ? tile.getCapability(capability, accessFace) : null;
        }
        return null;
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
        return oldState.getBlock() != newSate.getBlock();
    }
}
