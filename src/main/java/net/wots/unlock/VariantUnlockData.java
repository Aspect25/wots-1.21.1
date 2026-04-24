package net.wots.unlock;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

import java.util.*;

/**
 * Persistent per-world storage for which plushie variants each player has unlocked.
 * Keys are the full enum names: "N_PLUSH_SADGE", "UZI_PLUSH_OHNO", etc.
 */
public class VariantUnlockData extends SavedData {

    private final Map<UUID, Set<String>> playerUnlocks = new HashMap<>();

    // Free variants -- always available, never need unlocking
    public static final Set<String> DEFAULT_VARIANTS = Set.of(
        // N defaults
        "N_PLUSH", "N_PLUSH_HAPPY", "N_PLUSH_ANGY", "N_PLUSH_UNAMUSED", "N_PLUSH_WORRIED",
        // Uzi defaults
        "UZI_PLUSH", "UZI_PLUSH_HAPPY", "UZI_PLUSH_ANGY", "UZI_PLUSH_UNAMUSED", "UZI_PLUSH_WORRIED",
        // Doll defaults
        "DOLL_PLUSH", "DOLL_PLUSH_HAPPY", "DOLL_PLUSH_ANGY", "DOLL_PLUSH_UNAMUSED", "DOLL_PLUSH_WORRIED",
        // Kinger (TADC) -- base only for now
        "KINGER_PLUSH",
        // Tessa (MD) -- base only for now
        "TESSA_PLUSH",
        // J (MD) -- base only for now
        "J_PLUSH",
        // Lizzy defaults
        "LIZZY_PLUSH", "LIZZY_PLUSH_ANGY"
    );

    // Locked variants -- must be unlocked through gameplay
    public static final Set<String> LOCKED_VARIANTS = Set.of(
        // N locked
        "N_PLUSH_SADGE", "N_PLUSH_TRAUMATIZED", "N_PLUSH_SPOOKED",
        "N_PLUSH_SCAREDAF", "N_PLUSH_DRUNK", "N_PLUSH_ANGYAF", "N_PLUSH_WORRIEDAF",
        // Uzi locked
        "UZI_PLUSH_SADGE", "UZI_PLUSH_TRAUMATIZED", "UZI_PLUSH_SPOOKED",
        "UZI_PLUSH_SCAREDAF", "UZI_PLUSH_DRUNK", "UZI_PLUSH_ANGYAF", "UZI_PLUSH_WORRIEDAF",
        "UZI_PLUSH_OHNO", "UZI_PLUSH_SMIRK",
        // Doll locked
        "DOLL_PLUSH_SADGE", "DOLL_PLUSH_TRAUMATIZED", "DOLL_PLUSH_SPOOKED",
        "DOLL_PLUSH_SCAREDAF", "DOLL_PLUSH_DRUNK", "DOLL_PLUSH_ANGYAF", "DOLL_PLUSH_WORRIEDAF",
        "DOLL_PLUSH_OHNO", "DOLL_PLUSH_SMIRK",
        // Lizzy locked
        "LIZZY_PLUSH_ANGYAF", "LIZZY_PLUSH_DRUNK", "LIZZY_PLUSH_OHNO",
        "LIZZY_PLUSH_SADGE", "LIZZY_PLUSH_SCAREDAF", "LIZZY_PLUSH_SMIRK"
    );

    /**
     * Check if a variant enum name is available for a player.
     */
    public boolean isUnlocked(UUID playerUuid, String enumName) {
        if (DEFAULT_VARIANTS.contains(enumName)) return true;
        if (!LOCKED_VARIANTS.contains(enumName)) return true; // unknown = free
        Set<String> unlocks = playerUnlocks.get(playerUuid);
        return unlocks != null && unlocks.contains(enumName);
    }

    /**
     * Unlock a variant. Returns true if this was a NEW unlock.
     */
    public boolean unlock(UUID playerUuid, String enumName) {
        if (DEFAULT_VARIANTS.contains(enumName)) return false;
        if (!LOCKED_VARIANTS.contains(enumName)) return false;
        Set<String> unlocks = playerUnlocks.computeIfAbsent(playerUuid, k -> new HashSet<>());
        boolean isNew = unlocks.add(enumName);
        if (isNew) setDirty();
        return isNew;
    }

    /**
     * Get all unlocked variant keys for a player (for syncing to client).
     */
    public Set<String> getUnlockedVariants(UUID playerUuid) {
        return playerUnlocks.getOrDefault(playerUuid, Collections.emptySet());
    }

    // ==========================================
    //  Codec-based serialization (MC 26.1)
    // ==========================================

    /** Codec for a single player's unlock entry. */
    private static final Codec<Map.Entry<UUID, List<String>>> ENTRY_CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    Codec.STRING.fieldOf("uuid").forGetter(e -> e.getKey().toString()),
                    Codec.STRING.listOf().fieldOf("variants").forGetter(Map.Entry::getValue)
            ).apply(instance, (uuidStr, variants) ->
                    Map.entry(UUID.fromString(uuidStr), variants)));

    /** Codec for the full data. */
    public static final Codec<VariantUnlockData> CODEC =
            ENTRY_CODEC.listOf().fieldOf("players").codec().xmap(
                    entries -> {
                        VariantUnlockData data = new VariantUnlockData();
                        for (var entry : entries) {
                            data.playerUnlocks.put(entry.getKey(), new HashSet<>(entry.getValue()));
                        }
                        return data;
                    },
                    data -> {
                        List<Map.Entry<UUID, List<String>>> entries = new ArrayList<>();
                        for (var entry : data.playerUnlocks.entrySet()) {
                            entries.add(Map.entry(entry.getKey(), new ArrayList<>(entry.getValue())));
                        }
                        return entries;
                    }
            );

    // ==========================================
    //  SavedData setup
    // ==========================================

    private static final SavedDataType<VariantUnlockData> TYPE = new SavedDataType<>(
        Identifier.fromNamespaceAndPath("wots", "variant_unlocks"),
        VariantUnlockData::new,
        CODEC,
        DataFixTypes.SAVED_DATA_MAP_DATA
    );

    public static VariantUnlockData get(MinecraftServer server) {
        return server.overworld().getDataStorage().computeIfAbsent(TYPE);
    }
}
