package soot.tile;

import com.google.common.collect.Lists;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import soot.capability.IUpgradeProvider;
import soot.recipe.CraftingRegistry;
import soot.recipe.RecipeStamper;
import soot.util.UpgradeUtil;
import teamroots.embers.RegistryManager;
import teamroots.embers.block.BlockStamper;
import teamroots.embers.network.PacketHandler;
import teamroots.embers.network.message.MessageStamperFX;
import teamroots.embers.tileentity.TileEntityBin;
import teamroots.embers.tileentity.TileEntityStampBase;
import teamroots.embers.tileentity.TileEntityStamper;

import java.util.ArrayList;
import java.util.List;

public class TileEntityStamperImproved extends TileEntityStamper {

    public static final double EMBER_CONSUMPTION = 80.0;
    public static final int STAMP_TIME = 70;
    public static final int RETRACT_TIME = 10;

    public RecipeStamper getRecipe(ItemStack input, FluidStack fluid, ItemStack stamp) {
        return CraftingRegistry.getStamperRecipe(input, fluid, stamp);
    }

    @Override
    public void update() {
        this.ticksExisted++;
        prevPowered = powered;
        EnumFacing face = getWorld().getBlockState(getPos()).getValue(BlockStamper.facing);
        BlockPos basePos = getPos().offset(face, 2);
        if (getWorld().getBlockState(basePos).getBlock() == RegistryManager.stamp_base) {
            List<IUpgradeProvider> upgrades = UpgradeUtil.getUpgrades(world, pos, EnumFacing.VALUES); //TODO: Cache both of these calls
            UpgradeUtil.verifyUpgrades(this, upgrades);
            int stampTime = (int) (STAMP_TIME * (1 / UpgradeUtil.getTotalSpeedModifier(this, upgrades)));
            int retractTime = (int) (RETRACT_TIME * (1 / UpgradeUtil.getTotalSpeedModifier(this, upgrades)));
            if (!powered && !getWorld().isRemote && this.ticksExisted >= stampTime) {
                TileEntityStampBase stamp = (TileEntityStampBase) getWorld().getTileEntity(basePos);
                FluidStack fluid = null;
                if (stamp != null) {
                    IFluidHandler handler = stamp.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
                    if (handler != null)
                        fluid = handler.drain(stamp.getCapacity(), false); //God please kill me
                    RecipeStamper recipe = getRecipe(stamp.inputs.getStackInSlot(0), fluid, this.stamp.getStackInSlot(0));
                    if (recipe != null) {
                        float cost_multiplier = UpgradeUtil.getTotalEmberFuelEfficiency(this, upgrades);
                        double consumedEmber = EMBER_CONSUMPTION * cost_multiplier;
                        if (this.capability.getEmber() > consumedEmber) {
                            this.capability.removeAmount(consumedEmber, true);
                            if (!world.isRemote) {
                                PacketHandler.INSTANCE.sendToAll(new MessageStamperFX(basePos.getX() + 0.5f, basePos.getY() + 1.0f, basePos.getZ() + 0.5f));
                            }
                            powered = true;
                            this.ticksExisted = 0;

                            ArrayList<ItemStack> returns = Lists.newArrayList(recipe.getResult(this, stamp.inputs.getStackInSlot(0), fluid, this.stamp.getStackInSlot(0)));
                            UpgradeUtil.transformOutput(this, returns, upgrades);

                            stamp.inputs.extractItem(0, recipe.getInputConsumed(), false);
                            if (recipe.inputFluid != null) {
                                stamp.getTank().drain(recipe.inputFluid, true);
                            }

                            BlockPos middlePos = getPos().offset(face, 1);
                            TileEntity tile = getWorld().getTileEntity(basePos.offset(face, 1));
                            for (ItemStack result : returns) {
                                if (tile instanceof TileEntityBin) {
                                    TileEntityBin bin = (TileEntityBin) tile;
                                    ItemStack remainder = bin.inventory.insertItem(0, result, false);
                                    if (remainder != ItemStack.EMPTY && !getWorld().isRemote) {
                                        EntityItem item = new EntityItem(getWorld(), middlePos.getX() + 0.5, middlePos.getY() + 0.5, middlePos.getZ() + 0.5, remainder);
                                        getWorld().spawnEntity(item);
                                    }
                                    bin.markDirty();
                                    markDirty();
                                } else if (!getWorld().isRemote) {
                                    EntityItem item = new EntityItem(getWorld(), middlePos.getX() + 0.5, middlePos.getY() + 0.5, middlePos.getZ() + 0.5, result);
                                    getWorld().spawnEntity(item);
                                }
                            }
                            stamp.markDirty();
                        }
                    }
                }
                markDirty();
            } else if (powered && !getWorld().isRemote && this.ticksExisted >= retractTime) {
                powered = false;
                this.ticksExisted = 0;
                markDirty();
            }
        } else if (powered) {
            powered = false;
            markDirty();
        }
    }

    @Override
    public boolean activate(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {

        ItemStack heldItem = player.getHeldItem(hand);
        if (heldItem != ItemStack.EMPTY) {
            if (stamp.getStackInSlot(0) == ItemStack.EMPTY) {
                ItemStack newStack = new ItemStack(heldItem.getItem(), 1, heldItem.getMetadata());
                if (heldItem.hasTagCompound()) {
                    newStack.setTagCompound(heldItem.getTagCompound());
                }
                player.setHeldItem(hand, this.stamp.insertItem(0, newStack, false));
                markDirty();
                return true;
            }
        } else {
            if (stamp.getStackInSlot(0) != ItemStack.EMPTY && !world.isRemote) {
                world.spawnEntity(new EntityItem(world, player.posX, player.posY, player.posZ, stamp.getStackInSlot(0)));
                stamp.setStackInSlot(0, ItemStack.EMPTY);
                markDirty();
                return true;
            }
        }
        return false;
    }
}
