package net.wots.block.entity;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.HolderLookup;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundStopSoundPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import net.wots.block.ModBlocks;
import net.wots.block.plushies.uziplush.UziPlushVariant;
import net.wots.sound.ModSounds;
import net.wots.util.ShuffledSoundQueue;

import java.util.*;

/**
 * Uzi plushie block entity -- extends the base with per-variant sound maps
 * and redstone state tracking.
 */
public class UziPlushBlockEntity extends AbstractPlushieBlockEntity<UziPlushVariant> {

    // ── Redstone state ────────────────────────────────────────────────────────
    private boolean wasPowered = false;

    public boolean wasPowered() { return wasPowered; }

    public void setWasPowered(boolean powered) {
        if (this.wasPowered != powered) {
            this.wasPowered = powered;
            setChanged();
        }
    }

    // ── Sound durations per variant ───────────────────────────────────────────

    /** Default expression -- 10 voice lines. */
    public static final Map<SoundEvent, Integer> DEFAULT_SOUNDS = Map.ofEntries(
            Map.entry(ModSounds.UZI_PLUSH_DEFAULT_1,  115),
            Map.entry(ModSounds.UZI_PLUSH_DEFAULT_2,  175),
            Map.entry(ModSounds.UZI_PLUSH_DEFAULT_3,  165),
            Map.entry(ModSounds.UZI_PLUSH_DEFAULT_4,  108),
            Map.entry(ModSounds.UZI_PLUSH_DEFAULT_5,  125),
            Map.entry(ModSounds.UZI_PLUSH_DEFAULT_6,  190),
            Map.entry(ModSounds.UZI_PLUSH_DEFAULT_7,  166),
            Map.entry(ModSounds.UZI_PLUSH_DEFAULT_8,  119),
            Map.entry(ModSounds.UZI_PLUSH_DEFAULT_9,  190),
            Map.entry(ModSounds.UZI_PLUSH_DEFAULT_10, 138)
    );

    /** Maps every variant to its sound-duration table. */
    public static final Map<UziPlushVariant, Map<SoundEvent, Integer>> VARIANT_SOUND_MAP;

    static {
        Map<UziPlushVariant, Map<SoundEvent, Integer>> m = new EnumMap<>(UziPlushVariant.class);

        m.put(UziPlushVariant.UZI_PLUSH, DEFAULT_SOUNDS);

        m.put(UziPlushVariant.UZI_PLUSH_SADGE, Map.of(
                ModSounds.UZI_PLUSH_SADGE_1, 164,
                ModSounds.UZI_PLUSH_SADGE_2, 192,
                ModSounds.UZI_PLUSH_SADGE_3, 152
        ));

        m.put(UziPlushVariant.UZI_PLUSH_SCAREDAF, Map.of(
                ModSounds.UZI_PLUSH_SCAREDAF_1, 151,
                ModSounds.UZI_PLUSH_SCAREDAF_2, 141,
                ModSounds.UZI_PLUSH_SCAREDAF_3, 152
        ));

        m.put(UziPlushVariant.UZI_PLUSH_SPOOKED, Map.of(
                ModSounds.UZI_PLUSH_SPOOKED_1, 125,
                ModSounds.UZI_PLUSH_SPOOKED_2,  95,
                ModSounds.UZI_PLUSH_SPOOKED_3, 150
        ));

        m.put(UziPlushVariant.UZI_PLUSH_TRAUMATIZED, Map.of(
                ModSounds.UZI_PLUSH_TRAUMATIZED_1, 132,
                ModSounds.UZI_PLUSH_TRAUMATIZED_2, 159,
                ModSounds.UZI_PLUSH_TRAUMATIZED_3,  94
        ));

        m.put(UziPlushVariant.UZI_PLUSH_UNAMUSED, Map.of(
                ModSounds.UZI_PLUSH_UNAMUSED_1, 144,
                ModSounds.UZI_PLUSH_UNAMUSED_2, 117,
                ModSounds.UZI_PLUSH_UNAMUSED_3, 120
        ));

        m.put(UziPlushVariant.UZI_PLUSH_ANGY, Map.of(
                ModSounds.UZI_PLUSH_ANGY_1, 142,
                ModSounds.UZI_PLUSH_ANGY_2, 168,
                ModSounds.UZI_PLUSH_ANGY_3, 143
        ));

        m.put(UziPlushVariant.UZI_PLUSH_ANGYAF, Map.of(
                ModSounds.UZI_PLUSH_ANGYAF_1,  92,
                ModSounds.UZI_PLUSH_ANGYAF_2, 126,
                ModSounds.UZI_PLUSH_ANGYAF_3, 108
        ));

        m.put(UziPlushVariant.UZI_PLUSH_DRUNK, Map.of(
                ModSounds.UZI_PLUSH_DRUNK_1, 279,
                ModSounds.UZI_PLUSH_DRUNK_2, 267,
                ModSounds.UZI_PLUSH_DRUNK_3, 315,
                ModSounds.UZI_PLUSH_DRUNK_4, 277,
                ModSounds.UZI_PLUSH_DRUNK_5, 250
        ));

        m.put(UziPlushVariant.UZI_PLUSH_HAPPY, Map.of(
                ModSounds.UZI_PLUSH_HAPPY_1, 130,
                ModSounds.UZI_PLUSH_HAPPY_2, 124,
                ModSounds.UZI_PLUSH_HAPPY_3, 152
        ));

        m.put(UziPlushVariant.UZI_PLUSH_WORRIEDAF, Map.of(
                ModSounds.UZI_PLUSH_WORRIEDAF_1, 135,
                ModSounds.UZI_PLUSH_WORRIEDAF_2, 140,
                ModSounds.UZI_PLUSH_WORRIEDAF_3, 119
        ));

        m.put(UziPlushVariant.UZI_PLUSH_WORRIED, Map.of(
                ModSounds.UZI_PLUSH_WORRIED_1, 139,
                ModSounds.UZI_PLUSH_WORRIED_2, 130,
                ModSounds.UZI_PLUSH_WORRIED_3, 126
        ));

        m.put(UziPlushVariant.UZI_PLUSH_OHNO, Map.of(
                ModSounds.UZI_PLUSH_OHNO_1, 161,
                ModSounds.UZI_PLUSH_OHNO_2, 164,
                ModSounds.UZI_PLUSH_OHNO_3,  62
        ));

        m.put(UziPlushVariant.UZI_PLUSH_SMIRK, Map.of(
                ModSounds.UZI_PLUSH_SMIRK_1, 115,
                ModSounds.UZI_PLUSH_SMIRK_2, 136,
                ModSounds.UZI_PLUSH_SMIRK_3, 134
        ));

        VARIANT_SOUND_MAP = Collections.unmodifiableMap(m);
    }

    /** Shelf interactions use the default variant queue when variant is unknown. */
    public static final Map<SoundEvent, Integer> SOUND_DURATIONS = DEFAULT_SOUNDS;

    // ── Per-variant sound queues (lazily created) ─────────────────────────────
    private final Map<UziPlushVariant, ShuffledSoundQueue> variantQueues = new EnumMap<>(UziPlushVariant.class);
    private SoundEvent lastPlayedSound = null;

    public UziPlushBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.UZI_PLUSH_BLOCK_ENTITY, pos, state,
                UziPlushVariant.UZI_PLUSH, UziPlushVariant.class, DEFAULT_SOUNDS);
    }

    /** Returns (or creates) the shuffled queue for the given variant. */
    private ShuffledSoundQueue queueFor(UziPlushVariant v) {
        return variantQueues.computeIfAbsent(v, k -> {
            Map<SoundEvent, Integer> durations = VARIANT_SOUND_MAP.get(k);
            if (durations == null) durations = DEFAULT_SOUNDS;
            return new ShuffledSoundQueue(durations);
        });
    }

    @Override
    public void playNextSound() {
        if (level == null || level.isClientSide()) return;
        ShuffledSoundQueue queue = queueFor(getVariant());
        SoundEvent sound = queue.tryAdvance(level.getGameTime());
        if (sound != null) {
            lastPlayedSound = sound;
            level.playSound(null, getBlockPos(), sound, SoundSource.BLOCKS, 1.0f, 1.0f);
        }
    }

    @Override
    public void stopSound() {
        if (level == null || level.isClientSide()) return;
        SoundEvent current = queueFor(getVariant()).getCurrentlyPlaying();
        if (current == null) current = lastPlayedSound;
        if (current == null) return;

        ClientboundStopSoundPacket packet =
                new ClientboundStopSoundPacket(current.location(), SoundSource.BLOCKS);
        ((ServerLevel) level).getPlayers(
                player -> player.distanceToSqr(Vec3.atCenterOf(getBlockPos())) < 64 * 64
        ).forEach(player -> player.connection.send(packet));
        queueFor(getVariant()).clearCurrent();
        lastPlayedSound = null;
    }

    // ── NBT (extends base with wasPowered) ────────────────────────────────────

    @Override
    protected void saveAdditional(net.minecraft.world.level.storage.ValueOutput output) {
        super.saveAdditional(output);
        output.putBoolean("WasPowered", wasPowered);
    }

    @Override
    protected void loadAdditional(net.minecraft.world.level.storage.ValueInput input) {
        super.loadAdditional(input);
        wasPowered = input.getBooleanOr("WasPowered", false);
    }
}
