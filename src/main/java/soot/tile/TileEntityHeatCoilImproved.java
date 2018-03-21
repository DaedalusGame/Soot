package soot.tile;

import com.google.common.collect.Lists;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import soot.capability.IUpgradeProvider;
import soot.recipe.CraftingRegistry;
import soot.recipe.RecipeHeatCoil;
import soot.util.UpgradeUtil;
import teamroots.embers.particle.ParticleUtil;
import teamroots.embers.tileentity.TileEntityHeatCoil;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TileEntityHeatCoilImproved extends TileEntityHeatCoil {
    public static final double EMBER_COST = 1.0;
    public static final double HEATING_SPEED = 1.0;
    public static final double COOLING_SPEED = 1.0;
    public static final double MAX_HEAT = 280;
    public static final int MIN_COOK_TIME = 20;
    public static final int MAX_COOK_TIME = 300;

    private Random random = new Random();
    private int ticksExisted;
    private double heat;

    public double getHeat()
    {
        return heat;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        this.capability.writeToNBT(compound);
        compound.setDouble("heat",heat);
        compound.setTag("inventory",inventory.serializeNBT());
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        this.capability.readFromNBT(compound);
        if(compound.hasKey("heat"))
            heat = compound.getDouble("heat");
        inventory.deserializeNBT(compound.getCompoundTag("inventory"));
    }

    @Override
    public void update() {
        ticksExisted ++;

        List<IUpgradeProvider> upgrades = UpgradeUtil.getUpgradesForMultiblock(world,pos,new EnumFacing[] {EnumFacing.DOWN}); //TODO: Cache both of these calls
        UpgradeUtil.verifyUpgrades(this,upgrades);

        float cost_multiplier = UpgradeUtil.getTotalEmberFuelEfficiency(this,upgrades);
        if (capability.getEmber() >= EMBER_COST * cost_multiplier){
            capability.removeAmount(EMBER_COST * cost_multiplier, true);
            if (ticksExisted % 20 == 0){
                heat += UpgradeUtil.getOtherParameter(this,"heating_speed",HEATING_SPEED,upgrades);
            }
        }
        else {
            if (ticksExisted % 20 == 0){
                heat -= UpgradeUtil.getOtherParameter(this,"cooling_speed",COOLING_SPEED,upgrades);
            }
        }
        double maxHeat = UpgradeUtil.getOtherParameter(this,"max_heat",MAX_HEAT,upgrades);
        heat = MathHelper.clamp(heat,0, maxHeat);

        boolean cancel = UpgradeUtil.doWork(this,upgrades);
        int cookTime = (int)MathHelper.clampedLerp(MIN_COOK_TIME,MAX_COOK_TIME,1.0-(heat / maxHeat));
        if (!cancel && heat > 0 && ticksExisted % cookTime == 0 && !getWorld().isRemote){
            List<EntityItem> items = getWorld().getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(getPos().getX()-1,getPos().getY(),getPos().getZ()-1,getPos().getX()+2,getPos().getY()+2,getPos().getZ()+2));
            for (EntityItem item : items) {
                item.setAgeToCreativeDespawnTime();
                item.lifespan = 10800;
            }
            if (items.size() > 0){
                int i = random.nextInt(items.size());
                EntityItem entityItem = items.get(i);
                RecipeHeatCoil recipe = CraftingRegistry.getHeatCoilRecipe(entityItem.getItem());
                if (recipe != null){
                    ArrayList<ItemStack> returns = Lists.newArrayList(recipe.getResult(world,this, entityItem.getItem()));
                    UpgradeUtil.transformOutput(this,returns,upgrades);
                    int inputCount = recipe.getInputConsumed();
                    depleteItem(entityItem, inputCount);
                    boolean dirty = false;
                    for(ItemStack stack : returns) {
                        ItemStack remainder = inventory.insertItem(0, stack, false);
                        dirty = true;
                        if (remainder != ItemStack.EMPTY)
                            getWorld().spawnEntity(new EntityItem(getWorld(), entityItem.posX, entityItem.posY, entityItem.posZ, remainder));
                    }
                    if(dirty)
                        markDirty();
                }
            }
        }
        if (getWorld().isRemote && heat > 0){
            float particleCount = (1+random.nextInt(2))*(1+(float)Math.sqrt(heat));
            for (int i = 0; i < particleCount; i ++){
                ParticleUtil.spawnParticleGlow(getWorld(), getPos().getX()-0.2f+random.nextFloat()*1.4f, getPos().getY()+1.275f, getPos().getZ()-0.2f+random.nextFloat()*1.4f, 0, 0, 0, 255, 64, 16, 2.0f, 24);
            }
        }
    }

    public void depleteItem(EntityItem entityItem, int inputCount) {
        entityItem.getItem().shrink(inputCount);
        if (entityItem.getItem().isEmpty()) {
            entityItem.setDead();
            for (int j = 0; j < 3; j++) {
                if (random.nextBoolean()) {
                    getWorld().spawnParticle(EnumParticleTypes.SMOKE_NORMAL, entityItem.posX, entityItem.posY, entityItem.posZ, 0, 0, 0, 0);
                } else {
                    getWorld().spawnParticle(EnumParticleTypes.SMOKE_LARGE, entityItem.posX, entityItem.posY, entityItem.posZ, 0, 0, 0, 0);
                }
            }
            getWorld().removeEntity(entityItem);
        }
    }
}
