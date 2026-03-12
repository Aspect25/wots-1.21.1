package net.wots;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.wots.block.ModBlocks;
import net.wots.client.CustomStonecutterScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.wots.client.renderer.*;
import net.wots.item.ModItems;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class WotsClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        HandledScreens.register(Wots.CUSTOM_STONECUTTER_HANDLER, CustomStonecutterScreen::new);
        BlockEntityRendererRegistry.register(
                ModBlocks.PLUSHIE_SHELF_BLOCK_ENTITY,
                PlushieShelfBlockEntityRenderer::new
        );
        BlockEntityRendererRegistry.register(
                ModBlocks.TRASH_BLOCK_ENTITY,
                TrashBlockEntityRenderer::new);
        BlockEntityRendererFactories.register(ModBlocks.UZI_PLUSH_BLOCK_ENTITY, UziPlushBlockEntityRenderer::new);
        BlockEntityRendererFactories.register(ModBlocks.N_PLUSH_BLOCK_ENTITY, NPlushBlockEntityRenderer::new);
        BlockEntityRendererFactories.register(ModBlocks.UZI_HUGE_BLOCK_ENTITY,
                UziHugeBlockEntityRenderer::new);
        BlockEntityRendererFactories.register(ModBlocks.CYN_PLUSH_BLOCK_ENTITY, CynPlushBlockEntityRenderer::new);
        BlockEntityRendererRegistry.register(ModBlocks.SIGMA_BLOCK_ENTITY, SigmaBlockEntityRenderer::new);






    }



}
