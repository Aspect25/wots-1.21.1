package net.wots.network;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.core.BlockPos;

/**
 * Server -> Client payload that tells clients to spawn variant-change
 * particles at a plushie block position.
 *
 * The color int is the variant's color from the enum (e.g. 0xC94C4C for Angy).
 */
public record VariantChangeParticlePayload(BlockPos pos, int color) implements CustomPacketPayload {

    public static final Type<VariantChangeParticlePayload> TYPE =
            new Type<>(Identifier.fromNamespaceAndPath("wots", "variant_change_particles"));

    public static final StreamCodec<RegistryFriendlyByteBuf, VariantChangeParticlePayload> CODEC =
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC, VariantChangeParticlePayload::pos,
                    ByteBufCodecs.VAR_INT,  VariantChangeParticlePayload::color,
                    VariantChangeParticlePayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }

    public static void register() {
        PayloadTypeRegistry.clientboundPlay().register(TYPE, CODEC);
    }
}
