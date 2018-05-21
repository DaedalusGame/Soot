package soot.util;

import com.google.common.collect.HashMultimap;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Set;

public class HeatManager {
    static HashMultimap<Block,IHeatProvider> REGISTRY = HashMultimap.create();

    public static void register(Block block, IHeatProvider provider) {
        REGISTRY.put(block,provider);
    }

    public static void register(Block block, int heat) {
        REGISTRY.put(block,new BlockHeatProvider(heat));
    }

    public static void register(IBlockState state, int heat) {
        REGISTRY.put(state.getBlock(),new BlockStateHeatProvider(state,heat));
    }

    public static double getHeat(World world, BlockPos pos) {
        IBlockState state = world.getBlockState(pos);
        Set<IHeatProvider> heatProviders = REGISTRY.get(state.getBlock());
        for(IHeatProvider heatProvider : heatProviders) {
            double heat = heatProvider.provideHeat(world, pos, state);
            if(heat >= 0)
                return heat;
        }
        return -1;
    }

    public static class BlockStateHeatProvider implements IHeatProvider {
        IBlockState state;
        int heat;

        public BlockStateHeatProvider(IBlockState state, int heat) {
            this.state = state;
            this.heat = heat;
        }

        @Override
        public double provideHeat(World world, BlockPos pos, IBlockState state) {
            return this.state == state ? heat : -1;
        }
    }

    public static class BlockHeatProvider implements IHeatProvider {
        int heat;

        public BlockHeatProvider(int heat) {
            this.heat = heat;
        }

        @Override
        public double provideHeat(World world, BlockPos pos, IBlockState state) {
            return heat;
        }
    }
}
