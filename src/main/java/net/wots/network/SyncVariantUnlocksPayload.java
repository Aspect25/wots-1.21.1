package net.wots.network;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.util.HashSet;
import java.util.Set;

/**
 * Server → Client payload that syncs which variants a player has unlocked.
 */
public record SyncVariantUnlocksPayload(Set<String> unlocks) implements CustomPayload {

    public static final Id<SyncVariantUnlocksPayload> ID =
            new Id<>(Identifier.of("wots", "sync_variant_unlocks"));

    public static final PacketCodec<RegistryByteBuf, SyncVariantUnlocksPayload> CODEC =
            new PacketCodec<>() {
                @Override
                public SyncVariantUnlocksPayload decode(RegistryByteBuf buf) {
                    int count = buf.readVarInt();
                    Set<String> set = new HashSet<>();
                    for (int i = 0; i < count; i++) {
                        set.add(buf.readString());
                    }
                    return new SyncVariantUnlocksPayload(set);
                }

                @Override
                public void encode(RegistryByteBuf buf, SyncVariantUnlocksPayload payload) {
                    buf.writeVarInt(payload.unlocks().size());
                    for (String s : payload.unlocks()) {
                        buf.writeString(s);
                    }
                }
            };

    @Override
    public Id<? extends CustomPayload> getId() { return ID; }

    public static void register() {
        PayloadTypeRegistry.playS2C().register(ID, CODEC);
    }
}
