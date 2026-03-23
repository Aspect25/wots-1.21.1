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
import net.minecraft.world.World;
import net.wots.block.ModBlocks;
import net.wots.block.plushies.cynplush.CynPlushBlock;
import net.wots.sound.ModSounds;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.*;
import net.wots.util.ShuffledSoundQueue;

public class DollPlushBlockEntity extends BlockEntity implements GeoBlockEntity {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);



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

    public DollPlushBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.DOLL_PLUSH_BLOCK_ENTITY, pos, state);
        soundQueue = new ShuffledSoundQueue(SOUND_DURATIONS);
    }



    // ── Click sound ───────────────────────────────────────────────────────────
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
