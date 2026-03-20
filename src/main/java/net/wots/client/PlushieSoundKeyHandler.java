package net.wots.client;

import io.wispforest.accessories.api.AccessoriesCapability;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.wots.item.accessory.PlushieSoundAccessory;
import net.wots.network.PlushieSoundPayloads;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlushieSoundKeyHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger("wots/plushie-sound");

    private static final String SLOT_HAT  = "hat";
    private static final String SLOT_BACK = "back";

    // ── Client side ───────────────────────────────────────────────────────────

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;

            if (ModKeybindings.PLUSHIE_HAT_SOUND_KEY.wasPressed()) {
                LOGGER.info("[WOTS] Hat keybind pressed — sending packet");
                ClientPlayNetworking.send(new PlushieSoundPayloads.HatPayload());
            }

            if (ModKeybindings.PLUSHIE_BACK_SOUND_KEY.wasPressed()) {
                LOGGER.info("[WOTS] Back keybind pressed — sending packet");
                ClientPlayNetworking.send(new PlushieSoundPayloads.BackPayload());
            }
        });
    }

    // ── Server side ───────────────────────────────────────────────────────────

    public static void registerServerReceiver() {
        ServerPlayNetworking.registerGlobalReceiver(
                PlushieSoundPayloads.HatPayload.ID,
                (payload, context) -> context.server().execute(() -> {
                    LOGGER.info("[WOTS] Hat packet received for player: {}", context.player().getName().getString());
                    tryPlayForSlot(context.player(), SLOT_HAT);
                })
        );

        ServerPlayNetworking.registerGlobalReceiver(
                PlushieSoundPayloads.BackPayload.ID,
                (payload, context) -> context.server().execute(() -> {
                    LOGGER.info("[WOTS] Back packet received for player: {}", context.player().getName().getString());
                    tryPlayForSlot(context.player(), SLOT_BACK);
                })
        );
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private static void tryPlayForSlot(PlayerEntity player, String slotName) {
        var capability = AccessoriesCapability.get(player);
        if (capability == null) {
            LOGGER.warn("[WOTS] AccessoriesCapability is NULL for player {}", player.getName().getString());
            return;
        }

        // Log ALL available slot names so we can see what Accessories actually registered
        LOGGER.info("[WOTS] Available slots: {}", capability.getContainers().keySet());

        var container = capability.getContainers().get(slotName);
        if (container == null) {
            LOGGER.warn("[WOTS] No container found for slot '{}' — check slot name above", slotName);
            return;
        }

        var accessories = container.getAccessories();
        LOGGER.info("[WOTS] Slot '{}' has {} stacks", slotName, accessories.size());

        for (int i = 0; i < accessories.size(); i++) {
            ItemStack stack = accessories.getStack(i);
            LOGGER.info("[WOTS]   Stack {}: {} (implements PlushieSoundAccessory: {})",
                    i,
                    stack.isEmpty() ? "EMPTY" : stack.getItem().getClass().getSimpleName(),
                    stack.isEmpty() ? "n/a" : (stack.getItem() instanceof PlushieSoundAccessory)
            );
            if (stack.isEmpty()) continue;

            if (stack.getItem() instanceof PlushieSoundAccessory soundItem) {
                LOGGER.info("[WOTS] Playing sound for slot '{}'", slotName);
                soundItem.playNextPlushieSound(player);
                return;
            }
        }

        LOGGER.warn("[WOTS] No PlushieSoundAccessory item found in slot '{}'", slotName);
    }
}