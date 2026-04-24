package net.wots.network;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

import java.util.HashSet;
import java.util.Set;

/**
 * Server -> Client payload that syncs which variants a player has unlocked.
 */
public record SyncVariantUnlocksPayload(Set<String> unlocks) implements CustomPacketPayload {

    public static final Type<SyncVariantUnlocksPayload> TYPE =
            new Type<>(Identifier.fromNamespaceAndPath("wots", "sync_variant_unlocks"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SyncVariantUnlocksPayload> CODEC =
            new StreamCodec<>() {
                @Override
                public SyncVariantUnlocksPayload decode(RegistryFriendlyByteBuf buf) {
                    int count = buf.readVarInt();
                    Set<String> set = new HashSet<>();
                    for (int i = 0; i < count; i++) {
                        set.add(buf.readUtf());
                    }
                    return new SyncVariantUnlocksPayload(set);
                }

                @Override
                public void encode(RegistryFriendlyByteBuf buf, SyncVariantUnlocksPayload payload) {
                    buf.writeVarInt(payload.unlocks().size());
                    for (String s : payload.unlocks()) {
                        buf.writeUtf(s);
                    }
                }
            };

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }

    public static void register() {
        PayloadTypeRegistry.clientboundPlay().register(TYPE, CODEC);
    }
}
