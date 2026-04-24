package net.wots.unlock;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.wots.Wots;
import net.wots.block.ModBlocks;
import net.wots.block.plushies.PlushieVariant;
import net.wots.block.plushies.caineplush.CainePlushVariant;
import net.wots.block.plushies.cynplush.CynPlushVariant;
import net.wots.block.plushies.cynplushmaid.CynPlushMaidVariant;
import net.wots.block.plushies.dollplush.DollPlushVariant;
import net.wots.block.plushies.jplush.JPlushVariant;
import net.wots.block.plushies.lizzyplush.LizzyPlushVariant;
import net.wots.block.plushies.nplush.NPlushVariant;
import net.wots.block.plushies.pomniplush.PomniPlushVariant;
import net.wots.block.plushies.ribbitplush.RibbitPlushVariant;
import net.wots.block.plushies.tadc.kinger.KingerPlushVariant;
import net.wots.block.plushies.tessaplush.TessaPlushVariant;
import net.wots.block.plushies.uziplush.UziPlushVariant;
import net.wots.broadcast.CollectionBroadcast;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Checks unlock conditions and grants variant unlocks.
 *
 * "character" parameter is "n", "uzi", or "doll" (lowercase).
 */
public class VariantUnlockManager {

    private static final int MAX_HIT_ENTRIES = 256;
    @SuppressWarnings("serial")
    private static final Map<Long, int[]> HIT_COUNTS = new LinkedHashMap<>(16, 0.75f, true) {
        @Override protected boolean removeEldestEntry(Map.Entry<Long, int[]> eldest) {
            return size() > MAX_HIT_ENTRIES;
        }
    };

    // ==========================================
    //  PLACEMENT-BASED UNLOCKS
    // ==========================================

    public static void onPlushiePlaced(ServerLevel world, BlockPos pos, ServerPlayer player, String character) {
        checkSpooked(world, pos, player, character);
        checkScaredaf(world, pos, player, character);
        checkWorriedaf(world, pos, player, character);

        // Uzi-specific
        checkSmirk(world, pos, player, character);
        checkOhno(world, pos, player, character);

        // Doll-specific
        checkDollSmirk(world, pos, player, character);
        checkDollOhno(world, pos, player, character);

        // Lizzy-specific
        checkLizzySmirk(world, pos, player, character);
        checkLizzyOhno(world, pos, player, character);
    }

    private static void checkSpooked(ServerLevel world, BlockPos pos, ServerPlayer player, String character) {
        if (world.getMaxLocalRawBrightness(pos) == 0) {
            tryUnlock(player, character, "SPOOKED");
        }
    }

    private static void checkScaredaf(ServerLevel world, BlockPos pos, ServerPlayer player, String character) {
        if (world.dimension() == Level.END) {
            tryUnlock(player, character, "SCAREDAF");
        }
    }

    private static void checkWorriedaf(ServerLevel world, BlockPos pos, ServerPlayer player, String character) {
        if (world.isThundering() && world.canSeeSky(pos)) {
            tryUnlock(player, character, "WORRIEDAF");
        }
    }

    // ── Uzi SMIRK: Uzi placed on top of N ────────────────────────────────────
    private static void checkSmirk(ServerLevel world, BlockPos pos, ServerPlayer player, String character) {
        if (!character.equals("uzi")) return;
        BlockState below = world.getBlockState(pos.below());
        if (below.is(ModBlocks.N_PLUSH)) {
            tryUnlock(player, "uzi", "SMIRK");
        }
    }

    // ── Uzi OHNO: Uzi placed next to Cyn ─────────────────────────────────────
    private static void checkOhno(ServerLevel world, BlockPos pos, ServerPlayer player, String character) {
        if (!character.equals("uzi")) return;
        if (isAdjacentTo(world, pos, ModBlocks.CYN_PLUSH)) {
            tryUnlock(player, "uzi", "OHNO");
        }
    }

    // ── Doll SMIRK: Doll placed next to a Wither Rose ────────────────────────
    private static void checkDollSmirk(ServerLevel world, BlockPos pos, ServerPlayer player, String character) {
        if (!character.equals("doll")) return;
        if (isAdjacentTo(world, pos, Blocks.WITHER_ROSE)) {
            tryUnlock(player, "doll", "SMIRK");
        }
    }

    // ── Doll OHNO: Doll placed next to Cyn (same as Uzi) ─────────────────────
    private static void checkDollOhno(ServerLevel world, BlockPos pos, ServerPlayer player, String character) {
        if (!character.equals("doll")) return;
        if (isAdjacentTo(world, pos, ModBlocks.CYN_PLUSH)) {
            tryUnlock(player, "doll", "OHNO");
        }
    }

    // ── Lizzy SMIRK: Lizzy placed next to a Wither Rose ─────────────────────
    private static void checkLizzySmirk(ServerLevel world, BlockPos pos, ServerPlayer player, String character) {
        if (!character.equals("lizzy")) return;
        if (isAdjacentTo(world, pos, Blocks.WITHER_ROSE)) {
            tryUnlock(player, "lizzy", "SMIRK");
        }
    }

    // ── Lizzy OHNO: Lizzy placed next to Cyn ────────────────────────────────
    private static void checkLizzyOhno(ServerLevel world, BlockPos pos, ServerPlayer player, String character) {
        if (!character.equals("lizzy")) return;
        if (isAdjacentTo(world, pos, ModBlocks.CYN_PLUSH)) {
            tryUnlock(player, "lizzy", "OHNO");
        }
    }

    private static boolean isAdjacentTo(ServerLevel world, BlockPos pos, Block block) {
        BlockPos[] adjacent = { pos.north(), pos.south(), pos.east(), pos.west(), pos.above(), pos.below() };
        for (BlockPos adj : adjacent) {
            if (world.getBlockState(adj).is(block)) return true;
        }
        return false;
    }

    // ==========================================
    //  NEIGHBOR CHANGE
    // ==========================================

    public static void checkNeighborsForUziUnlocks(ServerLevel world, BlockPos placedPos, ServerPlayer player) {
        BlockState placedState = world.getBlockState(placedPos);
        BlockPos[] adjacent = { placedPos.north(), placedPos.south(), placedPos.east(), placedPos.west(), placedPos.above(), placedPos.below() };

        for (BlockPos adj : adjacent) {
            // ── Uzi unlock checks ────────────────────────────────────────
            if (world.getBlockState(adj).is(ModBlocks.UZI_PLUSH)) {
                if (placedState.is(ModBlocks.CYN_PLUSH)) {
                    tryUnlock(player, "uzi", "OHNO");
                }
                if (placedState.is(ModBlocks.N_PLUSH) && adj.equals(placedPos.above())) {
                    tryUnlock(player, "uzi", "SMIRK");
                }
            }

            // ── Doll unlock checks ───────────────────────────────────────
            if (world.getBlockState(adj).is(ModBlocks.DOLL_PLUSH)) {
                if (placedState.is(ModBlocks.CYN_PLUSH)) {
                    tryUnlock(player, "doll", "OHNO");
                }
                // Wither rose placed next to Doll -> SMIRK
                if (placedState.is(Blocks.WITHER_ROSE)) {
                    tryUnlock(player, "doll", "SMIRK");
                }
            }

            // ── Lizzy unlock checks ─────────────────────────────────────
            if (world.getBlockState(adj).is(ModBlocks.LIZZY_PLUSH)) {
                if (placedState.is(ModBlocks.CYN_PLUSH)) {
                    tryUnlock(player, "lizzy", "OHNO");
                }
                if (placedState.is(Blocks.WITHER_ROSE)) {
                    tryUnlock(player, "lizzy", "SMIRK");
                }
            }
        }

        // ── Uzi on top of N (existing) ───────────────────────────────────
        BlockPos above = placedPos.above();
        if (placedState.is(ModBlocks.N_PLUSH) && world.getBlockState(above).is(ModBlocks.UZI_PLUSH)) {
            tryUnlock(player, "uzi", "SMIRK");
        }
    }

    // ==========================================
    //  INTERACTION-BASED UNLOCKS
    // ==========================================

    public static void onPlushieHit(ServerLevel world, BlockPos pos, ServerPlayer player, String character) {
        long key = pos.asLong();
        int currentTick = (int) world.getGameTime();
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

    public static void onPlushieUsed(ServerPlayer player, String character) {
        if (player.hasEffect(MobEffects.NAUSEA)) {
            tryUnlock(player, character, "DRUNK");
        }
    }

    // ==========================================
    //  EVENT-BASED UNLOCKS
    // ==========================================

    public static void onPlushieDroppedInVoid(ServerPlayer thrower, String character) {
        tryUnlock(thrower, character, "SADGE");
    }

    public static void onNearbyExplosion(ServerPlayer nearestPlayer, String character) {
        tryUnlock(nearestPlayer, character, "TRAUMATIZED");
    }

    // ==========================================
    //  Internal
    // ==========================================

    private static String toEnumName(String character, String shortVariant) {
        return character.toUpperCase() + "_PLUSH_" + shortVariant;
    }

    private static void tryUnlock(ServerPlayer player, String character, String shortVariant) {
        String enumName = toEnumName(character, shortVariant);
        VariantUnlockData data = VariantUnlockData.get(player.level().getServer());
        boolean isNew = data.unlock(player.getUUID(), enumName);

        if (isNew) {
            Wots.LOGGER.info("Player {} unlocked variant {}", player.getName().getString(), enumName);

            String displayName = getDisplayName(character, enumName);
            CollectionBroadcast.broadcastUnlock(player, displayName);

            AdvancementHelper.grantVariantAdvancement(player, shortVariant);
            UnlockSyncHelper.syncToClient(player);
        }
    }

    private static String getDisplayName(String character, String enumName) {
        try {
            PlushieVariant variant = resolveVariant(character, enumName);
            if (variant != null) {
                return formatCharacterName(character) + " Plush (" + variant.displayName() + ")";
            }
        } catch (IllegalArgumentException ignored) {}
        return enumName.replace("_", " ");
    }

    private static PlushieVariant resolveVariant(String character, String enumName) {
        return switch (character) {
            case "uzi"      -> UziPlushVariant.valueOf(enumName);
            case "n"        -> NPlushVariant.valueOf(enumName);
            case "doll"     -> DollPlushVariant.valueOf(enumName);
            case "lizzy"    -> LizzyPlushVariant.valueOf(enumName);
            case "j"        -> JPlushVariant.valueOf(enumName);
            case "tessa"    -> TessaPlushVariant.valueOf(enumName);
            case "kinger"   -> KingerPlushVariant.valueOf(enumName);
            case "caine"    -> CainePlushVariant.valueOf(enumName);
            case "pomni"    -> PomniPlushVariant.valueOf(enumName);
            case "ribbit"   -> RibbitPlushVariant.valueOf(enumName);
            case "cyn_maid" -> CynPlushMaidVariant.valueOf(enumName);
            case "cyn"      -> CynPlushVariant.valueOf(enumName);
            default         -> null;
        };
    }

    private static String formatCharacterName(String character) {
        String[] parts = character.split("_");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < parts.length; i++) {
            if (i > 0) sb.append(" ");
            sb.append(parts[i].substring(0, 1).toUpperCase()).append(parts[i].substring(1));
        }
        return sb.toString();
    }
}
