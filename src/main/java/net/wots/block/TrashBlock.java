package net.wots.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.wots.block.entity.TrashBlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TrashBlock extends BlockWithEntity {
    public static final MapCodec<TrashBlock> CODEC = createCodec(TrashBlock::new);
    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }
    public TrashBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new TrashBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return validateTicker(type, ModBlocks.TRASH_BLOCK_ENTITY, TrashBlockEntity::tick);
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!world.isClient) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof TrashBlockEntity trashEntity) {
                ItemStack handStack = player.getStackInHand(Hand.MAIN_HAND);
                // If holding item, try to insert it
                if (!handStack.isEmpty()) {
                    if (trashEntity.getStack(0).isEmpty()) {
                        trashEntity.setStack(0, handStack.copy());
                        player.setStackInHand(Hand.MAIN_HAND, ItemStack.EMPTY);
                        trashEntity.markDirty();
                        return ActionResult.SUCCESS;
                    } else if (ItemStack.areItemsAndComponentsEqual(trashEntity.getStack(0), handStack)) {
                        int maxInsert = Math.min(handStack.getMaxCount() - trashEntity.getStack(0).getCount(), handStack.getCount());
                        if (maxInsert > 0) {
                            trashEntity.getStack(0).increment(maxInsert);
                            handStack.decrement(maxInsert);
                            trashEntity.markDirty();
                            return ActionResult.SUCCESS;
                        }
                    }
                } else if (!player.isSneaking()) {
                    // Try to extract item if empty hand
                    if (!trashEntity.getStack(0).isEmpty()) {
                        player.setStackInHand(Hand.MAIN_HAND, trashEntity.getStack(0).copy());
                        trashEntity.setStack(0, ItemStack.EMPTY);
                        trashEntity.markDirty();
                        return ActionResult.SUCCESS;
                    }
                }
            }
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof TrashBlockEntity) {
                ItemScatterer.spawn(world, pos, (TrashBlockEntity) blockEntity);
                world.updateComparators(pos, this);
            }
            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }


    @Override
    public void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType options) {
        tooltip.add(Text.translatable("block.wots.trash_block.tooltip1").formatted(Formatting.GRAY));
        tooltip.add(Text.translatable("block.wots.trash_block.tooltip2").formatted(Formatting.GRAY));
        tooltip.add(Text.translatable("block.wots.trash_block.tooltip3").formatted(Formatting.GRAY, Formatting.BOLD));
        tooltip.add(Text.translatable("block.wots.trash_block.tooltip4").formatted(Formatting.GRAY, Formatting.BOLD));
    }
}
