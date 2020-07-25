package soot.tile;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import soot.block.BlockDecanter;
import soot.brewing.EssenceStack;
import soot.item.IEssenceContainer;
import soot.item.ItemEssence;
import teamroots.embers.Embers;
import teamroots.embers.particle.ParticleUtil;
import teamroots.embers.tileentity.IItemPipeConnectable;
import teamroots.embers.tileentity.ITileEntityBase;
import teamroots.embers.util.EnumPipeConnection;
import teamroots.embers.util.Misc;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.Objects;
import java.util.Random;

public class TileEntityDecanterTop extends TileEntityDecanterBase implements ITileEntityBase, ITickable, IItemPipeConnectable {
    public ResourceLocation texture = new ResourceLocation(Embers.MODID + ":textures/blocks/item_pipe_tex.png");

    public static final int CAPACITY = 8000;

    public ItemStackHandler inventory = new ItemStackHandler(1){
        @Override
        protected void onContentsChanged(int slot) {
            TileEntityDecanterTop.this.markDirty();
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate){
            if(stack.isEmpty())
                return ItemStack.EMPTY;
            if(!getStackInSlot(slot).isEmpty())
                return stack;
            Item item = stack.getItem();
            if(item instanceof IEssenceContainer) {
                ItemStack filled = fillEssence(stack.copy(), (IEssenceContainer) item, simulate);
                ItemStack remainder = stack;
                if(!filled.isEmpty() && super.insertItem(slot, filled, simulate).isEmpty()) {
                    remainder = stack.copy();
                    remainder.shrink(1);
                }
                return remainder;
            }
            return stack;
        }

        @Override
        public int getSlotLimit(int slot) {
            return 1;
        }
    };
    EssenceStack currentEssence = EssenceStack.EMPTY;
    EssenceStack lastAdded = EssenceStack.EMPTY;

    private Random random = new Random();

    public TileEntityDecanterTop() {
    }

    public int getCapacity() {
        return CAPACITY;
    }

    @Override
    public boolean canAdd(EssenceStack stack) {
        return currentEssence.isEmpty() || Objects.equals(currentEssence.getEssence(), stack.getEssence());
    }

    @Override
    public EssenceStack add(EssenceStack stack) {
        EssenceStack remainder;
        if(currentEssence.isEmpty()) {
            currentEssence = stack.copy();
            remainder = EssenceStack.EMPTY;
        } else if(Objects.equals(currentEssence.getEssence(), stack.getEssence())) {
            remainder = currentEssence.merge(stack, getCapacity());
        } else {
            remainder = stack;
        }

        if(remainder.getAmount() < stack.getAmount()) { //We inserted some essence. Send an update.
            lastAdded = stack.copy();
            lastAdded.setAmount(20);
            markDirty();
        }

        return remainder;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            return true;
        return super.hasCapability(capability, facing);
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            return (T) inventory;
        return super.getCapability(capability, facing);
    }

    @Override
    public EnumFacing getFacing() {
        IBlockState state = world.getBlockState(pos);
        if(state.getBlock() instanceof BlockDecanter) {
            return state.getValue(BlockDecanter.FACING);
        }
        return null;
    }

    @Override
    public void update() {
        if(world.isRemote) {
            if(!lastAdded.isEmpty()) {
                EnumFacing facing = getFacing();
                EnumFacing facingRight = facing.rotateY();
                Color color = lastAdded.getEssence().getFillColor();

                float xBase = getPos().getX() + 0.5f;
                float yBase = getPos().getY() + 0.5f;
                float zBase = getPos().getZ() + 0.5f;

                float velocity = 0.03f;

                float xWide = 0.5f;
                float yWide = 0.1f;

                float xNarrow = 0.55f;
                float yNarrow = 0.275f;

                float x1 = xBase - xWide * facing.getFrontOffsetX();
                float y1 = yBase - yWide;
                float z1 = zBase - xWide * facing.getFrontOffsetZ();
                float motionx1 = -velocity * 0.5f * facing.getFrontOffsetX() + random.nextFloat() * 0.02f - 0.01f;
                float motiony1 = velocity + random.nextFloat() * 0.02f - 0.01f;
                float motionz1 = -velocity * 0.5f * facing.getFrontOffsetZ() + random.nextFloat() * 0.02f - 0.01f;

                float x2 = xBase + xNarrow * facingRight.getFrontOffsetX();
                float y2 = yBase - yNarrow;
                float z2 = zBase + xNarrow * facingRight.getFrontOffsetZ();
                float motionx2 = velocity * facingRight.getFrontOffsetX() + random.nextFloat() * 0.02f - 0.01f;
                float motiony2 = velocity + random.nextFloat() * 0.02f - 0.01f;
                float motionz2 = velocity * facingRight.getFrontOffsetZ() + random.nextFloat() * 0.02f - 0.01f;

                float x3 = xBase - xNarrow * facingRight.getFrontOffsetX();
                float y3 = yBase - yNarrow;
                float z3 = zBase - xNarrow * facingRight.getFrontOffsetZ();
                float motionx3 = -velocity * facingRight.getFrontOffsetX() + random.nextFloat() * 0.02f - 0.01f;
                float motiony3 = velocity + random.nextFloat() * 0.02f - 0.01f;
                float motionz3 = -velocity * facingRight.getFrontOffsetZ() + random.nextFloat() * 0.02f - 0.01f;


                ParticleUtil.spawnParticleVapor(getWorld(), x1, y1, z1, motionx1, motiony1, motionz1, color.getRed(), color.getGreen(), color.getBlue(), 1.0f, 1.0f, 2.0f, 24);
                ParticleUtil.spawnParticleVapor(getWorld(), x2, y2, z2, motionx2, motiony2, motionz2, color.getRed(), color.getGreen(), color.getBlue(), 1.0f, 1.0f, 2.0f, 24);
                ParticleUtil.spawnParticleVapor(getWorld(), x3, y3, z3, motionx3, motiony3, motionz3, color.getRed(), color.getGreen(), color.getBlue(), 1.0f, 1.0f, 2.0f, 24);
            }
        }
        lastAdded.shrink(1);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setTag("essence", currentEssence.writeToNBT(new NBTTagCompound()));
        tag.setTag("lastAdded", lastAdded.writeToNBT(new NBTTagCompound()));
        return tag;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        currentEssence = new EssenceStack(tag.getCompoundTag("essence"));
        lastAdded = new EssenceStack(tag.getCompoundTag("lastAdded"));
    }

    @Override
    public boolean activate(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        ItemStack heldItem = player.getHeldItem(hand);
        Item item = heldItem.getItem();

        if(item instanceof IEssenceContainer) {
            ItemStack filled = fillEssence(heldItem, (IEssenceContainer) item, false);
            if(!filled.isEmpty()) {
                heldItem.shrink(1);
                if (!world.isRemote)
                    world.spawnEntity(new EntityItem(world, player.posX, player.posY + (double)(player.height / 2.0F), player.posZ, filled));
            }
            return true;
        }

        return false;
    }

    private ItemStack fillEssence(ItemStack stack, IEssenceContainer container, boolean simulate) {
        EssenceStack itemEssence = container.getEssence(stack);
        int capacity = container.getCapacity(stack);
        int toAdd = capacity - itemEssence.getAmount();
        EssenceStack essenceToFill = this.currentEssence;
        if(simulate)
            essenceToFill = essenceToFill.copy();
        if(essenceToFill.getAmount() >= toAdd && toAdd > 0) {
            EssenceStack addedEssence = essenceToFill.split(toAdd);
            return container.addEssence(stack, addedEssence);
        }
        return ItemStack.EMPTY;
    }

    @Override
    public EnumPipeConnection getConnection(EnumFacing facing) {
        if(facing.getAxis() != EnumFacing.Axis.Y)
            return EnumPipeConnection.PIPE;
        else
            return EnumPipeConnection.BLOCK;
    }
}
