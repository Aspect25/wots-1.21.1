package net.wots.network;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

/**
 * Two tiny C2S payloads — one per slot.
 * Register both in Wots.java via PayloadTypeRegistry.
 */
public final class PlushieSoundPayloads {

    // ── Hat ───────────────────────────────────────────────────────────────────

    public record HatPayload() implements CustomPayload {
        public static final CustomPayload.Id<HatPayload> ID =
                new CustomPayload.Id<>(Identifier.of("wots", "plushie_hat_sound"));
        public static final PacketCodec<PacketByteBuf, HatPayload> CODEC =
                PacketCodec.unit(new HatPayload());

        @Override
        public CustomPayload.Id<? extends CustomPayload> getId() { return ID; }
    }

    // ── Back ──────────────────────────────────────────────────────────────────

    public record BackPayload() implements CustomPayload {
        public static final CustomPayload.Id<BackPayload> ID =
                new CustomPayload.Id<>(Identifier.of("wots", "plushie_back_sound"));
        public static final PacketCodec<PacketByteBuf, BackPayload> CODEC =
                PacketCodec.unit(new BackPayload());

        @Override
        public CustomPayload.Id<? extends CustomPayload> getId() { return ID; }
    }

    private PlushieSoundPayloads() {}
}