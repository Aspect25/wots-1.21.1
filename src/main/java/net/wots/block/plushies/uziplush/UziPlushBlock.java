package net.wots.block.plushies.uziplush;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.wots.block.ModBlocks;
import net.wots.block.entity.PlushieShelfBlockEntity;
import net.wots.block.entity.UziPlushBlockEntity;
import net.wots.block.plushies.PlushieSoundProvider;
import net.wots.unlock.VariantUnlockManager;

import java.util.*;
import net.wots.util.ShuffledSoundQueue;
import net.wots.util.VoxelShapeHelper;

public class UziPlushBlock extends BlockWithEntity implements PlushieSoundProvider {

    public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;

    private static final VoxelShape SHAPE_NORTH = makeShape();
    private static final VoxelShape SHAPE_SOUTH = VoxelShapeHelper.rotateShape(SHAPE_NORTH, 2);
    private static final VoxelShape SHAPE_EAST  = VoxelShapeHelper.rotateShape(SHAPE_NORTH, 1);
    private static final VoxelShape SHAPE_WEST  = VoxelShapeHelper.rotateShape(SHAPE_NORTH, 3);

    private static VoxelShape makeShape() {
        return VoxelShapes.cuboid(0.1875, 0, 0.25, 0.8125, 1, 0.875);
    }

    private record SlotKey(long pos, int slot) {}
    private static final Map<SlotKey, ShuffledSoundQueue> SHELF_QUEUES = new HashMap<>();

    public UziPlushBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH));
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return createCodec(UziPlushBlock::new);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new UziPlushBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        if (world.isClient) return null;
        if (type != ModBlocks.UZI_PLUSH_BLOCK_ENTITY) return null;
        //noinspection unchecked
        return (BlockEntityTicker<T>) (BlockEntityTicker<UziPlushBlockEntity>) UziPlushBlockEntity::tick;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext ctx) {
        return switch (state.get(FACING)) {
            case SOUTH -> SHAPE_SOUTH;
            case EAST  -> SHAPE_EAST;
            case WEST  -> SHAPE_WEST;
            default    -> SHAPE_NORTH;
        };
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext ctx) {
        return getOutlineShape(state, world, pos, ctx);
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.isOf(newState.getBlock()) && world.getBlockEntity(pos) instanceof UziPlushBlockEntity be) {
            be.stopSound();
            VariantUnlockManager.clearHitCount(pos);
            if (!world.isClient) {
                ItemStack stack = new ItemStack(state.getBlock().asItem());
                NbtCompound nbt = new NbtCompound();
                nbt.putString("id", "wots:uzi_plush");
                nbt.putString("Variant", be.getVariant().name());
                nbt.putBoolean("LazyMode", be.isLazyMode());
                stack.set(DataComponentTypes.BLOCK_ENTITY_DATA, NbtComponent.of(nbt));
                Block.dropStack(world, pos, stack);
            }
        }
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state,
                         LivingEntity placer, ItemStack stack) {
        super.onPlaced(world, pos, state, placer, stack);
        if (world.isClient) return;
        if (!(world.getBlockEntity(pos) instanceof UziPlushBlockEntity be)) return;

        var beData = stack.get(DataComponentTypes.BLOCK_ENTITY_DATA);
        if (beData != null) {
            NbtCompound nbt = beData.copyNbt();
            if (nbt.contains("Variant")) {
                try { be.setVariant(UziPlushVariant.valueOf(nbt.getString("Variant"))); }
                catch (IllegalArgumentException ignored) {}
            }
            if (nbt.contains("LazyMode")) {
                be.setLazyMode(nbt.getBoolean("LazyMode"));
            }
        }

        // ── Unlock checks on placement ───────────────────────────────────
        if (placer instanceof ServerPlayerEntity player) {
            VariantUnlockManager.onPlushiePlaced((ServerWorld) world, pos, player, "uzi");
            VariantUnlockManager.checkNeighborsForUziUnlocks((ServerWorld) world, pos, player);
        }
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos,
                                 PlayerEntity player, BlockHitResult hit) {
        // ── Drunk unlock check ───────────────────────────────────────────
        if (!world.isClient && player instanceof ServerPlayerEntity sp) {
            VariantUnlockManager.onPlushieUsed(sp, "uzi");
        }

        if (player.isSneaking()) {
            if (world.isClient) {
                net.minecraft.client.MinecraftClient.getInstance()
                        .setScreen(new net.wots.client.screen.UziVariantWheelScreen(pos));
            }
            return ActionResult.SUCCESS;
        }
        onShelfInteract(world, pos, 0, player);
        return ActionResult.SUCCESS;
    }

    // ── Angyaf unlock — hit detection ─────────────────────────────────────
    @Override
    public void onBlockBreakStart(BlockState state, World world, BlockPos pos, PlayerEntity player) {
        if (!world.isClient && player instanceof ServerPlayerEntity sp) {
            VariantUnlockManager.onPlushieHit((ServerWorld) world, pos, sp, "uzi");
        }
        super.onBlockBreakStart(state, world, pos, player);
    }

    // ── PlushieSoundProvider ──────────────────────────────────────────────────

    @Override
    public void onShelfInteract(World world, BlockPos shelfPos, int slot, PlayerEntity player) {
        var be = world.getBlockEntity(shelfPos);

        if (be instanceof UziPlushBlockEntity ube) {
            if (!world.isClient) {
                ube.playNextSound();
            }
            if (world.isClient) {
                ube.triggerAnim("controller", "bounce");
            }
            return;
        }

        if (be instanceof PlushieShelfBlockEntity shelf) {
            if (world.isClient) return;
            SlotKey key = new SlotKey(shelfPos.asLong(), slot);
            ShuffledSoundQueue queue = SHELF_QUEUES.computeIfAbsent(key,
                    k -> new ShuffledSoundQueue(UziPlushBlockEntity.SOUND_DURATIONS));
            SoundEvent sound = queue.tryAdvance(world.getTime());
            if (sound == null) return;
            world.playSound(null, shelfPos, sound, SoundCategory.BLOCKS, 1.0f, 1.0f);
            shelf.triggerBounce(slot);
        }
    }
}