package net.wots.block.plushies.the_duckler;

import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.wots.block.plushies.PlushieSoundProvider;
import net.wots.sound.ModSounds;

import java.util.*;
import net.wots.util.ShuffledSoundQueue;
import net.wots.util.VoxelShapeHelper;

public class TheDucklerPlush extends Block implements PlushieSoundProvider {

    private static final Map<SoundEvent, Integer> SOUND_DURATIONS_DUCK = Map.ofEntries(
            Map.entry(ModSounds.DUCK_SOUND, 20)
    );

    private static final ShuffledSoundQueue SOUND_QUEUE = new ShuffledSoundQueue(SOUND_DURATIONS_DUCK);
    public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;

    // ── Shapes ───────────────────────────────────────────────────────────
    private static final VoxelShape SHAPE_NORTH = makeShape();
    private static final VoxelShape SHAPE_SOUTH = VoxelShapeHelper.rotateShape(SHAPE_NORTH, 2);
    private static final VoxelShape SHAPE_EAST  = VoxelShapeHelper.rotateShape(SHAPE_NORTH, 1);
    private static final VoxelShape SHAPE_WEST  = VoxelShapeHelper.rotateShape(SHAPE_NORTH, 3);

    private static VoxelShape makeShape() {
        return Shapes.box(0.1875, 0, 0.25, 0.8125, 1, 0.875);
    }

    // ── Constructor ───────────────────────────────────────────────────────────
    public TheDucklerPlush(Properties properties) {
        super(properties);
        this.registerDefaultState(this.getStateDefinition().any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return this.defaultBlockState().setValue(FACING, ctx.getHorizontalDirection().getOpposite());
    }

    // ── Shapes ────────────────────────────────────────────────────────────────
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext ctx) {
        return switch (state.getValue(FACING)) {
            case SOUTH -> SHAPE_SOUTH;
            case EAST  -> SHAPE_EAST;
            case WEST  -> SHAPE_WEST;
            default    -> SHAPE_NORTH;
        };
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext ctx) {
        return getShape(state, world, pos, ctx);
    }

    // ── Interaction ───────────────────────────────────────────────────────────
    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                 Player player, BlockHitResult hit) {
        onShelfInteract(level, pos, 0, player);
        return InteractionResult.SUCCESS;
    }

    @Override
    public void onShelfInteract(Level level, BlockPos shelfPos, int slot, Player player) {
        if (!level.isClientSide()) {
            SoundEvent sound = SOUND_QUEUE.tryAdvance(level.getGameTime());
            if (sound != null) level.playSound(null, shelfPos, sound, SoundSource.BLOCKS, 1.0f, 1.0f);
        }
    }
}
