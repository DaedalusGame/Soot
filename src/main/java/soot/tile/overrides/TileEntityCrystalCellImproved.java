package soot.tile.overrides;

import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import soot.Soot;
import soot.SoundEvents;
import soot.util.ISoundController;
import teamroots.embers.particle.ParticleUtil;
import teamroots.embers.tileentity.TileEntityCrystalCell;
import teamroots.embers.util.EmberGenUtil;

import java.util.Random;

public class TileEntityCrystalCellImproved extends TileEntityCrystalCell implements ISoundController {
    public static final int SOUND_IDLE = 0;
    Random random = new Random();

    @Override
    public int getCurrentSoundType() {
        return SOUND_IDLE;
    }

    @Override
    public float getCurrentVolume(float volume) {
        return (float) (this.capability.getEmberCapacity() / 1440000);
    }

    @Override
    public float getCurrentPitch(float pitch) {
        return pitch;//(float) (1.5 - this.capability.getEmberCapacity() / 1440000);
    }

    @Override
    public void onLoad() {
        if(world.isRemote)
            Soot.proxy.playMachineSound(this, SOUND_IDLE, SoundEvents.CRYSTAL_CELL, SoundCategory.BLOCKS, 1.0f, 1.0f, true, (float)pos.getX() + 0.5f, (float)pos.getY() + 2.5f, (float)pos.getZ() + 0.5f);
    }

    @Override
    public void update() {
        ticksExisted ++;
        if (inventory.getStackInSlot(0) != ItemStack.EMPTY && ticksExisted % 4 == 0){
            ItemStack stack = inventory.extractItem(0, 1, true);
            if (!getWorld().isRemote && stack != ItemStack.EMPTY){
                inventory.extractItem(0, 1, false);
                if (EmberGenUtil.getEmberForItem(stack.getItem()) > 0){
                    this.capability.setEmberCapacity(Math.min(11440000, this.capability.getEmberCapacity()+EmberGenUtil.getEmberForItem(stack.getItem())*10));
                    markDirty();
                }
            }
            if (getWorld().isRemote && stack != ItemStack.EMPTY){
                double angle = random.nextDouble()*2.0*Math.PI;
                double x = getPos().getX()+0.5+0.5*Math.sin(angle);
                double z = getPos().getZ()+0.5+0.5*Math.cos(angle);
                double x2 = getPos().getX()+0.5;
                double z2 = getPos().getZ()+0.5;
                float layerHeight = 0.25f;
                float numLayers = 2+(float) Math.floor(capability.getEmberCapacity()/128000.0f);
                float height = layerHeight*numLayers;
                for (float i = 0; i < 72; i ++){
                    float coeff = i/72.0f;
                    ParticleUtil.spawnParticleGlow(getWorld(), (float)x*(1.0f-coeff)+(float)x2*coeff, getPos().getY()+(1.0f-coeff)+(height/2.0f+1.5f)*coeff, (float)z*(1.0f-coeff)+(float)z2*coeff, 0, 0, 0, 255, 64, 16, 2.0f, 24);
                }
            }
        }
        float numLayers = 2+(float) Math.floor(capability.getEmberCapacity()/128000.0f);
        for (int i = 0; i < numLayers; i ++){
            float layerHeight = 0.25f;
            float height = layerHeight*numLayers;
            float xDest = getPos().getX()+0.5f;
            float yDest = getPos().getY()+height/2.0f+1.5f;
            float zDest = getPos().getZ()+0.5f;
            float x = getPos().getX()+0.5f+2.0f*(random.nextFloat()-0.5f);
            float z = getPos().getZ()+0.5f+2.0f*(random.nextFloat()-0.5f);
            float y = getPos().getY()+1.0f;
            if (getWorld().isRemote){
                ParticleUtil.spawnParticleGlow(getWorld(), x, y, z, (xDest-x)/24.0f * random.nextFloat(), (yDest-y)/24.0f * random.nextFloat(), (zDest-z)/24.0f * random.nextFloat(), 255, 64, 16, 2.0f, Math.max(24,(int)(yDest-y)*10));
            }
        }
    }
}
