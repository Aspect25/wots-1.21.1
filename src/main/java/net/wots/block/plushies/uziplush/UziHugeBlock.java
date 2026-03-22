package net.wots.block.plushies.uziplush;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.wots.block.ModBlocks;
import net.wots.block.entity.UziHugeBlockEntity;
import net.wots.block.plushies.PlushieSoundProvider;
import net.wots.sound.ModSounds;

import java.util.*;
import net.wots.util.ShuffledSoundQueue;

public class UziHugeBlock extends BlockWithEntity implements PlushieSoundProvider {
    public static final List<BlockPos> OFFSETS = new ArrayList<>();
    static {
        for (int y = 0; y <= 2; y++)
            for (int x = -1; x <= 1; x++)
                for (int z = -1; z <= 1; z++)
                    if (x != 0 || y != 0 || z != 0)
                        OFFSETS.add(new BlockPos(x, y, z));
    }

    private static final Map<SoundEvent, Integer> SOUND_DURATIONS = Map.ofEntries(
            Map.entry(ModSounds.UZI_NOISE,   80),
            Map.entry(ModSounds.UZI_NOISE_2, 20),
            Map.entry(ModSounds.UZI_NOISE_3, 40),
            Map.entry(ModSounds.UZI_NOISE_4, 40),
            Map.entry(ModSounds.UZI_NOISE_5, 60),
            Map.entry(ModSounds.UZI_NOISE_6, 60)
    );
    private static final ShuffledSoundQueue SOUND_QUEUE = new ShuffledSoundQueue(SOUND_DURATIONS);

    public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;

    public UziHugeBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH));
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return createCodec(UziHugeBlock::new);
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
        return new UziHugeBlockEntity(pos, state);
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        for (BlockPos offset : OFFSETS) {
            BlockState existing = world.getBlockState(pos.add(offset));
            if (!existing.isAir() && !existing.isReplaceable()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state,
                         LivingEntity placer, ItemStack stack) {
        net.wots.Wots.LOGGER.debug("[UziHuge] onPlaced called, isClient=" + world.isClient);
        if (world.isClient) return;

        net.wots.Wots.LOGGER.debug("[UziHuge] phantom block is: " + ModBlocks.UZI_HUGE_PHANTOM);

        for (BlockPos offset : OFFSETS) {
            BlockPos phantomPos = pos.add(offset);

            boolean placed = world.setBlockState(phantomPos,
                    ModBlocks.UZI_HUGE_PHANTOM.getDefaultState(),
                    Block.NOTIFY_ALL);

            net.wots.Wots.LOGGER.debug("[UziHuge] placing phantom at " + phantomPos + " -> success=" + placed);

            if (!placed) {
                net.wots.Wots.LOGGER.debug("[UziHuge] ROLLBACK triggered at " + phantomPos);
                for (BlockPos rollback : OFFSETS) {
                    BlockPos rbPos = pos.add(rollback);
                    if (world.getBlockState(rbPos).getBlock() instanceof UziHugePhantomBlock) {
                        world.removeBlock(rbPos, false);
                    }
                }
                world.removeBlock(pos, false);
                return;
            }
        }
        net.wots.Wots.LOGGER.debug("[UziHuge] All phantoms placed successfully!");
    }

    @Override
    public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!world.isClient) {
            for (BlockPos offset : OFFSETS) {
                BlockPos phantomPos = pos.add(offset);
                if (world.getBlockState(phantomPos).getBlock() instanceof UziHugePhantomBlock) {
                    world.removeBlock(phantomPos, false);
                }
            }
        }
        return super.onBreak(world, pos, state, player);
    }

    // ── Sound ─────────────────────────────────────────────────────────────────

    // Called when the origin block itself is clicked
    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos,
                                 PlayerEntity player, BlockHitResult hit) {
        playFromPos(world, pos, pos, player);
        return ActionResult.SUCCESS;
    }

    // Called by PlushieSoundProvider — keeps shelf and other code working unchanged
    @Override
    public void onShelfInteract(World world, BlockPos shelfPos, int slot, PlayerEntity player) {
        playFromPos(world, shelfPos, shelfPos, player);
    }

    // Called by UziHugePhantomBlock — passes the clicked phantom pos so SPP
    // raycasts from the player to the exact block they touched, no muffling
    public void playFromPos(World world, BlockPos shelfPos, BlockPos soundPos, PlayerEntity player) {
        if (!world.isClient) {
            SoundEvent sound = SOUND_QUEUE.tryAdvance(world.getTime());
            if (sound != null) world.playSound(null, soundPos, sound, SoundCategory.BLOCKS, 1.0f, 1.0f);
        }

        if (world.isClient && world.getBlockEntity(shelfPos) instanceof UziHugeBlockEntity be) {
            be.triggerAnim("controller", "bounce");
        }
    }
}