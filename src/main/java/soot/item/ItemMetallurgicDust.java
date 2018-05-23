package soot.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import soot.util.OreTransmutationManager;

public class ItemMetallurgicDust extends Item {
    @Override
    public EnumActionResult onItemUseFirst(EntityPlayer player, World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, EnumHand hand) {
        if (worldIn.isRemote) {
            return EnumActionResult.SUCCESS;
        } else {
            ItemStack itemstack = player.getHeldItem(hand);

            if (!player.canPlayerEdit(pos, facing, itemstack)) {
                return EnumActionResult.FAIL;
            } else {
                boolean success = OreTransmutationManager.transmuteOres(worldIn,pos);

                if(success) {
                    if (!player.capabilities.isCreativeMode) {
                        itemstack.shrink(1);
                    }

                    return EnumActionResult.SUCCESS;
                }
            }
        }

        return EnumActionResult.PASS;
    }
}
