package soot.util;

import net.minecraft.block.state.IBlockState;

public interface IMigrateable {
    IBlockState getReplacementState(IBlockState state);
}
