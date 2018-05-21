package soot.util;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IHeatProvider {
    double provideHeat(World world, BlockPos pos, IBlockState state);
}
