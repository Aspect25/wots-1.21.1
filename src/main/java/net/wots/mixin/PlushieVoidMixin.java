package net.wots.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.wots.block.ModBlocks;
import net.wots.unlock.VariantUnlockManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Detects when a plushie ItemEntity falls into the void.
 * Triggers the "sadge" variant unlock for the player who threw it.
 */
@Mixin(ItemEntity.class)
public abstract class PlushieVoidMixin {

    @Inject(method = "tick", at = @At("HEAD"))
    private void wots$checkVoidFall(CallbackInfo ci) {
        ItemEntity self = (ItemEntity) (Object) this;
        if (self.level().isClientSide()) return;
        if (!self.isAlive()) return;

        // Check if below world bottom
        if (self.getY() < self.level().getMinY() - 32) {
            Item item = self.getItem().getItem();

            String character = null;
            if (item == ModBlocks.N_PLUSH.asItem()) character = "n";
            else if (item == ModBlocks.UZI_PLUSH.asItem()) character = "uzi";
            else if (item == ModBlocks.DOLL_PLUSH.asItem()) character = "doll";
            else if (item == ModBlocks.CYN_PLUSH.asItem()) character = "cyn";
            else if (item == ModBlocks.LIZZY_PLUSH.asItem()) character = "lizzy";

            if (character != null && self.level() instanceof ServerLevel serverWorld) {
                // Find the thrower/owner
                Entity owner = self.getOwner();
                if (owner instanceof ServerPlayer player) {
                    VariantUnlockManager.onPlushieDroppedInVoid(player, character);
                } else {
                    // Fallback: find nearest player within 64 blocks
                    Player closest = serverWorld.getNearestPlayer(
                            self.getX(), self.getY() + 64, self.getZ(), 64, false
                    );
                    if (closest instanceof ServerPlayer serverPlayer) {
                        VariantUnlockManager.onPlushieDroppedInVoid(serverPlayer, character);
                    }
                }
            }
        }
    }
}
