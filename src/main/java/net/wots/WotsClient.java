package net.wots;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityRenderLayerRegistrationCallback;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.wots.block.ModBlocks;
import net.wots.block.entity.*;
import net.wots.block.plushies.PlushieVariant;
import net.wots.block.plushies.VariantHolder;
import net.wots.block.plushies.caineplush.CainePlushVariant;
import net.wots.block.plushies.cynplushmaid.CynPlushMaidVariant;
import net.wots.block.plushies.dollplush.DollPlushVariant;
import net.wots.block.plushies.jplush.JPlushVariant;
import net.wots.block.plushies.pomniplush.PomniPlushVariant;
import net.wots.block.plushies.ribbitplush.RibbitPlushVariant;
import net.wots.block.plushies.tadc.kinger.KingerPlushVariant;
import net.wots.block.plushies.nplush.NPlushVariant;
import net.wots.block.plushies.tessaplush.TessaPlushVariant;
import net.wots.block.plushies.lizzyplush.LizzyPlushVariant;
import net.wots.block.plushies.uziplush.UziPlushVariant;
import net.wots.client.ModKeybindings;
import net.wots.client.PlushieClientSync;
import net.wots.client.PlushieSoundKeyHandler;
import net.wots.client.VariantChangeParticles;
import net.wots.block.plushies.PlushieGeoModel;
import net.wots.block.plushies.cynplush.CynPlushModel;
import net.wots.client.renderer.*;
import net.wots.client.screen.StevePlushScreen;
import net.wots.network.S2CPlushieSyncPayload;
import net.wots.screen.StevePlushScreenHandler;
import com.geckolib.renderer.GeoBlockRenderer;
import net.wots.particle.*;
import net.fabricmc.fabric.api.client.particle.v1.ParticleProviderRegistry;
import net.wots.network.SyncVariantUnlocksPayload;
import net.wots.network.VariantChangeParticlePayload;
import net.wots.unlock.UnlockSyncHelper;

import java.util.function.BiFunction;

public class WotsClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        // ── Variant unlock sync receiver ─────────────────────────────────────
        ClientPlayNetworking.registerGlobalReceiver(SyncVariantUnlocksPayload.TYPE, (payload, context) -> {
            UnlockSyncHelper.updateClientCache(payload.unlocks());
        });

        // ── Variant change particle receiver ─────────────────────────────────
        ClientPlayNetworking.registerGlobalReceiver(VariantChangeParticlePayload.TYPE, (payload, context) -> {
            VariantChangeParticles.spawnBurst(payload.pos(), payload.color());
        });

        // ── Block entity renderers ────────────────────────────────────────────
        BlockEntityRendererRegistry.register(ModBlocks.PLUSHIE_SHELF_BLOCK_ENTITY, PlushieShelfBlockEntityRenderer::new);
        BlockEntityRendererRegistry.register(ModBlocks.UZI_PLUSH_BLOCK_ENTITY,
                ctx -> new PlushieBlockEntityRenderer<>(ctx, new PlushieGeoModel<>("uzi_plush", "uzi_plush")));
        BlockEntityRendererRegistry.register(ModBlocks.N_PLUSH_BLOCK_ENTITY,
                ctx -> new PlushieBlockEntityRenderer<>(ctx, new PlushieGeoModel<>("n_plush", "n_plush")));
        BlockEntityRendererRegistry.register(ModBlocks.UZI_HUGE_BLOCK_ENTITY,
                ctx -> new PlushieBlockEntityRenderer<>(ctx, new PlushieGeoModel<>("uzi_plush_huge", "uzi_plush")));
        BlockEntityRendererRegistry.register(ModBlocks.CYN_PLUSH_BLOCK_ENTITY,
                ctx -> new PlushieBlockEntityRenderer<>(ctx, new CynPlushModel()));
        BlockEntityRendererRegistry.register(ModBlocks.SIGMA_BLOCK_ENTITY,
                ctx -> new PlushieBlockEntityRenderer<>(ctx, new PlushieGeoModel<>("sigma", "sigma", "sigma")));
        BlockEntityRendererRegistry.register(ModBlocks.DOLL_PLUSH_BLOCK_ENTITY,
                ctx -> new PlushieBlockEntityRenderer<>(ctx, new PlushieGeoModel<>("doll_plush", "doll_plush")));
        BlockEntityRendererRegistry.register(ModBlocks.KINGER_PLUSH_BLOCK_ENTITY,
                ctx -> new PlushieBlockEntityRenderer<>(ctx, new PlushieGeoModel<>("kinger_plush", "j_plush")));
        BlockEntityRendererRegistry.register(ModBlocks.TESSA_PLUSH_BLOCK_ENTITY,
                ctx -> new PlushieBlockEntityRenderer<>(ctx, new PlushieGeoModel<>("tessa_plush", "j_plush")));
        BlockEntityRendererRegistry.register(ModBlocks.J_PLUSH_BLOCK_ENTITY,
                ctx -> new PlushieBlockEntityRenderer<>(ctx, new PlushieGeoModel<>("j_plush", "j_plush")));
        BlockEntityRendererRegistry.register(ModBlocks.LIZZY_PLUSH_BLOCK_ENTITY,
                ctx -> new PlushieBlockEntityRenderer<>(ctx, new PlushieGeoModel<>("lizzy_plush", "lizzy_plush")));
        BlockEntityRendererRegistry.register(ModBlocks.CAINE_PLUSH_BLOCK_ENTITY,
                ctx -> new PlushieBlockEntityRenderer<>(ctx, new PlushieGeoModel<>("caine", "j_plush")));
        BlockEntityRendererRegistry.register(ModBlocks.RIBBIT_PLUSH_BLOCK_ENTITY,
                ctx -> new PlushieBlockEntityRenderer<>(ctx, new PlushieGeoModel<>("ribbit", "j_plush")));
        BlockEntityRendererRegistry.register(ModBlocks.POMNI_PLUSH_BLOCK_ENTITY,
                ctx -> new PlushieBlockEntityRenderer<>(ctx, new PlushieGeoModel<>("pomni_plush", "j_plush")));
        BlockEntityRendererRegistry.register(ModBlocks.JAX_PLUSH_BLOCK_ENTITY,
                ctx -> new PlushieBlockEntityRenderer<>(ctx, new PlushieGeoModel<>("jax_plush", "j_plush")));
        BlockEntityRendererRegistry.register(ModBlocks.CYN_PLUSH_MAID_BLOCK_ENTITY,
                ctx -> new PlushieBlockEntityRenderer<>(ctx, new PlushieGeoModel<>("cyn_plush_maid", "cyn_plush")));
        BlockEntityRendererRegistry.register(ModBlocks.PLUSHIE_PEDESTAL_BLOCK_ENTITY, PlushiePedestalBlockEntityRenderer::new);
        BlockEntityRendererRegistry.register(ModBlocks.STEVE_PLUSH_BLOCK_ENTITY, StevePlushBlockEntityRenderer::new);

        // ── Item renderers (plushies with variants) ──────────────────────────
        // TODO [26.1] BuiltinItemRendererRegistry removed in Fabric API 26.1.
        // These need to be replaced with GeoItemRenderer or SpecialModelWrapper.
        // For now, item rendering will fall back to the default block model.
        /*
        registerVariantItemRenderer(ModBlocks.UZI_PLUSH, UziPlushBlockEntity::new, UziPlushVariant.class);
        registerVariantItemRenderer(ModBlocks.N_PLUSH, NPlushBlockEntity::new, NPlushVariant.class);
        registerVariantItemRenderer(ModBlocks.DOLL_PLUSH, DollPlushBlockEntity::new, DollPlushVariant.class);
        registerVariantItemRenderer(ModBlocks.KINGER_PLUSH, KingerPlushBlockEntity::new, KingerPlushVariant.class);
        registerVariantItemRenderer(ModBlocks.TESSA_PLUSH, TessaPlushBlockEntity::new, TessaPlushVariant.class);
        registerVariantItemRenderer(ModBlocks.J_PLUSH, JPlushBlockEntity::new, JPlushVariant.class);
        registerVariantItemRenderer(ModBlocks.LIZZY_PLUSH, LizzyPlushBlockEntity::new, LizzyPlushVariant.class);
        registerVariantItemRenderer(ModBlocks.CAINE_PLUSH, CainePlushBlockEntity::new, CainePlushVariant.class);
        registerVariantItemRenderer(ModBlocks.RIBBIT_PLUSH, RibbitPlushBlockEntity::new, RibbitPlushVariant.class);
        registerVariantItemRenderer(ModBlocks.POMNI_PLUSH, PomniPlushBlockEntity::new, PomniPlushVariant.class);
        registerVariantItemRenderer(ModBlocks.CYN_PLUSH_MAID, CynPlushMaidBlockEntity::new, CynPlushMaidVariant.class);
        registerVariantItemRenderer(ModBlocks.UZI_HUGE, UziHugeBlockEntity::new, UziPlushVariant.class);
        registerSimpleItemRenderer(ModBlocks.CYN_PLUSH, CynPlushBlockEntity::new);
        registerSimpleItemRenderer(ModBlocks.SIGMA_BLOCK, SigmaBlockEntity::new);
        // Steve Plush builtin item renderer also removed
        */

        // ── Plushie feature renderer (renders equipped plushies on player) ────
        LivingEntityRenderLayerRegistrationCallback.EVENT.register(
                (entityType, entityRenderer, registrationHelper, context) -> {
                    if (entityRenderer instanceof AvatarRenderer playerRenderer) {
                        registrationHelper.register(new PlushieFeatureRenderer(playerRenderer));
                    }
                });

        // ── Plushie sync receiver ────────────────────────────────────────────
        ClientPlayNetworking.registerGlobalReceiver(S2CPlushieSyncPayload.TYPE, (payload, context) -> {
            PlushieClientSync.updateEquipped(payload.entityId(), payload.hat(), payload.back());
        });

        // Variant change particles (colored per-variant)
        ParticleProviderRegistry.getInstance().register(ModParticles.VARIANT_CORE, VariantCoreParticle.Factory::new);
        ParticleProviderRegistry.getInstance().register(ModParticles.VARIANT_WISP, VariantWispParticle.Factory::new);
        ParticleProviderRegistry.getInstance().register(ModParticles.VARIANT_SPARK, VariantSparkParticle.Factory::new);
        ParticleProviderRegistry.getInstance().register(ModParticles.VARIANT_RING, VariantRingParticle.Factory::new);

        // ── RGB Wool color providers ─────────────────────────────────────────
        // MC 26.1: ColorProviderRegistry replaced with BlockColorRegistry + BlockTintSource
        net.fabricmc.fabric.api.client.rendering.v1.BlockColorRegistry.register(
                java.util.List.of((net.minecraft.client.color.block.BlockTintSource) state -> {
                    float hue = (System.currentTimeMillis() % 5000) / 5000f;
                    return net.minecraft.util.Mth.hsvToRgb(hue, 1f, 1f);
                }), ModBlocks.RGB_WOOL);

        // ── Item tooltips (dummy-proofing) ───────────────────────────────────
        registerItemTooltips();

        // ── Keybindings & sound ───────────────────────────────────────────────
        ModKeybindings.register();
        PlushieSoundKeyHandler.register();

        // ── Screen handlers ───────────────────────────────────────────────────
        MenuScreens.register(Wots.STEVE_PLUSH_HANDLER, StevePlushScreen::new);

        // ── Cinematic subtitles ──────────────────────────────────────────────
        net.wots.client.CinematicSubtitleRenderer.register();

        ClientPlayNetworking.registerGlobalReceiver(net.wots.network.SubtitlePayload.TYPE, (payload, context) -> {
            net.wots.client.CinematicSubtitleRenderer.show(payload.speaker(), payload.text(), payload.color(), payload.holdTicks());
        });
    }

    // ── Item tooltips ─────────────────────────────────────────────────────────

    private static void registerItemTooltips() {
        // Plushies with hidden unlockable expressions
        java.util.Set<Item> secretPlushies = java.util.Set.of(
                ModBlocks.UZI_PLUSH.asItem(), ModBlocks.N_PLUSH.asItem(),
                ModBlocks.DOLL_PLUSH.asItem(), ModBlocks.LIZZY_PLUSH.asItem(),
                ModBlocks.UZI_HUGE.asItem()
        );

        // Squeeze-only plushies (only 1 variant or no expressions)
        java.util.Set<Item> squeezePlushies = java.util.Set.of(
                ModBlocks.CYN_PLUSH.asItem(),
                ModBlocks.J_PLUSH.asItem(), ModBlocks.TESSA_PLUSH.asItem(),
                ModBlocks.KINGER_PLUSH.asItem(),
                ModBlocks.CAINE_PLUSH.asItem(), ModBlocks.POMNI_PLUSH.asItem(),
                ModBlocks.JAX_PLUSH.asItem(), ModBlocks.RIBBIT_PLUSH.asItem(),
                ModBlocks.CYN_PLUSH_MAID.asItem(),
                ModBlocks.SIGMA_BLOCK.asItem(), ModBlocks.THE_DUCKLER.asItem()
        );

        net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback.EVENT.register((stack, context, type, lines) -> {
            Item item = stack.getItem();

            if (secretPlushies.contains(item)) {
                lines.add(Component.translatable("tooltip.wots.plush.squeeze")
                        .withStyle(ChatFormatting.GRAY));
                lines.add(Component.translatable("tooltip.wots.plush.expression")
                        .withStyle(ChatFormatting.GRAY));
                lines.add(Component.translatable("tooltip.wots.plush.secret")
                        .withStyle(ChatFormatting.DARK_PURPLE));
                lines.add(Component.translatable("tooltip.wots.plush.wearable")
                        .withStyle(ChatFormatting.DARK_GRAY));
            } else if (squeezePlushies.contains(item)) {
                lines.add(Component.translatable("tooltip.wots.plush.squeeze")
                        .withStyle(ChatFormatting.GRAY));
                lines.add(Component.translatable("tooltip.wots.plush.wearable")
                        .withStyle(ChatFormatting.DARK_GRAY));
            } else if (item == ModBlocks.STEVE_PLUSH.asItem()) {
                lines.add(Component.translatable("tooltip.wots.steve_plush.skin")
                        .withStyle(ChatFormatting.GRAY));
                lines.add(Component.translatable("tooltip.wots.plush.wearable")
                        .withStyle(ChatFormatting.DARK_GRAY));
            } else if (item == ModBlocks.RGB_WOOL.asItem()) {
                lines.add(Component.translatable("tooltip.wots.rgb_wool")
                        .withStyle(ChatFormatting.GRAY));
            } else if (item == ModBlocks.LAND_MINE.asItem()) {
                lines.add(Component.translatable("tooltip.wots.land_mine")
                        .withStyle(ChatFormatting.GRAY));
            } else if (item == ModBlocks.PLUSHIE_SHELF.asItem()) {
                lines.add(Component.translatable("tooltip.wots.plushie_shelf")
                        .withStyle(ChatFormatting.GRAY));
                lines.add(Component.translatable("tooltip.wots.plushie_shelf.remove")
                        .withStyle(ChatFormatting.DARK_GRAY));
            } else if (item == ModBlocks.PLUSHIE_PEDESTAL.asItem()) {
                lines.add(Component.translatable("tooltip.wots.plushie_pedestal")
                        .withStyle(ChatFormatting.GRAY));
                lines.add(Component.translatable("tooltip.wots.plushie_pedestal.remove")
                        .withStyle(ChatFormatting.DARK_GRAY));
            }
        });
    }
}
