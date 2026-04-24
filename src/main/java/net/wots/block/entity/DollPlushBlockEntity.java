package net.wots.block.entity;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.protocol.game.ClientboundStopSoundPacket;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.BlockPos;
import net.wots.block.ModBlocks;
import net.wots.block.plushies.dollplush.DollPlushVariant;
import net.wots.client.SubtitleSender;
import net.wots.sound.DollSubtitles;
import net.wots.sound.ModSounds;

import java.util.Map;

/**
 * Doll plushie block entity -- extends the base with variant-specific
 * single voice lines and cinematic subtitles.
 */
public class DollPlushBlockEntity extends AbstractPlushieBlockEntity<DollPlushVariant> {

    // ── Sound: base/default queue ─────────────────────────────────────────────
    public static final Map<SoundEvent, Integer> SOUND_DURATIONS = Map.ofEntries(
            Map.entry(ModSounds.DOLL_DEFAULT_1, 20),
            Map.entry(ModSounds.DOLL_DEFAULT_2, 60),
            Map.entry(ModSounds.DOLL_DEFAULT_3, 60)
    );

    // ── Sound: variant-specific mapping ───────────────────────────────────────
    private static final Map<DollPlushVariant, SoundEvent> VARIANT_SOUNDS = Map.ofEntries(
            Map.entry(DollPlushVariant.DOLL_PLUSH_HAPPY,       ModSounds.DOLL_HAPPY_1),
            Map.entry(DollPlushVariant.DOLL_PLUSH_ANGY,        ModSounds.DOLL_ANGY_1),
            Map.entry(DollPlushVariant.DOLL_PLUSH_UNAMUSED,    ModSounds.DOLL_UNAMUSED_1),
            Map.entry(DollPlushVariant.DOLL_PLUSH_WORRIED,     ModSounds.DOLL_WORRIED_1),
            Map.entry(DollPlushVariant.DOLL_PLUSH_SADGE,       ModSounds.DOLL_SADGE_1),
            Map.entry(DollPlushVariant.DOLL_PLUSH_TRAUMATIZED, ModSounds.DOLL_TRAUMATIZED_1),
            Map.entry(DollPlushVariant.DOLL_PLUSH_SPOOKED,     ModSounds.DOLL_SPOOKED_1),
            Map.entry(DollPlushVariant.DOLL_PLUSH_SCAREDAF,    ModSounds.DOLL_SCAREDAF_1),
            Map.entry(DollPlushVariant.DOLL_PLUSH_DRUNK,       ModSounds.DOLL_DRUNK_1),
            Map.entry(DollPlushVariant.DOLL_PLUSH_ANGYAF,      ModSounds.DOLL_ANGYAF_1),
            Map.entry(DollPlushVariant.DOLL_PLUSH_WORRIEDAF,   ModSounds.DOLL_WORRIEDAF_1),
            Map.entry(DollPlushVariant.DOLL_PLUSH_OHNO,        ModSounds.DOLL_OHNO_1),
            Map.entry(DollPlushVariant.DOLL_PLUSH_SMIRK,       ModSounds.DOLL_SMIRK_1)
    );

    private static final Map<DollPlushVariant, Integer> VARIANT_COOLDOWNS = Map.ofEntries(
            Map.entry(DollPlushVariant.DOLL_PLUSH_HAPPY,       40),
            Map.entry(DollPlushVariant.DOLL_PLUSH_ANGY,        30),
            Map.entry(DollPlushVariant.DOLL_PLUSH_UNAMUSED,    40),
            Map.entry(DollPlushVariant.DOLL_PLUSH_WORRIED,     50),
            Map.entry(DollPlushVariant.DOLL_PLUSH_SADGE,       50),
            Map.entry(DollPlushVariant.DOLL_PLUSH_TRAUMATIZED, 60),
            Map.entry(DollPlushVariant.DOLL_PLUSH_SPOOKED,     50),
            Map.entry(DollPlushVariant.DOLL_PLUSH_SCAREDAF,    40),
            Map.entry(DollPlushVariant.DOLL_PLUSH_DRUNK,       80),
            Map.entry(DollPlushVariant.DOLL_PLUSH_ANGYAF,      50),
            Map.entry(DollPlushVariant.DOLL_PLUSH_WORRIEDAF,   60),
            Map.entry(DollPlushVariant.DOLL_PLUSH_OHNO,        70),
            Map.entry(DollPlushVariant.DOLL_PLUSH_SMIRK,       50)
    );

    private long lastVariantSoundTick = -1;
    private SoundEvent lastPlayedSound = null;

    public DollPlushBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.DOLL_PLUSH_BLOCK_ENTITY, pos, state,
                DollPlushVariant.DOLL_PLUSH, DollPlushVariant.class, SOUND_DURATIONS);
    }

    @Override
    public void playNextSound() {
        if (level == null || level.isClientSide()) return;

        SoundEvent sound;
        DollPlushVariant variant = getVariant();

        SoundEvent variantSound = VARIANT_SOUNDS.get(variant);
        if (variantSound != null) {
            long now = level.getGameTime();
            int cooldown = VARIANT_COOLDOWNS.getOrDefault(variant, 40);
            if (now - lastVariantSoundTick < cooldown) return;
            sound = variantSound;
            lastVariantSoundTick = now;
            lastPlayedSound = sound;
        } else {
            sound = getSoundQueue().tryAdvance(level.getGameTime());
            if (sound == null) return;
            lastPlayedSound = sound;
        }

        level.playSound(null, getBlockPos(), sound, SoundSource.BLOCKS, 1.0f, 1.0f);

        String subtitle = DollSubtitles.get(sound);
        if (subtitle != null) {
            SubtitleSender.sendNearby(
                    (ServerLevel) level, getBlockPos(),
                    "Doll", subtitle, SubtitleSender.COLOR_DOLL, 0
            );
        }
    }

    @Override
    public void stopSound() {
        if (level == null || level.isClientSide()) return;
        SoundEvent current = getSoundQueue().getCurrentlyPlaying();
        if (current == null) current = lastPlayedSound;
        if (current == null) return;
        ClientboundStopSoundPacket packet =
                new ClientboundStopSoundPacket(current.location(), SoundSource.BLOCKS);
        ((ServerLevel) level).getPlayers(
                player -> player.distanceToSqr(Vec3.atCenterOf(getBlockPos())) < 64 * 64
        ).forEach(player -> player.connection.send(packet));
        getSoundQueue().clearCurrent();
        lastPlayedSound = null;
    }
}
