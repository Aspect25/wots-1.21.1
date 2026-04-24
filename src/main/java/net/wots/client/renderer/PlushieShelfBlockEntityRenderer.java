package net.wots.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.wots.block.PlushieShelfBlock;
import net.wots.block.entity.PlushieShelfBlockEntity;

/**
 * Renderer for the plushie shelf — exact port of v1.0.6 logic to MC 26.1's
 * extractRenderState + submit pipeline.
 */
@Environment(EnvType.CLIENT)
public class PlushieShelfBlockEntityRenderer
        implements BlockEntityRenderer<PlushieShelfBlockEntity, PlushieShelfBlockEntityRenderer.State> {

    private static final float SHELF_TOP_Y = 0.57f;
    private static final float SHELF_CENTER_Z = 0.75f;

    private final ItemModelResolver itemModelResolver;

    public PlushieShelfBlockEntityRenderer(BlockEntityRendererProvider.Context ctx) {
        this.itemModelResolver = ctx.itemModelResolver();
    }

    // ── Render state ─────────────────────────────────────────────────────────

    public static class State extends BlockEntityRenderState {
        public final ItemStackRenderState[] plushieStates = new ItemStackRenderState[PlushieShelfBlockEntity.SLOTS];
        public final boolean[] occupied = new boolean[PlushieShelfBlockEntity.SLOTS];
        public Direction facing = Direction.NORTH;
        public PlushieShelfBlock.Shape shape = PlushieShelfBlock.Shape.STRAIGHT;

        public State() {
            for (int i = 0; i < plushieStates.length; i++) {
                plushieStates[i] = new ItemStackRenderState();
            }
        }
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(PlushieShelfBlockEntity be, State state, float partialTick,
                                   Vec3 pos, ModelFeatureRenderer.CrumblingOverlay crumbling) {
        BlockEntityRenderer.super.extractRenderState(be, state, partialTick, pos, crumbling);
        var blockState = be.getBlockState();
        state.facing = blockState.getValue(PlushieShelfBlock.FACING);
        state.shape = blockState.getValue(PlushieShelfBlock.SHAPE);
        int baseSeed = (int) be.getBlockPos().asLong();
        for (int i = 0; i < PlushieShelfBlockEntity.SLOTS; i++) {
            ItemStack stack = be.getPlushie(i);
            state.occupied[i] = !stack.isEmpty();
            if (state.occupied[i]) {
                itemModelResolver.updateForTopItem(
                        state.plushieStates[i], stack,
                        ItemDisplayContext.GROUND, be.getLevel(), null, baseSeed + i);
            } else {
                state.plushieStates[i].clear();
            }
        }
    }

    // ── Submit (exact v1.0.6 logic) ──────────────────────────────────────────

    @Override
    public void submit(State state, PoseStack matrices,
                       SubmitNodeCollector collector, CameraRenderState cameraState) {

        for (int slot = 0; slot < PlushieShelfBlockEntity.SLOTS; slot++) {
            if (!state.occupied[slot] || state.plushieStates[slot].isEmpty()) continue;

            matrices.pushPose();

            // 1. Translate to block center for rotation pivot
            matrices.translate(0.5, 0.0, 0.5);

            // 2. Rotate based on facing
            float yaw = switch (state.facing) {
                case SOUTH -> 180f;
                case WEST  -> 90f;
                case EAST  -> 270f;
                default    -> 0f; // NORTH
            };
            matrices.mulPose(Axis.YP.rotationDegrees(yaw));

            // 3. Translate back to block origin
            matrices.translate(-0.5, 0.0, -0.5);

            // 4. Compute per-slot position in block-local 0..1 coords
            boolean leftCorner  = state.shape == PlushieShelfBlock.Shape.CORNER_LEFT;
            boolean rightCorner = state.shape == PlushieShelfBlock.Shape.CORNER_RIGHT;

            float x = 0f;
            float z = SHELF_CENTER_Z;
            float rot = 0f;

            // Base front-row positions for slots 0, 1, 2
            if (slot == 0)      x = 1f / 6f;
            else if (slot == 1) x = 3f / 6f;
            else if (slot == 2) x = 5f / 6f;

            // Corner adjustments
            if (leftCorner) {
                if (slot == 2) {
                    rot = 45f;
                } else if (slot > 2) {
                    x = 5f / 6f;
                    z = (slot == 3) ? 0.40f : 0.15f;
                    rot = 90f;
                }
            } else if (rightCorner) {
                if (slot == 0) {
                    rot = -45f;
                } else if (slot > 2) {
                    x = 1f / 6f;
                    z = (slot == 3) ? 0.40f : 0.15f;
                    rot = -90f;
                }
            }

            matrices.translate(x, SHELF_TOP_Y, z);
            if (rot != 0f) matrices.mulPose(Axis.YP.rotationDegrees(rot));
            matrices.scale(0.5f, 0.5f, 0.5f);

            state.plushieStates[slot].submit(matrices, collector, state.lightCoords,
                    OverlayTexture.NO_OVERLAY, 0);

            matrices.popPose();
        }
    }
}
