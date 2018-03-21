package soot.tile;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import soot.block.BlockStill;
import soot.capability.IUpgradeProvider;
import soot.recipe.CraftingRegistry;
import soot.recipe.RecipeStill;
import soot.util.UpgradeUtil;
import teamroots.embers.EventManager;
import teamroots.embers.tileentity.ITileEntityBase;
import teamroots.embers.tileentity.TileEntityHeatCoil;

import javax.annotation.Nullable;
import java.util.List;

public class TileEntityStillBase extends TileEntity implements ITileEntityBase, ITickable {
    public FluidTank tank = new FluidTank(5000);

    public TileEntityStillBase()
    {
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag){
        super.writeToNBT(tag);
        NBTTagCompound tankTag = new NBTTagCompound();
        tank.writeToNBT(tankTag);
        tag.setTag("tank", tankTag);
        return tag;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag){
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
    public void markForUpdate(){
        EventManager.markTEForUpdate(getPos(), this);
    }

    @Override
    public void markDirty(){
        markForUpdate();
        super.markDirty();
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing){
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && facing.getAxis() != EnumFacing.Axis.Y){
            return true;
        }
        return super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing){
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && facing.getAxis() != EnumFacing.Axis.Y){
            return (T) tank;
        }
        return super.getCapability(capability, facing);
    }

    public boolean hasValidHeatsource()
    {
        TileEntity tile = world.getTileEntity(pos.down());
        if(tile instanceof TileEntityHeatCoilImproved)
            return ((TileEntityHeatCoilImproved) tile).getHeat() > TileEntityHeatCoilImproved.MAX_HEAT / 2; //TODO: come on now
        return false;
    }

    @Override
    public void update() {
        IBlockState state = world.getBlockState(pos);
        if(!(state.getBlock() instanceof BlockStill))
            return;
        if(!hasValidHeatsource())
            return;
        BlockStill block = (BlockStill) state.getBlock();
        BlockPos tipPos = block.getTip(pos,state);
        TileEntity tileEntity = world.getTileEntity(tipPos);
        if(tileEntity instanceof TileEntityStillTip)
        {
            List<IUpgradeProvider> upgrades = UpgradeUtil.getUpgrades(world,pos,EnumFacing.HORIZONTALS); //TODO: Cache both of these calls
            UpgradeUtil.verifyUpgrades(this,upgrades);

            boolean cancel = UpgradeUtil.doWork(this,upgrades);
            if(!cancel) {
                TileEntityStillTip tip = (TileEntityStillTip) tileEntity;
                FluidTank output = tip.tank;
                FluidStack inputStack = tank.getFluid();
                RecipeStill recipe = CraftingRegistry.getStillRecipe(inputStack, tip.getCurrentCatalyst());
                if (recipe != null) {
                    inputStack = tank.drain(recipe.getInputConsumed(), false);
                    FluidStack outputStack = UpgradeUtil.transformOutput(this, recipe.getOutput(world, this, inputStack), upgrades);
                    if (output.fill(outputStack, false) == outputStack.amount) {
                        if(inputStack != null)
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
