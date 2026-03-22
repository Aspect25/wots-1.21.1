package net.wots.client.renderer;


import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.wots.block.entity.SigmaBlockEntity;
import net.wots.block.plushies.tadc.sigma.SigmaBlockModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class SigmaBlockEntityRenderer extends GeoBlockRenderer<SigmaBlockEntity> {

    public SigmaBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
        super(new SigmaBlockModel());
    }
}
