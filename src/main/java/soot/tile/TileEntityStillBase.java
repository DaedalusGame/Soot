package soot.tile;

import net.minecraft.block.BlockWallSign;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import soot.block.BlockStill;
import soot.capability.IUpgradeProvider;
import soot.recipe.CraftingRegistry;
import soot.recipe.RecipeStill;
import soot.util.FluidUtil;
import soot.util.HeatManager;
import soot.util.UpgradeUtil;
import teamroots.embers.EventManager;
import teamroots.embers.tileentity.ITileEntityBase;
import teamroots.embers.tileentity.TileEntityHeatCoil;

import javax.annotation.Nullable;
import java.util.List;

public class TileEntityStillBase extends TileEntity implements ITileEntityBase, ITickable {
    public FluidTank tank = new FluidTank(5000);
    public List<IUpgradeProvider> upgrades;
    private int ticksExisted;

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
    public void markForUpdate() {
        EventManager.markTEForUpdate(getPos(), this);
    }

    @Override
    public void markDirty() {
        markForUpdate();
        super.markDirty();
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && facing.getAxis() != EnumFacing.Axis.Y) {
            return true;
        }
        return super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && facing.getAxis() != EnumFacing.Axis.Y) {
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
        double heat = HeatManager.getHeat(world, pos.down());
        if (heat <= 0)
            return;
        TileEntityStillTip tip = getTip();
        if (tip != null) {
            upgrades = UpgradeUtil.getUpgrades(world, pos, EnumFacing.HORIZONTALS); //TODO: Cache both of these calls
            UpgradeUtil.verifyUpgrades(this, upgrades);

            int cookTime = (int) Math.ceil(MathHelper.clampedLerp(1, 40, 1.0 - (heat / 200)) * (1.0 / UpgradeUtil.getTotalSpeedModifier(this, upgrades)));
            boolean cancel = UpgradeUtil.doWork(this, upgrades);
            if (!cancel && ticksExisted % cookTime == 0) {
                FluidTank output = tip.tank;
                FluidStack inputStack = tank.getFluid();
                RecipeStill recipe = CraftingRegistry.getStillRecipe(this, inputStack, tip.getCurrentCatalyst());
                if (recipe != null) {
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
                        if (inputStack != null)
                            tank.drain(inputStack.amount, true);
                        output.fill(outputStack, true);
                        tip.depleteCatalyst(recipe.catalystConsumed);
                        markDirty();
                        tip.markDirty();
                    }
                }

            }
        }
    }
}
