package net.wots.block.entity;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.network.protocol.game.ClientboundStopSoundPacket;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.core.BlockPos;
import net.wots.block.ModBlocks;
import net.wots.block.plushies.uziplush.UziPlushVariant;
import net.wots.util.ShuffledSoundQueue;

import java.util.EnumMap;
import java.util.Map;

/**
 * Huge Uzi block entity -- shares all variants and per-variant voice lines
 * with the regular Uzi plush.
 */
public class UziHugeBlockEntity extends AbstractPlushieBlockEntity<UziPlushVariant> {

    private final EnumMap<UziPlushVariant, ShuffledSoundQueue> variantQueues =
            new EnumMap<>(UziPlushVariant.class);

    public UziHugeBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.UZI_HUGE_BLOCK_ENTITY, pos, state,
                UziPlushVariant.UZI_PLUSH, UziPlushVariant.class,
                UziPlushBlockEntity.DEFAULT_SOUNDS);
    }

    private ShuffledSoundQueue queueForCurrentVariant() {
        return variantQueues.computeIfAbsent(getVariant(), v -> {
            Map<SoundEvent, Integer> durations = UziPlushBlockEntity.VARIANT_SOUND_MAP.get(v);
            if (durations != null && !durations.isEmpty()) {
                return new ShuffledSoundQueue(durations);
            }
            return new ShuffledSoundQueue(UziPlushBlockEntity.DEFAULT_SOUNDS);
        });
    }

    @Override
    public void playNextSound() {
        if (level == null || level.isClientSide()) return;
        ShuffledSoundQueue queue = queueForCurrentVariant();
        SoundEvent sound = queue.tryAdvance(level.getGameTime());
        if (sound == null) return;
        level.playSound(null, getBlockPos(), sound, SoundSource.BLOCKS, 1.0f, 1.0f);
    }

    @Override
    public void stopSound() {
        if (level == null || level.isClientSide()) return;
        ShuffledSoundQueue queue = queueForCurrentVariant();
        SoundEvent current = queue.getCurrentlyPlaying();
        if (current != null) {
            ClientboundStopSoundPacket packet = new ClientboundStopSoundPacket(
                    BuiltInRegistries.SOUND_EVENT.getKey(current), SoundSource.BLOCKS);
            for (var player : level.getServer().getPlayerList().getPlayers()) {
                if (player.distanceToSqr(getBlockPos().getX(), getBlockPos().getY(), getBlockPos().getZ()) < 64 * 64) {
                    player.connection.send(packet);
                }
            }
            queue.clearCurrent();
        }
    }
}
