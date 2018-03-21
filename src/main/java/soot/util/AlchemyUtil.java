package soot.util;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import teamroots.embers.RegistryManager;
import teamroots.embers.block.BlockAlchemyPedestal;
import teamroots.embers.tileentity.TileEntityAlchemyPedestal;

import java.util.ArrayList;
import java.util.List;

public class AlchemyUtil {
    //TODO: Highly doubt there will ever be more aspects, but if there are, move this to a 'registry'
    public static String getAspect(ItemStack aspect)
    {
        Item item = aspect.getItem();
        if (item == RegistryManager.aspectus_iron)
            return "iron";
        if (item == RegistryManager.aspectus_dawnstone)
            return "dawnstone";
        if (item == RegistryManager.aspectus_copper)
            return "copper";
        if (item == RegistryManager.aspectus_silver)
            return "silver";
        if (item == RegistryManager.aspectus_lead)
            return "lead";
        return null;
    }

    public static ItemStack getAspectStack(String aspect) {
        switch(aspect)
        {
            case("iron"):return new ItemStack(RegistryManager.aspectus_iron);
            case("dawnstone"):return new ItemStack(RegistryManager.aspectus_dawnstone);
            case("copper"):return new ItemStack(RegistryManager.aspectus_copper);
            case("silver"):return new ItemStack(RegistryManager.aspectus_silver);
            case("lead"):return new ItemStack(RegistryManager.aspectus_lead);
        }
        return ItemStack.EMPTY;
    }

    public static List<TileEntityAlchemyPedestal> getNearbyPedestals(World world, BlockPos pos){
        ArrayList<TileEntityAlchemyPedestal> pedestals = new ArrayList<>();
        BlockPos.MutableBlockPos pedestalPos = new BlockPos.MutableBlockPos(pos);
        for (int i = -3; i < 4; i ++){
            for (int j = -3; j < 4; j ++){
                pedestalPos.setPos(pos.getX()+i,pos.getY(),pos.getZ()+j);
                IBlockState state = world.getBlockState(pedestalPos);
                if(state.getBlock() instanceof BlockAlchemyPedestal)
                {
                    if(!state.getValue(BlockAlchemyPedestal.isTop))
                        pedestalPos.move(EnumFacing.UP);
                    TileEntity tile = world.getTileEntity(pedestalPos);
                    if (tile instanceof TileEntityAlchemyPedestal){
                        pedestals.add((TileEntityAlchemyPedestal)tile);
                    }
                }

            }
        }
        return pedestals;
    }
}
