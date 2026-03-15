package net.wots.network;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.wots.block.entity.NPlushBlockEntity;
import net.wots.block.plushies.nplush.NPlushVariant;

public record SetNVariantPayload(BlockPos pos, String variantName, boolean lazyMode)
        implements CustomPayload {

    public static final Id<SetNVariantPayload> ID =
            new Id<>(Identifier.of("wots", "set_n_variant"));

    public static final PacketCodec<RegistryByteBuf, SetNVariantPayload> CODEC =
            PacketCodec.tuple(
                    BlockPos.PACKET_CODEC, SetNVariantPayload::pos,
                    PacketCodecs.STRING,   SetNVariantPayload::variantName,
                    PacketCodecs.BOOL,     SetNVariantPayload::lazyMode,
                    SetNVariantPayload::new
            );

    @Override
    public Id<? extends CustomPayload> getId() { return ID; }

    public static void registerServer() {
        PayloadTypeRegistry.playC2S().register(ID, CODEC);

        ServerPlayNetworking.registerGlobalReceiver(ID, (payload, ctx) ->
                ctx.server().execute(() -> {
                    var world = ctx.player().getServerWorld();
                    if (world.getBlockEntity(payload.pos()) instanceof NPlushBlockEntity be) {
                        if (payload.lazyMode()) {
                            be.setLazyMode(true);
                        } else {
                            be.setLazyMode(false);
                            try {
                                be.setVariant(NPlushVariant.valueOf(payload.variantName()));
                            } catch (IllegalArgumentException ignored) {}
                        }
                    }
                })
        );
    }
}