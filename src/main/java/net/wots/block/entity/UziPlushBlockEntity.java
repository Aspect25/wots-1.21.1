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
import net.wots.block.plushies.uziplush.UziPlushVariant;
import net.wots.sound.ModSounds;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.*;
import net.wots.util.ShuffledSoundQueue;

public class UziPlushBlockEntity extends BlockEntity implements GeoBlockEntity {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    // ── Variant ───────────────────────────────────────────────────────────────
    private UziPlushVariant variant = UziPlushVariant.UZI_PLUSH;

    // ── Redstone state ────────────────────────────────────────────────────────
    // Persisted so we correctly detect rising edge after chunk reload.
    private boolean wasPowered = false;

    public UziPlushVariant getVariant() { return variant; }
    public boolean wasPowered() { return wasPowered; }

    public void setVariant(UziPlushVariant variant) {
        this.variant = variant;
        markDirty();
        if (world instanceof ServerWorld sw) {
            sw.updateListeners(pos, getCachedState(), getCachedState(), 3);
        }
    }

    public void setWasPowered(boolean powered) {
        if (this.wasPowered != powered) {
            this.wasPowered = powered;
            markDirty();
        }
    }

    // ── Sound ─────────────────────────────────────────────────────────────────
    public static final Map<SoundEvent, Integer> SOUND_DURATIONS = Map.ofEntries(
            Map.entry(ModSounds.UZI_NOISE,   80),
            Map.entry(ModSounds.UZI_NOISE_2, 20),
            Map.entry(ModSounds.UZI_NOISE_3, 40),
            Map.entry(ModSounds.UZI_NOISE_4, 40),
            Map.entry(ModSounds.UZI_NOISE_5, 60),
            Map.entry(ModSounds.UZI_NOISE_6, 60),
            Map.entry(ModSounds.UZI_NOISE_7, 80)
    );

    private final ShuffledSoundQueue soundQueue;

    public UziPlushBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.UZI_PLUSH_BLOCK_ENTITY, pos, state);
        soundQueue = new ShuffledSoundQueue(SOUND_DURATIONS);
    }

    public void playNextSound() {
        if (world == null || world.isClient) return;
        SoundEvent sound = soundQueue.tryAdvance(world.getTime());
        if (sound != null) {
            world.playSound(null, pos, sound, SoundCategory.BLOCKS, 1.0f, 1.0f);
        }
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
        nbt.putBoolean("WasPowered", wasPowered);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        try {
            variant = UziPlushVariant.valueOf(nbt.getString("Variant"));
        } catch (IllegalArgumentException e) {
            variant = UziPlushVariant.UZI_PLUSH;
        }
        wasPowered = nbt.getBoolean("WasPowered");
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