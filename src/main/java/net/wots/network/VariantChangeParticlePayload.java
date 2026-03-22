package net.wots.network;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

/**
 * Server → Client payload that tells clients to spawn variant-change
 * particles at a plushie block position.
 *
 * The color int is the variant's color from the enum (e.g. 0xC94C4C for Angy).
 */
public record VariantChangeParticlePayload(BlockPos pos, int color) implements CustomPayload {

    public static final Id<VariantChangeParticlePayload> ID =
            new Id<>(Identifier.of("wots", "variant_change_particles"));

    public static final PacketCodec<RegistryByteBuf, VariantChangeParticlePayload> CODEC =
            PacketCodec.tuple(
                    BlockPos.PACKET_CODEC, VariantChangeParticlePayload::pos,
                    PacketCodecs.VAR_INT,  VariantChangeParticlePayload::color,
                    VariantChangeParticlePayload::new
            );

    @Override
    public Id<? extends CustomPayload> getId() { return ID; }

    public static void register() {
        PayloadTypeRegistry.playS2C().register(ID, CODEC);
    }
}
