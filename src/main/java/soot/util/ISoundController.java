package soot.util;

public interface ISoundController {
    default int getCurrentSoundType() {
        return 0;
    }

    default float getCurrentVolume(float volume) {
        return volume;
    }

    default float getCurrentPitch(float pitch) {
        return pitch;
    }
}
