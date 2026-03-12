package net.wots.client.renderer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import net.wots.block.PlushieShelfBlock;
import net.wots.block.entity.PlushieShelfBlockEntity;

@Environment(EnvType.CLIENT)
public class PlushieShelfBlockEntityRenderer implements BlockEntityRenderer<PlushieShelfBlockEntity> {

    public PlushieShelfBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {}



    // Shelf board top is y=8px = 0.5, nudged up a little so plushie sits on top nicely
    private static final float SHELF_TOP_Y = 0.57f;

    // Centre of shelf depth (z=8..16 → 0.75)
    private static final float SHELF_CENTER_Z = 0.75f;

    @Override
    public void render(PlushieShelfBlockEntity entity, float tickDelta, MatrixStack matrices,
                       VertexConsumerProvider vertexConsumers, int light, int overlay) {

        BlockState state = entity.getCachedState();
        Direction facing = state.get(Properties.HORIZONTAL_FACING);

        for (int slot = 0; slot < PlushieShelfBlockEntity.SLOTS; slot++) {
            ItemStack stack = entity.getPlushie(slot);
            if (stack.isEmpty()) continue;

            matrices.push();

            matrices.translate(0.5, 0.0, 0.5);

            float yaw = switch (facing) {
                case SOUTH -> 180f;
                case WEST  -> 90f;
                case EAST  -> 270f;
                default    -> 0f;
            };
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(yaw));

            matrices.translate(-0.5, 0.0, -0.5);

            boolean isCorner    = state.get(PlushieShelfBlock.SHAPE) != PlushieShelfBlock.Shape.STRAIGHT;
            boolean leftCorner  = state.get(PlushieShelfBlock.SHAPE) == PlushieShelfBlock.Shape.CORNER_LEFT;
            boolean rightCorner = state.get(PlushieShelfBlock.SHAPE) == PlushieShelfBlock.Shape.CORNER_RIGHT;

            float x = 0f;
            float z = SHELF_CENTER_Z;
            float rot = 0f;

            if (slot == 0) x = 1f/6f;
            else if (slot == 1) x = 3f/6f;
            else if (slot == 2) x = 5f/6f;

            if (leftCorner) {
                if (slot == 2) {
                    rot = 45f;
                } else if (slot > 2) {
                    x = 5f/6f;
                    z = (slot == 3) ? 0.40f : 0.15f;
                    rot = 90f;
                }
            } else if (rightCorner) {
                if (slot == 0) {
                    rot = -45f;
                } else if (slot > 2) {
                    x = 1f/6f;
                    z = (slot == 3) ? 0.40f : 0.15f;
                    rot = -90f;
                }
            }

            matrices.translate(x, SHELF_TOP_Y, z);
            if (rot != 0f) matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(rot));

            float scale = 0.5f;
            matrices.scale(scale, scale, scale);

            MinecraftClient.getInstance().getItemRenderer().renderItem(
                    stack,
                    ModelTransformationMode.GROUND,
                    light,
                    overlay,
                    matrices,
                    vertexConsumers,
                    entity.getWorld(),
                    0
            );

            matrices.pop();
        }
    }
}