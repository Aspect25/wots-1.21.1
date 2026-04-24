package net.wots.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.ChatFormatting;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.Containers;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.wots.block.entity.TrashBlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TrashBlock extends BaseEntityBlock {
    public static final MapCodec<TrashBlock> CODEC = simpleCodec(TrashBlock::new);
    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }
    public TrashBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TrashBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, ModBlocks.TRASH_BLOCK_ENTITY, TrashBlockEntity::tick);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!level.isClientSide()) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof TrashBlockEntity trashEntity) {
                ItemStack handStack = player.getItemInHand(InteractionHand.MAIN_HAND);
                // If holding item, try to insert it
                if (!handStack.isEmpty()) {
                    if (trashEntity.getItem(0).isEmpty()) {
                        trashEntity.setItem(0, handStack.copy());
                        player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
                        trashEntity.setChanged();
                        return InteractionResult.SUCCESS;
                    } else if (ItemStack.isSameItemSameComponents(trashEntity.getItem(0), handStack)) {
                        int maxInsert = Math.min(handStack.getMaxStackSize() - trashEntity.getItem(0).getCount(), handStack.getCount());
                        if (maxInsert > 0) {
                            trashEntity.getItem(0).grow(maxInsert);
                            handStack.shrink(maxInsert);
                            trashEntity.setChanged();
                            return InteractionResult.SUCCESS;
                        }
                    }
                } else if (!player.isShiftKeyDown()) {
                    // Try to extract item if empty hand
                    if (!trashEntity.getItem(0).isEmpty()) {
                        player.setItemInHand(InteractionHand.MAIN_HAND, trashEntity.getItem(0).copy());
                        trashEntity.setItem(0, ItemStack.EMPTY);
                        trashEntity.setChanged();
                        return InteractionResult.SUCCESS;
                    }
                }
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    protected void affectNeighborsAfterRemoval(BlockState state, net.minecraft.server.level.ServerLevel level, BlockPos pos, boolean moved) {
        super.affectNeighborsAfterRemoval(state, level, pos, moved);
    }


    // appendHoverText moved to BlockItem/Item in MC 26.1; handled in TrashBlockItem instead
}
