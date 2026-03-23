package net.wots.network;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.wots.block.entity.UziPlushBlockEntity;
import net.wots.block.plushies.uziplush.UziPlushVariant;
import net.wots.unlock.VariantUnlockData;

public record SetUziVariantPayload(BlockPos pos, String variantName)
        implements CustomPayload {

    public static final Id<SetUziVariantPayload> ID =
            new Id<>(Identifier.of("wots", "set_uzi_variant"));

    public static final PacketCodec<RegistryByteBuf, SetUziVariantPayload> CODEC =
            PacketCodec.tuple(
                    BlockPos.PACKET_CODEC, SetUziVariantPayload::pos,
                    PacketCodecs.STRING,   SetUziVariantPayload::variantName,
                    SetUziVariantPayload::new
            );

    @Override
    public Id<? extends CustomPayload> getId() { return ID; }

    public static void registerServer() {
        PayloadTypeRegistry.playC2S().register(ID, CODEC);

        ServerPlayNetworking.registerGlobalReceiver(ID, (payload, ctx) ->
                ctx.server().execute(() -> {
                    ServerPlayerEntity player = ctx.player();
                    var world = player.getServerWorld();
                    if (world.getBlockEntity(payload.pos()) instanceof UziPlushBlockEntity be) {
                        try {
                            UziPlushVariant variant = UziPlushVariant.valueOf(payload.variantName());

                            // ── Unlock check ──────────────────────────────
                            VariantUnlockData data = VariantUnlockData.get(ctx.server());
                            if (!data.isUnlocked(player.getUuid(), variant.name())) {
                                return; // Variant is locked — ignore the request
                            }

                            UziPlushVariant oldVariant = be.getVariant();
                            be.setVariant(variant);

                            // ── Particle effect on variant change ─────────
                            if (oldVariant != variant) {
                                sendVariantChangeParticles(world, payload.pos(), variant.color);
                            }
                        } catch (IllegalArgumentException ignored) {}
                    }
                })
        );
    }

    private static void sendVariantChangeParticles(ServerWorld world, BlockPos pos, int color) {
        VariantChangeParticlePayload particlePayload = new VariantChangeParticlePayload(pos, color);
        for (ServerPlayerEntity nearby : world.getPlayers(
                p -> p.squaredDistanceTo(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) < 64 * 64)) {
            ServerPlayNetworking.send(nearby, particlePayload);
        }
    }
}