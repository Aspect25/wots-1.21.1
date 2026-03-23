package net.wots.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.StopSoundS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.wots.block.ModBlocks;
import net.wots.block.plushies.nplush.NPlushVariant;
import net.wots.sound.ModSounds;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.*;
import net.wots.util.ShuffledSoundQueue;

public class NPlushBlockEntity extends BlockEntity implements GeoBlockEntity {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    // ── Aura constants ────────────────────────────────────────────────────────
    /** Radius in blocks where N confuses nearby hostile mob targeting. */
    private static final double AURA_RADIUS = 8.0;
    /**
     * Every N ticks the aura runs. 40 = every 2 seconds.
     * Low enough to feel responsive, high enough not to tank performance.
     */
    private static final int AURA_INTERVAL = 40;
    /**
     * Probability per mob per aura pulse that its target is cleared.
     * 35% feels subtle — mobs hesitate, they don't ignore the player forever.
     */
    private static final float AURA_CLEAR_CHANCE = 0.35f;

    // ── Variant ───────────────────────────────────────────────────────────────
    private NPlushVariant variant = NPlushVariant.N_PLUSH;

    public NPlushVariant getVariant() { return variant; }

    public void setVariant(NPlushVariant variant) {
        this.variant = variant;
        markDirty();
        if (world instanceof ServerWorld sw) {
            sw.updateListeners(pos, getCachedState(), getCachedState(), 3);
        }
    }

    // ── Tick ──────────────────────────────────────────────────────────────────
    public static void tick(World world, BlockPos pos, BlockState state, NPlushBlockEntity be) {
        // ── N's aura: confuse nearby hostile mob targeting ────────────────
        // Runs server-side only on a fixed interval.
        // Each hostile mob in range has a small chance of losing its current
        // target for one tick — a brief hesitation, not a permanent repel.
        if (!world.isClient && world.getTime() % AURA_INTERVAL == 0) {
            Box auraBox = Box.of(Vec3d.ofCenter(pos), AURA_RADIUS * 2, AURA_RADIUS * 2, AURA_RADIUS * 2);
            world.getEntitiesByClass(MobEntity.class, auraBox, e -> e instanceof HostileEntity)
                    .forEach(mob -> {
                        if (mob.getTarget() != null && world.random.nextFloat() < AURA_CLEAR_CHANCE) {
                            mob.setTarget(null);
                        }
                    });
        }
    }

    // ── Sound ─────────────────────────────────────────────────────────────────
    public static final Map<SoundEvent, Integer> SOUND_DURATIONS = Map.ofEntries(
            Map.entry(ModSounds.N_NOISE_1,   20),
            Map.entry(ModSounds.N_NOISE_2,   20),
            Map.entry(ModSounds.N_NOISE_3,   20),
            Map.entry(ModSounds.N_NOISE_4,   20),
            Map.entry(ModSounds.N_NOISE_5,   40),
            Map.entry(ModSounds.N_NOISE_6,  120),
            Map.entry(ModSounds.N_NOISE_7,  100),
            Map.entry(ModSounds.N_NOISE_8,   20),
            Map.entry(ModSounds.N_NOISE_9,   20),
            Map.entry(ModSounds.N_NOISE_10,  20),
            Map.entry(ModSounds.N_NOISE_11,  20),
            Map.entry(ModSounds.N_NOISE_12,  20),
            Map.entry(ModSounds.N_NOISE_13,  20),
            Map.entry(ModSounds.N_NOISE_14,  20),
            Map.entry(ModSounds.N_NOISE_15, 100)
    );

    private final ShuffledSoundQueue soundQueue;

    public NPlushBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.N_PLUSH_BLOCK_ENTITY, pos, state);
        soundQueue = new ShuffledSoundQueue(SOUND_DURATIONS);
    }

    public void playNextSound() {
        if (world == null || world.isClient) return;
        SoundEvent sound = soundQueue.tryAdvance(world.getTime());
        if (sound != null) world.playSound(null, pos, sound, SoundCategory.BLOCKS, 1.0f, 1.0f);
    }

    public void stopSound() {
        if (world == null || world.isClient) return;
        SoundEvent current = soundQueue.getCurrentlyPlaying();
        if (current == null) return;
        StopSoundS2CPacket packet = new StopSoundS2CPacket(current.getId(), SoundCategory.BLOCKS);
        ((ServerWorld) world).getPlayers(
                player -> player.squaredDistanceTo(Vec3d.ofCenter(pos)) < 64 * 64
        ).forEach(player -> ((ServerPlayerEntity) player).networkHandler.sendPacket(packet));
        soundQueue.clearCurrent();
    }

    // ── NBT ───────────────────────────────────────────────────────────────────
    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        nbt.putString("Variant", variant.name());
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        try {
            variant = NPlushVariant.valueOf(nbt.getString("Variant"));
        } catch (IllegalArgumentException e) {
            variant = NPlushVariant.N_PLUSH;
        }
    }

    // ── Sync ──────────────────────────────────────────────────────────────────
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        return createNbt(registryLookup);
    }

    // ── GeckoLib ──────────────────────────────────────────────────────────────
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(
                new AnimationController<>(this, "controller", 2, state -> PlayState.STOP)
                        .triggerableAnim("bounce", RawAnimation.begin()
                                .then("squeesh", Animation.LoopType.PLAY_ONCE))
        );
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}