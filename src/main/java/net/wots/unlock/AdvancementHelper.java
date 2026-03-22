package net.wots.unlock;

import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.wots.Wots;

/**
 * Grants advancements directly through Minecraft's AdvancementTracker.
 * No custom criteria needed — uses minecraft:impossible triggers
 * and grants them programmatically.
 *
 * Minecraft handles persistence of advancements automatically via
 * world/advancements/<uuid>.json — no re-granting on join needed.
 */
public class AdvancementHelper {

    /**
     * Grant an advancement to a player. Minecraft saves this automatically.
     *
     * @param player    The server player
     * @param advId     Advancement path like "plushie/sadge"
     * @param criterion The criterion key in the JSON (typically "unlock")
     */
    public static void grant(ServerPlayerEntity player, String advId, String criterion) {
        MinecraftServer server = player.getServer();
        if (server == null) return;

        Identifier id = Identifier.of(Wots.MOD_ID, advId);
        AdvancementEntry advancement = server.getAdvancementLoader().get(id);
        if (advancement == null) {
            Wots.LOGGER.warn("Advancement not found: {}", id);
            return;
        }

        player.getAdvancementTracker().grantCriterion(advancement, criterion);
    }

    /**
     * Check if a player already has an advancement.
     */
    public static boolean hasAdvancement(ServerPlayerEntity player, String advId) {
        MinecraftServer server = player.getServer();
        if (server == null) return false;

        Identifier id = Identifier.of(Wots.MOD_ID, advId);
        AdvancementEntry advancement = server.getAdvancementLoader().get(id);
        if (advancement == null) return false;

        AdvancementProgress progress = player.getAdvancementTracker().getProgress(advancement);
        return progress.isDone();
    }

    // ==========================================
    //  Variant-specific advancement grants
    // ==========================================

    /**
     * Grant the advancement for a specific variant unlock.
     * Maps variant short name to advancement path.
     */
    public static void grantVariantAdvancement(ServerPlayerEntity player, String shortVariant) {
        String advPath = switch (shortVariant) {
            case "SADGE"       -> "plushie/sadge";
            case "TRAUMATIZED" -> "plushie/traumatized";
            case "SPOOKED"     -> "plushie/spooked";
            case "SCAREDAF"    -> "plushie/scaredaf";
            case "DRUNK"       -> "plushie/drunk";
            case "ANGYAF"      -> "plushie/angyaf";
            case "WORRIEDAF"   -> "plushie/worriedaf";
            case "OHNO"        -> "plushie/ohno";
            case "SMIRK"       -> "plushie/smirk";
            default            -> null;
        };

        if (advPath != null) {
            grant(player, advPath, "unlock");
        }

        // Check if ALL variants are now unlocked → grant master advancement
        checkMasterAdvancement(player);
    }

    /**
     * Check if the player has unlocked all variants and grant
     * "Every Version of You" if so.
     */
    private static void checkMasterAdvancement(ServerPlayerEntity player) {
        VariantUnlockData data = VariantUnlockData.get(player.getServer());
        boolean allUnlocked = true;

        for (String locked : VariantUnlockData.LOCKED_VARIANTS) {
            if (!data.isUnlocked(player.getUuid(), locked)) {
                allUnlocked = false;
                break;
            }
        }

        if (allUnlocked) {
            grant(player, "plushie/every_version", "unlock");
        }
    }
}
