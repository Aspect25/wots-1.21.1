package net.wots.unlock;

import net.minecraft.server.network.ServerPlayerEntity;

import java.util.HashSet;
import java.util.Set;

/**
 * Client-side cache of unlocked variants + hint text for the variant wheel.
 */
public class UnlockSyncHelper {

    // ── Client-side cache ─────────────────────────────────────────────────────
    private static Set<String> clientUnlocks = new HashSet<>();

    /**
     * Check if a variant (by enum name) is unlocked on the client.
     * Used by the variant wheel to grey out locked variants.
     */
    public static boolean isUnlockedClient(String enumName) {
        if (VariantUnlockData.DEFAULT_VARIANTS.contains(enumName)) return true;
        if (!VariantUnlockData.LOCKED_VARIANTS.contains(enumName)) return true;
        return clientUnlocks.contains(enumName);
    }

    /**
     * Called on client when receiving sync packet from server.
     */
    public static void updateClientCache(Set<String> unlocks) {
        clientUnlocks = new HashSet<>(unlocks);
    }

    // ── Server-side sync ──────────────────────────────────────────────────────

    /**
     * Send the player's full unlock set to their client.
     */
    public static void syncToClient(ServerPlayerEntity player) {
        VariantUnlockData data = VariantUnlockData.get(player.getServer());
        Set<String> unlocks = data.getUnlockedVariants(player.getUuid());
        net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.send(
            player, new net.wots.network.SyncVariantUnlocksPayload(unlocks)
        );
    }

    /**
     * Sync on player join. Advancements are handled by Minecraft's own
     * save system now — no re-granting needed.
     */
    public static void onPlayerJoin(ServerPlayerEntity player) {
        syncToClient(player);
    }

    // ── Hint text ─────────────────────────────────────────────────────────────

    /**
     * Get hint text for a locked variant. Shown in the wheel on hover.
     */
    public static String getHintText(String enumName) {
        String shortName;
        if (enumName.startsWith("N_PLUSH_")) {
            shortName = enumName.substring("N_PLUSH_".length());
        } else if (enumName.startsWith("UZI_PLUSH_")) {
            shortName = enumName.substring("UZI_PLUSH_".length());
        } else {
            return "\u00a77???";
        }

        return switch (shortName) {
            case "SADGE"       -> "\u00a77What happens when something\n\u00a77falls where nothing returns?";
            case "TRAUMATIZED" -> "\u00a77Some things can't be unseen.\n\u00a77Especially explosions.";
            case "SPOOKED"     -> "\u00a77Darkness holds more\n\u00a77than you think.";
            case "SCAREDAF"    -> "\u00a77Some places are too alien.\n\u00a77Even for a plushie.";
            case "DRUNK"       -> "\u00a77If you're dizzy, maybe\n\u00a77your plushie is too.";
            case "ANGYAF"      -> "\u00a77Hit something enough times\n\u00a77and it hits back... emotionally.";
            case "WORRIEDAF"   -> "\u00a77Thunder makes everyone nervous.\n\u00a77Even the small ones.";
            case "OHNO"        -> "\u00a77Some neighbors are a\n\u00a77bad influence. Especially\n\u00a77yellow ones.";
            case "SMIRK"       -> "\u00a77Always has to be on top.\n\u00a77Especially over a\n\u00a77certain someone.";
            default            -> "\u00a77???";
        };
    }
}
