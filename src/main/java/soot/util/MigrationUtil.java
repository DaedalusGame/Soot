package soot.util;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MigrationUtil {
    public static void migrateBlock(World world,BlockPos pos) {
        IBlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        if(block instanceof IMigrateable) {
            TileEntity tile = world.getTileEntity(pos);
            NBTTagCompound compound = null;
            if (tile != null)
                compound = tile.serializeNBT();
            IBlockState stateNew = ((IMigrateable) block).getReplacementState(state);
            world.setBlockState(pos,stateNew,2);
            TileEntity tileNew = world.getTileEntity(pos);
            if(compound != null && tileNew != null)
                tileNew.deserializeNBT(compound);
        }
    }
}
