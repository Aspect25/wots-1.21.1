package net.wots.client.renderer;


import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.wots.block.entity.NPlushBlockEntity;
import net.wots.block.plushies.nplush.NPlushModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class NPlushBlockEntityRenderer extends GeoBlockRenderer<NPlushBlockEntity> {

    public NPlushBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        super(new NPlushModel());
    }
}