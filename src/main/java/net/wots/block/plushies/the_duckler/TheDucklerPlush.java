package net.wots.block.plushies.the_duckler;

import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
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
import net.wots.block.plushies.PlushieSoundProvider;
import net.wots.sound.ModSounds;

import java.util.*;

public class TheDucklerPlush extends Block implements PlushieSoundProvider {

    private static final Map<SoundEvent, Integer> SOUND_DURATIONS_DUCK = Map.ofEntries(
            Map.entry(ModSounds.DUCK_SOUND, 20)
    );

    private static final List<SoundEvent> SOUNDS = new ArrayList<>(SOUND_DURATIONS_DUCK.keySet());
    private static final Map<BlockPos, Long> SOUND_COOLDOWNS = new HashMap<>();
    private static int soundIndex = 0;
    public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;

    // ── VoxelShapes ───────────────────────────────────────────────────────────
    private static final VoxelShape SHAPE_NORTH = makeShape();
    private static final VoxelShape SHAPE_SOUTH = rotateShape(SHAPE_NORTH, 2);
    private static final VoxelShape SHAPE_EAST  = rotateShape(SHAPE_NORTH, 1);
    private static final VoxelShape SHAPE_WEST  = rotateShape(SHAPE_NORTH, 3);

    private static VoxelShape rotateShape(VoxelShape shape, int times) {
        VoxelShape[] buffer = {VoxelShapes.empty()};
        shape.forEachBox((minX, minY, minZ, maxX, maxY, maxZ) -> {
            double x1 = minX, z1 = minZ, x2 = maxX, z2 = maxZ;
            for (int i = 0; i < times; i++) {
                double newX1 = 1 - z2;
                double newZ1 = x1;
                double newX2 = 1 - z1;
                double newZ2 = x2;
                x1 = newX1; z1 = newZ1;
                x2 = newX2; z2 = newZ2;
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

    // ── Constructor ───────────────────────────────────────────────────────────
    public TheDucklerPlush(Settings settings) {
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

    // ── Shapes ────────────────────────────────────────────────────────────────
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

    // ── Interaction ───────────────────────────────────────────────────────────
    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos,
                                 PlayerEntity player, BlockHitResult hit) {
        onShelfInteract(world, pos, 0, player);
        return ActionResult.SUCCESS;
    }

    @Override
    public void onShelfInteract(World world, BlockPos shelfPos, int slot, PlayerEntity player) {
        if (!world.isClient) {
            long currentTime = world.getTime();
            BlockPos key = shelfPos.add(slot, 0, 0);
            long cooldownEnd = SOUND_COOLDOWNS.getOrDefault(key, 0L);

            if (currentTime < cooldownEnd) return;

            if (soundIndex == 0) Collections.shuffle(SOUNDS);

            SoundEvent randomSound = SOUNDS.get(soundIndex);
            soundIndex = (soundIndex + 1) % SOUNDS.size();

            SOUND_COOLDOWNS.put(key, currentTime + SOUND_DURATIONS_DUCK.get(randomSound));
            world.playSound(null, shelfPos, randomSound, SoundCategory.BLOCKS, 1.0f, 1.0f);
        }
    }
}