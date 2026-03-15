package net.wots.block.luminite;

import net.minecraft.block.*;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class LuminiteBlock extends Block implements Waterloggable {

    public static final BooleanProperty LIT        = Properties.LIT;
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
    public static final IntProperty     POWER       = Properties.POWER;

    public LuminiteBlock(Settings settings) {
        super(settings);
        setDefaultState(getStateManager().getDefaultState()
            .with(LIT,         true)
            .with(WATERLOGGED, false)
            .with(POWER,       15)
        );
    }

    // ── Block state properties ─────────────────────────────────────────────────

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(LIT, WATERLOGGED, POWER);
    }

    // ── Placement ──────────────────────────────────────────────────────────────

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        World world     = ctx.getWorld();
        BlockPos pos    = ctx.getBlockPos();
        boolean watered = world.getFluidState(pos).getFluid() == Fluids.WATER;
        int     rs      = world.getReceivedRedstonePower(pos);

        if (watered) return getDefaultState().with(WATERLOGGED, true).with(LIT, false).with(POWER, 0);
        if (rs > 0)  return getDefaultState().with(POWER, Math.max(1, 15 - rs));
        return getDefaultState();
    }

    // ── Waterlogging ───────────────────────────────────────────────────────────

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction,
            BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (state.get(WATERLOGGED))
            world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        return state;
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
    }

    // ── Neighbour update: redstone dimmer + water extinguish ──────────────────

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos,
                               Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if (world.isClient) return;

        boolean wasLit      = state.get(LIT);
        boolean waterlogged = state.get(WATERLOGGED);
        int     rs          = world.getReceivedRedstonePower(pos);

        boolean newLit   = !waterlogged;
        int     newPower = waterlogged ? 0 : (rs > 0 ? Math.max(1, 15 - rs) : 15);

        BlockState newState = state.with(LIT, newLit).with(POWER, newPower);
        if (!newState.equals(state)) {
            world.setBlockState(pos, newState);

            if (wasLit && !newLit) {
                Random rng = world.getRandom();
                world.playSound(null, pos,
                    SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS,
                    0.5f, 2.6f + (rng.nextFloat() - rng.nextFloat()) * 0.8f
                );
            }
        }
    }

    // ── Let light pass through when lit ───────────────────────────────────────

    @Override
    public boolean isTransparent(BlockState state, BlockView world, BlockPos pos) {
        return state.get(LIT);
    }
}
