package net.wots.client.screen;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.wots.block.plushies.nplush.NPlushVariant;
import net.wots.network.SetNVariantPayload;
import org.joml.Matrix4f;

public class NVariantWheelScreen extends Screen {

    private final BlockPos targetPos;
    private static final NPlushVariant[] VARIANTS = NPlushVariant.values();

    private static final float INNER_R = 38f;
    private static final float OUTER_R = 120f;
    private static final float LABEL_R = 86f;
    private static final float GAP_DEG = 2.5f;

    private int hoveredIndex = -1;

    public NVariantWheelScreen(BlockPos pos) {
        super(Text.empty());
        this.targetPos = pos;
    }

    @Override public boolean shouldPause()      { return false; }
    @Override public boolean shouldCloseOnEsc() { return true; }

    @Override
    public void renderBackground(DrawContext ctx, int mouseX, int mouseY, float delta) {
        ctx.fill(0, 0, width, height, 0x60000000);
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        int cx = width / 2;
        int cy = height / 2;

        float dx = mouseX - cx;
        float dy = mouseY - cy;
        float distSq = dx * dx + dy * dy;
        float mouseAngleDeg = (float) Math.toDegrees(Math.atan2(dy, dx));
        if (mouseAngleDeg < 0) mouseAngleDeg += 360f;

        float segDeg = 360f / VARIANTS.length;
        hoveredIndex = -1;

        if (distSq >= INNER_R * INNER_R && distSq <= OUTER_R * OUTER_R) {
            hoveredIndex = (int)(mouseAngleDeg / segDeg);
        }

        for (int i = 0; i < VARIANTS.length; i++) {
            boolean hovered  = (i == hoveredIndex);
            float outerR    = hovered ? OUTER_R + 8f : OUTER_R;
            float startDeg  = i * segDeg + GAP_DEG / 2f;
            float endDeg    = (i + 1) * segDeg - GAP_DEG / 2f;

            int baseColor = VARIANTS[i].color;
            int fillColor = hovered ? brighten(baseColor, 55) : darken(baseColor, 15);
            int alpha     = hovered ? 0xEE : 0xCC;

            drawRing(ctx, cx, cy, INNER_R, outerR, startDeg, endDeg, (alpha << 24) | fillColor);

            float midRad = (float) Math.toRadians(i * segDeg + segDeg / 2f);
            float labelR = hovered ? LABEL_R + 5f : LABEL_R;
            int lx = cx + (int)(labelR * Math.cos(midRad));
            int ly = cy + (int)(labelR * Math.sin(midRad));

            ctx.drawCenteredTextWithShadow(textRenderer, VARIANTS[i].displayName,
                    lx, ly - textRenderer.fontHeight / 2,
                    hovered ? 0xFFFFFF : 0xDDDDDD);
        }

        // Centre disc
        drawDisc(ctx, cx, cy, (int)INNER_R, 0xBB1A1A1A);

        boolean hoveringCentre = (dx * dx + dy * dy) <= INNER_R * INNER_R;
        ctx.drawCenteredTextWithShadow(textRenderer, "Lazy",
                cx, cy - textRenderer.fontHeight / 2,
                hoveringCentre ? 0xFFFF55 : 0xAAAAAA);

        super.render(ctx, mouseX, mouseY, delta);
    }

    private void drawRing(DrawContext ctx, int cx, int cy,
                          float inner, float outer,
                          float startDeg, float endDeg, int argb) {
        int steps = Math.max(1, (int)(endDeg - startDeg));
        int a = (argb >> 24) & 0xFF, r = (argb >> 16) & 0xFF,
                g = (argb >> 8)  & 0xFF, b =  argb        & 0xFF;

        Matrix4f mat = ctx.getMatrices().peek().getPositionMatrix();
        VertexConsumer buf = ctx.getVertexConsumers().getBuffer(RenderLayer.getGui());

        for (int s = 0; s < steps; s++) {
            float a1 = (float) Math.toRadians(startDeg + s);
            float a2 = (float) Math.toRadians(startDeg + s + 1);

            float ox1 = cx + outer * (float)Math.cos(a1), oy1 = cy + outer * (float)Math.sin(a1);
            float ox2 = cx + outer * (float)Math.cos(a2), oy2 = cy + outer * (float)Math.sin(a2);
            float ix1 = cx + inner * (float)Math.cos(a1), iy1 = cy + inner * (float)Math.sin(a1);
            float ix2 = cx + inner * (float)Math.cos(a2), iy2 = cy + inner * (float)Math.sin(a2);

            buf.vertex(mat, ox1, oy1, 0).color(r, g, b, a);
            buf.vertex(mat, ox2, oy2, 0).color(r, g, b, a);
            buf.vertex(mat, ix1, iy1, 0).color(r, g, b, a);

            buf.vertex(mat, ox2, oy2, 0).color(r, g, b, a);
            buf.vertex(mat, ix2, iy2, 0).color(r, g, b, a);
            buf.vertex(mat, ix1, iy1, 0).color(r, g, b, a);
        }
    }

    private void drawDisc(DrawContext ctx, int cx, int cy, int radius, int argb) {
        int a = (argb >> 24) & 0xFF, r = (argb >> 16) & 0xFF,
                g = (argb >> 8)  & 0xFF, b =  argb        & 0xFF;

        Matrix4f mat = ctx.getMatrices().peek().getPositionMatrix();
        VertexConsumer buf = ctx.getVertexConsumers().getBuffer(RenderLayer.getGui());

        for (int s = 0; s < 360; s++) {
            float a1 = (float) Math.toRadians(s);
            float a2 = (float) Math.toRadians(s + 1);
            buf.vertex(mat, cx, cy, 0).color(r, g, b, a);
            buf.vertex(mat, cx + radius * (float)Math.cos(a1), cy + radius * (float)Math.sin(a1), 0).color(r, g, b, a);
            buf.vertex(mat, cx + radius * (float)Math.cos(a2), cy + radius * (float)Math.sin(a2), 0).color(r, g, b, a);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int cx = width / 2, cy = height / 2;
        double dx = mouseX - cx, dy = mouseY - cy;

        if (dx*dx + dy*dy <= INNER_R * INNER_R) {
            ClientPlayNetworking.send(new SetNVariantPayload(targetPos, "LAZY", true));
            close();
            return true;
        }

        if (button == 0 && hoveredIndex >= 0) {
            ClientPlayNetworking.send(new SetNVariantPayload(targetPos, VARIANTS[hoveredIndex].name(), false));
            close();
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    private int brighten(int rgb, int amt) {
        return (Math.min(255, ((rgb >> 16) & 0xFF) + amt) << 16)
                | (Math.min(255, ((rgb >> 8)  & 0xFF) + amt) << 8)
                |  Math.min(255, ( rgb        & 0xFF) + amt);
    }

    private int darken(int rgb, int amt) {
        return (Math.max(0, ((rgb >> 16) & 0xFF) - amt) << 16)
                | (Math.max(0, ((rgb >> 8)  & 0xFF) - amt) << 8)
                |  Math.max(0, ( rgb        & 0xFF) - amt);
    }
}