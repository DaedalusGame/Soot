package soot.tile;

import net.minecraft.block.BlockWallSign;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import soot.SoundEvents;
import soot.block.BlockStill;
import soot.recipe.CraftingRegistry;
import soot.recipe.RecipeStill;
import soot.util.FluidUtil;
import soot.util.HeatManager;
import teamroots.embers.Embers;
import teamroots.embers.EventManager;
import teamroots.embers.api.event.MachineRecipeEvent;
import teamroots.embers.api.upgrades.IUpgradeProvider;
import teamroots.embers.api.upgrades.UpgradeUtil;
import teamroots.embers.tileentity.ITileEntityBase;
import teamroots.embers.util.Misc;
import teamroots.embers.util.sound.ISoundController;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.List;

public class TileEntityStillBase extends TileEntity implements ITileEntityBase, ITickable, ISoundController {
    public static final int PROCESS_TIME = 40;
    public FluidTank tank = new FluidTank(5000);
    public List<IUpgradeProvider> upgrades;
    private int ticksExisted;
    double progress;

    public static final int SOUND_NONE = 0;
    public static final int SOUND_HOT = 1;
    public static final int SOUND_WORK_SLOW = 2;
    public static final int SOUND_WORK_FAST = 3;
    public static final int[] SOUND_IDS = new int[]{SOUND_HOT, SOUND_WORK_SLOW, SOUND_WORK_FAST};

    HashSet<Integer> soundsPlaying = new HashSet<>();
    int currentSound;

    public TileEntityStillBase() {
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        NBTTagCompound tankTag = new NBTTagCompound();
        tank.writeToNBT(tankTag);
        tag.setTag("tank", tankTag);
        return tag;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        tank.readFromNBT(tag.getCompoundTag("tank"));
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return writeToNBT(new NBTTagCompound());
    }

    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(getPos(), 0, getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        readFromNBT(pkt.getNbtCompound());
    }

    @Override
    public boolean activate(World world, BlockPos blockPos, IBlockState iBlockState, EntityPlayer entityPlayer, EnumHand enumHand, EnumFacing enumFacing, float v, float v1, float v2) {
        return false;
    }

    @Override
    public void breakBlock(World world, BlockPos blockPos, IBlockState iBlockState, EntityPlayer entityPlayer) {
        this.invalidate();
        world.setTileEntity(pos, null);
    }

    @Override
    public void markDirty() {
        super.markDirty();
        Misc.syncTE(this);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && (facing == null || facing.getAxis() != EnumFacing.Axis.Y)) {
            return true;
        }
        return super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && (facing == null || facing.getAxis() != EnumFacing.Axis.Y)) {
            return (T) tank;
        }
        return super.getCapability(capability, facing);
    }

    private String getNameFromSign() { //You know, this could be an upgrade capability on signs
        String name = null;
        for (EnumFacing dir : EnumFacing.HORIZONTALS) {
            BlockPos signPos = pos.offset(dir);
            IBlockState signState = world.getBlockState(signPos);
            TileEntity tile = world.getTileEntity(signPos);
            if (signState.getBlock() instanceof BlockWallSign && tile instanceof TileEntitySign && signState.getValue(BlockWallSign.FACING) == dir) {
                TileEntitySign sign = (TileEntitySign) tile;
                name = "";
                for (ITextComponent text : sign.signText) {
                    String line = text.getUnformattedText().trim();
                    if (line.endsWith("-"))
                        name += line.substring(0, line.length() - 2);
                    else
                        name += line + " ";
                }
                name = name.trim();
                break;
            }
        }
        return name;
    }

    public TileEntityStillTip getTip() {
        IBlockState state = world.getBlockState(pos);
        if (!(state.getBlock() instanceof BlockStill))
            return null;
        BlockStill block = (BlockStill) state.getBlock();
        BlockPos tipPos = block.getTip(pos, state);
        TileEntity tileEntity = world.getTileEntity(tipPos);
        if (tileEntity instanceof TileEntityStillTip)
            return (TileEntityStillTip) tileEntity;
        return null;
    }

    @Override
    public void update() {
        ticksExisted++;
        if(getWorld().isRemote)
            handleSound();
        double heat = HeatManager.getHeat(world, pos.down());
        TileEntityStillTip tip = getTip();
        if (tip != null) {
            upgrades = UpgradeUtil.getUpgrades(world, pos, EnumFacing.HORIZONTALS); //TODO: Cache both of these calls
            UpgradeUtil.verifyUpgrades(this, upgrades);

            double speedMod = UpgradeUtil.getTotalSpeedModifier(this, upgrades);
            double heatSpeed = 1;
            if(heat > 200)
                heatSpeed = 1 + (heat - 200) / 100;
            progress += speedMod * heatSpeed;
            int cookTime = (int) Math.ceil(MathHelper.clampedLerp(1, 40, 1.0 - (heat / 200)) * (1.0 / speedMod));
            if (progress >= cookTime) {
                FluidTank output = tip.tank;
                FluidStack inputStack = tank.getFluid();
                RecipeStill recipe = getRecipe(inputStack, tip.getCurrentCatalyst());
                if(recipe == null) {
                    setIdleSound(heat);
                    progress = 0;
                }
                while (recipe != null && progress >= cookTime && heat > 0) {
                    boolean cancel = UpgradeUtil.doWork(this, upgrades);
                    if (!cancel) {
                        currentSound = cookTime > 1 ? SOUND_WORK_SLOW : SOUND_WORK_FAST;
                        inputStack = tank.drain(recipe.getInputConsumed(), false);
                        FluidStack outputStack = UpgradeUtil.transformOutput(this, recipe.getOutput(this, inputStack), upgrades);
                        String brewName = getNameFromSign();
                        if (brewName != null) {
                            NBTTagCompound compound = FluidUtil.createModifiers(outputStack);
                            if (!brewName.isEmpty())
                                compound.setString("custom_name", brewName);
                            else
                                compound.removeTag("custom_name");
                        }
                        if (output.fill(outputStack, false) == outputStack.amount) {
                            UpgradeUtil.throwEvent(this, new MachineRecipeEvent.Success<>(this, recipe), upgrades);
                            if (inputStack != null)
                                tank.drain(inputStack.amount, true);
                            output.fill(outputStack, true);
                            tip.depleteCatalyst(recipe.catalystConsumed);
                            markDirty();
                            tip.markDirty();
                        }
                    }
                    if(world.isRemote)
                        progress = 0;
                    else
                        progress -= cookTime;
                    inputStack = tank.getFluid();
                    if(!recipe.matches(this, inputStack, tip.getCurrentCatalyst()))
                        recipe = null;
                }
            }
        } else {
            setIdleSound(heat);
        }
    }

    private RecipeStill getRecipe(FluidStack fluid, ItemStack catalyst) {
        RecipeStill recipe = CraftingRegistry.getStillRecipe(this, fluid, catalyst);
        MachineRecipeEvent<RecipeStill> event = new MachineRecipeEvent<>(this, recipe);
        UpgradeUtil.throwEvent(this, event, upgrades);
        return event.getRecipe();
    }

    public void setIdleSound(double heat) {
        currentSound = SOUND_NONE;
        if (heat > 0) {
            currentSound = SOUND_HOT;
        }
    }

    @Override
    public void playSound(int id) {
        switch (id) {
            case SOUND_HOT:
                Embers.proxy.playMachineSound(this, SOUND_HOT, SoundEvents.STILL_LOOP, SoundCategory.BLOCKS, true, 1.0f, 1.0f, (float)pos.getX()+0.5f,(float)pos.getY()+0.5f,(float)pos.getZ()+0.5f);
                break;
            case SOUND_WORK_SLOW:
                Embers.proxy.playMachineSound(this, SOUND_WORK_SLOW, SoundEvents.STILL_SLOW, SoundCategory.BLOCKS, true, 1.0f, 1.0f, (float)pos.getX()+0.5f,(float)pos.getY()+0.5f,(float)pos.getZ()+0.5f);
                break;
            case SOUND_WORK_FAST:
                Embers.proxy.playMachineSound(this, SOUND_WORK_FAST, SoundEvents.STILL_FAST, SoundCategory.BLOCKS, true, 1.0f, 1.0f, (float)pos.getX()+0.5f,(float)pos.getY()+0.5f,(float)pos.getZ()+0.5f);
                break;
        }
        soundsPlaying.add(id);
    }

    @Override
    public void stopSound(int id) {
        soundsPlaying.remove(id);
    }

    @Override
    public boolean isSoundPlaying(int id) {
        return soundsPlaying.contains(id);
    }

    @Override
    public int[] getSoundIDs() {
        return SOUND_IDS;
    }

    @Override
    public boolean shouldPlaySound(int id) {
        return id == currentSound;
    }
}
