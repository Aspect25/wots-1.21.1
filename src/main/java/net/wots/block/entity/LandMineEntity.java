package net.wots.block.entity;


import net.minecraft.world.entity.EntityType;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import com.geckolib.animatable.GeoEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.util.GeckoLibUtil;

import java.util.List;

public class LandMineEntity extends PathfinderMob implements GeoEntity {

    private static final int FUSE_LENGTH = 40;
    private boolean triggered = false;
    private int fuseTicks = -1;

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public LandMineEntity(EntityType<? extends LandMineEntity> type, Level level) {
        super(type, level);
        this.setNoAi(true);
        this.setCustomName(Component.literal("\u00a7aI'm a mine!"));
        this.setCustomNameVisible(true);
    }

    @Override
    protected void registerGoals() {}

    @Override
    public void tick() {
        super.tick();
        this.setDeltaMovement(0, this.getDeltaMovement().y, 0);

        if (this.level().isClientSide()) return;

        if (!triggered) {
            List<Player> nearby = this.level().getEntitiesOfClass(
                    Player.class,
                    this.getBoundingBox().inflate(1.0, 0.4, 1.0),
                    p -> !p.isCreative() && !p.isSpectator()
            );
            if (!nearby.isEmpty()) {
                triggered = true;
                fuseTicks = FUSE_LENGTH;
                this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                        SoundEvents.TNT_PRIMED, SoundSource.BLOCKS, 1.0f, 1.0f);
                this.level().players().forEach(p -> {
                        if (p instanceof ServerPlayer sp) sp.sendOverlayMessage(Component.literal("\u00a7c\u00a7lMINE! MINE! MINE!"));
                });
            }
        }

        if (triggered && fuseTicks > 0) {
            fuseTicks--;
            if (fuseTicks % 4 == 0 && this.level() instanceof ServerLevel sw) {
                sw.sendParticles(ParticleTypes.SMOKE,
                        this.getX(), this.getY() + 0.3, this.getZ(),
                        3, 0.1, 0.1, 0.1, 0.02);
            }
            if (fuseTicks <= 0) {
                this.discard();
                this.level().explode(this,
                        this.getX(), this.getY(), this.getZ(),
                        3.5f, Level.ExplosionInteraction.MOB);
            }
        }
    }

    @Override
    public boolean isPushedByFluid() { return false; }

    @Override
    public boolean hurtServer(ServerLevel serverLevel, DamageSource source, float amount) {
        return !triggered && super.hurtServer(serverLevel, source, amount);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {}

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() { return cache; }
}
