package soot.util;

import net.minecraft.client.audio.PositionedSound;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;

public class GlobalSound extends PositionedSound {
    public GlobalSound(SoundEvent soundIn, SoundCategory categoryIn, float volume, float pitch) {
        super(soundIn, categoryIn);
        this.volume = volume;
        this.pitch = pitch;
        this.attenuationType = AttenuationType.NONE;
    }
}
