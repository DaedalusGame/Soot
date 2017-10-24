package soot.block;

import net.minecraft.block.state.IBlockState;

public interface IBlockVariants {
    Iterable<IBlockState> getValidStates();

    String getBlockStateName(IBlockState state);
}
