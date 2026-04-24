package net.wots.block.plushies.cynplush;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.wots.block.ModBlocks;
import net.wots.block.entity.CynPlushBlockEntity;
import net.wots.block.plushies.AbstractPlushieBlock;
import net.wots.unlock.VariantUnlockManager;

public class CynPlushBlock extends AbstractPlushieBlock<CynPlushVariant> {

    public CynPlushBlock(Properties properties) {
        super(properties, "wots:cyn_plush", "cyn",
                CynPlushVariant.values(), CynPlushBlockEntity.SOUND_DURATIONS);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return simpleCodec(CynPlushBlock::new);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CynPlushBlockEntity(pos, state);
    }

    // ── Ticker: drives the solver particle system ────────────────────────────────

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide()) return null;
        if (type != ModBlocks.CYN_PLUSH_BLOCK_ENTITY) return null;
        //noinspection unchecked
        return (BlockEntityTicker<T>) (BlockEntityTicker<CynPlushBlockEntity>) CynPlushBlockEntity::tick;
    }

    // ── Overrides: CynPlushBlockEntity doesn't extend AbstractPlushieBlockEntity ─

    @Override
    protected void affectNeighborsAfterRemoval(BlockState state, net.minecraft.server.level.ServerLevel level, BlockPos pos, boolean moved) {
        // Note: in MC 26.1, affectNeighborsAfterRemoval no longer receives newState.
        // Block entity may already be gone. Variant-preserving drops should use loot tables.
        super.affectNeighborsAfterRemoval(state, level, pos, moved);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state,
                         LivingEntity placer, ItemStack stack) {
        if (level.isClientSide()) return;
        if (level.getBlockEntity(pos) instanceof CynPlushBlockEntity be) {
            var beData = stack.get(DataComponents.CUSTOM_DATA);
            if (beData != null) {
                CompoundTag nbt = beData.copyTag();
                if (nbt.contains("Variant")) {
                    try { be.setVariant(CynPlushVariant.valueOf(nbt.getStringOr("Variant", ""))); }
                    catch (IllegalArgumentException ignored) {}
                }
            }
        }
        if (placer instanceof ServerPlayer player) {
            VariantUnlockManager.checkNeighborsForUziUnlocks((ServerLevel) level, pos, player);
        }
    }

    // ── No variant wheel -- just play sound ───────────────────────────────────────

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                 Player player, BlockHitResult hit) {
        onShelfInteract(level, pos, 0, player);
        return InteractionResult.SUCCESS;
    }

    @Override
    public void onShelfInteract(Level level, BlockPos shelfPos, int slot, Player player) {
        if (!level.isClientSide() && level.getBlockEntity(shelfPos) instanceof CynPlushBlockEntity be) {
            be.playNextSound();
        }
        if (level.isClientSide() && level.getBlockEntity(shelfPos) instanceof CynPlushBlockEntity be) {
            be.triggerAnim("controller", "bounce");
        }
    }
}
