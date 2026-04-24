package net.wots.block.plushies.dollplush;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.wots.block.entity.DollPlushBlockEntity;
import net.wots.block.plushies.AbstractPlushieBlock;
import net.wots.unlock.VariantUnlockManager;

public class DollPlushBlock extends AbstractPlushieBlock<DollPlushVariant> {

    public DollPlushBlock(Properties properties) {
        super(properties, "wots:doll_plush", "doll",
                DollPlushVariant.values(), DollPlushBlockEntity.SOUND_DURATIONS);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return simpleCodec(DollPlushBlock::new);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new DollPlushBlockEntity(pos, state);
    }

    // ── Unlock hooks ─────────────────────────────────────────────────────────────

    @Override
    protected void onPlushieBroken(Level level, BlockPos pos) {
        VariantUnlockManager.clearHitCount(pos);
    }

    @Override
    protected void onPlushiePlaced(Level level, BlockPos pos, LivingEntity placer) {
        if (placer instanceof ServerPlayer player) {
            VariantUnlockManager.onPlushiePlaced((ServerLevel) level, pos, player, "doll");
        }
    }

    @Override
    protected void onPlushieInteracted(Level level, BlockPos pos, Player player) {
        if (!level.isClientSide() && player instanceof ServerPlayer sp) {
            VariantUnlockManager.onPlushieUsed(sp, "doll");
        }
    }

    @Override
    public void attack(BlockState state, Level level, BlockPos pos, Player player) {
        if (!level.isClientSide() && player instanceof ServerPlayer sp) {
            VariantUnlockManager.onPlushieHit((ServerLevel) level, pos, sp, "doll");
        }
        super.attack(state, level, pos, player);
    }
}
