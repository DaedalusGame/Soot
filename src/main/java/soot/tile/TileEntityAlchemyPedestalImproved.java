package soot.tile;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.items.ItemStackHandler;
import soot.util.ItemUtil;
import teamroots.embers.tileentity.TileEntityAlchemyPedestal;

import java.lang.reflect.Field;

public class TileEntityAlchemyPedestalImproved extends TileEntityAlchemyPedestal {
    Field fieldAngle;
    boolean noSpin = false;

    public TileEntityAlchemyPedestalImproved()
    {
        fieldAngle = ReflectionHelper.findField(TileEntityAlchemyPedestal.class,"angle");
        inventory = new ItemStackHandler(2) {
            protected void onContentsChanged(int slot) {
                markDirty();
            }

            public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
                return slot == 0 && !ItemUtil.matchesOreDict(stack,"dustAsh") ? this.insertItem(slot + 1, stack, simulate) : super.insertItem(slot, stack, simulate);
            }
        };
    }

    @Override
    public boolean activate(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        ItemStack heldItem = player.getHeldItem(hand);
        if (heldItem != ItemStack.EMPTY){
            boolean isAsh = ItemUtil.matchesOreDict(heldItem,"dustAsh");

            if (isAsh)
                player.setHeldItem(hand, inventory.insertItem(0,heldItem,false));
            else
                player.setHeldItem(hand, inventory.insertItem(1,heldItem,false));
            markDirty();
            return true;
        }
        else {
            ItemStack ashStack = inventory.getStackInSlot(0);
            ItemStack itemStack = inventory.getStackInSlot(1);
            if (ashStack != ItemStack.EMPTY){
                if (!world.isRemote){
                    player.setHeldItem(hand, inventory.extractItem(0, ashStack.getCount(), false));
                    markDirty();
                }
                return true;
            }
            else if (itemStack != ItemStack.EMPTY) {
                if (!world.isRemote) {
                    player.setHeldItem(hand, inventory.extractItem(1, itemStack.getCount(), false));
                    markDirty();
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public void update() {
        if(noSpin)
            return;
        try {
            fieldAngle.set(this,(int)fieldAngle.get(this) + 1);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            noSpin = true;
        }
    }
}
