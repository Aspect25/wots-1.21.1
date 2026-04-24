package net.wots.unlock;

import net.minecraft.server.level.ServerPlayer;

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
    public static void syncToClient(ServerPlayer player) {
        VariantUnlockData data = VariantUnlockData.get(player.level().getServer());
        Set<String> unlocks = data.getUnlockedVariants(player.getUUID());
        net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.send(
            player, new net.wots.network.SyncVariantUnlocksPayload(unlocks)
        );
    }

    /**
     * Sync on player join. Also reverse-syncs advancements → unlocks
     * so /advancement grant commands take effect.
     */
    public static void onPlayerJoin(ServerPlayer player) {
        AdvancementHelper.syncAdvancementsToUnlocks(player);
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
        } else if (enumName.startsWith("DOLL_PLUSH_")) {
            shortName = enumName.substring("DOLL_PLUSH_".length());
        } else if (enumName.startsWith("LIZZY_PLUSH_")) {
            shortName = enumName.substring("LIZZY_PLUSH_".length());
        } else {
            return "\u00a77???";
        }

        // Doll-specific overrides
        if (enumName.startsWith("DOLL_PLUSH_")) {
            return switch (shortName) {
                case "SMIRK" -> "\u00a77Even death has beauty.\n\u00a77Place something withered\n\u00a77beside her.";
                case "OHNO"  -> "\u00a77Some neighbors are a\n\u00a77bad influence. Especially\n\u00a77yellow ones.";
                default -> getDollSharedHint(shortName);
            };
        }

        // Uzi-specific overrides
        if (enumName.startsWith("UZI_PLUSH_")) {
            return switch (shortName) {
                case "SMIRK" -> "\u00a77Always has to be on top.\n\u00a77Especially over a\n\u00a77certain someone.";
                case "OHNO"  -> "\u00a77Some neighbors are a\n\u00a77bad influence. Especially\n\u00a77yellow ones.";
                default -> getSharedHint(shortName);
            };
        }

        // Lizzy-specific overrides
        if (enumName.startsWith("LIZZY_PLUSH_")) {
            return switch (shortName) {
                case "SMIRK" -> "\u00a77Even death has beauty.\n\u00a77Place something withered\n\u00a77beside her.";
                case "OHNO"  -> "\u00a77Some neighbors are a\n\u00a77bad influence. Especially\n\u00a77yellow ones.";
                default -> getSharedHint(shortName);
            };
        }

        // N + fallback
        return getSharedHint(shortName);
    }

    private static String getSharedHint(String shortName) {
        return switch (shortName) {
            case "SADGE"       -> "\u00a77What happens when something\n\u00a77falls where nothing returns?";
            case "TRAUMATIZED" -> "\u00a77Some things can't be unseen.\n\u00a77Especially explosions.";
            case "SPOOKED"     -> "\u00a77Darkness holds more\n\u00a77than you think.";
            case "SCAREDAF"    -> "\u00a77Some places are too alien.\n\u00a77Even for a plushie.";
            case "DRUNK"       -> "\u00a77If you're dizzy, maybe\n\u00a77your plushie is too.";
            case "ANGYAF"      -> "\u00a77Hit something enough times\n\u00a77and it hits back... emotionally.";
            case "WORRIEDAF"   -> "\u00a77Thunder makes everyone nervous.\n\u00a77Even the small ones.";
            default            -> "\u00a77???";
        };
    }

    private static String getDollSharedHint(String shortName) {
        // Doll shares most hints with the others
        return getSharedHint(shortName);
    }
}
