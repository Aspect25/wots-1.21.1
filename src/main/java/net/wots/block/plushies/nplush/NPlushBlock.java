package net.wots.block.plushies.nplush;

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
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.wots.block.ModBlocks;
import net.wots.block.entity.NPlushBlockEntity;
import net.wots.block.entity.PlushieShelfBlockEntity;
import net.wots.block.plushies.PlushieSoundProvider;

import java.util.*;

public class NPlushBlock extends BlockWithEntity implements PlushieSoundProvider {

    public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;

    private static final VoxelShape SHAPE = VoxelShapes.cuboid(0.1875, 0, 0.25, 0.8125, 1, 0.875);

    // ── Per-shelf-slot sound state ────────────────────────────────────────────
    // Keyed by (blockPos.asLong(), slot) so every shelf slot is independent.
    // These maps live on the server; they are not persisted (sound resets on
    // restart which is fine — the plushie just shuffles again from the beginning).

    private record SlotKey(long pos, int slot) {}

    private static final Map<SlotKey, Long>             SHELF_COOLDOWNS   = new HashMap<>();
    private static final Map<SlotKey, Integer>          SHELF_SOUND_IDX   = new HashMap<>();
    private static final Map<SlotKey, SoundEvent>       SHELF_LAST_PLAYED = new HashMap<>();
    private static final Map<SlotKey, List<SoundEvent>> SHELF_SOUND_LISTS = new HashMap<>();

    // ── Constructor ───────────────────────────────────────────────────────────

    public NPlushBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH));
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return createCodec(NPlushBlock::new);
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
        return new NPlushBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        if (world.isClient) return null;
        if (type != ModBlocks.N_PLUSH_BLOCK_ENTITY) return null;
        //noinspection unchecked
        return (BlockEntityTicker<T>) (BlockEntityTicker<NPlushBlockEntity>) NPlushBlockEntity::tick;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext ctx) {
        return SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext ctx) {
        return SHAPE;
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.isOf(newState.getBlock()) && world.getBlockEntity(pos) instanceof NPlushBlockEntity be) {
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
                        .setScreen(new net.wots.client.screen.NVariantWheelScreen(pos));
            }
            return ActionResult.SUCCESS;
        }
        onShelfInteract(world, pos, 0, player);
        return ActionResult.SUCCESS;
    }

    // ── PlushieSoundProvider ──────────────────────────────────────────────────

    /**
     * Called both when this NPlush is placed as its own block (pos holds an
     * NPlushBlockEntity) and when it sits on a shelf (pos holds a
     * PlushieShelfBlockEntity). The two cases are handled separately:
     *
     *  • NPlushBlockEntity  → delegate to the BE's own sound/anim methods
     *                         (existing behaviour, unchanged).
     *  • PlushieShelfBlockEntity → play sound via per-slot static maps and ask
     *                              the shelf BE to sync a bounce trigger to clients.
     */
    @Override
    public void onShelfInteract(World world, BlockPos shelfPos, int slot, PlayerEntity player) {
        var be = world.getBlockEntity(shelfPos);

        // ── Case 1: NPlush placed as its own full block ───────────────────────
        if (be instanceof NPlushBlockEntity nbe) {
            if (!world.isClient) {
                nbe.playNextSound();
            }
            if (world.isClient) {
                nbe.triggerAnim("controller", "bounce");
            }
            return;
        }

        // ── Case 2: NPlush sitting on a PlushieShelfBlock ─────────────────────
        if (be instanceof PlushieShelfBlockEntity shelf) {
            if (world.isClient) return; // everything below runs server-side only

            // Sound ────────────────────────────────────────────────────────────
            SlotKey key = new SlotKey(shelfPos.asLong(), slot);
            long now = world.getTime();

            if (now < SHELF_COOLDOWNS.getOrDefault(key, 0L)) return;

            // Each shelf slot keeps its own independently-shuffled sound list.
            List<SoundEvent> sounds = SHELF_SOUND_LISTS.computeIfAbsent(key, k -> {
                List<SoundEvent> list = new ArrayList<>(NPlushBlockEntity.SOUND_DURATIONS.keySet());
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
            SHELF_COOLDOWNS.put(key, now + NPlushBlockEntity.SOUND_DURATIONS.get(sound));

            world.playSound(null, shelfPos, sound, SoundCategory.BLOCKS, 1.0f, 1.0f);

            // Animation ───────────────────────────────────────────────────────
            // triggerBounce() sets bounceSlot and fires an update packet; the
            // client reads it in PlushieShelfBlockEntity.readNbt() and calls
            // triggerAnim("controller", "bounce") on the matching proxy BE.
            shelf.triggerBounce(slot);
        }
    }
}