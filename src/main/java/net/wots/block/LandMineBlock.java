package net.wots.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class LandMineBlock extends Block {

    // ── Edit these ────────────────────────────────────────────────
    private static final int FUSE_TICKS = 40; // 20 ticks = 1 second
    private static final float EXPLOSION_POWER = 3.5f;
    // ─────────────────────────────────────────────────────────────

    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;

    public LandMineBlock(Settings settings) {
        super(settings);
        setDefaultState(getStateManager().getDefaultState().with(FACING, Direction.NORTH));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    public void onSteppedOn(World world, BlockPos pos, BlockState state, Entity entity) {
        if (!world.isClient() && entity instanceof PlayerEntity player && !player.isCreative() && !player.isSpectator()) {
            // Play trigger sound immediately
            world.playSound(null, pos.getX(), pos.getY(), pos.getZ(),
                    SoundEvents.ENTITY_TNT_PRIMED, SoundCategory.BLOCKS, 1.0f, 1.0f);
            // Schedule the explosion after the delay
            world.scheduleBlockTick(pos, this, FUSE_TICKS);
        }
        super.onSteppedOn(world, pos, state, entity);
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        world.removeBlock(pos, false);
        if (world instanceof ServerWorld sw) {
            sw.spawnParticles(ParticleTypes.EXPLOSION,
                    pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5,
                    5, 0.1, 0.1, 0.1, 0.02);
        }
        world.createExplosion(null,
                pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5,
                EXPLOSION_POWER, World.ExplosionSourceType.TNT);
    }
}