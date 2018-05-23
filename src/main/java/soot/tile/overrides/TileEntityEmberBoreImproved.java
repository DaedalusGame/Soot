package soot.tile.overrides;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.WeightedRandom;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.items.ItemStackHandler;
import soot.capability.IUpgradeProvider;
import soot.util.UpgradeUtil;
import teamroots.embers.tileentity.TileEntityEmberBore;
import teamroots.embers.util.EmberGenUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

public class TileEntityEmberBoreImproved extends TileEntityEmberBore {
    static ArrayList<BoreOutput> REGISTRY = new ArrayList<>();
    static BoreOutput DEFAULT;

    public static final int MAX_LEVEL = 7;
    public static final int BORE_TIME = 200;
    public static final int SLOT_FUEL = 8;

    Random random = new Random();
    public List<IUpgradeProvider> upgrades;
    public float lastAngle;

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

        double speedMod = UpgradeUtil.getTotalSpeedModifier(this, upgrades);
        if (ticksFueled > 0){
            lastAngle = angle;
            angle += 12.0f * speedMod;
        }
        boolean cancel = UpgradeUtil.doWork(this,upgrades);
        if (!cancel && getPos().getY() <= UpgradeUtil.getOtherParameter(this,"max_level",MAX_LEVEL,upgrades) && !getWorld().isRemote){
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
            }
            else {
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
        }
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
            if (slot == SLOT_FUEL && TileEntityFurnace.getItemBurnTime(stack) != 0){
                return super.insertItem(slot, stack, simulate);
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
