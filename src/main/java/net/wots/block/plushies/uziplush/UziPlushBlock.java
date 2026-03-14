package net.wots.block.plushies.uziplush;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
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
import net.wots.block.entity.UziPlushBlockEntity;
import net.wots.block.plushies.PlushieSoundProvider;

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

    @Override
    public void onShelfInteract(World world, BlockPos shelfPos, int slot, PlayerEntity player) {
        if (!world.isClient && world.getBlockEntity(shelfPos) instanceof UziPlushBlockEntity be) {
            be.playNextSound();
        }
        if (world.isClient && world.getBlockEntity(shelfPos) instanceof UziPlushBlockEntity be) {
            be.triggerAnim("controller", "bounce");
        }
    }
}