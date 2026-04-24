package net.wots.client;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.wots.inventory.PlushieInventory;
import net.wots.item.accessory.PlushieSoundAccessory;
import net.wots.network.PlushieSoundPayloads;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlushieSoundKeyHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger("wots/plushie-sound");

    // ── Client side ───────────────────────────────────────────────────────────

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;

            if (ModKeybindings.PLUSHIE_HAT_SOUND_KEY.consumeClick()) {
                LOGGER.debug("[WOTS] Hat keybind pressed — sending packet");
                ClientPlayNetworking.send(new PlushieSoundPayloads.HatPayload());
            }

            if (ModKeybindings.PLUSHIE_BACK_SOUND_KEY.consumeClick()) {
                LOGGER.debug("[WOTS] Back keybind pressed — sending packet");
                ClientPlayNetworking.send(new PlushieSoundPayloads.BackPayload());
            }
        });
    }

    // ── Server side ───────────────────────────────────────────────────────────

    public static void registerServerReceiver() {
        ServerPlayNetworking.registerGlobalReceiver(
                PlushieSoundPayloads.HatPayload.TYPE,
                (payload, context) -> context.server().execute(() -> {
                    LOGGER.debug("[WOTS] Hat packet received for player: {}", context.player().getName().getString());
                    tryPlayForSlot(context.player(), PlushieInventory.HAT_SLOT);
                })
        );

        ServerPlayNetworking.registerGlobalReceiver(
                PlushieSoundPayloads.BackPayload.TYPE,
                (payload, context) -> context.server().execute(() -> {
                    LOGGER.debug("[WOTS] Back packet received for player: {}", context.player().getName().getString());
                    tryPlayForSlot(context.player(), PlushieInventory.BACK_SLOT);
                })
        );
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private static void tryPlayForSlot(Player player, int slotIndex) {
        PlushieInventory inv = PlushieInventory.get(player);
        ItemStack stack = inv.getStack(slotIndex);

        if (stack.isEmpty()) {
            LOGGER.debug("[WOTS] Plushie slot {} is empty", slotIndex);
            return;
        }

        if (stack.getItem() instanceof PlushieSoundAccessory soundItem) {
            LOGGER.debug("[WOTS] Playing sound for plushie slot {}", slotIndex);
            soundItem.playNextPlushieSound(player, stack);
        } else {
            LOGGER.debug("[WOTS] Item in slot {} does not support sound playback", slotIndex);
        }
    }
}
