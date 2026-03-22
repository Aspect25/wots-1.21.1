package net.wots.util;

import net.minecraft.sound.SoundEvent;
import java.util.*;

/**
 * Encapsulates the shuffled-sound-with-cooldown pattern used by all plushies.
 * Replaces ~40 lines of duplicated code in each class that plays sounds.
 */
public class ShuffledSoundQueue {

    private final List<SoundEvent> sounds;
    private final Map<SoundEvent, Integer> durations;
    private int index = 0;
    private SoundEvent lastPlayed = null;
    private SoundEvent currentlyPlaying = null;
    private long cooldownEnd = 0L;

    public ShuffledSoundQueue(Map<SoundEvent, Integer> soundDurations) {
        this.durations = soundDurations;
        this.sounds = new ArrayList<>(soundDurations.keySet());
        Collections.shuffle(sounds);
    }

    /** Returns the next sound to play, or null if still on cooldown. */
    public SoundEvent tryAdvance(long currentTime) {
        if (currentTime < cooldownEnd) return null;

        if (index == 0) {
            Collections.shuffle(sounds);
            if (sounds.size() > 1 && sounds.get(0).equals(lastPlayed)) {
                Collections.swap(sounds, 0, 1);
            }
        }

        SoundEvent sound = sounds.get(index);
        lastPlayed = sound;
        currentlyPlaying = sound;
        index = (index + 1) % sounds.size();
        cooldownEnd = currentTime + durations.get(sound);
        return sound;
    }

    /** The sound that is currently playing (for stop-sound packets). */
    public SoundEvent getCurrentlyPlaying() { return currentlyPlaying; }

    /** Clear the currently-playing reference after stopping. */
    public void clearCurrent() {
        currentlyPlaying = null;
        cooldownEnd = 0L;
    }
}
