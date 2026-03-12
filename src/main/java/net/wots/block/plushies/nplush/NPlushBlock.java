package net.wots.block.plushies.nplush;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
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
import net.wots.block.entity.NPlushBlockEntity;
import net.wots.block.plushies.PlushieSoundProvider;
import net.wots.sound.ModSounds;

import java.util.*;

public class NPlushBlock extends BlockWithEntity implements PlushieSoundProvider {

    private static final Map<SoundEvent, Integer> SOUND_DURATIONS_N = Map.ofEntries(
            Map.entry(ModSounds.N_NOISE_1,  20),
            Map.entry(ModSounds.N_NOISE_2,  20),
            Map.entry(ModSounds.N_NOISE_3,  20),
            Map.entry(ModSounds.N_NOISE_4,  20),
            Map.entry(ModSounds.N_NOISE_5,  40),
            Map.entry(ModSounds.N_NOISE_6,  120),
            Map.entry(ModSounds.N_NOISE_7,  100),
            Map.entry(ModSounds.N_NOISE_8,  20),
            Map.entry(ModSounds.N_NOISE_9,  20),
            Map.entry(ModSounds.N_NOISE_10, 20),
            Map.entry(ModSounds.N_NOISE_11, 20),
            Map.entry(ModSounds.N_NOISE_12, 20),
            Map.entry(ModSounds.N_NOISE_13, 20),
            Map.entry(ModSounds.N_NOISE_14, 20),
            Map.entry(ModSounds.N_NOISE_15, 100)
    );

    private static final List<SoundEvent> SOUNDS = new ArrayList<>(SOUND_DURATIONS_N.keySet());
    private static final Map<BlockPos, Long> SOUND_COOLDOWNS = new HashMap<>();
    private static int soundIndex = 0;
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

            SOUND_COOLDOWNS.put(key, currentTime + SOUND_DURATIONS_N.get(randomSound));
            world.playSound(null, shelfPos, randomSound, SoundCategory.BLOCKS, 1.0f, 1.0f);
        }

        // Trigger the animation client-side only
        if (world.isClient && world.getBlockEntity(shelfPos) instanceof NPlushBlockEntity blockEntity) {
            blockEntity.triggerAnim("controller", "bounce");
        }
    }
}