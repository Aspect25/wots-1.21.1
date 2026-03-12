package net.wots.block;

import net.wots.block.entity.PlushieShelfBlockEntity;
import net.wots.block.plushies.PlushieSoundProvider;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

public class PlushieShelfBlock extends BlockWithEntity {

    // ── Shape enum ────────────────────────────────────────────────────────────

    public enum Shape implements StringIdentifiable {
        STRAIGHT, CORNER_LEFT, CORNER_RIGHT;

        @Override
        public String asString() {
            return switch (this) {
                case STRAIGHT     -> "straight";
                case CORNER_LEFT  -> "corner_left";
                case CORNER_RIGHT -> "corner_right";
            };
        }
    }

    public static final MapCodec<PlushieShelfBlock> CODEC = createCodec(PlushieShelfBlock::new);
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    public static final EnumProperty<Shape> SHAPE = EnumProperty.of("shape", Shape.class);

    // ── Shapes ────────────────────────────────────────────────────────────────

    private static final VoxelShape SHAPE_NORTH = VoxelShapes.union(
            Block.createCuboidShape(0,  6, 8, 16, 8, 16),
            Block.createCuboidShape(0,  0, 14, 16, 8, 16),
            Block.createCuboidShape(0,  0, 8,  2,  8, 16),
            Block.createCuboidShape(14, 0, 8,  16, 8, 16)
    );
    private static final VoxelShape SHAPE_SOUTH = VoxelShapes.union(
            Block.createCuboidShape(0,  6, 0, 16, 8, 8),
            Block.createCuboidShape(0,  0, 0, 16, 8, 2),
            Block.createCuboidShape(0,  0, 0, 2,  8, 8),
            Block.createCuboidShape(14, 0, 0, 16, 8, 8)
    );
    private static final VoxelShape SHAPE_WEST = VoxelShapes.union(
            Block.createCuboidShape(8,  6, 0, 16, 8, 16),
            Block.createCuboidShape(14, 0, 0, 16, 8, 16),
            Block.createCuboidShape(8,  0, 0, 16, 8, 2),
            Block.createCuboidShape(8,  0, 14, 16, 8, 16)
    );
    private static final VoxelShape SHAPE_EAST = VoxelShapes.union(
            Block.createCuboidShape(0, 6, 0, 8, 8, 16),
            Block.createCuboidShape(0, 0, 0, 2, 8, 16),
            Block.createCuboidShape(0, 0, 0, 8, 8, 2),
            Block.createCuboidShape(0, 0, 14, 8, 8, 16)
    );

    // Corner extension shapes (the L arm) - indexed by facing + left/right
    private static final VoxelShape EXT_NORTH_LEFT  = Block.createCuboidShape(8, 6, 0, 16, 8, 8);
    private static final VoxelShape EXT_NORTH_RIGHT = Block.createCuboidShape(0, 6, 0, 8,  8, 8);
    private static final VoxelShape EXT_SOUTH_LEFT  = Block.createCuboidShape(0, 6, 8, 8,  8, 16);
    private static final VoxelShape EXT_SOUTH_RIGHT = Block.createCuboidShape(8, 6, 8, 16, 8, 16);
    private static final VoxelShape EXT_WEST_LEFT   = Block.createCuboidShape(0, 6, 0, 8,  8, 8);
    private static final VoxelShape EXT_WEST_RIGHT  = Block.createCuboidShape(0, 6, 8, 8,  8, 16);
    private static final VoxelShape EXT_EAST_LEFT   = Block.createCuboidShape(8, 6, 8, 16, 8, 16);
    private static final VoxelShape EXT_EAST_RIGHT  = Block.createCuboidShape(8, 6, 0, 16, 8, 8);

    public PlushieShelfBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState()
                .with(FACING, Direction.NORTH)
                .with(SHAPE, Shape.STRAIGHT));
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() { return CODEC; }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, SHAPE);
    }

    // ── Placement & neighbor updates ──────────────────────────────────────────

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        Direction facing = ctx.getHorizontalPlayerFacing().getOpposite();
        BlockState state = getDefaultState().with(FACING, facing);
        return state.with(SHAPE, computeShape(ctx.getWorld(), ctx.getBlockPos(), facing));
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction,
                                                BlockState neighborState, WorldAccess world,
                                                BlockPos pos, BlockPos neighborPos) {
        Direction facing = state.get(FACING);
        // Only recompute if a relevant neighbor changed
        if (direction == getLeftDir(facing) || direction == getRightDir(facing) || direction == facing) {
            return state.with(SHAPE, computeShape(world, pos, facing));
        }
        return state;
    }

    // ── Shape computation (the only place connection logic lives) ─────────────

    private Shape computeShape(BlockView world, BlockPos pos, Direction facing) {
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
    private boolean isCornerSide(BlockView world, BlockPos pos, Direction sideDir, Direction facing) {
        BlockState nb = world.getBlockState(pos.offset(sideDir));
        if (!(nb.getBlock() instanceof PlushieShelfBlock)) return false;
        Direction nf = nb.get(FACING);
        if (nf == facing || nf == sideDir.getOpposite()) return false; // ← was missing
        return nf == facing.rotateYClockwise() || nf == facing.rotateYCounterclockwise();
    }

    /** Front neighbor is a shelf whose face is perpendicular to ours. */
    private boolean isFrontCorner(BlockView world, BlockPos pos, Direction facing, boolean isLeft) {
        BlockState nb = world.getBlockState(pos.offset(facing));
        if (!(nb.getBlock() instanceof PlushieShelfBlock)) return false;
        Direction nf = nb.get(FACING);
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
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext ctx) {
        Direction facing = state.get(FACING);
        Shape shape = state.get(SHAPE);

        VoxelShape base = switch (facing) {
            case SOUTH -> SHAPE_SOUTH;
            case WEST  -> SHAPE_WEST;
            case EAST  -> SHAPE_EAST;
            default    -> SHAPE_NORTH;
        };

        if (shape == Shape.CORNER_LEFT) base = VoxelShapes.union(base, switch (facing) {
            case SOUTH -> EXT_SOUTH_LEFT;
            case WEST  -> EXT_WEST_LEFT;
            case EAST  -> EXT_EAST_LEFT;
            default    -> EXT_NORTH_LEFT;
        });
        if (shape == Shape.CORNER_RIGHT) base = VoxelShapes.union(base, switch (facing) {
            case SOUTH -> EXT_SOUTH_RIGHT;
            case WEST  -> EXT_WEST_RIGHT;
            case EAST  -> EXT_EAST_RIGHT;
            default    -> EXT_NORTH_RIGHT;
        });

        return base;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) { return BlockRenderType.MODEL; }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new PlushieShelfBlockEntity(pos, state);
    }

    // ── Interaction ───────────────────────────────────────────────────────────

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos,
                                 PlayerEntity player, BlockHitResult hit) {

        if (!(world.getBlockEntity(pos) instanceof PlushieShelfBlockEntity shelf))
            return ActionResult.PASS;

        int slot = getSlotFromHit(state, hit);
        ItemStack held = player.getMainHandStack();

        if (player.isSneaking()) {
            if (!shelf.isEmpty(slot)) {
                ItemStack taken = shelf.take(slot);
                if (!world.isClient) {
                    player.getInventory().offerOrDrop(taken);
                    world.updateListeners(pos, state, state, 3);
                }
                return ActionResult.SUCCESS;
            }
            return ActionResult.PASS;
        }

        if (!held.isEmpty()
                && held.getItem() instanceof BlockItem bi
                && bi.getBlock() instanceof PlushieSoundProvider
                && shelf.isEmpty(slot)) {
            if (!world.isClient) {
                shelf.place(slot, held);
                if (!player.isCreative()) held.decrement(1);
                world.updateListeners(pos, state, state, 3);
            }
            return ActionResult.SUCCESS;
        }

        if (held.isEmpty() && !shelf.isEmpty(slot)) {
            ItemStack plushie = shelf.getPlushie(slot);
            if (plushie.getItem() instanceof BlockItem bi
                    && bi.getBlock() instanceof PlushieSoundProvider provider) {
                provider.onShelfInteract(world, pos, slot, player);
                return ActionResult.SUCCESS;
            }
        }

        return ActionResult.PASS;
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos,
                                BlockState newState, boolean moved) {
        if (!state.isOf(newState.getBlock())) {
            if (world.getBlockEntity(pos) instanceof PlushieShelfBlockEntity shelf) {
                for (int i = 0; i < PlushieShelfBlockEntity.SLOTS; i++) {
                    if (!shelf.isEmpty(i)) Block.dropStack(world, pos, shelf.getPlushie(i));
                }
            }
        }
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    // ── Slot hit detection ────────────────────────────────────────────────────

    private Vec3d worldToLocal(double wx, double wz, Direction facing) {
        return switch (facing) {
            case NORTH -> new Vec3d(wx,     0, wz);
            case SOUTH -> new Vec3d(1 - wx, 0, 1 - wz);
            case WEST  -> new Vec3d(1 - wz, 0, wx);
            case EAST  -> new Vec3d(wz,     0, 1 - wx);
            default    -> new Vec3d(wx,     0, wz);
        };
    }

    private int getSlotFromHit(BlockState state, BlockHitResult hit) {
        Vec3d p = hit.getPos();
        Direction facing = state.get(FACING);
        Shape shape = state.get(SHAPE);

        double wx = p.x - Math.floor(p.x);
        double wz = p.z - Math.floor(p.z);
        Vec3d local = worldToLocal(wx, wz, facing);
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