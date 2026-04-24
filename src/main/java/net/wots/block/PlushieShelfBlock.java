package net.wots.block;

import net.wots.block.entity.PlushieShelfBlockEntity;
import net.wots.block.plushies.PlushieSoundProvider;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.InteractionResult;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import org.jetbrains.annotations.Nullable;

public class PlushieShelfBlock extends BaseEntityBlock {

    // ── Shape enum ────────────────────────────────────────────────────────────

    public enum Shape implements StringRepresentable {
        STRAIGHT, CORNER_LEFT, CORNER_RIGHT;

        @Override
        public String getSerializedName() {
            return switch (this) {
                case STRAIGHT     -> "straight";
                case CORNER_LEFT  -> "corner_left";
                case CORNER_RIGHT -> "corner_right";
            };
        }
    }

    public static final MapCodec<PlushieShelfBlock> CODEC = simpleCodec(PlushieShelfBlock::new);
    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final EnumProperty<Shape> SHAPE = EnumProperty.create("shape", Shape.class);

    // ── Shapes ────────────────────────────────────────────────────────────────

    private static final VoxelShape SHAPE_NORTH = Shapes.or(
            Block.box(0,  6, 8, 16, 8, 16),
            Block.box(0,  0, 14, 16, 8, 16),
            Block.box(0,  0, 8,  2,  8, 16),
            Block.box(14, 0, 8,  16, 8, 16)
    );
    private static final VoxelShape SHAPE_SOUTH = Shapes.or(
            Block.box(0,  6, 0, 16, 8, 8),
            Block.box(0,  0, 0, 16, 8, 2),
            Block.box(0,  0, 0, 2,  8, 8),
            Block.box(14, 0, 0, 16, 8, 8)
    );
    private static final VoxelShape SHAPE_WEST = Shapes.or(
            Block.box(8,  6, 0, 16, 8, 16),
            Block.box(14, 0, 0, 16, 8, 16),
            Block.box(8,  0, 0, 16, 8, 2),
            Block.box(8,  0, 14, 16, 8, 16)
    );
    private static final VoxelShape SHAPE_EAST = Shapes.or(
            Block.box(0, 6, 0, 8, 8, 16),
            Block.box(0, 0, 0, 2, 8, 16),
            Block.box(0, 0, 0, 8, 8, 2),
            Block.box(0, 0, 14, 8, 8, 16)
    );

    // Corner extension shapes (the L arm) - indexed by facing + left/right
    private static final VoxelShape EXT_NORTH_LEFT  = Block.box(8, 6, 0, 16, 8, 8);
    private static final VoxelShape EXT_NORTH_RIGHT = Block.box(0, 6, 0, 8,  8, 8);
    private static final VoxelShape EXT_SOUTH_LEFT  = Block.box(0, 6, 8, 8,  8, 16);
    private static final VoxelShape EXT_SOUTH_RIGHT = Block.box(8, 6, 8, 16, 8, 16);
    private static final VoxelShape EXT_WEST_LEFT   = Block.box(0, 6, 0, 8,  8, 8);
    private static final VoxelShape EXT_WEST_RIGHT  = Block.box(0, 6, 8, 8,  8, 16);
    private static final VoxelShape EXT_EAST_LEFT   = Block.box(8, 6, 8, 16, 8, 16);
    private static final VoxelShape EXT_EAST_RIGHT  = Block.box(8, 6, 0, 16, 8, 8);

    // Pre-cached corner unions (avoids allocating new Shapes every frame)
    private static final VoxelShape SHAPE_NORTH_CORNER_LEFT  = Shapes.or(SHAPE_NORTH, EXT_NORTH_LEFT);
    private static final VoxelShape SHAPE_NORTH_CORNER_RIGHT = Shapes.or(SHAPE_NORTH, EXT_NORTH_RIGHT);
    private static final VoxelShape SHAPE_SOUTH_CORNER_LEFT  = Shapes.or(SHAPE_SOUTH, EXT_SOUTH_LEFT);
    private static final VoxelShape SHAPE_SOUTH_CORNER_RIGHT = Shapes.or(SHAPE_SOUTH, EXT_SOUTH_RIGHT);
    private static final VoxelShape SHAPE_WEST_CORNER_LEFT   = Shapes.or(SHAPE_WEST, EXT_WEST_LEFT);
    private static final VoxelShape SHAPE_WEST_CORNER_RIGHT  = Shapes.or(SHAPE_WEST, EXT_WEST_RIGHT);
    private static final VoxelShape SHAPE_EAST_CORNER_LEFT   = Shapes.or(SHAPE_EAST, EXT_EAST_LEFT);
    private static final VoxelShape SHAPE_EAST_CORNER_RIGHT  = Shapes.or(SHAPE_EAST, EXT_EAST_RIGHT);

    public PlushieShelfBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState()
                .setValue(FACING, Direction.NORTH)
                .setValue(SHAPE, Shape.STRAIGHT));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() { return CODEC; }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, SHAPE);
    }

    // ── Placement & neighbor updates ──────────────────────────────────────────

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        Direction facing = ctx.getHorizontalDirection().getOpposite();
        BlockState state = defaultBlockState().setValue(FACING, facing);
        return state.setValue(SHAPE, computeShape(ctx.getLevel(), ctx.getClickedPos(), facing));
    }

    @Override
    protected BlockState updateShape(BlockState state, net.minecraft.world.level.LevelReader world,
                                     net.minecraft.world.level.ScheduledTickAccess tickAccess,
                                     BlockPos pos, Direction direction,
                                     BlockPos neighborPos, BlockState neighborState,
                                     net.minecraft.util.RandomSource random) {
        Direction facing = state.getValue(FACING);
        // Only recompute if a relevant neighbor changed
        if (direction == getLeftDir(facing) || direction == getRightDir(facing) || direction == facing) {
            return state.setValue(SHAPE, computeShape(world, pos, facing));
        }
        return state;
    }

    // ── Shape computation (the only place connection logic lives) ─────────────

    private Shape computeShape(BlockGetter world, BlockPos pos, Direction facing) {
        Direction left  = getLeftDir(facing);
        Direction right = getRightDir(facing);

        // A corner forms when a perpendicular shelf sits to our side OR in front
        if (isCornerSide(world, pos, left, facing) || isFrontCorner(world, pos, facing, true)) {
            return Shape.CORNER_LEFT;
        }
        if (isCornerSide(world, pos, right, facing) || isFrontCorner(world, pos, facing, false)) {
            return Shape.CORNER_RIGHT;
        }
        return Shape.STRAIGHT;
    }

    /** Side neighbor is a perpendicular shelf (not same-facing = straight). */
    private boolean isCornerSide(BlockGetter world, BlockPos pos, Direction sideDir, Direction facing) {
        BlockState nb = world.getBlockState(pos.relative(sideDir));
        if (!(nb.getBlock() instanceof PlushieShelfBlock)) return false;
        Direction nf = nb.getValue(FACING);
        if (nf == facing || nf == sideDir.getOpposite()) return false;
        return nf == facing.getClockWise() || nf == facing.getCounterClockWise();
    }

    /** Front neighbor is a shelf whose face is perpendicular to ours. */
    private boolean isFrontCorner(BlockGetter world, BlockPos pos, Direction facing, boolean isLeft) {
        BlockState nb = world.getBlockState(pos.relative(facing));
        if (!(nb.getBlock() instanceof PlushieShelfBlock)) return false;
        Direction nf = nb.getValue(FACING);
        return isLeft ? nf == getRightDir(facing) : nf == getLeftDir(facing);
    }

    // ── Direction helpers ─────────────────────────────────────────────────────

    private Direction getLeftDir(Direction facing) {
        return switch (facing) {
            case NORTH -> Direction.EAST;
            case SOUTH -> Direction.WEST;
            case EAST  -> Direction.SOUTH;
            case WEST  -> Direction.NORTH;
            default    -> Direction.EAST;
        };
    }

    private Direction getRightDir(Direction facing) {
        return switch (facing) {
            case NORTH -> Direction.WEST;
            case SOUTH -> Direction.EAST;
            case EAST  -> Direction.NORTH;
            case WEST  -> Direction.SOUTH;
            default    -> Direction.WEST;
        };
    }

    // ── Shape / render ────────────────────────────────────────────────────────

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext ctx) {
        Direction facing = state.getValue(FACING);
        Shape shape = state.getValue(SHAPE);

        if (shape == Shape.CORNER_LEFT) return switch (facing) {
            case SOUTH -> SHAPE_SOUTH_CORNER_LEFT;
            case WEST  -> SHAPE_WEST_CORNER_LEFT;
            case EAST  -> SHAPE_EAST_CORNER_LEFT;
            default    -> SHAPE_NORTH_CORNER_LEFT;
        };
        if (shape == Shape.CORNER_RIGHT) return switch (facing) {
            case SOUTH -> SHAPE_SOUTH_CORNER_RIGHT;
            case WEST  -> SHAPE_WEST_CORNER_RIGHT;
            case EAST  -> SHAPE_EAST_CORNER_RIGHT;
            default    -> SHAPE_NORTH_CORNER_RIGHT;
        };

        return switch (facing) {
            case SOUTH -> SHAPE_SOUTH;
            case WEST  -> SHAPE_WEST;
            case EAST  -> SHAPE_EAST;
            default    -> SHAPE_NORTH;
        };
    }

    @Override
    public RenderShape getRenderShape(BlockState state) { return RenderShape.MODEL; }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PlushieShelfBlockEntity(pos, state);
    }

    // ── Interaction ───────────────────────────────────────────────────────────

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                 Player player, BlockHitResult hit) {

        if (!(level.getBlockEntity(pos) instanceof PlushieShelfBlockEntity shelf))
            return InteractionResult.PASS;

        int slot = getSlotFromHit(state, hit);
        ItemStack held = player.getMainHandItem();

        if (player.isShiftKeyDown()) {
            if (!shelf.isEmpty(slot)) {
                ItemStack taken = shelf.take(slot);
                if (!level.isClientSide()) {
                    player.getInventory().add(taken);
                    level.sendBlockUpdated(pos, state, state, 3);
                }
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.PASS;
        }

        if (!held.isEmpty()
                && held.getItem() instanceof BlockItem bi
                && bi.getBlock() instanceof PlushieSoundProvider
                && shelf.isEmpty(slot)) {
            if (!level.isClientSide()) {
                shelf.place(slot, held);
                if (!player.isCreative()) held.shrink(1);
                level.sendBlockUpdated(pos, state, state, 3);
            }
            return InteractionResult.SUCCESS;
        }

        if (held.isEmpty() && !shelf.isEmpty(slot)) {
            ItemStack plushie = shelf.getPlushie(slot);
            if (plushie.getItem() instanceof BlockItem bi
                    && bi.getBlock() instanceof PlushieSoundProvider provider) {
                provider.onShelfInteract(level, pos, slot, player);
                return InteractionResult.SUCCESS;
            }
        }

        return InteractionResult.PASS;
    }

    @Override
    protected void affectNeighborsAfterRemoval(BlockState state, net.minecraft.server.level.ServerLevel level, BlockPos pos,
                                boolean moved) {
        // Block entity may already be gone in 26.1. Drop handled elsewhere.
        super.affectNeighborsAfterRemoval(state, level, pos, moved);
    }

    // ── Slot hit detection ────────────────────────────────────────────────────

    private Vec3 worldToLocal(double wx, double wz, Direction facing) {
        return switch (facing) {
            case NORTH -> new Vec3(wx,     0, wz);
            case SOUTH -> new Vec3(1 - wx, 0, 1 - wz);
            case WEST  -> new Vec3(1 - wz, 0, wx);
            case EAST  -> new Vec3(wz,     0, 1 - wx);
            default    -> new Vec3(wx,     0, wz);
        };
    }

    private int getSlotFromHit(BlockState state, BlockHitResult hit) {
        Vec3 p = hit.getLocation();
        Direction facing = state.getValue(FACING);
        Shape shape = state.getValue(SHAPE);

        double wx = p.x - Math.floor(p.x);
        double wz = p.z - Math.floor(p.z);
        Vec3 local = worldToLocal(wx, wz, facing);
        double lx = local.x;
        double lz = local.z;

        if (shape == Shape.CORNER_LEFT || shape == Shape.CORNER_RIGHT) {
            if (lz <= 0.5) return lz > 0.25 ? 3 : 4;   // arm slots
        }

        if (lx < 0.33) return 0;
        if (lx < 0.66) return 1;
        return 2;
    }
}
