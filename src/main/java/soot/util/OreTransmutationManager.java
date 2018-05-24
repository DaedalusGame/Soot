package soot.util;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.*;
import java.util.stream.Collectors;

public class OreTransmutationManager {
    public static final int ITERATIONS_PER_TICK = 10;
    public static final int MAX_BLOCKS = 16;
    static LinkedHashMap<String,TransmutationSet> REGISTRY = new LinkedHashMap<>();
    static ArrayList<TransmutationIterator> iterators = new ArrayList<>();
    static int iteratorIndex;
    static Random random = new Random();

    public static void registerTransmutationSet(String name, IBlockState failure) {
        REGISTRY.put(name, new TransmutationSet(name,failure));
    }

    public static void registerTransmutationSet(String name, ResourceLocation failure, int meta) {
        if(!ForgeRegistries.BLOCKS.containsKey(failure))
            return;
        Block block = ForgeRegistries.BLOCKS.getValue(failure);
        registerTransmutationSet(name,block.getStateFromMeta(meta));
    }

    public static void registerOre(String name, IBlockState ore) {
        TransmutationSet set = REGISTRY.get(name);
        if(set != null) {
            set.ores.add(ore);
        }
    }

    public static void registerOre(String name, Block ore) {
        TransmutationSet set = REGISTRY.get(name);
        if(set != null) {
            NonNullList<ItemStack> stacks = NonNullList.create();
            ore.getSubBlocks(CreativeTabs.SEARCH,stacks);
            for (ItemStack stack : stacks) {
                set.ores.add(ore.getStateFromMeta(stack.getMetadata()));
            }
        }
    }

    public static void registerOre(String name, ResourceLocation ore, int meta) {
        if(!ForgeRegistries.BLOCKS.containsKey(ore))
            return;
        Block block = ForgeRegistries.BLOCKS.getValue(ore);
        registerOre(name,block.getStateFromMeta(meta));
    }

    public static void registerOre(String name, ResourceLocation ore) {
        if(!ForgeRegistries.BLOCKS.containsKey(ore))
            return;
        Block block = ForgeRegistries.BLOCKS.getValue(ore);
        registerOre(name,block);
    }

    public static TransmutationSet getFromOre(IBlockState state) {
        for(TransmutationSet entry : REGISTRY.values())
            if(entry.ores.contains(state))
                return entry;
        return null;
    }

    public static boolean transmuteOres(World world, BlockPos pos) {
        IBlockState fromReplace = world.getBlockState(pos);
        TransmutationSet set = getFromOre(fromReplace);
        if(set != null) {
            List<IBlockState> replacements = set.ores.stream().filter(ore -> ore != fromReplace).collect(Collectors.toList());
            if(replacements.isEmpty())
                return false;
            IBlockState toReplace = replacements.get(random.nextInt(replacements.size()));
            iterators.add(new TransmutationIterator(world,pos,fromReplace,toReplace,set.failure, MAX_BLOCKS));
            return true;
        }
        return false;
    }

    @SubscribeEvent
    public static void onWorldTick(TickEvent.WorldTickEvent event) {
        if(event.phase == TickEvent.Phase.END)
            return;
        if(iterators.isEmpty())
            return;
        for (int i = 0; i < ITERATIONS_PER_TICK; i++) {
            TransmutationIterator iterator = iterators.get(iteratorIndex % iterators.size());
            iterator.iterate();
            iteratorIndex++;
        }
        iterators.removeIf(TransmutationIterator::isDone);
    }

    public static class TransmutationSet {
        public String name;
        public IBlockState failure;
        public HashSet<IBlockState> ores = new HashSet<>();

        public TransmutationSet(String name, IBlockState failure) {
            this.name = name;
            this.failure = failure;
        }
    }

    public static class TransmutationIterator {
        public Random random = new Random();
        public World world;
        public IBlockState fromReplace;
        public IBlockState toReplace;
        public IBlockState toFailure;
        public HashSet<BlockPos> visitedPositions = new HashSet<>();
        public ArrayList<BlockPos> toVisit = new ArrayList<>();
        public int maxBlocks = Integer.MAX_VALUE;

        public TransmutationIterator(World world,BlockPos start, IBlockState fromReplace, IBlockState toReplace, IBlockState toFailure, int maxBlocks) {
            this.world = world;
            this.fromReplace = fromReplace;
            this.toReplace = toReplace;
            this.toFailure = toFailure;
            this.toVisit.add(start);
            this.maxBlocks = maxBlocks;
        }

        public boolean isDone() {
            return toVisit.isEmpty() || visitedPositions.size() >= maxBlocks;
        }

        public void iterate() {
            if(toVisit.isEmpty())
                return;
            int index = random.nextInt(toVisit.size());
            BlockPos visit = toVisit.get(index);
            toVisit.remove(index);
            IBlockState state = world.getBlockState(visit);
            if(state != fromReplace)
                return;
            if(random.nextDouble() < 0.1)
                world.setBlockState(visit,toFailure,2);
            else
                world.setBlockState(visit,toReplace,2);
            visitedPositions.add(visit);
            for (EnumFacing facing : EnumFacing.VALUES) {
                BlockPos neighbor = visit.offset(facing);
                if(!visitedPositions.contains(neighbor))
                    toVisit.add(neighbor);
            }
        }
    }
}
