package net.wots.block.plushies.uziplush;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.function.BooleanBiFunction;
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

import java.util.*;

public class UziPlushBlock extends BlockWithEntity implements PlushieSoundProvider {

    public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;

    private static final VoxelShape SHAPE_NORTH = makeShape();
    private static final VoxelShape SHAPE_SOUTH = rotateShape(SHAPE_NORTH, 2);
    private static final VoxelShape SHAPE_EAST  = rotateShape(SHAPE_NORTH, 1);
    private static final VoxelShape SHAPE_WEST  = rotateShape(SHAPE_NORTH, 3);

    private static VoxelShape rotateShape(VoxelShape shape, int times) {
        VoxelShape[] buffer = {VoxelShapes.empty()};
        shape.forEachBox((minX, minY, minZ, maxX, maxY, maxZ) -> {
            double x1 = minX, z1 = minZ, x2 = maxX, z2 = maxZ;
            for (int i = 0; i < times; i++) {
                double newX1 = 1 - z2, newZ1 = x1, newX2 = 1 - z1, newZ2 = x2;
                x1 = newX1; z1 = newZ1; x2 = newX2; z2 = newZ2;
            }
            buffer[0] = VoxelShapes.combine(buffer[0],
                    VoxelShapes.cuboid(x1, minY, z1, x2, maxY, z2),
                    BooleanBiFunction.OR);
        });
        return buffer[0];
    }

    private static VoxelShape makeShape() {
        return VoxelShapes.cuboid(0.1875, 0, 0.25, 0.8125, 1, 0.875);
    }

    // ── Per-shelf-slot sound state ────────────────────────────────────────────
    // Keyed by (blockPos.asLong(), slot) so every shelf slot is independent.
    // Server-side only, not persisted — sound state resets on restart.

    private record SlotKey(long pos, int slot) {}

    private static final Map<SlotKey, Long>             SHELF_COOLDOWNS   = new HashMap<>();
    private static final Map<SlotKey, Integer>          SHELF_SOUND_IDX   = new HashMap<>();
    private static final Map<SlotKey, SoundEvent>       SHELF_LAST_PLAYED = new HashMap<>();
    private static final Map<SlotKey, List<SoundEvent>> SHELF_SOUND_LISTS = new HashMap<>();

    // ── Constructor ───────────────────────────────────────────────────────────

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
        }
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos,
                                 PlayerEntity player, BlockHitResult hit) {
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

    // ── PlushieSoundProvider ──────────────────────────────────────────────────

    /**
     * Called both when the Uzi Plush is placed as its own block (pos holds a
     * UziPlushBlockEntity) and when it sits on a shelf (pos holds a
     * PlushieShelfBlockEntity). Handled as two separate cases:
     *
     *  • UziPlushBlockEntity      → delegate to the BE's own sound/anim methods
     *                               (original behaviour, unchanged).
     *  • PlushieShelfBlockEntity  → run per-slot static sound state and ask the
     *                               shelf BE to sync a bounce trigger to clients.
     */
    @Override
    public void onShelfInteract(World world, BlockPos shelfPos, int slot, PlayerEntity player) {
        var be = world.getBlockEntity(shelfPos);

        // ── Case 1: Uzi placed as its own full block ──────────────────────────
        if (be instanceof UziPlushBlockEntity ube) {
            if (!world.isClient) {
                ube.playNextSound();
            }
            if (world.isClient) {
                ube.triggerAnim("controller", "bounce");
            }
            return;
        }

        // ── Case 2: Uzi sitting on a PlushieShelfBlock ────────────────────────
        if (be instanceof PlushieShelfBlockEntity shelf) {
            if (world.isClient) return; // all logic below is server-side only

            // Sound ────────────────────────────────────────────────────────────
            SlotKey key = new SlotKey(shelfPos.asLong(), slot);
            long now = world.getTime();

            if (now < SHELF_COOLDOWNS.getOrDefault(key, 0L)) return;

            // Each shelf slot keeps its own independently-shuffled sound list.
            List<SoundEvent> sounds = SHELF_SOUND_LISTS.computeIfAbsent(key, k -> {
                List<SoundEvent> list = new ArrayList<>(UziPlushBlockEntity.SOUND_DURATIONS.keySet());
                Collections.shuffle(list);
                return list;
            });

            int idx = SHELF_SOUND_IDX.getOrDefault(key, 0);
            if (idx == 0) {
                Collections.shuffle(sounds);
                // Avoid repeating the previous sound at the start of a new shuffle pass.
                SoundEvent last = SHELF_LAST_PLAYED.get(key);
                if (last != null && sounds.size() > 1 && sounds.get(0).equals(last)) {
                    Collections.swap(sounds, 0, 1);
                }
            }

            SoundEvent sound = sounds.get(idx);
            SHELF_LAST_PLAYED.put(key, sound);
            SHELF_SOUND_IDX.put(key, (idx + 1) % sounds.size());
            SHELF_COOLDOWNS.put(key, now + UziPlushBlockEntity.SOUND_DURATIONS.get(sound));

            world.playSound(null, shelfPos, sound, SoundCategory.BLOCKS, 1.0f, 1.0f);

            // Animation ───────────────────────────────────────────────────────
            // triggerBounce() writes bounceSlot into the update packet so the
            // client can call triggerAnim("controller", "bounce") on the correct
            // proxy UziPlushBlockEntity inside PlushieShelfBlockEntity.
            shelf.triggerBounce(slot);
        }
    }
}