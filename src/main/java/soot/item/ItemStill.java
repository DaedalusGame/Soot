package soot.item;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import soot.Registry;
import soot.block.BlockStill;

public class ItemStill extends ItemBlock {
    public ItemStill(Block block) {
        super(block);
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        EnumFacing placementFacing = getFacingForPlacement(player);

        BlockPos placePos = pos;
        if (!world.getBlockState(pos).getBlock().isReplaceable(world, pos))
            placePos = placePos.offset(facing);

        if(!isFree(world,placePos) || !isFree(world,placePos.up()) || !isFree(world,placePos.up().offset(placementFacing)))
            return EnumActionResult.FAIL;

        return super.onItemUse(player, world, pos, hand, facing, hitX, hitY, hitZ);
    }

    public EnumFacing getFacingForPlacement(EntityPlayer player) {
        int i = MathHelper.floor((double)(player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
        return EnumFacing.getHorizontal(i);
    }

    private boolean isFree(World world, BlockPos pos)
    {
        IBlockState state = world.getBlockState(pos);
        return state.getBlock().isReplaceable(world,pos);
    }

    @Override
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState newState) {
        EnumFacing placementFacing = getFacingForPlacement(player);

        IBlockState placeState = Registry.STILL.getDefaultState().withProperty(BlockStill.FACING, placementFacing);
        if (!world.setBlockState(pos, placeState.withProperty(BlockStill.PART,0), 11)) return false;
        world.setBlockState(pos.up(), placeState.withProperty(BlockStill.PART,1), 11);
        world.setBlockState(pos.up().offset(placementFacing), placeState.withProperty(BlockStill.PART,2), 11);

        IBlockState state = world.getBlockState(pos);
        if (state.getBlock() == this.block)
        {
            this.block.onBlockPlacedBy(world, pos, state, player, stack);

            if (player instanceof EntityPlayerMP)
                CriteriaTriggers.PLACED_BLOCK.trigger((EntityPlayerMP)player, pos, stack);
        }

        return true;
    }
}
