package soot.tile;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import soot.capability.CapabilityMixerOutput;
import soot.capability.IUpgradeProvider;
import soot.util.UpgradeUtil;
import teamroots.embers.recipe.FluidMixingRecipe;
import teamroots.embers.recipe.RecipeRegistry;
import teamroots.embers.tileentity.TileEntityMixerBottom;
import teamroots.embers.tileentity.TileEntityMixerTop;

import java.util.ArrayList;
import java.util.List;

public class TileEntityMixerBottomImproved extends TileEntityMixerBottom {
    CapabilityMixerOutput mixerOutput;
    FluidTank[] tanks;

    public TileEntityMixerBottomImproved()
    {
        super();
        mixerOutput = new CapabilityMixerOutput(this);
        //north = new ResettingFluidTank(8000);
        //east = new ResettingFluidTank(8000);
        //south = new ResettingFluidTank(8000);
        //west = new ResettingFluidTank(8000);
        tanks = new FluidTank[] {north,east,south,west};
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag){
        super.writeToNBT(tag);
        NBTTagCompound northTank = new NBTTagCompound();
        north.writeToNBT(northTank);
        tag.setTag("northTank", northTank);
        NBTTagCompound southTank = new NBTTagCompound();
        south.writeToNBT(southTank);
        tag.setTag("southTank", southTank);
        NBTTagCompound eastTank = new NBTTagCompound();
        east.writeToNBT(eastTank);
        tag.setTag("eastTank", eastTank);
        NBTTagCompound westTank = new NBTTagCompound();
        west.writeToNBT(westTank);
        tag.setTag("westTank", westTank);
        return tag;
    }

    //Unfortunately I have to do this, the interfaces are all out of whack in the deobf jar
    @Override
    public void update() {
        World world = getWorld();
        BlockPos pos = getPos();
        TileEntityMixerTop top = (TileEntityMixerTop) world.getTileEntity(pos.up());
        if (top != null) {
            List<IUpgradeProvider> upgrades = UpgradeUtil.getUpgrades(world,pos.up(),EnumFacing.values()); //TODO: Cache both of these calls
            UpgradeUtil.verifyUpgrades(this,upgrades);
            boolean cancel = UpgradeUtil.doWork(this,upgrades);
            if(cancel)
                return;
            double emberCost = 2.0 * UpgradeUtil.getTotalEmberFuelEfficiency(this,upgrades);
            if (top.capability.getEmber() >= emberCost) {
                ArrayList<FluidStack> fluids = getFluids();
                FluidMixingRecipe recipe = RecipeRegistry.getMixingRecipe(fluids);
                if (recipe != null) {
                    IFluidHandler tank = top.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
                    FluidStack output = UpgradeUtil.transformOutput(this,recipe.output,upgrades);
                    int amount = tank.fill(output, false);
                    if (amount != 0) {
                        tank.fill(output, true);
                        consumeFluids(fluids, recipe);
                        top.capability.removeAmount(emberCost, true);
                        markDirty();
                        top.markDirty();
                    }
                }
            }
        }
    }

    public FluidTank[] getTanks()
    {
        return tanks;
    }

    public void consumeFluids(ArrayList<FluidStack> fluids, FluidMixingRecipe recipe) {
        for (FluidTank tank : tanks) {
            FluidStack tankFluid = tank.getFluid();
            boolean doContinue = true;
            for (int j = 0; j < recipe.inputs.size() && doContinue; j++) {
                FluidStack recipeFluid = recipe.inputs.get(j);
                if (recipeFluid != null && tankFluid != null && recipeFluid.getFluid() == tankFluid.getFluid()) {
                    doContinue = false;
                    tank.drain(recipeFluid.amount,true);
                }
            }
        }
    }

    public ArrayList<FluidStack> getFluids() {
        ArrayList<FluidStack> fluids = new ArrayList<>();
        if (north.getFluid() != null) {
            fluids.add(north.getFluid());
        }
        if (south.getFluid() != null) {
            fluids.add(south.getFluid());
        }
        if (east.getFluid() != null) {
            fluids.add(east.getFluid());
        }
        if (west.getFluid() != null) {
            fluids.add(west.getFluid());
        }
        return fluids;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY || super.hasCapability(capability,facing);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && (facing == null || facing.getAxis() == EnumFacing.Axis.Y) ? (T)mixerOutput : super.getCapability(capability, facing);
    }
}
