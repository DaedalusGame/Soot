package soot.util;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;

public class ParallelMachineSound extends MachineSound {
    public ParallelMachineSound(TileEntity tile, int type, SoundEvent soundIn, SoundCategory categoryIn, float volumeIn, float pitchIn, boolean repeatIn, float xIn, float yIn, float zIn) {
        super(tile, type, soundIn, categoryIn, volumeIn, pitchIn, repeatIn, xIn, yIn, zIn);
    }

    @Override
    public void update() {
        if(boundTile == null || boundTile.isInvalid())
            donePlaying = true;
        else if(boundTile instanceof ISoundController) {
            ISoundController controller = (ISoundController) boundTile;
            int currentSound = controller.getCurrentSoundType();
            if(currentSound == 0)
                donePlaying = true;
            if(currentSound != id)
                volume = 0;
            else
                volume = controller.getCurrentVolume(volume);
            pitch = controller.getCurrentPitch(pitch);
        }
    }
}
