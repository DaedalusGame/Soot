package soot.tile;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
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

    @Override
    public void update() {
        ticksExisted ++;

        List<IUpgradeProvider> upgrades = UpgradeUtil.getUpgradesForMultiblock(world,pos,new EnumFacing[] {EnumFacing.DOWN}); //TODO: Cache both of these calls
        UpgradeUtil.verifyUpgrades(this,upgrades);

        float cost_multiplier = UpgradeUtil.getTotalEmberFuelEfficiency(this,upgrades);
        if (capability.getEmber() >= EMBER_COST * cost_multiplier){
            capability.removeAmount(EMBER_COST * cost_multiplier, true);
            if (ticksExisted % 20 == 0){
                heat += HEATING_SPEED;
            }
        }
        else {
            if (ticksExisted % 20 == 0){
                heat -= COOLING_SPEED;
            }
        }
        heat = MathHelper.clamp(heat,0,MAX_HEAT);

        boolean cancel = UpgradeUtil.doWork(this,upgrades);
        int cookTime = (int)MathHelper.clampedLerp(MIN_COOK_TIME,MAX_COOK_TIME,1.0-(heat / MAX_HEAT));
        if (!cancel && heat > 0 && ticksExisted % cookTime == 0 && !getWorld().isRemote){
            List<EntityItem> items = getWorld().getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(getPos().getX()-1,getPos().getY(),getPos().getZ()-1,getPos().getX()+2,getPos().getY()+2,getPos().getZ()+2));
            for (int i = 0; i < items.size(); i ++){
                items.get(i).setAgeToCreativeDespawnTime();
                items.get(i).lifespan = 10800;
            }
            if (items.size() > 0){
                int i = random.nextInt(items.size());
                ItemStack itemToSmelt = items.get(i).getItem();
                RecipeHeatCoil recipe = CraftingRegistry.getHeatCoilRecipe(itemToSmelt);
                if (recipe != null){
                    ItemStack stack = recipe.getResult(world,this,itemToSmelt);
                    ItemStack remainder = inventory.insertItem(0, stack, false);
                    itemToSmelt.shrink(1);
                    if (itemToSmelt.getCount() == 0){
                        items.get(i).setDead();
                        for (int j = 0; j < 3; j ++){
                            if (random.nextBoolean()){
                                getWorld().spawnParticle(EnumParticleTypes.SMOKE_NORMAL, items.get(i).posX, items.get(i).posY, items.get(i).posZ, 0, 0, 0, 0);
                            }
                            else {
                                getWorld().spawnParticle(EnumParticleTypes.SMOKE_LARGE, items.get(i).posX, items.get(i).posY, items.get(i).posZ, 0, 0, 0, 0);
                            }
                        }
                        getWorld().removeEntity(items.get(i));
                    }
                    markDirty();
                    if (remainder != ItemStack.EMPTY){
                        getWorld().spawnEntity(new EntityItem(getWorld(),items.get(i).posX,items.get(i).posY,items.get(i).posZ,remainder));
                    }
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
}
