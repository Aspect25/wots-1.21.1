package net.wots.client.renderer;


import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.wots.block.entity.CynPlushBlockEntity;
import net.wots.block.entity.DollPlushBlockEntity;
import net.wots.block.plushies.cynplush.CynPlushModel;
import net.wots.block.plushies.dollplush.DollPlushModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class DollPlushBlockEntityRenderer extends GeoBlockRenderer<DollPlushBlockEntity> {

    public DollPlushBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        super(new DollPlushModel());
    }
}