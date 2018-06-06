package soot.util;

import net.minecraft.client.audio.ITickableSound;
import net.minecraft.client.audio.PositionedSound;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import soot.Registry;

public class InspirationSound extends PositionedSound implements ITickableSound {
    EntityPlayer boundPlayer;

    public InspirationSound(EntityPlayer player, SoundEvent soundIn, SoundCategory categoryIn, float volume, float pitch) {
        super(soundIn, categoryIn);
        this.boundPlayer = player;
        this.volume = volume;
        this.pitch = pitch;
        this.repeat = true;
        this.attenuationType = AttenuationType.NONE;
    }

    boolean isDonePlaying;

    @Override
    public boolean isDonePlaying() {
        return isDonePlaying;
    }

    @Override
    public void update() {
        if (boundPlayer == null || !boundPlayer.isPotionActive(Registry.POTION_INSPIRATION)) {
            isDonePlaying = true;
        }
    }
}
