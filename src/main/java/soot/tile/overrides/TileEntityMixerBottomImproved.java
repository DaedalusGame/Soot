package soot.tile.overrides;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import soot.Soot;
import soot.SoundEvents;
import soot.capability.CapabilityMixerOutput;
import soot.capability.IUpgradeProvider;
import soot.util.ISoundController;
import soot.util.UpgradeUtil;
import teamroots.embers.recipe.FluidMixingRecipe;
import teamroots.embers.recipe.RecipeRegistry;
import teamroots.embers.tileentity.TileEntityMixerBottom;
import teamroots.embers.tileentity.TileEntityMixerTop;

import java.util.ArrayList;
import java.util.List;

public class TileEntityMixerBottomImproved extends TileEntityMixerBottom implements ISoundController {
    public static final int SOUND_NONE = 0;
    public static final int SOUND_ON = 1;

    CapabilityMixerOutput mixerOutput;
    public FluidTank[] tanks;
    public List<IUpgradeProvider> upgrades;
    private boolean isSoundPlaying;
    private int soundToPlay;

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

    @Override
    public int getCurrentSoundType() {
        return soundToPlay;
    }

    private void setSoundToPlay(int id) {
        soundToPlay = id;
    }

    public void turnOnSound() {
        if(!isSoundPlaying) {
            if(world.isRemote) {
                Soot.proxy.playMachineSound(this, SOUND_ON, SoundEvents.MIXER_LOOP, SoundCategory.BLOCKS, 1.0f, 1.0f, true, (float) pos.getX() + 0.5f, (float) pos.getY() + 1.0f, (float) pos.getZ() + 0.5f);
            }
            isSoundPlaying = true;
        }
    }

    public void turnOffSound() {
        if(isSoundPlaying) {
            isSoundPlaying = false;
        }
    }

    public void handleSound() {
        if(soundToPlay != SOUND_NONE)
            turnOnSound();
        else
            turnOffSound();
    }

    //Unfortunately I have to do this, the interfaces are all out of whack in the deobf jar
    @Override
    public void update() {
        World world = getWorld();
        BlockPos pos = getPos();
        TileEntityMixerTop top = (TileEntityMixerTop) world.getTileEntity(pos.up());
        if (top != null) {
            handleSound();
            upgrades = UpgradeUtil.getUpgrades(world,pos.up(),EnumFacing.values()); //TODO: Cache both of these calls
            UpgradeUtil.verifyUpgrades(this,upgrades);
            setSoundToPlay(SOUND_NONE);
            boolean cancel = UpgradeUtil.doWork(this,upgrades);
            if(cancel)
                return;
            double emberCost = 2.0 * UpgradeUtil.getTotalEmberConsumption(this,upgrades);
            if (top.capability.getEmber() >= emberCost) {
                ArrayList<FluidStack> fluids = getFluids();
                FluidMixingRecipe recipe = RecipeRegistry.getMixingRecipe(fluids);
                if (recipe != null) {
                    setSoundToPlay(SOUND_ON);
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
