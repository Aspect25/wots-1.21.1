package net.wots.item.accessory;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * Implement this on any plushie accessory Item (hat or back slot).
 * The key handler calls {@link #playNextPlushieSound} when the keybind fires.
 */
public interface PlushieSoundAccessory {

    /**
     * Play the next sound in this accessory's sequence.
     * Called server-side, so use {@code level.playSound(null, …)}.
     *
     * @param player the player currently wearing this accessory
     * @param stack  the worn ItemStack (used to read variant NBT)
     */
    void playNextPlushieSound(Player player, ItemStack stack);
}
