package net.wots;

import io.wispforest.accessories.api.client.AccessoriesRendererRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityFeatureRendererRegistrationCallback;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.wots.block.ModBlocks;
import net.wots.block.entity.CynPlushBlockEntity;
import net.wots.block.entity.NPlushBlockEntity;
import net.wots.block.entity.UziPlushBlockEntity;
import net.wots.block.plushies.nplush.NPlushVariant;
import net.wots.block.plushies.uziplush.UziPlushVariant;
import net.wots.client.CustomStonecutterScreen;
import net.wots.client.ModKeybindings;
import net.wots.client.PlushieSoundKeyHandler;
import net.wots.client.renderer.*;

public class WotsClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {


        BlockEntityRendererRegistry.register(ModBlocks.PLUSHIE_SHELF_BLOCK_ENTITY, PlushieShelfBlockEntityRenderer::new);
        BlockEntityRendererRegistry.register(ModBlocks.TRASH_BLOCK_ENTITY, TrashBlockEntityRenderer::new);
        BlockEntityRendererFactories.register(ModBlocks.UZI_PLUSH_BLOCK_ENTITY, UziPlushBlockEntityRenderer::new);
        BlockEntityRendererFactories.register(ModBlocks.N_PLUSH_BLOCK_ENTITY, NPlushBlockEntityRenderer::new);
        BlockEntityRendererFactories.register(ModBlocks.UZI_HUGE_BLOCK_ENTITY, UziHugeBlockEntityRenderer::new);
        BlockEntityRendererFactories.register(ModBlocks.CYN_PLUSH_BLOCK_ENTITY, CynPlushBlockEntityRenderer::new);
        BlockEntityRendererRegistry.register(ModBlocks.SIGMA_BLOCK_ENTITY, SigmaBlockEntityRenderer::new);

        BuiltinItemRendererRegistry.INSTANCE.register(
                ModBlocks.UZI_PLUSH.asItem(),
                (ItemStack stack, ModelTransformationMode mode, MatrixStack matrices,
                 VertexConsumerProvider vertexConsumers, int light, int overlay) -> {
                    UziPlushBlockEntity be = new UziPlushBlockEntity(
                            BlockPos.ORIGIN, ModBlocks.UZI_PLUSH.getDefaultState());
                    var beData = stack.get(DataComponentTypes.BLOCK_ENTITY_DATA);
                    if (beData != null) {
                        NbtCompound nbt = beData.copyNbt();
                        if (nbt.contains("Variant")) {
                            try { be.setVariant(UziPlushVariant.valueOf(nbt.getString("Variant"))); }
                            catch (IllegalArgumentException ignored) {}
                        }
                    }
                    net.minecraft.client.MinecraftClient.getInstance()
                            .getBlockEntityRenderDispatcher()
                            .renderEntity(be, matrices, vertexConsumers, light, overlay);
                }
        );

        BuiltinItemRendererRegistry.INSTANCE.register(
                ModBlocks.N_PLUSH.asItem(),
                (ItemStack stack, ModelTransformationMode mode, MatrixStack matrices,
                 VertexConsumerProvider vertexConsumers, int light, int overlay) -> {
                    NPlushBlockEntity be = new NPlushBlockEntity(
                            BlockPos.ORIGIN, ModBlocks.N_PLUSH.getDefaultState());
                    var beData = stack.get(DataComponentTypes.BLOCK_ENTITY_DATA);
                    if (beData != null) {
                        NbtCompound nbt = beData.copyNbt();
                        if (nbt.contains("Variant")) {
                            try { be.setVariant(NPlushVariant.valueOf(nbt.getString("Variant"))); }
                            catch (IllegalArgumentException ignored) {}
                        }
                    }
                    net.minecraft.client.MinecraftClient.getInstance()
                            .getBlockEntityRenderDispatcher()
                            .renderEntity(be, matrices, vertexConsumers, light, overlay);
                }
        );
        AccessoriesRendererRegistry.registerRenderer(ModBlocks.UZI_PLUSH.asItem(),
                UziPlushAccessoryRenderer::new);
        AccessoriesRendererRegistry.registerRenderer(ModBlocks.N_PLUSH.asItem(),
                UziPlushAccessoryRenderer::new);
        ModKeybindings.register();
        PlushieSoundKeyHandler.register();
        BuiltinItemRendererRegistry.INSTANCE.register(
                ModBlocks.CYN_PLUSH.asItem(),
                (ItemStack stack, ModelTransformationMode mode, MatrixStack matrices,
                 VertexConsumerProvider vertexConsumers, int light, int overlay) -> {
                    CynPlushBlockEntity be = new CynPlushBlockEntity(
                            BlockPos.ORIGIN, ModBlocks.CYN_PLUSH.getDefaultState());
                    net.minecraft.client.MinecraftClient.getInstance()
                            .getBlockEntityRenderDispatcher()
                            .renderEntity(be, matrices, vertexConsumers, light, overlay);
                }
        );

        AccessoriesRendererRegistry.registerRenderer(ModBlocks.CYN_PLUSH.asItem(),
                UziPlushAccessoryRenderer::new);
}}