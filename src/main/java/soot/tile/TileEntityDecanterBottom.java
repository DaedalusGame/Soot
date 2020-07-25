package soot.tile;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import soot.block.BlockDecanter;
import soot.brewing.EssenceStack;
import soot.upgrade.UpgradeDecanter;
import teamroots.embers.api.capabilities.EmbersCapabilities;
import teamroots.embers.tileentity.ITileEntityBase;
import teamroots.embers.util.Misc;

import javax.annotation.Nullable;
import java.util.Objects;

public class TileEntityDecanterBottom extends TileEntityDecanterBase {
    UpgradeDecanter upgrade;

    public TileEntityDecanterBottom() {
        upgrade = new UpgradeDecanter(this);
    }

    @Override
    public boolean canAdd(EssenceStack stack) {
        TileEntity top = world.getTileEntity(pos.up());
        if(top instanceof TileEntityDecanterTop) {
            return ((TileEntityDecanterTop) top).canAdd(stack);
        }
        return false;
    }

    @Override
    public EssenceStack add(EssenceStack stack) {
        TileEntity top = world.getTileEntity(pos.up());
        if(top instanceof TileEntityDecanterTop) {
            return ((TileEntityDecanterTop) top).add(stack);
        }
        return stack;
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
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        if(capability == EmbersCapabilities.UPGRADE_PROVIDER_CAPABILITY && facing == getFacing())
            return true;
        return super.hasCapability(capability, facing);
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if(capability == EmbersCapabilities.UPGRADE_PROVIDER_CAPABILITY && facing == getFacing())
            return (T) upgrade;
        return super.getCapability(capability, facing);
    }

    @Override
    public boolean activate(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        return false;
    }
}
