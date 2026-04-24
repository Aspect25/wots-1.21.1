package net.wots.block.plushies.uziplush;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.wots.block.entity.UziPlushBlockEntity;
import net.wots.block.plushies.AbstractPlushieBlock;
import net.wots.sound.ModSounds;
import net.wots.unlock.VariantUnlockManager;

public class UziPlushBlock extends AbstractPlushieBlock<UziPlushVariant> {

    public UziPlushBlock(Properties properties) {
        super(properties, "wots:uzi_plush", "uzi",
                UziPlushVariant.values(), UziPlushBlockEntity.SOUND_DURATIONS);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return simpleCodec(UziPlushBlock::new);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new UziPlushBlockEntity(pos, state);
    }

    // ── Unlock hooks ─────────────────────────────────────────────────────────────

    @Override
    protected void onPlushieBroken(Level level, BlockPos pos) {
        VariantUnlockManager.clearHitCount(pos);
    }

    @Override
    protected void onPlushiePlaced(Level level, BlockPos pos, LivingEntity placer) {
        if (placer instanceof ServerPlayer player) {
            VariantUnlockManager.onPlushiePlaced((ServerLevel) level, pos, player, "uzi");
            VariantUnlockManager.checkNeighborsForUziUnlocks((ServerLevel) level, pos, player);
        }
    }

    @Override
    protected void onPlushieInteracted(Level level, BlockPos pos, Player player) {
        if (!level.isClientSide() && player instanceof ServerPlayer sp) {
            VariantUnlockManager.onPlushieUsed(sp, "uzi");
        }
    }

    @Override
    public void attack(BlockState state, Level level, BlockPos pos, Player player) {
        if (!level.isClientSide() && player instanceof ServerPlayer sp) {
            VariantUnlockManager.onPlushieHit((ServerLevel) level, pos, sp, "uzi");
        }
        super.attack(state, level, pos, player);
    }

    // ── Redstone burst: warning shot on rising edge ──────────────────────────────

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos,
                               Block sourceBlock, net.minecraft.world.level.redstone.Orientation orientation, boolean notify) {
        super.neighborChanged(state, level, pos, sourceBlock, orientation, notify);
        if (level.isClientSide()) return;
        if (!(level.getBlockEntity(pos) instanceof UziPlushBlockEntity be)) return;

        boolean powered = level.hasNeighborSignal(pos);

        if (powered && !be.wasPowered()) {
            level.playSound(null, pos, ModSounds.UZI_NOISE_7, SoundSource.BLOCKS, 1.0f, 1.0f);

            if (level instanceof ServerLevel sw) {
                sw.sendParticles(
                        ParticleTypes.CRIT,
                        pos.getX() + 0.5, pos.getY() + 0.6, pos.getZ() + 0.5,
                        18, 0.25, 0.25, 0.25, 0.2
                );
            }
        }

        be.setWasPowered(powered);
    }
}
