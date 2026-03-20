package net.wots.block.entity;


import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;

public class LandMineEntity extends PathAwareEntity implements GeoEntity {

    private static final int FUSE_LENGTH = 40;
    private boolean triggered = false;
    private int fuseTicks = -1;

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public LandMineEntity(EntityType<? extends LandMineEntity> type, World world) {
        super(type, world);
        this.setAiDisabled(true);
        this.setCustomName(Text.literal("§aI'm a mine!"));
        this.setCustomNameVisible(true);
    }

    @Override
    protected void initGoals() {}

    @Override
    public void tick() {
        super.tick();
        this.setVelocity(0, this.getVelocity().y, 0);

        if (this.getWorld().isClient()) return;

        if (!triggered) {
            List<PlayerEntity> nearby = this.getWorld().getEntitiesByClass(
                    PlayerEntity.class,
                    this.getBoundingBox().expand(1.0, 0.4, 1.0),
                    p -> !p.isCreative() && !p.isSpectator()
            );
            if (!nearby.isEmpty()) {
                triggered = true;
                fuseTicks = FUSE_LENGTH;
                this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(),
                        SoundEvents.ENTITY_TNT_PRIMED, SoundCategory.BLOCKS, 1.0f, 1.0f);
                this.getWorld().getPlayers().forEach(p ->
                        p.sendMessage(Text.literal("§c§lMINE! MINE! MINE!"), true));
            }
        }

        if (triggered && fuseTicks > 0) {
            fuseTicks--;
            if (fuseTicks % 4 == 0 && this.getWorld() instanceof ServerWorld sw) {
                sw.spawnParticles(ParticleTypes.SMOKE,
                        this.getX(), this.getY() + 0.3, this.getZ(),
                        3, 0.1, 0.1, 0.1, 0.02);
            }
            if (fuseTicks <= 0) {
                this.discard();
                this.getWorld().createExplosion(this,
                        this.getX(), this.getY(), this.getZ(),
                        3.5f, World.ExplosionSourceType.MOB);
            }
        }
    }

    @Override
    public boolean isPushedByFluids() { return false; }

    @Override
    public boolean damage(DamageSource source, float amount) {
        return !triggered && super.damage(source, amount);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {}

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() { return cache; }
}
