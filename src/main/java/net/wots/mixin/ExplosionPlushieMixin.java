package net.wots.mixin;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ServerExplosion;
import net.minecraft.world.phys.Vec3;
import net.wots.block.ModBlocks;
import net.wots.unlock.VariantUnlockManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * After an explosion collects blocks/damage, scan for plushies within 5 blocks.
 * Triggers the "traumatized" variant unlock.
 */
@Mixin(ServerExplosion.class)
public abstract class ExplosionPlushieMixin {

    @Shadow @Final private ServerLevel level;
    @Shadow public abstract Vec3 center();

    @Inject(method = "explode", at = @At("TAIL"))
    private void wots$checkForPlushiesNearExplosion(CallbackInfoReturnable<Integer> cir) {
        double x = center().x;
        double y = center().y;
        double z = center().z;
        BlockPos center = BlockPos.containing(x, y, z);

        for (BlockPos pos : BlockPos.withinManhattan(center, 5, 5, 5)) {
            BlockState state = level.getBlockState(pos);

            String character = null;
            if (state.is(ModBlocks.N_PLUSH)) character = "n";
            else if (state.is(ModBlocks.UZI_PLUSH)) character = "uzi";
            else if (state.is(ModBlocks.DOLL_PLUSH)) character = "doll";
            else if (state.is(ModBlocks.CYN_PLUSH)) character = "cyn";
            else if (state.is(ModBlocks.LIZZY_PLUSH)) character = "lizzy";

            if (character != null) {
                Player closest = level.getNearestPlayer(
                        pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 32, false
                );
                if (closest instanceof ServerPlayer serverPlayer) {
                    VariantUnlockManager.onNearbyExplosion(serverPlayer, character);
                }
            }
        }
    }
}
