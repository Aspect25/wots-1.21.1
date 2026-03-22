package net.wots.client.renderer;


import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.wots.block.entity.CynPlushBlockEntity;
import net.wots.block.plushies.cynplush.CynPlushModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class CynPlushBlockEntityRenderer extends GeoBlockRenderer<CynPlushBlockEntity> {

    public CynPlushBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        super(new CynPlushModel());
    }
}