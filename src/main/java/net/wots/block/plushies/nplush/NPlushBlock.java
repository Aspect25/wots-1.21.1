package net.wots.block.plushies.nplush;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
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
import net.wots.block.entity.NPlushBlockEntity;
import net.wots.block.plushies.PlushieSoundProvider;

public class NPlushBlock extends BlockWithEntity implements PlushieSoundProvider {

    public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;

    // ── Shape ─────────────────────────────────────────────────────────────────
    private static final VoxelShape SHAPE = VoxelShapes.cuboid(0.1875, 0, 0.25, 0.8125, 1, 0.875);

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
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext ctx) {
        return SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext ctx) {
        return SHAPE;
    }

    // ── Stop sound on break ───────────────────────────────────────────────────
    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.isOf(newState.getBlock()) && world.getBlockEntity(pos) instanceof NPlushBlockEntity blockEntity) {
            blockEntity.stopSound();
        }
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    // ── Interaction ───────────────────────────────────────────────────────────
    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos,
                                 PlayerEntity player, BlockHitResult hit) {
        onShelfInteract(world, pos, 0, player);
        return ActionResult.SUCCESS;
    }

    @Override
    public void onShelfInteract(World world, BlockPos shelfPos, int slot, PlayerEntity player) {
        if (!world.isClient && world.getBlockEntity(shelfPos) instanceof NPlushBlockEntity blockEntity) {
            blockEntity.playNextSound();
        }

        if (world.isClient && world.getBlockEntity(shelfPos) instanceof NPlushBlockEntity blockEntity) {
            blockEntity.triggerAnim("controller", "bounce");
        }
    }
}