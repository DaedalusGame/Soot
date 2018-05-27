package soot.tile.overrides;

import com.google.common.collect.Lists;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import soot.Soot;
import soot.SoundEvents;
import soot.recipe.CraftingRegistry;
import soot.recipe.RecipeAlchemyTablet;
import soot.util.AlchemyResult;
import soot.util.AlchemyUtil;
import soot.util.AspectList;
import soot.util.ISoundController;
import teamroots.embers.item.ItemAlchemicWaste;
import teamroots.embers.network.PacketHandler;
import teamroots.embers.network.message.MessageEmberSphereFX;
import teamroots.embers.particle.ParticleUtil;
import teamroots.embers.tileentity.TileEntityAlchemyPedestal;
import teamroots.embers.tileentity.TileEntityAlchemyTablet;

import java.util.List;
import java.util.Random;

public class TileEntityAlchemyTabletImproved extends TileEntityAlchemyTablet implements ISoundController {
    public static final int SOUND_NONE = 0;
    public static final int SOUND_LOOP = 1;
    public float angle;
    private Random random = new Random();
    private AspectList aspects = new AspectList();

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setInteger("progress", this.progress);
        compound.setTag("aspects", this.aspects.serializeNBT());
        compound.setTag("north", this.north.serializeNBT());
        compound.setTag("south", this.south.serializeNBT());
        compound.setTag("east", this.east.serializeNBT());
        compound.setTag("west", this.west.serializeNBT());
        compound.setTag("center", this.center.serializeNBT());
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        this.progress = compound.getInteger("progress");
        this.aspects.deserializeNBT(compound.getCompoundTag("aspects"));
        this.north.deserializeNBT(compound.getCompoundTag("north"));
        this.south.deserializeNBT(compound.getCompoundTag("south"));
        this.east.deserializeNBT(compound.getCompoundTag("east"));
        this.west.deserializeNBT(compound.getCompoundTag("west"));
        this.center.deserializeNBT(compound.getCompoundTag("center"));
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        return !(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && facing == null) && super.hasCapability(capability, facing);
    }

    @Override
    public void sparkProgress() {
        if(progress != 0)
            return;
        RecipeAlchemyTablet recipe = getRecipe();
        if(recipe == null)
            return;
        List<TileEntityAlchemyPedestal> pedestals = AlchemyUtil.getNearbyPedestals(getWorld(),getPos());
        AspectList list = new AspectList();
        list.collect(pedestals);
        AlchemyResult result = recipe.matchAshes(list,world);
        if(result.areAllPresent()) {
            aspects.reset();
            progress = 1;
            markDirty();
            world.playSound(null,pos.getX()+0.5,pos.getY()+0.5,pos.getZ()+0.5,SoundEvents.ALCHEMY_START, SoundCategory.BLOCKS, 1.0f, 1.0f);
            if(world.isRemote)
                Soot.proxy.playMachineSound(this, SOUND_LOOP, SoundEvents.ALCHEMY_LOOP, SoundCategory.BLOCKS, 1.5f, 1.0f, true, (float)pos.getX() + 0.5f, (float)pos.getY() + 0.5f, (float)pos.getZ() + 0.5f);
        }
    }

    @Override
    public int getCurrentSoundType() {
        return progress > 0 ? SOUND_LOOP : SOUND_NONE;
    }

    public RecipeAlchemyTablet getRecipe() {
        return CraftingRegistry.getAlchemyTabletRecipe(center.getStackInSlot(0), Lists.newArrayList(north.getStackInSlot(0),east.getStackInSlot(0),south.getStackInSlot(0),west.getStackInSlot(0)));
    }

    @Override
    public void update() {
        angle += 1.0f;
        if (progress == 1){
            if (process < 20){
                process ++;
            }
            List<TileEntityAlchemyPedestal> pedestals = AlchemyUtil.getNearbyPedestals(getWorld(),getPos());
            if (getWorld().isRemote){
                for (TileEntityAlchemyPedestal pedestal : pedestals) {
                    if(pedestal instanceof TileEntityAlchemyPedestalImproved)
                        ((TileEntityAlchemyPedestalImproved) pedestal).setActive(3);
                    ParticleUtil.spawnParticleStar(getWorld(), pedestal.getPos().getX() + 0.5f, pedestal.getPos().getY() + 1.0f, pedestal.getPos().getZ() + 0.5f, 0.0125f * (random.nextFloat() - 0.5f), 0.0125f * (random.nextFloat() - 0.5f), 0.0125f * (random.nextFloat() - 0.5f), 255, 64, 16, 3.5f + 0.5f * random.nextFloat(), 40);
                    for (int j = 0; j < 8; j++) {
                        float coeff = random.nextFloat();
                        float x = (getPos().getX() + 0.5f) * coeff + (1.0f - coeff) * (pedestal.getPos().getX() + 0.5f);
                        float y = (getPos().getY() + 0.875f) * coeff + (1.0f - coeff) * (pedestal.getPos().getY() + 1.0f);
                        float z = (getPos().getZ() + 0.5f) * coeff + (1.0f - coeff) * (pedestal.getPos().getZ() + 0.5f);
                        ParticleUtil.spawnParticleGlow(getWorld(), x, y, z, 0.0125f * (random.nextFloat() - 0.5f), 0.0125f * (random.nextFloat() - 0.5f), 0.0125f * (random.nextFloat() - 0.5f), 255, 64, 16, 2.0f, 24);
                    }
                }
            }
            if (angle % 10 == 0){
                if (getNearbyAsh(pedestals) > 0){
                    TileEntityAlchemyPedestal pedestal = pedestals.get(random.nextInt(pedestals.size()));
                    while (pedestal.inventory.extractItem(0, 1, true) == ItemStack.EMPTY){
                        pedestal = pedestals.get(random.nextInt(pedestals.size()));
                    }
                    if (pedestal.inventory.getStackInSlot(1) != ItemStack.EMPTY){
                        if (getWorld().isRemote){
                            for (int j = 0; j < 20; j ++){
                                float dx = (getPos().getX()+0.5f) - (pedestal.getPos().getX()+0.5f);
                                float dy = (getPos().getY()+0.875f) - (pedestal.getPos().getY()+1.0f);
                                float dz = (getPos().getZ()+0.5f) - (pedestal.getPos().getZ()+0.5f);
                                float lifetime = random.nextFloat()*24.0f+24.0f;
                                ParticleUtil.spawnParticleStar(getWorld(), pedestal.getPos().getX()+0.5f, pedestal.getPos().getY()+1.0f, pedestal.getPos().getZ()+0.5f, dx/lifetime, dy/lifetime, dz/lifetime, 255, 64, 16, 4.0f, (int)lifetime);
                            }
                        }
                        pedestal.inventory.extractItem(0, 1, false);
                        aspects.addAspect(AlchemyUtil.getAspect(pedestal.inventory.getStackInSlot(1)),1);
                        markDirty();
                        pedestal.markDirty();
                    }
                }
                else {
                    RecipeAlchemyTablet recipe = getRecipe();
                    if (recipe != null && !getWorld().isRemote){
                        ItemStack stack = recipe.getResult(this, aspects);
                        if (!getWorld().isRemote){
                            SoundEvent finishSound = stack.getItem() instanceof ItemAlchemicWaste ? SoundEvents.ALCHEMY_FAIL : SoundEvents.ALCHEMY_SUCCESS;
                            world.playSound(null,pos.getX()+0.5,pos.getY()+0.5,pos.getZ()+0.5, finishSound, SoundCategory.BLOCKS, 1.0f, 1.0f);
                            getWorld().spawnEntity(new EntityItem(getWorld(),getPos().getX()+0.5,getPos().getY()+1.0f,getPos().getZ()+0.5,stack));
                            PacketHandler.INSTANCE.sendToAll(new MessageEmberSphereFX(getPos().getX()+0.5,getPos().getY()+0.875,getPos().getZ()+0.5));
                        }
                        this.progress = 0;
                        this.center.setStackInSlot(0, decrStack(this.center.getStackInSlot(0)));
                        this.north.setStackInSlot(0, decrStack(this.north.getStackInSlot(0)));
                        this.south.setStackInSlot(0, decrStack(this.south.getStackInSlot(0)));
                        this.east.setStackInSlot(0, decrStack(this.east.getStackInSlot(0)));
                        this.west.setStackInSlot(0, decrStack(this.west.getStackInSlot(0)));
                        markDirty();
                    }
                }
            }
        }
        if (progress == 0){
            if (process > 0){
                process --;
            }
        }
    }
}
