package net.wots.client.renderer;

import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.wots.block.entity.UziHugeBlockEntity;
import net.wots.block.plushies.uziplush.UziHugeModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class UziHugeBlockEntityRenderer extends GeoBlockRenderer<UziHugeBlockEntity> {

    public UziHugeBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        super(new UziHugeModel());
    }
}