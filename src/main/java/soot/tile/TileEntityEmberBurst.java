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
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import soot.block.BlockEmberBurst;
import teamroots.embers.SoundManager;
import teamroots.embers.api.capabilities.EmbersCapabilities;
import teamroots.embers.api.power.IEmberCapability;
import teamroots.embers.api.power.IEmberPacketReceiver;
import teamroots.embers.block.BlockEmberEmitter;
import teamroots.embers.entity.EntityEmberPacket;
import teamroots.embers.power.DefaultEmberCapability;
import teamroots.embers.tileentity.ITileEntityBase;
import teamroots.embers.util.Misc;

import javax.annotation.Nullable;
import java.util.Random;

public class TileEntityEmberBurst extends TileEntity implements ITileEntityBase, ITickable, teamroots.embers.api.power.IEmberPacketProducer {
    public static final double TRANSFER_RATE = 80.0;
    public static final double PULL_RATE = 100.0;

    public enum EnumConnection{
        NONE, LEVER
    }

    public IEmberCapability capability = new DefaultEmberCapability(){
        @Override
        public boolean acceptsVolatile() {
            return false;
        }
    };
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
            BlockPos target = new BlockPos(targetCompound.getInteger("x"), targetCompound.getInteger("y"), targetCompound.getInteger("z"));
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
        return Misc.isValidLever(world,pos,side) ? EnumConnection.LEVER : EnumConnection.NONE;
    }

    @Override
    public void markDirty() {
        super.markDirty();
        Misc.syncTE(this);
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
        IBlockState state = getWorld().getBlockState(getPos());
        EnumFacing facing = state.getValue(BlockEmberBurst.facing);
        TileEntity attachedTile = getWorld().getTileEntity(getPos().offset(facing.getOpposite()));
        if (ticksExisted % 5 == 0 && attachedTile != null){
            if (attachedTile.hasCapability(EmbersCapabilities.EMBER_CAPABILITY, null)){
                IEmberCapability cap = attachedTile.getCapability(EmbersCapabilities.EMBER_CAPABILITY, null);
                if (cap.getEmber() > 0 && capability.getEmber() < capability.getEmberCapacity()){
                    double removed = cap.removeAmount(PULL_RATE, true);
                    capability.addAmount(removed, true);
                    markDirty();
                    attachedTile.markDirty();
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

                        Vec3d velocity = getBurstVelocity(targetFacing);
                        packet.initCustom(pos, target, velocity.x, velocity.y, velocity.z, Math.min(TRANSFER_RATE, capability.getEmber()));
                        packet.setPosition(pos.getX() + 0.5f + facing.getFrontOffsetX() * 0.4f,pos.getY() + 0.5f + facing.getFrontOffsetY() * 0.4f,pos.getZ() + 0.5f + facing.getFrontOffsetZ() * 0.4f);
                        this.capability.removeAmount(Math.min(TRANSFER_RATE, capability.getEmber()), true);
                        getWorld().spawnEntity(packet);
                        getWorld().playSound(null, pos, SoundManager.EMBER_EMIT, SoundCategory.BLOCKS, 1.0f, random.nextFloat()+0.5f);
                        markDirty();
                    }
                }
            }
        }
    }

    private Vec3d getBurstVelocity(EnumFacing facing) {
        switch(facing)
        {
            case DOWN:
                return new Vec3d(0, -0.5, 0);
            case UP:
                return new Vec3d(0, 0.5, 0);
            case NORTH:
                return new Vec3d(0, -0.01, -0.5);
            case SOUTH:
                return new Vec3d(0, -0.01, 0.5);
            case WEST:
                return new Vec3d(-0.5, -0.01, 0);
            case EAST:
                return new Vec3d(0.5, -0.01, 0);
            default:
                return Vec3d.ZERO;
        }
    }

    private void nextTarget() {
        targetIndex = (targetIndex+1) % targets.length;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing){
        if (capability == EmbersCapabilities.EMBER_CAPABILITY){
            return true;
        }
        return super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing){
        if (capability == EmbersCapabilities.EMBER_CAPABILITY){
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
