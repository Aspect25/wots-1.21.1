package net.wots.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

/**
 * Two tiny C2S payloads -- one per slot.
 * Register both in Wots.java via PayloadTypeRegistry.
 */
public final class PlushieSoundPayloads {

    // -- Hat ---
    public record HatPayload() implements CustomPacketPayload {
        public static final CustomPacketPayload.Type<HatPayload> TYPE =
                new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath("wots", "plushie_hat_sound"));
        public static final StreamCodec<FriendlyByteBuf, HatPayload> CODEC =
                StreamCodec.unit(new HatPayload());

        @Override
        public CustomPacketPayload.Type<? extends CustomPacketPayload> type() { return TYPE; }
    }

    // -- Back ---
    public record BackPayload() implements CustomPacketPayload {
        public static final CustomPacketPayload.Type<BackPayload> TYPE =
                new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath("wots", "plushie_back_sound"));
        public static final StreamCodec<FriendlyByteBuf, BackPayload> CODEC =
                StreamCodec.unit(new BackPayload());

        @Override
        public CustomPacketPayload.Type<? extends CustomPacketPayload> type() { return TYPE; }
    }

    private PlushieSoundPayloads() {}
}
