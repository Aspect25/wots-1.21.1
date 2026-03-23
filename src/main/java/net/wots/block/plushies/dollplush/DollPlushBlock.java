package net.wots.block.plushies.dollplush;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.wots.block.entity.DollPlushBlockEntity;
import net.wots.util.VoxelShapeHelper;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.wots.block.ModBlocks;
import net.wots.block.entity.CynPlushBlockEntity;
import net.wots.block.plushies.PlushieSoundProvider;
import net.wots.unlock.VariantUnlockManager;

public class DollPlushBlock extends BlockWithEntity implements PlushieSoundProvider {

    public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;

    private static final VoxelShape SHAPE_NORTH = makeShape();
    private static final VoxelShape SHAPE_SOUTH = VoxelShapeHelper.rotateShape(SHAPE_NORTH, 2);
    private static final VoxelShape SHAPE_EAST  = VoxelShapeHelper.rotateShape(SHAPE_NORTH, 1);
    private static final VoxelShape SHAPE_WEST  = VoxelShapeHelper.rotateShape(SHAPE_NORTH, 3);

    private static VoxelShape makeShape() {
        return VoxelShapes.cuboid(0.1875, 0, 0.25, 0.8125, 1, 0.875);
    }

    public DollPlushBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH));
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return createCodec(DollPlushBlock::new);
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
        return new DollPlushBlockEntity(pos, state);
    }

//    // ── Ticker: drives the cluster hum every 5 seconds ───────────────────────
//    @Override
//    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
//        if (world.isClient) return null;
//        if (type != ModBlocks.CYN_PLUSH_BLOCK_ENTITY) return null;
//        //noinspection unchecked
//        return (BlockEntityTicker<T>) (BlockEntityTicker<CynPlushBlockEntity>) CynPlushBlockEntity::tick;
//    }

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
        if (!state.isOf(newState.getBlock()) && world.getBlockEntity(pos) instanceof DollPlushBlockEntity be) {
            be.stopSound();
            if (!world.isClient) {
                Block.dropStack(world, pos, new ItemStack(state.getBlock().asItem()));
            }
        }
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state,
                         LivingEntity placer, ItemStack stack) {
        super.onPlaced(world, pos, state, placer, stack);
        // Cyn placed next to Uzi → triggers Uzi's OHNO unlock
        if (!world.isClient && placer instanceof ServerPlayerEntity player) {
            VariantUnlockManager.checkNeighborsForUziUnlocks((ServerWorld) world, pos, player);
        }
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos,
                                 PlayerEntity player, BlockHitResult hit) {
        onShelfInteract(world, pos, 0, player);
        return ActionResult.SUCCESS;
    }

    @Override
    public void onShelfInteract(World world, BlockPos shelfPos, int slot, PlayerEntity player) {
        if (!world.isClient && world.getBlockEntity(shelfPos) instanceof DollPlushBlockEntity be) {
            be.playNextSound();
        }
        if (world.isClient && world.getBlockEntity(shelfPos) instanceof DollPlushBlockEntity be) {
            be.triggerAnim("controller", "bounce");
        }
    }
}
