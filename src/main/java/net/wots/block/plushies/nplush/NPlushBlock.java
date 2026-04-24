package net.wots.block.plushies.nplush;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.wots.block.ModBlocks;
import net.wots.block.entity.NPlushBlockEntity;
import net.wots.block.plushies.AbstractPlushieBlock;
import net.wots.unlock.VariantUnlockManager;

public class NPlushBlock extends AbstractPlushieBlock<NPlushVariant> {

    public NPlushBlock(Properties properties) {
        super(properties, "wots:n_plush", "n",
                NPlushVariant.values(), NPlushBlockEntity.SOUND_DURATIONS);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return simpleCodec(NPlushBlock::new);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new NPlushBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide()) return null;
        if (type != ModBlocks.N_PLUSH_BLOCK_ENTITY) return null;
        //noinspection unchecked
        return (BlockEntityTicker<T>) (BlockEntityTicker<NPlushBlockEntity>) NPlushBlockEntity::tick;
    }

    // ── Unlock hooks ─────────────────────────────────────────────────────────────

    @Override
    protected void onPlushieBroken(Level level, BlockPos pos) {
        VariantUnlockManager.clearHitCount(pos);
    }

    @Override
    protected void onPlushiePlaced(Level level, BlockPos pos, LivingEntity placer) {
        if (placer instanceof ServerPlayer player) {
            VariantUnlockManager.onPlushiePlaced((ServerLevel) level, pos, player, "n");
            VariantUnlockManager.checkNeighborsForUziUnlocks((ServerLevel) level, pos, player);
        }
    }

    @Override
    protected void onPlushieInteracted(Level level, BlockPos pos, Player player) {
        if (!level.isClientSide() && player instanceof ServerPlayer sp) {
            VariantUnlockManager.onPlushieUsed(sp, "n");
        }
    }

    @Override
    public void attack(BlockState state, Level level, BlockPos pos, Player player) {
        if (!level.isClientSide() && player instanceof ServerPlayer sp) {
            VariantUnlockManager.onPlushieHit((ServerLevel) level, pos, sp, "n");
        }
        super.attack(state, level, pos, player);
    }
}
