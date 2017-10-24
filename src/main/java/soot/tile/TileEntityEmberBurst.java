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
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import soot.util.EmberUtil;
import teamroots.embers.EventManager;
import teamroots.embers.block.BlockEmberEmitter;
import teamroots.embers.entity.EntityEmberPacket;
import teamroots.embers.power.*;
import teamroots.embers.tileentity.ITileEntityBase;

import javax.annotation.Nullable;
import java.util.Random;

public class TileEntityEmberBurst extends TileEntity implements ITileEntityBase, ITickable, IEmberPacketProducer {
    public enum EnumConnection{
        NONE, LEVER
    }

    public IEmberCapability capability = new DefaultEmberCapability();
    public BlockPos[] targets = new BlockPos[6];
    int targetIndex = 0;

    public long ticksExisted = 0;
    Random random = new Random();
    int offset = random.nextInt(40);

    public static EnumConnection connectionFromInt(int value){
        switch (value){
            case 0:
                return EnumConnection.NONE;
            case 1:
                return EnumConnection.LEVER;
        }
        return EnumConnection.NONE;
    }

    public EnumConnection up = EnumConnection.NONE, down = EnumConnection.NONE, north = EnumConnection.NONE, south = EnumConnection.NONE, east = EnumConnection.NONE, west = EnumConnection.NONE;

    public TileEntityEmberBurst(){
        super();
        capability.setEmberCapacity(200);
    }

    public void updateNeighbors(IBlockAccess world){
        up = getConnection(world,getPos().up(),EnumFacing.DOWN);
        down = getConnection(world,getPos().down(),EnumFacing.UP);
        north = getConnection(world,getPos().north(),EnumFacing.NORTH);
        south = getConnection(world,getPos().south(),EnumFacing.SOUTH);
        west = getConnection(world,getPos().west(),EnumFacing.WEST);
        east = getConnection(world,getPos().east(),EnumFacing.EAST);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag){
        super.writeToNBT(tag);
        tag.setInteger("up", up.ordinal());
        tag.setInteger("down", down.ordinal());
        tag.setInteger("north", north.ordinal());
        tag.setInteger("south", south.ordinal());
        tag.setInteger("west", west.ordinal());
        tag.setInteger("east", east.ordinal());
        for (int i = 0; i < 6; i++)
        {
            EnumFacing facing = EnumFacing.getFront(i);
            String key = "target" + facing.getName();
            BlockPos target = targets[i];
            if(target == null)
                continue;
            NBTTagCompound targetCompound = new NBTTagCompound();
            targetCompound.setInteger("x",target.getX());
            targetCompound.setInteger("y",target.getY());
            targetCompound.setInteger("z",target.getZ());
            tag.setTag(key,targetCompound);
        }
        capability.writeToNBT(tag);
        return tag;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag){
        super.readFromNBT(tag);
        up = connectionFromInt(tag.getInteger("up"));
        down = connectionFromInt(tag.getInteger("down"));
        north = connectionFromInt(tag.getInteger("north"));
        south = connectionFromInt(tag.getInteger("south"));
        west = connectionFromInt(tag.getInteger("west"));
        east = connectionFromInt(tag.getInteger("east"));
        for (int i = 0; i < 6; i++)
        {
            EnumFacing facing = EnumFacing.getFront(i);
            String key = "target" + facing.getName();
            if(!tag.hasKey(key))
                continue;
            NBTTagCompound targetCompound = tag.getCompoundTag(key);
            BlockPos target = new BlockPos(targetCompound.getInteger("x"), tag.getInteger("y"), tag.getInteger("z"));
            targets[i] = target;
        }
        capability.readFromNBT(tag);
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

    public EnumConnection getConnection(IBlockAccess world, BlockPos pos, EnumFacing side){
        return EmberUtil.isValidLever(world,pos,side) ? EnumConnection.LEVER : EnumConnection.NONE;
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
    public boolean activate(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand,
                            EnumFacing side, float hitX, float hitY, float hitZ) {
        return false;
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
        this.invalidate();
        world.setTileEntity(pos, null);
    }

    @Override
    public void update() {
        this.ticksExisted ++;
        IBlockState state = world.getBlockState(pos);
        EnumFacing facing = state.getValue(BlockEmberEmitter.facing);
        TileEntity attachedTile = world.getTileEntity(pos.offset(facing, -1));
        if (ticksExisted % 5 == 0 && attachedTile != null){
            if (attachedTile.hasCapability(EmberCapabilityProvider.emberCapability, null)){
                IEmberCapability cap = attachedTile.getCapability(EmberCapabilityProvider.emberCapability, null);
                if (cap.getEmber() > 0 && capability.getEmber() < capability.getEmberCapacity()){
                    double removed = cap.removeAmount(100, true);
                    double added = capability.addAmount(removed, true);
                    markDirty();
                    BlockPos offset = pos.offset(facing,-1);
                    attachedTile.markDirty();
                    if (!world.isRemote && !(attachedTile instanceof ITileEntityBase)){
                        attachedTile.markDirty();
                        EventManager.markTEForUpdate(offset, attachedTile);
                    }
                }
            }
        }

        EnumFacing targetFacing = EnumFacing.getFront(targetIndex);
        if(targetFacing == facing.getOpposite()) //Don't try to shoot out the back
            nextTarget();

        if (!world.isRemote && (this.ticksExisted+offset) % 4 == 0 && world.isBlockIndirectlyGettingPowered(pos) != 0 && this.capability.getEmber() > 10){
            BlockPos target = targets[targetIndex];
            nextTarget();
            if(target != null) {
                TileEntity targetTile = world.getTileEntity(target);

                if (targetTile instanceof IEmberPacketReceiver) {
                    if (!(((IEmberPacketReceiver) targetTile).isFull())) {
                        EntityEmberPacket packet = new EntityEmberPacket(world);
                        double vx = 0, vy = 0, vz = 0;

                        switch (targetFacing)
                        {
                            case DOWN:
                                vy = -0.5;
                                break;
                            case UP:
                                vy = 0.5;
                                break;
                            case NORTH:
                                vz = -0.5;
                                vy = -0.01;
                                break;
                            case SOUTH:
                                vz = 0.5;
                                vy = -0.01;
                                break;
                            case WEST:
                                vx = -0.5;
                                vy = -0.01;
                                break;
                            case EAST:
                                vx = 0.5;
                                vy = -0.01;
                                break;
                        }

                        double sentAmount = Math.min(80.0, capability.getEmber());
                        packet.initCustom(pos, target, vx, vy, vz, sentAmount);
                        packet.setPosition(pos.getX() + 0.5f + facing.getFrontOffsetX() * 0.4f,pos.getY() + 0.5f + facing.getFrontOffsetY() * 0.4f,pos.getZ() + 0.5f + facing.getFrontOffsetZ() * 0.4f);
                        this.capability.removeAmount(sentAmount, true);
                        world.spawnEntity(packet);
                        markDirty();
                    }
                }
            }
        }
    }

    private void nextTarget() {
        targetIndex = (targetIndex+1) % targets.length;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing){
        if (capability == EmberCapabilityProvider.emberCapability){
            return true;
        }
        return super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing){
        if (capability == EmberCapabilityProvider.emberCapability){
            return (T)this.capability;
        }
        return super.getCapability(capability, facing);
    }

    @Override
    public void setTargetPosition(BlockPos targetpos, EnumFacing side) {
        EnumFacing facing = world.getBlockState(pos).getValue(BlockEmberEmitter.facing);
        if(side != facing.getOpposite()) {
            targets[side.getIndex()] = targetpos;
            markDirty();
        }
    }
}
