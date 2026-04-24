package net.wots.block.entity;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.network.protocol.game.ClientboundStopSoundPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.wots.block.ModBlocks;
import net.wots.block.plushies.cynplush.CynPlushBlock;
import net.wots.particle.ModParticles;
import net.wots.sound.ModSounds;
import com.geckolib.animatable.GeoBlockEntity;
import com.geckolib.animation.AnimationController;
import com.geckolib.animation.RawAnimation;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.animation.object.PlayState;
import com.geckolib.animation.object.LoopType;
import com.geckolib.util.GeckoLibUtil;

import java.util.*;
import net.wots.util.ShuffledSoundQueue;
import net.wots.block.plushies.VariantHolder;
import net.wots.block.plushies.cynplush.CynPlushVariant;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.core.HolderLookup;

public class CynPlushBlockEntity extends BlockEntity implements GeoBlockEntity, VariantHolder<CynPlushVariant> {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    // ── Cluster hum constants ─────────────────────────────────────────────────
    /** How often (in ticks) we scan for nearby Cyn plushies. 100 = every 5 sec. */
    private static final int HUM_CHECK_INTERVAL = 100;
    /** Horizontal/vertical radius to scan for sibling Cyns. */
    private static final int HUM_SCAN_RADIUS_H = 6;
    private static final int HUM_SCAN_RADIUS_V = 2;
    /** Minimum cluster size before particles start. A lone Cyn is silent. */
    private static final int HUM_MIN_COUNT = 2;

    public static final Map<SoundEvent, Integer> SOUND_DURATIONS = Map.ofEntries(
            Map.entry(ModSounds.CYN_NOISE_1,  40),
            Map.entry(ModSounds.CYN_NOISE_2,  20),
            Map.entry(ModSounds.CYN_NOISE_3,  80),
            Map.entry(ModSounds.CYN_NOISE_4,  80),
            Map.entry(ModSounds.CYN_NOISE_5,  80),
            Map.entry(ModSounds.CYN_NOISE_6,  20),
            Map.entry(ModSounds.CYN_NOISE_7,  60),
            Map.entry(ModSounds.CYN_NOISE_8,  60),
            Map.entry(ModSounds.CYN_NOISE_10, 60)
    );

    private final ShuffledSoundQueue soundQueue;
    private CynPlushVariant variant = CynPlushVariant.CYN_PLUSH;

    public CynPlushBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.CYN_PLUSH_BLOCK_ENTITY, pos, state);
        soundQueue = new ShuffledSoundQueue(SOUND_DURATIONS);
    }

    // ── VariantHolder ────────────────────────────────────────────────────────

    @Override
    public Class<CynPlushVariant> variantEnumClass() { return CynPlushVariant.class; }

    @Override
    public CynPlushVariant getVariant() { return variant; }

    @Override
    public void setVariant(CynPlushVariant variant) {
        this.variant = variant;
        setChanged();
        if (level instanceof ServerLevel sw) {
            sw.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
        }
    }

    // ── NBT ──────────────────────────────────────────────────────────────────

    @Override
    protected void saveAdditional(net.minecraft.world.level.storage.ValueOutput output) {
        super.saveAdditional(output);
        output.putString("Variant", variant.name());
    }

    @Override
    protected void loadAdditional(net.minecraft.world.level.storage.ValueInput input) {
        super.loadAdditional(input);
        try {
            variant = CynPlushVariant.valueOf(input.getStringOr("Variant", "CYN_PLUSH"));
        } catch (IllegalArgumentException e) {
            variant = CynPlushVariant.CYN_PLUSH;
        }
    }

    // ── Sync ─────────────────────────────────────────────────────────────────

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registryLookup) {
        return saveWithFullMetadata(registryLookup);
    }

    // ── Server tick: solver particle system ───────────────────────────────────
    public static void tick(Level level, BlockPos pos, BlockState state, CynPlushBlockEntity be) {
        if (level.isClientSide()) return;
        if (level.getGameTime() % HUM_CHECK_INTERVAL != 0) return;

        // Count nearby Cyn plushies (not counting ourselves).
        int cynCount = 0;
        for (int dx = -HUM_SCAN_RADIUS_H; dx <= HUM_SCAN_RADIUS_H; dx++) {
            for (int dy = -HUM_SCAN_RADIUS_V; dy <= HUM_SCAN_RADIUS_V; dy++) {
                for (int dz = -HUM_SCAN_RADIUS_H; dz <= HUM_SCAN_RADIUS_H; dz++) {
                    if (dx == 0 && dy == 0 && dz == 0) continue;
                    if (level.getBlockState(pos.offset(dx, dy, dz)).getBlock() instanceof CynPlushBlock) {
                        cynCount++;
                    }
                }
            }
        }

        if (cynCount < HUM_MIN_COUNT) return;
        if (!(level instanceof ServerLevel serverWorld)) return;

        double cx = pos.getX() + 0.5;
        double cy = pos.getY() + 0.5;
        double cz = pos.getZ() + 0.5;

        // ── CORE ORBS: orbital bright particles ──────────────────────────────
        // Scale count with cluster size: 2 Cyns = 3, 5+ = 8
        int coreCount = Math.min(3 + cynCount, 8);
        for (int i = 0; i < coreCount; i++) {
            double angle = (2 * Math.PI / coreCount) * i;
            double radius = 0.3 + level.getRandom().nextDouble() * 0.25;
            double px = cx + Math.cos(angle) * radius;
            double pz = cz + Math.sin(angle) * radius;
            double py = cy + (level.getRandom().nextDouble() - 0.3) * 0.5;

            serverWorld.sendParticles(ModParticles.SOLVER_PARTICLE,
                    px, py, pz, 1,
                    Math.cos(angle) * 0.01, 0.005, Math.sin(angle) * 0.01,
                    0.01);
        }

        // ── WISPS: slow drifting tendrils ────────────────────────────────────
        // Fewer wisps, they last longer so they accumulate
        int wispCount = Math.min(1 + cynCount / 2, 4);
        for (int i = 0; i < wispCount; i++) {
            double angle = level.getRandom().nextDouble() * Math.PI * 2;
            double radius = 0.2 + level.getRandom().nextDouble() * 0.3;
            double px = cx + Math.cos(angle) * radius;
            double pz = cz + Math.sin(angle) * radius;
            double py = cy - 0.1 + level.getRandom().nextDouble() * 0.4;

            serverWorld.sendParticles(ModParticles.SOLVER_WISP,
                    px, py, pz, 1,
                    Math.cos(angle) * 0.003, 0.001, Math.sin(angle) * 0.003,
                    0.005);
        }

        // ── SPARKS: quick bright flashes ─────────────────────────────────────
        // More sparks with bigger clusters — adds energy
        int sparkCount = Math.min(2 + cynCount, 10);
        for (int i = 0; i < sparkCount; i++) {
            double px = cx + (level.getRandom().nextDouble() - 0.5) * 0.4;
            double py = cy + (level.getRandom().nextDouble() - 0.3) * 0.6;
            double pz = cz + (level.getRandom().nextDouble() - 0.5) * 0.4;

            double angle = level.getRandom().nextDouble() * Math.PI * 2;
            double speed = 0.03 + level.getRandom().nextDouble() * 0.05;

            serverWorld.sendParticles(ModParticles.SOLVER_SPARK,
                    px, py, pz, 1,
                    Math.cos(angle) * speed, 0.02 + level.getRandom().nextDouble() * 0.04,
                    Math.sin(angle) * speed,
                    0.02);
        }

        // ── RING PULSE: one expanding ring per cycle ─────────────────────────
        // Only spawn if cluster is big enough (3+)
        if (cynCount >= 3) {
            serverWorld.sendParticles(ModParticles.SOLVER_RING,
                    cx, cy, cz, 1,
                    0, 0.001, 0,
                    0.01);
        }
    }

    // ── Click sound ───────────────────────────────────────────────────────────
    public void playNextSound() {
        if (level == null || level.isClientSide()) return;
        SoundEvent sound = soundQueue.tryAdvance(level.getGameTime());
        if (sound != null) level.playSound(null, getBlockPos(), sound, SoundSource.BLOCKS, 1.0f, 1.0f);
    }

    public void stopSound() {
        if (level == null || level.isClientSide()) return;
        SoundEvent current = soundQueue.getCurrentlyPlaying();
        if (current == null) return;
        ClientboundStopSoundPacket packet = new ClientboundStopSoundPacket(current.location(), SoundSource.BLOCKS);
        ((ServerLevel) level).getPlayers(
                player -> player.distanceToSqr(Vec3.atCenterOf(getBlockPos())) < 64 * 64
        ).forEach(player -> player.connection.send(packet));
        soundQueue.clearCurrent();
    }

    // ── GeckoLib ──────────────────────────────────────────────────────────────
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(
                new AnimationController<>("controller", 2, state -> PlayState.STOP)
                        .triggerableAnim("bounce", RawAnimation.begin()
                                .then("squeesh", LoopType.PLAY_ONCE))
        );
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
