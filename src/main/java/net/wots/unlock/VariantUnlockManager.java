package net.wots.unlock;

import net.minecraft.block.BlockState;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.wots.Wots;
import net.wots.block.ModBlocks;

import java.util.HashMap;
import java.util.Map;

/**
 * Checks unlock conditions and grants variant unlocks.
 *
 * "character" parameter is always "n" or "uzi" (lowercase).
 * Internally converts to enum names like "N_PLUSH_SADGE", "UZI_PLUSH_OHNO".
 */
public class VariantUnlockManager {

    // ── Hit counter for ANGYAF unlock ─────────────────────────────────────────
    private static final Map<Long, int[]> HIT_COUNTS = new HashMap<>();

    // ==========================================
    //  PLACEMENT-BASED UNLOCKS
    // ==========================================

    public static void onPlushiePlaced(ServerWorld world, BlockPos pos, ServerPlayerEntity player, String character) {
        checkSpooked(world, pos, player, character);
        checkScaredaf(world, pos, player, character);
        checkWorriedaf(world, pos, player, character);
        checkSmirk(world, pos, player, character);
        checkOhno(world, pos, player, character);
    }

    private static void checkSpooked(ServerWorld world, BlockPos pos, ServerPlayerEntity player, String character) {
        if (world.getLightLevel(pos) == 0) {
            tryUnlock(player, character, "SPOOKED");
        }
    }

    private static void checkScaredaf(ServerWorld world, BlockPos pos, ServerPlayerEntity player, String character) {
        if (world.getRegistryKey() == World.END) {
            tryUnlock(player, character, "SCAREDAF");
        }
    }

    private static void checkWorriedaf(ServerWorld world, BlockPos pos, ServerPlayerEntity player, String character) {
        if (world.isThundering() && world.isSkyVisible(pos)) {
            tryUnlock(player, character, "WORRIEDAF");
        }
    }

    private static void checkSmirk(ServerWorld world, BlockPos pos, ServerPlayerEntity player, String character) {
        if (!character.equals("uzi")) return;
        BlockState below = world.getBlockState(pos.down());
        if (below.isOf(ModBlocks.N_PLUSH)) {
            tryUnlock(player, "uzi", "SMIRK");
        }
    }

    private static void checkOhno(ServerWorld world, BlockPos pos, ServerPlayerEntity player, String character) {
        if (!character.equals("uzi")) return;
        BlockPos[] adjacent = { pos.north(), pos.south(), pos.east(), pos.west(), pos.up(), pos.down() };
        for (BlockPos adj : adjacent) {
            if (world.getBlockState(adj).isOf(ModBlocks.CYN_PLUSH)) {
                tryUnlock(player, "uzi", "OHNO");
                return;
            }
        }
    }

    // ==========================================
    //  NEIGHBOR CHANGE
    // ==========================================

    public static void checkNeighborsForUziUnlocks(ServerWorld world, BlockPos placedPos, ServerPlayerEntity player) {
        BlockState placedState = world.getBlockState(placedPos);
        BlockPos[] adjacent = { placedPos.north(), placedPos.south(), placedPos.east(), placedPos.west(), placedPos.up(), placedPos.down() };

        for (BlockPos adj : adjacent) {
            if (!world.getBlockState(adj).isOf(ModBlocks.UZI_PLUSH)) continue;
            if (placedState.isOf(ModBlocks.CYN_PLUSH)) {
                tryUnlock(player, "uzi", "OHNO");
            }
            if (placedState.isOf(ModBlocks.N_PLUSH) && adj.equals(placedPos.up())) {
                tryUnlock(player, "uzi", "SMIRK");
            }
        }
        BlockPos above = placedPos.up();
        if (placedState.isOf(ModBlocks.N_PLUSH) && world.getBlockState(above).isOf(ModBlocks.UZI_PLUSH)) {
            tryUnlock(player, "uzi", "SMIRK");
        }
    }

    // ==========================================
    //  INTERACTION-BASED UNLOCKS
    // ==========================================

    public static void onPlushieHit(ServerWorld world, BlockPos pos, ServerPlayerEntity player, String character) {
        long key = pos.asLong();
        int currentTick = (int) world.getTime();
        int[] data = HIT_COUNTS.computeIfAbsent(key, k -> new int[]{0, 0});

        if (currentTick - data[1] > 100) {
            data[0] = 0;
        }
        data[0]++;
        data[1] = currentTick;

        if (data[0] >= 10) {
            tryUnlock(player, character, "ANGYAF");
            data[0] = 0;
        }
    }

    public static void clearHitCount(BlockPos pos) {
        HIT_COUNTS.remove(pos.asLong());
    }

    public static void onPlushieUsed(ServerPlayerEntity player, String character) {
        if (player.hasStatusEffect(StatusEffects.NAUSEA)) {
            tryUnlock(player, character, "DRUNK");
        }
    }

    // ==========================================
    //  EVENT-BASED UNLOCKS
    // ==========================================

    public static void onPlushieDroppedInVoid(ServerPlayerEntity thrower, String character) {
        tryUnlock(thrower, character, "SADGE");
    }

    public static void onNearbyExplosion(ServerPlayerEntity nearestPlayer, String character) {
        tryUnlock(nearestPlayer, character, "TRAUMATIZED");
    }

    // ==========================================
    //  Internal
    // ==========================================

    private static String toEnumName(String character, String shortVariant) {
        return character.toUpperCase() + "_PLUSH_" + shortVariant;
    }

    private static void tryUnlock(ServerPlayerEntity player, String character, String shortVariant) {
        String enumName = toEnumName(character, shortVariant);
        VariantUnlockData data = VariantUnlockData.get(player.getServer());
        boolean isNew = data.unlock(player.getUuid(), enumName);

        if (isNew) {
            Wots.LOGGER.info("Player {} unlocked variant {}", player.getName().getString(), enumName);

            // Grant advancement directly via Minecraft's tracker (persists automatically)
            AdvancementHelper.grantVariantAdvancement(player, shortVariant);

            // Sync unlock data to client for the variant wheel UI
            UnlockSyncHelper.syncToClient(player);
        }
    }
}
