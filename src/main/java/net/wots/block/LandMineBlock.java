package net.wots.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;

public class LandMineBlock extends Block {

    // ── Edit these ────────────────────────────────────────────────
    private static final int FUSE_TICKS = 40; // 20 ticks = 1 second
    private static final float EXPLOSION_POWER = 3.5f;
    // ─────────────────────────────────────────────────────────────

    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty TRIGGERED = BooleanProperty.create("triggered");

    // Visual outline -- the full mine shape
    private static final VoxelShape OUTLINE = Shapes.or(
            Block.box(2, 0, 2, 14, 9, 14),
            Block.box(0, 4, 0, 16, 5, 16)
    );

    // Collision -- thin plate so the player's feet end up INSIDE the block space,
    // which guarantees onEntityCollision fires
    private static final VoxelShape COLLISION = Block.box(2, 0, 2, 14, 4, 14);

    public LandMineBlock(Properties properties) {
        super(properties);
        registerDefaultState(getStateDefinition().any()
                .setValue(FACING, Direction.NORTH)
                .setValue(TRIGGERED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, TRIGGERED);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return OUTLINE;
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return COLLISION;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return defaultBlockState().setValue(FACING, ctx.getHorizontalDirection().getOpposite());
    }

    // Fires when an entity's bounding box overlaps the block position.
    // Works because COLLISION is shorter than OUTLINE -- the player's feet
    // sit at y=4/16 which is inside the block's 0-1 range.
    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity,
                                net.minecraft.world.entity.InsideBlockEffectApplier effectApplier, boolean headInside) {
        if (!level.isClientSide() && !state.getValue(TRIGGERED)
                && entity instanceof Player player
                && !player.isCreative() && !player.isSpectator()) {
            level.setBlockAndUpdate(pos, state.setValue(TRIGGERED, true));
            level.playSound(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                    SoundEvents.TNT_PRIMED, SoundSource.BLOCKS, 1.0f, 1.0f);
            level.scheduleTick(pos, this, FUSE_TICKS);
        }
    }

    // Backup: also trigger when stepping on top
    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        if (!level.isClientSide() && !state.getValue(TRIGGERED)
                && entity instanceof Player player
                && !player.isCreative() && !player.isSpectator()) {
            level.setBlockAndUpdate(pos, state.setValue(TRIGGERED, true));
            level.playSound(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                    SoundEvents.TNT_PRIMED, SoundSource.BLOCKS, 1.0f, 1.0f);
            level.scheduleTick(pos, this, FUSE_TICKS);
        }
        super.stepOn(level, pos, state, entity);
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!state.getValue(TRIGGERED)) return;
        level.removeBlock(pos, false);
        level.sendParticles(ParticleTypes.EXPLOSION,
                pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                5, 0.1, 0.1, 0.1, 0.02);
        level.explode(null,
                pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                EXPLOSION_POWER, Level.ExplosionInteraction.TNT);
    }
}
