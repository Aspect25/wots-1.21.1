package net.wots.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import net.wots.block.ModBlocks;
import net.wots.unlock.VariantUnlockManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * After an explosion collects blocks/damage, scan for plushies within 5 blocks.
 * Triggers the "traumatized" variant unlock.
 */
@Mixin(Explosion.class)
public abstract class ExplosionPlushieMixin {

    @Shadow @Final private World world;

    @Inject(method = "collectBlocksAndDamageEntities", at = @At("TAIL"))
    private void wots$checkForPlushiesNearExplosion(CallbackInfo ci) {
        if (world.isClient || !(world instanceof ServerWorld serverWorld)) return;

        Explosion self = (Explosion) (Object) this;
        double x = self.getPosition().x;
        double y = self.getPosition().y;
        double z = self.getPosition().z;
        BlockPos center = BlockPos.ofFloored(x, y, z);

        // Scan 5-block radius for plushie blocks
        for (BlockPos pos : BlockPos.iterateOutwards(center, 5, 5, 5)) {
            BlockState state = serverWorld.getBlockState(pos);

            String character = null;
            if (state.isOf(ModBlocks.N_PLUSH)) character = "n";
            else if (state.isOf(ModBlocks.UZI_PLUSH)) character = "uzi";

            if (character != null) {
                // getClosestPlayer returns PlayerEntity — cast via instanceof
                PlayerEntity closest = serverWorld.getClosestPlayer(
                        pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 32, false
                );
                if (closest instanceof ServerPlayerEntity serverPlayer) {
                    VariantUnlockManager.onNearbyExplosion(serverPlayer, character);
                }
            }
        }
    }
}