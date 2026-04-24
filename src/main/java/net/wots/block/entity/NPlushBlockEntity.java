package net.wots.block.entity;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.wots.block.ModBlocks;
import net.wots.block.plushies.nplush.NPlushVariant;
import net.wots.sound.ModSounds;

import java.util.Map;

public class NPlushBlockEntity extends AbstractPlushieBlockEntity<NPlushVariant> {

    // ── Aura constants ────────────────────────────────────────────────────────
    private static final double AURA_RADIUS = 8.0;
    private static final int AURA_INTERVAL = 40;
    private static final float AURA_CLEAR_CHANCE = 0.35f;

    public static final Map<SoundEvent, Integer> SOUND_DURATIONS = Map.ofEntries(
            Map.entry(ModSounds.N_NOISE_1,   20),
            Map.entry(ModSounds.N_NOISE_2,   20),
            Map.entry(ModSounds.N_NOISE_3,   20),
            Map.entry(ModSounds.N_NOISE_4,   20),
            Map.entry(ModSounds.N_NOISE_5,   40),
            Map.entry(ModSounds.N_NOISE_6,  120),
            Map.entry(ModSounds.N_NOISE_7,  100),
            Map.entry(ModSounds.N_NOISE_8,   20),
            Map.entry(ModSounds.N_NOISE_9,   20),
            Map.entry(ModSounds.N_NOISE_10,  20),
            Map.entry(ModSounds.N_NOISE_11,  20),
            Map.entry(ModSounds.N_NOISE_12,  20),
            Map.entry(ModSounds.N_NOISE_13,  20),
            Map.entry(ModSounds.N_NOISE_14,  20),
            Map.entry(ModSounds.N_NOISE_15, 100)
    );

    public NPlushBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.N_PLUSH_BLOCK_ENTITY, pos, state,
                NPlushVariant.N_PLUSH, NPlushVariant.class, SOUND_DURATIONS);
    }

    // ── N's aura: confuse nearby hostile mob targeting ────────────────────────
    public static void tick(Level level, BlockPos pos, BlockState state, NPlushBlockEntity be) {
        if (!level.isClientSide() && level.getGameTime() % AURA_INTERVAL == 0) {
            AABB auraBox = new AABB(
                    pos.getX() - AURA_RADIUS, pos.getY() - AURA_RADIUS, pos.getZ() - AURA_RADIUS,
                    pos.getX() + AURA_RADIUS, pos.getY() + AURA_RADIUS, pos.getZ() + AURA_RADIUS);
            level.getEntitiesOfClass(Mob.class, auraBox, e -> e instanceof Monster)
                    .forEach(mob -> {
                        if (mob.getTarget() != null && level.getRandom().nextFloat() < AURA_CLEAR_CHANCE) {
                            mob.setTarget(null);
                        }
                    });
        }
    }
}
