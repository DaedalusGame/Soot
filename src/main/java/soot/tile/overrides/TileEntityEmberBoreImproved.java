package soot.tile.overrides;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.items.ItemStackHandler;
import soot.Soot;
import soot.SoundEvents;
import soot.capability.IUpgradeProvider;
import soot.util.ISoundController;
import soot.util.UpgradeUtil;
import teamroots.embers.tileentity.TileEntityEmberBore;
import teamroots.embers.util.EmberGenUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

public class TileEntityEmberBoreImproved extends TileEntityEmberBore implements ISoundController {
    static ArrayList<BoreOutput> REGISTRY = new ArrayList<>();
    public static BoreOutput DEFAULT;

    public static final int SOUND_NONE = 0;
    public static final int SOUND_ON = 1;
    public static final int SOUND_ON_DRILL = 2;

    public static final int MAX_LEVEL = 7;
    public static final int BORE_TIME = 200;
    public static final int SLOT_FUEL = 8;

    Random random = new Random();
    public List<IUpgradeProvider> upgrades;
    public float lastAngle;
    private boolean isSoundPlaying;
    private int soundToPlay;
    boolean isRunning;

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return new AxisAlignedBB(pos.add(-1, -2, -1), pos.add(2, 1, 2));
    }

    public TileEntityEmberBoreImproved() {
        super();
        inventory = new EmberBoreInventory(9);
    }

    public static void registerBoreOutput(BoreOutput output) {
        REGISTRY.add(output);
    }

    public static void setDefault(BoreOutput output) {
        DEFAULT = output;
    }

    private BoreOutput getBoreOutput() {
        int dimensionId = world.provider.getDimension();
        Biome biome = world.getBiome(pos);
        for(BoreOutput output : REGISTRY) {
            if(output.dimensionIds.contains(dimensionId) && output.biomeIds.contains(biome.getRegistryName()))
                return output;
        }
        return DEFAULT;
    }

    public EmberBoreInventory getInventory() {
        return (EmberBoreInventory)inventory;
    }

    @Override
    public void update() {
        upgrades = UpgradeUtil.getUpgradesForMultiblock(world, pos, new EnumFacing[]{EnumFacing.UP}); //TODO: Cache both of these calls
        UpgradeUtil.verifyUpgrades(this, upgrades);
        handleSound();

        double speedMod = UpgradeUtil.getTotalSpeedModifier(this, upgrades);
        if (ticksFueled > 0){
            lastAngle = angle;
            angle += 12.0f * speedMod;
        }
        setSoundToPlay(ticksFueled > 0 ? SOUND_ON : SOUND_NONE);
        boolean cancel = UpgradeUtil.doWork(this,upgrades);
        if (!cancel && !getWorld().isRemote){
            ticksExisted ++;
            if (ticksFueled > 0){
                ticksFueled --;
            }

            if (ticksFueled == 0){
                ItemStack fuel = inventory.getStackInSlot(SLOT_FUEL);
                if (!fuel.isEmpty()){
                    ItemStack fuelCopy = fuel.copy();
                    ticksFueled = TileEntityFurnace.getItemBurnTime(fuelCopy);
                    fuel.shrink(1);
                    if (fuel.isEmpty()){
                        inventory.setStackInSlot(SLOT_FUEL, fuelCopy.getItem().getContainerItem(fuelCopy));
                    }
                    markDirty();
                }
            } else if(canMine()) {
                setSoundToPlay(SOUND_ON_DRILL);
                int boreTime = (int)Math.ceil(BORE_TIME * (1 / speedMod));
                if (ticksExisted % boreTime == 0){
                    if (random.nextFloat() < EmberGenUtil.getEmberDensity(world.getSeed(), getPos().getX(), getPos().getZ())){
                        BoreOutput output = getBoreOutput();
                        if(output != null) {
                            ArrayList<ItemStack> returns = new ArrayList<>();
                            if(!output.stacks.isEmpty()) {
                                WeightedItemStack picked = WeightedRandom.getRandomItem(random, output.stacks);
                                returns.add(picked.getStack().copy());
                            }
                            UpgradeUtil.transformOutput(this, returns, upgrades);
                            if(canInsert(returns)) {
                                insert(returns);
                            }
                        }
                    }
                }
            }

            if (isRunning != ticksFueled > 0) {
                isRunning = ticksFueled > 0;
                markDirty();
            }
        }
    }

    @Override
    public float getCurrentPitch(float pitch) {
        return (float) UpgradeUtil.getTotalSpeedModifier(this,upgrades);
    }

    @Override
    public int getCurrentSoundType() {
        return soundToPlay;
    }

    private void setSoundToPlay(int id) {
        soundToPlay = id;
    }

    public void handleSound() {
        if(soundToPlay != SOUND_NONE)
            turnOnSound();
        else
            turnOffSound();
    }

    public void turnOnSound() {
        if(!isSoundPlaying) {
            if(world.isRemote) {
                Soot.proxy.playParallelMachineSound(this, SOUND_ON, SoundEvents.BORE_LOOP, SoundCategory.BLOCKS, 1.0f, 1.0f, true, (float) pos.getX() + 0.5f, (float) pos.getY() - 0.5f, (float) pos.getZ() + 0.5f);
                Soot.proxy.playParallelMachineSound(this, SOUND_ON_DRILL, SoundEvents.BORE_LOOP_MINE, SoundCategory.BLOCKS, 1.0f, 1.0f, true, (float) pos.getX() + 0.5f, (float) pos.getY() - 0.5f, (float) pos.getZ() + 0.5f);
            }
            world.playSound(null,pos.getX()+0.5,pos.getY()-0.5,pos.getZ()+0.5,SoundEvents.BORE_START, SoundCategory.BLOCKS, 1.0f, 1.0f);
            isSoundPlaying = true;
        }
    }

    public void turnOffSound() {
        if(isSoundPlaying) {
            world.playSound(null,pos.getX()+0.5,pos.getY()-0.5,pos.getZ()+0.5,SoundEvents.BORE_STOP, SoundCategory.BLOCKS, 1.0f, 1.0f);
            isSoundPlaying = false;
        }
    }

    public boolean canMine() {
        return getPos().getY() <= UpgradeUtil.getOtherParameter(this,"max_level",MAX_LEVEL,upgrades);
    }

    public boolean canInsert(ArrayList<ItemStack> returns) {
        for(ItemStack stack : returns) {
            ItemStack returned = stack;
            for(int slot = 0; slot < getInventory().getSlots()-1; slot++) {
                returned = getInventory().insertItemInternal(slot,returned,true);
            }
            if(!returned.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public void insert(ArrayList<ItemStack> returns) {
        for(ItemStack stack : returns) {
            ItemStack returned = stack;
            for(int slot = 0; slot < getInventory().getSlots()-1; slot++) {
                returned = getInventory().insertItemInternal(slot,returned,false);
            }
        }
    }

    public class EmberBoreInventory extends ItemStackHandler {
        public EmberBoreInventory() {
        }

        public EmberBoreInventory(int size) {
            super(size);
        }

        public EmberBoreInventory(NonNullList<ItemStack> stacks) {
            super(stacks);
        }

        @Override
        protected void onContentsChanged(int slot) {
            TileEntityEmberBoreImproved.this.markDirty();
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate){
            int burntime = TileEntityFurnace.getItemBurnTime(stack);
            if (slot == SLOT_FUEL && burntime != 0){
                return super.insertItem(slot, stack, simulate);
            } else if(burntime != 0) {
                return super.insertItem(SLOT_FUEL, stack, simulate);
            }
            return stack;
        }

        public ItemStack insertItemInternal(int slot, ItemStack stack, boolean simulate){
            return super.insertItem(slot, stack, simulate);
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate){
            if (slot == SLOT_FUEL){
                return ItemStack.EMPTY;
            }
            return super.extractItem(slot, amount, simulate);
        }
    }

    public static class BoreOutput {
        public HashSet<Integer> dimensionIds;
        public HashSet<ResourceLocation> biomeIds;
        public ArrayList<WeightedItemStack> stacks = new ArrayList<>();

        public BoreOutput() {
            this(new HashSet<>(), new HashSet<>(), new ArrayList<>());
        }

        public BoreOutput(HashSet<Integer> dimensionIds, HashSet<ResourceLocation> biomeIds, ArrayList<WeightedItemStack> stacks) {
            this.dimensionIds = dimensionIds;
            this.biomeIds = biomeIds;
            this.stacks = stacks;
        }
    }

    public static class WeightedItemStack extends WeightedRandom.Item {
        ItemStack stack;

        public WeightedItemStack(ItemStack stack, int itemWeightIn) {
            super(itemWeightIn);
            this.stack = stack;
        }

        public ItemStack getStack() {
            return stack;
        }
    }
}
