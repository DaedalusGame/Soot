package soot.tile;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import soot.block.BlockRedstoneBin;
import teamroots.embers.tileentity.TileEntityBin;

import java.util.List;

public class TileEntityRedstoneBin extends TileEntityBin {
    int ticksExisted = 0;

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
        return oldState.getBlock() != newSate.getBlock();
    }

    @Override
    public void update() {
        ticksExisted ++;
        if (ticksExisted % 10 == 0){
            List<EntityItem> items = getWorld().getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(getPos().getX(),getPos().getY(),getPos().getZ(),getPos().getX()+1,getPos().getY()+1.25,getPos().getZ()+1));
            for (int i = 0; i < items.size(); i ++){
                ItemStack stack = inventory.insertItem(0, items.get(i).getItem(), false);
                if (!stack.isEmpty())
                    items.get(i).setItem(stack);
                else
                    items.get(i).setDead();
            }
        }
        IBlockState state = world.getBlockState(pos);
        IBlockState bottomState = world.getBlockState(pos.down());
        if(!world.isRemote && state.getBlock() instanceof BlockRedstoneBin && bottomState.getBlock().isReplaceable(world,pos.down())) {
            boolean open = state.getValue(BlockRedstoneBin.OPEN);
            if(open && !inventory.getStackInSlot(0).isEmpty()) {
                ItemStack toEject = inventory.extractItem(0,64,false);
                markDirty();
                if(!toEject.isEmpty()) {
                    EntityItem item = new EntityItem(world, pos.getX() + 0.5, pos.getY() - 0.7f, pos.getZ() + 0.5, toEject);
                    item.motionX = 0;
                    item.motionY = 0;
                    item.motionZ = 0;
                    item.setDefaultPickupDelay();
                    world.spawnEntity(item);
                }
            }
        }
    }
}
