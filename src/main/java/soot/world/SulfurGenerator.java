package soot.world;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import soot.Config;
import soot.Registry;

import java.util.Random;

public class SulfurGenerator extends WorldGenerator {
    @Override
    public boolean generate(World worldIn, Random rand, BlockPos position) {
        int amount = Config.SULFUR_PER_CHUNK;
        IBlockState sulfurOre = Registry.SULFUR_ORE.getDefaultState();
        for (int i = 0; i < 64 && amount > 0; ++i)
        {
            BlockPos blockpos = position.add(rand.nextInt(8) - rand.nextInt(8), rand.nextInt(4) - rand.nextInt(4), rand.nextInt(8) - rand.nextInt(8));
            IBlockState state = worldIn.getBlockState(blockpos);

            if (isStone(state) && worldIn.isAirBlock(blockpos.up()) && worldIn.isAirBlock(blockpos.up(2)))
            {
                worldIn.setBlockState(blockpos, sulfurOre, 2);
                amount--;
            }
        }

        return true;
    }

    public boolean isStone(IBlockState state) {
        return state.getBlock() == Blocks.STONE; //IKR????
    }
}
