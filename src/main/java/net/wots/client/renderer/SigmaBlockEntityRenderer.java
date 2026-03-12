package net.wots.client.renderer;


import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import net.wots.block.entity.SigmaBlockEntity;
import net.wots.block.plushies.tadc.sigma.SigmaBlockModel;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class SigmaBlockEntityRenderer extends GeoBlockRenderer<SigmaBlockEntity> {

    public SigmaBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
        super(new SigmaBlockModel());
    }
}
