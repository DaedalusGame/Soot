package soot.block;

import net.minecraft.block.BlockStairs;
import net.minecraft.block.state.IBlockState;

public class BlockModStairs extends BlockStairs {
    public BlockModStairs(IBlockState modelState) {
        super(modelState);
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean getUseNeighborBrightness(IBlockState state) {
        return true;
    }
}
