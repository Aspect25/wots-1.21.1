package net.wots.unlock;

import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.resources.Identifier;
import net.wots.Wots;

/**
 * Grants advancements directly through Minecraft's AdvancementTracker.
 * No custom criteria needed -- uses minecraft:impossible triggers
 * and grants them programmatically.
 */
public class AdvancementHelper {

    /**
     * Grant an advancement to a player. Minecraft saves this automatically.
     */
    public static void grant(ServerPlayer player, String advId, String criterion) {
        MinecraftServer server = player.level().getServer();
        if (server == null) return;

        Identifier id = Identifier.fromNamespaceAndPath(Wots.MOD_ID, advId);
        AdvancementHolder advancement = server.getAdvancements().get(id);
        if (advancement == null) {
            Wots.LOGGER.warn("Advancement not found: {}", id);
            return;
        }

        player.getAdvancements().award(advancement, criterion);
    }

    /**
     * Check if a player already has an advancement.
     */
    public static boolean hasAdvancement(ServerPlayer player, String advId) {
        MinecraftServer server = player.level().getServer();
        if (server == null) return false;

        Identifier id = Identifier.fromNamespaceAndPath(Wots.MOD_ID, advId);
        AdvancementHolder advancement = server.getAdvancements().get(id);
        if (advancement == null) return false;

        AdvancementProgress progress = player.getAdvancements().getOrStartProgress(advancement);
        return progress.isDone();
    }

    // ==========================================
    //  Variant-specific advancement grants
    // ==========================================

    public static void grantVariantAdvancement(ServerPlayer player, String shortVariant) {
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

        // Check if ALL variants are now unlocked -> grant master advancement
        checkMasterAdvancement(player);
    }

    // ==========================================
    //  Reverse sync: advancements -> variant unlocks
    // ==========================================

    /** Map advancement paths back to their short variant names. */
    private static final java.util.Map<String, String> ADV_TO_SHORT = java.util.Map.of(
            "plushie/sadge",       "SADGE",
            "plushie/traumatized", "TRAUMATIZED",
            "plushie/spooked",     "SPOOKED",
            "plushie/scaredaf",    "SCAREDAF",
            "plushie/drunk",       "DRUNK",
            "plushie/angyaf",      "ANGYAF",
            "plushie/worriedaf",   "WORRIEDAF",
            "plushie/ohno",        "OHNO",
            "plushie/smirk",       "SMIRK"
    );

    /** Characters that can have locked variants. */
    private static final String[] CHARACTERS = { "n", "uzi", "doll", "lizzy" };

    /**
     * Sync advancements -> variant unlock data.
     */
    public static void syncAdvancementsToUnlocks(ServerPlayer player) {
        VariantUnlockData data = VariantUnlockData.get(player.level().getServer());
        boolean changed = false;

        for (var entry : ADV_TO_SHORT.entrySet()) {
            if (hasAdvancement(player, entry.getKey())) {
                String shortVariant = entry.getValue();
                for (String character : CHARACTERS) {
                    String enumName = character.toUpperCase() + "_PLUSH_" + shortVariant;
                    if (VariantUnlockData.LOCKED_VARIANTS.contains(enumName)) {
                        if (data.unlock(player.getUUID(), enumName)) {
                            changed = true;
                        }
                    }
                }
            }
        }

        if (changed) {
            UnlockSyncHelper.syncToClient(player);
        }
    }

    /**
     * Check if the player has unlocked all variants and grant
     * "Every Version of You" if so.
     */
    private static void checkMasterAdvancement(ServerPlayer player) {
        VariantUnlockData data = VariantUnlockData.get(player.level().getServer());
        boolean allUnlocked = true;

        for (String locked : VariantUnlockData.LOCKED_VARIANTS) {
            if (!data.isUnlocked(player.getUUID(), locked)) {
                allUnlocked = false;
                break;
            }
        }

        if (allUnlocked) {
            grant(player, "plushie/every_version", "unlock");
        }
    }
}
