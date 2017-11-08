package soot.util;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import soot.capability.CapabilityUpgradeProvider;
import soot.capability.IUpgradeProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UpgradeUtil {
    public static ArrayList<IUpgradeProvider> getUpgrades(World world, BlockPos pos, EnumFacing[] facings)
    {
        ArrayList<IUpgradeProvider> upgrades = new ArrayList<>();
        for (EnumFacing facing: facings) {
            TileEntity te = world.getTileEntity(pos.offset(facing));
            if(te != null && te.hasCapability(CapabilityUpgradeProvider.UPGRADE_PROVIDER_CAPABILITY,facing.getOpposite()))
            {
                upgrades.add(te.getCapability(CapabilityUpgradeProvider.UPGRADE_PROVIDER_CAPABILITY,facing.getOpposite()));
            }
        }
        return upgrades;
    }

    public static void verifyUpgrades(TileEntity tile,List<IUpgradeProvider> list)
    {
        //Count, remove, sort
        //This call is expensive. Ideally should be cached.
        HashMap<String,Integer> upgradeCounts = new HashMap<>();
        list.forEach(x -> {
            String id = x.getUpgradeId();
            upgradeCounts.put(x.getUpgradeId(), upgradeCounts.containsKey(id) ? upgradeCounts.get(x.getUpgradeId()) : 1);
        });
        list.removeIf(x -> upgradeCounts.get(x.getUpgradeId()) > x.getLimit(tile));
        list.sort((x,y) -> Integer.compare(x.getPriority(),y.getPriority()));
    }

    public static float getTotalEmberFuelEfficiency(TileEntity tile,List<IUpgradeProvider> list)
    {
        float total = 1.0f;

        for (IUpgradeProvider upgrade : list) {
            total += upgrade.getEmberFuelEfficiency(tile);
        }

        return total;
    }

    public static float getTotalEmberProductEfficiency(TileEntity tile,List<IUpgradeProvider> list)
    {
        float total = 1.0f;

        for (IUpgradeProvider upgrade : list) {
            total += upgrade.getEmberProductEfficiency(tile);
        }

        return total;
    }

    public static float getTotalSpeedModifier(TileEntity tile,List<IUpgradeProvider> list)
    {
        float total = 1.0f;

        for (IUpgradeProvider upgrade : list) {
            total += upgrade.getSpeed(tile);
        }

        return total;
    }

    //DO NOT CALL FROM AN UPGRADE'S doWork METHOD!!
    public static boolean doWork(TileEntity tile, List<IUpgradeProvider> list)
    {
        for (IUpgradeProvider upgrade: list) {
            if(upgrade.doWork(tile,list))
                return true;
        }

        return false;
    }

    public static void transformOutput(TileEntity tile, List<ItemStack> outputs, List<IUpgradeProvider> list)
    {
        for (IUpgradeProvider upgrade : list) {
            upgrade.transformOutput(tile,outputs);
        }
    }

    public static FluidStack transformOutput(TileEntity tile, FluidStack output, List<IUpgradeProvider> list)
    {
        for (IUpgradeProvider upgrade : list) {
            output = upgrade.transformOutput(tile,output);
        }

        return output;
    }
}
