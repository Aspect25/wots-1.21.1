package net.wots.client.renderer;

import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.wots.block.entity.UziPlushBlockEntity;
import net.wots.block.plushies.uziplush.UziPlushModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class UziPlushBlockEntityRenderer extends GeoBlockRenderer<UziPlushBlockEntity> {

    public UziPlushBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        super(new UziPlushModel());
    }
}