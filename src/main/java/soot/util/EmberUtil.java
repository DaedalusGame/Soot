package soot.util;

import net.minecraft.block.Block;
import net.minecraft.block.BlockButton;
import net.minecraft.block.BlockLever;
import net.minecraft.block.BlockRedstoneTorch;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.registries.IForgeRegistryEntry;
import soot.Soot;
import teamroots.embers.RegistryManager;
import teamroots.embers.block.BlockCaminiteLever;
import teamroots.embers.util.Misc;

public class EmberUtil {
    public static boolean isValidLever(IBlockAccess world, BlockPos pos, EnumFacing side)
    {
        IBlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        if (block instanceof BlockLever){
            EnumFacing face = state.getValue(BlockLever.FACING).getFacing();
            return face == Misc.getOppositeVerticalFace(side);
        }
        else if (block instanceof BlockButton){
            EnumFacing face = state.getValue(BlockButton.FACING);
            return face == Misc.getOppositeVerticalFace(side);
        }
        else if (block instanceof BlockRedstoneTorch){
            EnumFacing face = state.getValue(BlockRedstoneTorch.FACING);
            return face == Misc.getOppositeVerticalFace(side);
        }
        else if (block == RegistryManager.caminite_lever){
            EnumFacing face = state.getValue(BlockCaminiteLever.FACING).getFacing();
            return face == Misc.getOppositeVerticalFace(side);
        }
        return false;
    }

    public static void overrideRegistryLocation(IForgeRegistryEntry.Impl forgeRegistryEntry, String name) {
        try {
            ReflectionHelper.findField(IForgeRegistryEntry.Impl.class,"registryName").set(forgeRegistryEntry,new ResourceLocation(Soot.MODID,name));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
