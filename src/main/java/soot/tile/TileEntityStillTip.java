package soot.tile;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
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
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import soot.recipe.CraftingRegistry;
import teamroots.embers.EventManager;
import teamroots.embers.tileentity.IItemPipeConnectable;
import teamroots.embers.tileentity.ITileEntityBase;
import teamroots.embers.util.EnumPipeConnection;
import teamroots.embers.util.Misc;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Random;

public class TileEntityStillTip extends TileEntity implements ITileEntityBase, ITickable, IItemPipeConnectable {
    public FluidTank tank = new FluidTank(1000);
    public ItemStackHandler inventory = new ItemStackHandler(1){
        @Override
        protected void onContentsChanged(int slot) {
            TileEntityStillTip.this.markDirty();
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate){
            return super.insertItem(slot, stack, simulate);
        }

        @Override
        public int getSlotLimit(int slot) {
            return 4;
        }

        @Nonnull
        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            ItemStack current = getStackInSlot(slot);
            if(catalystAmount > 0)
                amount = Math.min(amount,current.getCount()-1); //Extract one less item if the item is in use.
            return super.extractItem(slot, amount, simulate);
        }
    };

    public int catalystAmount = 0;
    private Random random = new Random();
    public boolean connectUp, connectNorth, connectSouth, connectEast, connectWest;

    public void updateConnections() {
        connectUp = canConnectTo(EnumFacing.UP);
        connectNorth = canConnectTo(EnumFacing.NORTH);
        connectSouth = canConnectTo(EnumFacing.SOUTH);
        connectEast = canConnectTo(EnumFacing.EAST);
        connectWest = canConnectTo(EnumFacing.WEST);
        markDirty();
    }

    public boolean canConnectTo(EnumFacing facing) {
        TileEntity tile = world.getTileEntity(pos.offset(facing));
        return tile != null && tile.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing.getOpposite());
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag){
        super.writeToNBT(tag);
        tag.setBoolean("up",connectUp);
        tag.setBoolean("north",connectNorth);
        tag.setBoolean("south",connectSouth);
        tag.setBoolean("east",connectEast);
        tag.setBoolean("west",connectWest);
        tag.setTag("inventory", inventory.serializeNBT());
        return tag;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag){
        super.readFromNBT(tag);
        connectUp = tag.getBoolean("up");
        connectNorth = tag.getBoolean("north");
        connectSouth = tag.getBoolean("south");
        connectEast = tag.getBoolean("east");
        connectWest = tag.getBoolean("west");
        inventory.deserializeNBT(tag.getCompoundTag("inventory"));
    }

    public void depleteCatalyst(int amount) {
        if(catalystAmount <= 0)
            catalystAmount = getCatalystAmount(getCurrentCatalyst());
        catalystAmount = Math.max(catalystAmount - amount, 0);
        if(catalystAmount <= 0)
            depleteItem(0,random);
    }

    public void depleteItem(int slot, Random random)
    {
        ItemStack stack = inventory.getStackInSlot(slot);
        if(stack.isItemStackDamageable())
            stack.attemptDamageItem(1,random,null);
        else
            stack.shrink(1);
        inventory.setStackInSlot(slot,stack);
    }

    public ItemStack getCurrentCatalyst()
    {
        return inventory.getStackInSlot(0);
    }

    public int getCatalystAmount(ItemStack stack)
    {
        return CraftingRegistry.getStillCatalyst(stack);
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
    public void breakBlock(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
        this.invalidate();
        Misc.spawnInventoryInWorld(getWorld(), pos.getX()+0.5, pos.getY()+0.5, pos.getZ()+0.5, inventory);
        world.setTileEntity(pos, null);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing){
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY){
            return true;
        }
        return super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing){
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY){
            return (T)this.inventory;
        }
        return super.getCapability(capability, facing);
    }

    @Override
    public void markDirty() {
        super.markDirty();
        Misc.syncTE(this);
    }

    @Override
    public void update() {
        if(tank.getFluidAmount() > 0) {
            TileEntity tileEntity = world.getTileEntity(pos.down());
            if(tileEntity != null && tileEntity.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY,EnumFacing.UP)) {
                IFluidHandler fluidTank = tileEntity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, EnumFacing.UP);
                int amount = fluidTank.fill(tank.getFluid(),true); //If this is null the girl reading this triple gay
                if(amount > 0) {
                    tank.drain(amount, true);
                    markDirty();
                }
            }
        }
    }

    @Override
    public EnumPipeConnection getConnection(EnumFacing enumFacing) {
        return EnumPipeConnection.PIPE;
    }
}
