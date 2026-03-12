package net.wots.block.plushies;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Implement this on any plushie Block so it works on a PlushieShelfBlock.
 *
 * The shelf calls onShelfInteract() when a player right-clicks the slot
 * holding this plushie. The plushie block handles ALL of its own sound
 * logic (cooldowns, shuffling, etc.) exactly as it already does in onUse().
 *
 * Minimal change to existing plushie blocks — only 2 steps:
 *   1. Add "implements PlushieSoundProvider" to the class declaration
 *   2. Implement onShelfInteract() and copy your onUse() sound logic into it
 *      Use  shelfPos.add(slot, 0, 0)  as the cooldown map key so each
 *      shelf slot gets its own independent cooldown!
 *
 * ── Example for UziPlushBlock ────────────────────────────────────────────────
 *
 *   public class UziPlushBlock extends Block implements PlushieSoundProvider {
 *
 *       // ... all your existing fields and methods stay exactly the same ...
 *
 *       @Override
 *       public void onShelfInteract(World world, BlockPos shelfPos, int slot, PlayerEntity player) {
 *           if (!world.isClient) {
 *               long currentTime = world.getTime();
 *               BlockPos key = shelfPos.add(slot, 0, 0); // unique key per slot!
 *               long cooldownEnd = SOUND_COOLDOWNS.getOrDefault(key, 0L);
 *               if (currentTime < cooldownEnd) return;
 *
 *               if (soundIndex == 0) Collections.shuffle(SOUNDS);
 *               SoundEvent sound = SOUNDS.get(soundIndex);
 *               soundIndex = (soundIndex + 1) % SOUNDS.size();
 *               SOUND_COOLDOWNS.put(key, currentTime + SOUND_DURATIONS_UZI.get(sound));
 *
 *               world.playSound(null, shelfPos, sound, SoundCategory.BLOCKS, 1.0f, 1.0f);
 *           }
 *       }
 *   }
 */
public interface PlushieSoundProvider {

    /**
     * Called by the shelf when a player right-clicks the slot holding this plushie.
     *
     * @param world    the world
     * @param shelfPos position of the shelf block
     * @param slot     which slot (0, 1, 2) — use shelfPos.add(slot,0,0) as cooldown key
     * @param player   the player who clicked
     */
    void onShelfInteract(World world, BlockPos shelfPos, int slot, PlayerEntity player);

}