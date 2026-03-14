package net.wots.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.packet.s2c.play.StopSoundS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.wots.block.ModBlocks;
import net.wots.sound.ModSounds;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.*;

public class NPlushBlockEntity extends BlockEntity implements GeoBlockEntity {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    // ── Per-instance shuffle state ────────────────────────────────────────────
    private static final Map<SoundEvent, Integer> SOUND_DURATIONS = Map.ofEntries(
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

    private final List<SoundEvent> sounds;
    private int soundIndex = 0;
    private SoundEvent lastPlayed = null;
    private SoundEvent currentSound = null;
    private long cooldownEnd = 0L;

    public NPlushBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.N_PLUSH_BLOCK_ENTITY, pos, state);
        sounds = new ArrayList<>(SOUND_DURATIONS.keySet());
        Collections.shuffle(sounds);
    }

    // ── Sound playback ────────────────────────────────────────────────────────
    public void playNextSound() {
        if (world == null || world.isClient) return;

        long currentTime = world.getTime();
        if (currentTime < cooldownEnd) return;

        if (soundIndex == 0) {
            Collections.shuffle(sounds);
            if (sounds.size() > 1 && sounds.get(0).equals(lastPlayed)) {
                Collections.swap(sounds, 0, 1);
            }
        }

        SoundEvent sound = sounds.get(soundIndex);
        lastPlayed = sound;
        currentSound = sound;
        soundIndex = (soundIndex + 1) % sounds.size();

        cooldownEnd = currentTime + SOUND_DURATIONS.get(sound);
        world.playSound(null, pos, sound, SoundCategory.BLOCKS, 1.0f, 1.0f);
    }

    public void stopSound() {
        if (world == null || world.isClient || currentSound == null) return;

        StopSoundS2CPacket packet = new StopSoundS2CPacket(currentSound.getId(), SoundCategory.BLOCKS);
        ((ServerWorld) world).getPlayers(
                player -> player.squaredDistanceTo(Vec3d.ofCenter(pos)) < 64 * 64
        ).forEach(player -> ((ServerPlayerEntity) player).networkHandler.sendPacket(packet));

        currentSound = null;
        cooldownEnd = 0L;
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