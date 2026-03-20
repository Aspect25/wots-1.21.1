package net.wots.item.accessory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvent;

/**
 * Implement this on any plushie accessory Item (hat or back slot).
 * The key handler calls {@link #playNextPlushieSound} when the keybind fires.
 *
 * The implementation mirrors UziPlushBlockEntity's shuffled-queue logic so
 * each accessory slot has its own independent sound state.
 */
public interface PlushieSoundAccessory {

    /**
     * Play the next sound in this accessory's sequence.
     * Called server-side, so use {@code world.playSound(null, …)}.
     *
     * @param player the player currently wearing this accessory
     */
    void playNextPlushieSound(PlayerEntity player);
}