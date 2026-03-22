package net.wots.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
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

    // ── Variant ───────────────────────────────────────────────────────────────
    private NPlushVariant variant = NPlushVariant.N_PLUSH;
    private boolean lazyMode = false;
    private int lazyTimer = 0;

    public NPlushVariant getVariant() { return variant; }
    public boolean isLazyMode() { return lazyMode; }

    public void setVariant(NPlushVariant variant) {
        this.variant = variant;
        markDirty();
        if (world instanceof ServerWorld sw) {
            sw.updateListeners(pos, getCachedState(), getCachedState(), 3);
        }
    }

    public void setLazyMode(boolean lazy) {
        this.lazyMode = lazy;
        this.lazyTimer = 0;
        markDirty();
    }

    // ── Lazy Mode tick ────────────────────────────────────────────────────────
    public static void tick(World world, BlockPos pos, BlockState state, NPlushBlockEntity be) {
        if (!be.lazyMode) return;
        be.lazyTimer--;
        if (be.lazyTimer <= 0) {
            be.lazyTimer = 6000 + world.random.nextInt(210000);
            NPlushVariant[] variants = NPlushVariant.values();
            NPlushVariant next;
            do {
                next = variants[world.random.nextInt(variants.length)];
            } while (next == be.variant);
            be.setVariant(next);
        }
    }

    // ── Sound ─────────────────────────────────────────────────────────────────
    // public so NPlushBlock can reuse these durations for shelf-slot sound state
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
        nbt.putBoolean("LazyMode", lazyMode);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        try {
            variant = NPlushVariant.valueOf(nbt.getString("Variant"));
        } catch (IllegalArgumentException e) {
            variant = NPlushVariant.N_PLUSH;
        }
        lazyMode = nbt.getBoolean("LazyMode");
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