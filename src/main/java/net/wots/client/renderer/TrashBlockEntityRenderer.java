package net.wots.client.renderer;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.wots.block.entity.TrashBlockEntity;

public class TrashBlockEntityRenderer implements BlockEntityRenderer<TrashBlockEntity> {

    public TrashBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
    }

    @Override
    public void render(TrashBlockEntity entity, float tickDelta, MatrixStack matrices,
                       VertexConsumerProvider vertexConsumers, int light, int overlay) {
        // You'd need to render the model here using Minecraft's rendering API
        // But for a simple block, just remove the renderer registration instead
    }
}