package net.wots.unlock;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;

import java.util.*;

/**
 * Persistent per-world storage for which plushie variants each player has unlocked.
 * Keys are the full enum names: "N_PLUSH_SADGE", "UZI_PLUSH_OHNO", etc.
 */
public class VariantUnlockData extends PersistentState {

    private final Map<UUID, Set<String>> playerUnlocks = new HashMap<>();

    // Free variants — always available, never need unlocking
    public static final Set<String> DEFAULT_VARIANTS = Set.of(
        // N defaults
        "N_PLUSH", "N_PLUSH_HAPPY", "N_PLUSH_ANGY", "N_PLUSH_UNAMUSED", "N_PLUSH_WORRIED",
        // Uzi defaults
        "UZI_PLUSH", "UZI_PLUSH_HAPPY", "UZI_PLUSH_ANGY", "UZI_PLUSH_UNAMUSED", "UZI_PLUSH_WORRIED"
    );

    // Locked variants — must be unlocked through gameplay
    public static final Set<String> LOCKED_VARIANTS = Set.of(
        // N locked
        "N_PLUSH_SADGE", "N_PLUSH_TRAUMATIZED", "N_PLUSH_SPOOKED",
        "N_PLUSH_SCAREDAF", "N_PLUSH_DRUNK", "N_PLUSH_ANGYAF", "N_PLUSH_WORRIEDAF",
        // Uzi locked (same set + exclusives)
        "UZI_PLUSH_SADGE", "UZI_PLUSH_TRAUMATIZED", "UZI_PLUSH_SPOOKED",
        "UZI_PLUSH_SCAREDAF", "UZI_PLUSH_DRUNK", "UZI_PLUSH_ANGYAF", "UZI_PLUSH_WORRIEDAF",
        "UZI_PLUSH_OHNO", "UZI_PLUSH_SMIRK"
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
        if (isNew) markDirty();
        return isNew;
    }

    /**
     * Get all unlocked variant keys for a player (for syncing to client).
     */
    public Set<String> getUnlockedVariants(UUID playerUuid) {
        return playerUnlocks.getOrDefault(playerUuid, Collections.emptySet());
    }

    // ==========================================
    //  NBT serialization
    // ==========================================

    @Override
    public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        NbtCompound playersNbt = new NbtCompound();
        for (Map.Entry<UUID, Set<String>> entry : playerUnlocks.entrySet()) {
            NbtList list = new NbtList();
            for (String variant : entry.getValue()) {
                list.add(NbtString.of(variant));
            }
            playersNbt.put(entry.getKey().toString(), list);
        }
        nbt.put("PlayerUnlocks", playersNbt);
        return nbt;
    }

    public static VariantUnlockData fromNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        VariantUnlockData data = new VariantUnlockData();
        NbtCompound playersNbt = nbt.getCompound("PlayerUnlocks");
        for (String uuidStr : playersNbt.getKeys()) {
            try {
                UUID uuid = UUID.fromString(uuidStr);
                NbtList list = playersNbt.getList(uuidStr, 8);
                Set<String> variants = new HashSet<>();
                for (int i = 0; i < list.size(); i++) {
                    variants.add(list.getString(i));
                }
                data.playerUnlocks.put(uuid, variants);
            } catch (IllegalArgumentException ignored) {}
        }
        return data;
    }

    // ==========================================
    //  PersistentState setup
    // ==========================================

    private static final Type<VariantUnlockData> TYPE = new Type<>(
        VariantUnlockData::new,
        VariantUnlockData::fromNbt,
        null
    );

    private static final String DATA_NAME = "wots_variant_unlocks";

    public static VariantUnlockData get(MinecraftServer server) {
        PersistentStateManager manager = server.getOverworld().getPersistentStateManager();
        return manager.getOrCreate(TYPE, DATA_NAME);
    }
}
