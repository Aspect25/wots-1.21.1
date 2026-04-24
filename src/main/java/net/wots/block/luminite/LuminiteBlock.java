package net.wots.block.luminite;

import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;

public class LuminiteBlock extends Block implements SimpleWaterloggedBlock {

    public static final BooleanProperty LIT        = BlockStateProperties.LIT;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final IntegerProperty     POWER       = BlockStateProperties.POWER;

    public LuminiteBlock(Properties properties) {
        super(properties);
        registerDefaultState(getStateDefinition().any()
            .setValue(LIT,         true)
            .setValue(WATERLOGGED, false)
            .setValue(POWER,       15)
        );
    }

    // ── Block state properties ─────────────────────────────────────────────────

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LIT, WATERLOGGED, POWER);
    }

    // ── Placement ──────────────────────────────────────────────────────────────

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        Level level     = ctx.getLevel();
        BlockPos pos    = ctx.getClickedPos();
        boolean watered = level.getFluidState(pos).getType() == Fluids.WATER;
        int     rs      = level.getBestNeighborSignal(pos);

        if (watered) return defaultBlockState().setValue(WATERLOGGED, true).setValue(LIT, false).setValue(POWER, 0);
        if (rs > 0)  return defaultBlockState().setValue(POWER, Math.max(1, 15 - rs));
        return defaultBlockState();
    }

    // ── Waterlogging ───────────────────────────────────────────────────────────

    @Override
    protected BlockState updateShape(BlockState state, net.minecraft.world.level.LevelReader world,
            net.minecraft.world.level.ScheduledTickAccess tickAccess, BlockPos pos, Direction direction,
            BlockPos neighborPos, BlockState neighborState, net.minecraft.util.RandomSource random) {
        if (state.getValue(WATERLOGGED))
            tickAccess.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
        return state;
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    // ── Neighbour update: redstone dimmer + water extinguish ──────────────────

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos,
                               Block sourceBlock, net.minecraft.world.level.redstone.Orientation orientation, boolean notify) {
        if (level.isClientSide()) return;

        boolean wasLit      = state.getValue(LIT);
        boolean waterlogged = state.getValue(WATERLOGGED);
        int     rs          = level.getBestNeighborSignal(pos);

        boolean newLit   = !waterlogged;
        int     newPower = waterlogged ? 0 : (rs > 0 ? Math.max(1, 15 - rs) : 15);

        BlockState newState = state.setValue(LIT, newLit).setValue(POWER, newPower);
        if (!newState.equals(state)) {
            level.setBlockAndUpdate(pos, newState);

            if (wasLit && !newLit) {
                RandomSource rng = level.getRandom();
                level.playSound(null, pos,
                    SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS,
                    0.5f, 2.6f + (rng.nextFloat() - rng.nextFloat()) * 0.8f
                );
            }
        }
    }

    // ── Let light pass through when lit ───────────────────────────────────────

    @Override
    protected boolean propagatesSkylightDown(BlockState state) {
        return state.getValue(LIT);
    }
}
