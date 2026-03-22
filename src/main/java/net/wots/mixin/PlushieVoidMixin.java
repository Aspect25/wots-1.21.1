package net.wots.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
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
        if (self.getWorld().isClient) return;
        if (!self.isAlive()) return;

        // Check if below world bottom
        if (self.getY() < self.getWorld().getBottomY() - 32) {
            Item item = self.getStack().getItem();

            String character = null;
            if (item == ModBlocks.N_PLUSH.asItem()) character = "n";
            else if (item == ModBlocks.UZI_PLUSH.asItem()) character = "uzi";

            if (character != null && self.getWorld() instanceof ServerWorld serverWorld) {
                // Find the thrower/owner
                Entity owner = self.getOwner();
                if (owner instanceof ServerPlayerEntity player) {
                    VariantUnlockManager.onPlushieDroppedInVoid(player, character);
                } else {
                    // Fallback: find nearest player within 64 blocks
                    PlayerEntity closest = serverWorld.getClosestPlayer(
                            self.getX(), self.getY() + 64, self.getZ(), 64, false
                    );
                    if (closest instanceof ServerPlayerEntity serverPlayer) {
                        VariantUnlockManager.onPlushieDroppedInVoid(serverPlayer, character);
                    }
                }
            }
        }
    }
}