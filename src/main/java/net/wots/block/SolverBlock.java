package net.wots.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.util.math.Direction;

/**
 * Solver Block — crafted from Soul Sand + Nether Quartz.
 *
 * QUIRK: Emits light level 7. Enough to see clearly, NOT enough to prevent
 * mob spawns (which require level 8+). Builders who want moody, dangerous-feeling
 * rooms will love it. Players who want a safe base will need to supplement it.
 *
 * This is the soul-torch equivalent for WOTS: useful, but with a deliberate limit.
 *
 * No block entity needed — luminance is handled in AbstractBlock.Settings in ModBlocks.
 */
public class SolverBlock extends Block {

    public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;

    public SolverBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }
}
