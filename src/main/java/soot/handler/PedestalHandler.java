package soot.handler;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import soot.util.ItemUtil;
import teamroots.embers.tileentity.TileEntityAlchemyPedestal;

public class PedestalHandler {
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onActivate(PlayerInteractEvent.RightClickBlock event)
    {
        EntityPlayer player = event.getEntityPlayer();
        ItemStack heldItem = event.getItemStack();
        EnumHand hand = event.getHand();
        World world = event.getWorld();
        BlockPos pos = event.getPos();

        TileEntity tileEntity = world.getTileEntity(pos);

        if(tileEntity instanceof TileEntityAlchemyPedestal)
        {
            TileEntityAlchemyPedestal pedestal = (TileEntityAlchemyPedestal) tileEntity;
            if (heldItem != ItemStack.EMPTY){
                boolean isAsh = ItemUtil.matchesOreDict(heldItem,"dustAsh");

                if (isAsh)
                    player.setHeldItem(hand, pedestal.inventory.insertItem(0,heldItem,false));
                else
                    player.setHeldItem(hand, pedestal.inventory.insertItem(1,heldItem,false));
                pedestal.markDirty();
                event.setCancellationResult(EnumActionResult.SUCCESS);
                event.setCanceled(true);
            }
            else {
                if (pedestal.inventory.getStackInSlot(0) != ItemStack.EMPTY){
                    if (!world.isRemote){
                        player.setHeldItem(hand, pedestal.inventory.extractItem(0, pedestal.inventory.getStackInSlot(0).getCount(), false));
                        pedestal.markDirty();
                    }
                    event.setCancellationResult(EnumActionResult.SUCCESS);
                    event.setCanceled(true);
                }
                else if (pedestal.inventory.getStackInSlot(1) != ItemStack.EMPTY){
                    if (!world.isRemote){
                        player.setHeldItem(hand, pedestal.inventory.extractItem(1, pedestal.inventory.getStackInSlot(1).getCount(), false));
                        pedestal.markDirty();
                    }
                    event.setCancellationResult(EnumActionResult.SUCCESS);
                    event.setCanceled(true);
                }
            }
        }
    }
}
