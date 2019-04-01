package soot.upgrade;

import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import soot.SoundEvents;
import soot.network.PacketHandler;
import soot.network.message.MessageMixerBlastFX;
import soot.network.message.MessageMixerFailFX;
import soot.recipe.CraftingRegistry;
import soot.recipe.RecipeAlchemicalMixer;
import soot.tile.TileEntityAlchemyGlobe;
import teamroots.embers.api.alchemy.AlchemyResult;
import teamroots.embers.api.event.MachineRecipeEvent;
import teamroots.embers.api.event.UpgradeEvent;
import teamroots.embers.api.upgrades.IUpgradeProvider;
import teamroots.embers.tileentity.TileEntityMixerBottom;
import teamroots.embers.tileentity.TileEntityMixerTop;
import teamroots.embers.util.DefaultUpgradeProvider;

import java.util.List;

public class UpgradeAlchemyGlobe extends DefaultUpgradeProvider {

    public static final int STARTUP_TIME = 200;
    public static final double STARTUP_COST = 15;
    public static final double FAILURE_COST = 6000;
    public static final double COST_MULTIPLIER = 20;

    public enum Status
    {
        Idle,
        PreStarting,
        Starting,
        Crafting,
        Success,
        Failure,
    }

    public UpgradeAlchemyGlobe(TileEntity tile) {
        super("alchemy_globe", tile);
    }

    Status status = Status.Idle;
    int statusTick;
    RecipeAlchemicalMixer currentRecipe;

    public int getStatusTick() {
        return statusTick;
    }

    public void setStatus(Status status)
    {
        setStatus(status,0);
    }

    public void setStatus(Status status, int tick)
    {
        this.status = status;
        this.statusTick = tick;
    }

    public Status getStatus() {
        return status;
    }

    @Override
    public int getLimit(TileEntity tile) {
        return 1;
    }

    /*@Override
    public boolean doTick(TileEntity tile, List<IUpgradeProvider> upgrades) {
        return true;
    }

    @Override
    public boolean doWork(TileEntity tile, List<IUpgradeProvider> upgrades) {
        if(failure > 0)
            failure--;

        if (tile instanceof TileEntityMixerBottom && failure <= 0) //Only works with the fixed up class
        {
            doAlchemicMixing((TileEntityMixerBottom) tile, upgrades);
            return true; //Block normal mixing.
        }

        return false;
    }*/

    private boolean canCraft() {
        switch (status) {
            case Idle:
            case PreStarting:
            case Starting:
            case Failure:
            default:
                return false;
            case Crafting:
            case Success:
                return true;
        }
    }

    @Override
    public double transformEmberConsumption(TileEntity tile, double ember) {
        return ember * COST_MULTIPLIER;
    }

    @Override
    public boolean doTick(TileEntity tile, List<IUpgradeProvider> upgrades) {
        statusTick++;
        if(!this.tile.getWorld().isRemote)
        switch (status)
        {
            case Idle:
            case Crafting:
            case Success:
                break;
            case PreStarting:
                setStatus(Status.Starting);
                break;
            case Starting:
                if(tile instanceof TileEntityMixerBottom) {
                    TileEntityMixerBottom bottom = (TileEntityMixerBottom) tile;
                    TileEntityMixerTop top = (TileEntityMixerTop) bottom.getWorld().getTileEntity(bottom.getPos().up());
                    double consumed = top.capability.removeAmount(STARTUP_COST,true);
                    if(consumed < STARTUP_COST) //Fizzle
                        setStatus(Status.Idle);
                }
                if(statusTick >= STARTUP_TIME) {
                    BlockPos pos = this.tile.getPos();
                    PacketHandler.INSTANCE.sendToAll(new MessageMixerBlastFX(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 0, 4.5));
                    setStatus(Status.Crafting);
                }
                break;
            case Failure:
                if(statusTick >= 0)
                    setStatus(Status.Idle);
                break;
        }
        return false;
    }

    @Override
    public boolean doWork(TileEntity tile, List<IUpgradeProvider> upgrades) {
        if(!(this.tile instanceof TileEntityAlchemyGlobe))
            return false;
        if(!canCraft())
            return true;
        TileEntityAlchemyGlobe globe = (TileEntityAlchemyGlobe) this.tile;
        World world = globe.getWorld();
        if(!world.isRemote && tile instanceof TileEntityMixerBottom)
        {
            TileEntityMixerBottom bottom = (TileEntityMixerBottom) tile;
            TileEntityMixerTop top = (TileEntityMixerTop) world.getTileEntity(bottom.getPos().up());
            if (currentRecipe != null) {
                AlchemyResult result = currentRecipe.matchAshes(globe.getAspects(), world);
                if(result.getAccuracy() == 1.0) {
                    setStatus(Status.Success);
                } else if(result.areAllPresent()) { //That's not fair if we just void fluid without any ash
                    ejectFailure(world, top.getPos(), result.createFailure(), EnumFacing.HORIZONTALS);
                    bottom.consumeFluids(currentRecipe);
                    top.capability.removeAmount(FAILURE_COST, true);
                    globe.consumeAsh();
                    setStatus(Status.Failure,-(world.rand.nextInt(100)+200));
                    BlockPos pos = globe.getPos();
                    PacketHandler.INSTANCE.sendToAll(new MessageMixerBlastFX(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 8, 0.8));
                    return true;
                } else {
                    setStatus(Status.Failure,-(world.rand.nextInt(100)+200));
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void throwEvent(TileEntity tile, UpgradeEvent event) {
        if(!(this.tile instanceof TileEntityAlchemyGlobe))
            return;
        if(event.getTile() instanceof TileEntityMixerBottom) {
            TileEntityMixerBottom bottom = (TileEntityMixerBottom) event.getTile();
            if (event instanceof MachineRecipeEvent) {
                MachineRecipeEvent recipeEvent = (MachineRecipeEvent) event;
                if(!canCraft())
                    recipeEvent.setRecipe(null);
                else {
                    currentRecipe = CraftingRegistry.getAlchemicalMixingRecipe(bottom.getFluids());
                    if(currentRecipe == null)
                        setStatus(Status.Crafting);
                    recipeEvent.setRecipe(currentRecipe);
                }
            }
        }
    }

    @Override
    public FluidStack transformOutput(TileEntity tile, FluidStack output) {
        if(status == Status.Success)
            return output;
        return null;
    }

    /*public void doAlchemicMixing(TileEntityMixerBottom bottom, List<IUpgradeProvider> upgrades)
    {
        if(!(tile instanceof TileEntityAlchemyGlobe))
            return;
        TileEntityAlchemyGlobe globe = (TileEntityAlchemyGlobe) this.tile;
        World world = bottom.getWorld();
        TileEntityMixerTop top = (TileEntityMixerTop) world.getTileEntity(bottom.getPos().up());
        if (top != null) {
            double emberCost = UpgradeUtil.getTotalEmberConsumption(bottom, 2.0, upgrades);
            if (top.capability.getEmber() >= emberCost) {
                ArrayList<FluidStack> fluids = bottom.getFluids();
                RecipeAlchemicalMixer recipe = CraftingRegistry.getAlchemicalMixingRecipe(fluids);
                if (recipe != null && !world.isRemote) {
                    AlchemyResult result = recipe.matchAshes(globe.getAspects(), world);
                    if(result.getAccuracy() == 1.0) {
                        IFluidHandler tank = top.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
                        FluidStack output = UpgradeUtil.transformOutput(bottom, recipe.output, upgrades);
                        int amount = tank.fill(output, false);
                        if (amount != 0) {
                            tank.fill(output, true);
                            bottom.consumeFluids(recipe);
                            top.capability.removeAmount(emberCost, true);
                            bottom.markDirty();
                            top.markDirty();
                        }
                    }
                    else if(result.areAllPresent()) //That's not fair if we just void fluid without any ash
                    {
                        ItemStack failure = result.createFailure();
                        BlockPos topPos = top.getPos();
                        ejectFailure(world, topPos,failure,EnumFacing.HORIZONTALS);
                        bottom.consumeFluids(recipe);
                        top.capability.removeAmount(emberCost*200, true);
                        fail(world.rand.nextInt(100)+200);
                        globe.consumeAsh();
                    }
                }
            }
        }
    }*/

    public void ejectFailure(World world, BlockPos pos, ItemStack failure, EnumFacing[] directions)
    {
        int ioff = 0;

        for(int i = 0; i < directions.length; i++)
        {
            EnumFacing direction = directions[(i + ioff) % directions.length];
            BlockPos ejectPos = pos.offset(direction);
            IBlockState state = world.getBlockState(ejectPos);
            if(state.getBlockFaceShape(world,ejectPos,direction.getOpposite()) == BlockFaceShape.UNDEFINED)
            {
                ejectFailure(world,pos,failure,direction);
                return;
            }
        }

        ejectFailure(world,pos,failure,EnumFacing.UP);
    }

    //TODO: this should probably be a utility method
    public void ejectFailure(World world, BlockPos pos, ItemStack failure, EnumFacing direction)
    {
        float xEject = direction.getFrontOffsetX();
        float zEject = direction.getFrontOffsetZ();

        float xOff = world.rand.nextFloat() * 0.05F + 0.475F + xEject * 0.7F;
        float yOff = 0.5F;
        float zOff = world.rand.nextFloat() * 0.05F + 0.475F + zEject * 0.7F;

        world.playSound(null,pos.getX() + xOff, pos.getY() + yOff, pos.getZ() + zOff, SoundEvents.ALCHEMICAL_MIXER_WASTE, SoundCategory.BLOCKS, 1.0f, 1.0f);
        PacketHandler.INSTANCE.sendToAll(new MessageMixerFailFX(pos.getX() + xOff, pos.getY() + yOff, pos.getZ() + zOff, direction));
        EntityItem item = new EntityItem(world, pos.getX() + xOff, pos.getY() + yOff - 0.4f, pos.getZ() + zOff, failure);
        item.motionX = xEject * 0.1f;
        item.motionY = 0.0D;
        item.motionZ = zEject * 0.1f;
        item.setDefaultPickupDelay();
        world.spawnEntity(item);
    }
}
