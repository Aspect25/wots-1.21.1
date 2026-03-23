package net.wots.client.screen;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.wots.unlock.UnlockSyncHelper;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

/**
 * Radial variant-selection wheel with locked variant support.
 * Locked variants appear greyed out with hint text on hover.
 */
public abstract class AbstractVariantWheelScreen extends Screen {

    protected final BlockPos targetPos;
    private static final float INNER_R = 38f, OUTER_R = 120f, LABEL_R = 86f, GAP_DEG = 2.5f;
    private int hoveredIndex = -1;

    protected AbstractVariantWheelScreen(BlockPos pos) {
        super(Text.empty());
        this.targetPos = pos;
    }

    protected abstract int variantCount();
    protected abstract String variantDisplayName(int i);
    protected abstract int variantColor(int i);
    protected abstract String variantEnumName(int i);
    protected abstract void sendPayload(BlockPos pos, String variantName);

    /**
     * Override in subclass to return "n" or "uzi".
     * Used for unlock checking.
     */
    protected abstract String characterId();

    /**
     * Check if a variant at index i is unlocked for the local player.
     */
    private boolean isVariantUnlocked(int i) {
        return UnlockSyncHelper.isUnlockedClient(variantEnumName(i));
    }

    @Override public boolean shouldPause()     { return false; }
    @Override public boolean shouldCloseOnEsc() { return true; }

    @Override
    public void renderBackground(DrawContext ctx, int mouseX, int mouseY, float delta) {
        ctx.fill(0, 0, width, height, 0x60000000);
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        int cx = width / 2, cy = height / 2, count = variantCount();
        float dx = mouseX - cx, dy = mouseY - cy, distSq = dx * dx + dy * dy;
        float mouseAngle = (float) Math.toDegrees(Math.atan2(dy, dx));
        if (mouseAngle < 0) mouseAngle += 360f;
        float segDeg = 360f / count;
        hoveredIndex = (distSq >= INNER_R * INNER_R && distSq <= OUTER_R * OUTER_R)
                ? (int) (mouseAngle / segDeg) : -1;

        for (int i = 0; i < count; i++) {
            boolean hov = (i == hoveredIndex);
            boolean unlocked = isVariantUnlocked(i);
            float oR = hov ? OUTER_R + 8f : OUTER_R;

            int fill;
            int alpha;
            if (unlocked) {
                int base = variantColor(i);
                fill = hov ? brighten(base, 55) : darken(base, 15);
                alpha = hov ? 0xEE : 0xCC;
            } else {
                // Locked — dark grey with slight variation
                fill = hov ? 0x555555 : 0x333333;
                alpha = hov ? 0xBB : 0x88;
            }

            drawRing(ctx, cx, cy, INNER_R, oR,
                    i * segDeg + GAP_DEG / 2f, (i + 1) * segDeg - GAP_DEG / 2f, (alpha << 24) | fill);

            float midRad = (float) Math.toRadians(i * segDeg + segDeg / 2f);
            float lR = hov ? LABEL_R + 5f : LABEL_R;
            int labelX = cx + (int) (lR * Math.cos(midRad));
            int labelY = cy + (int) (lR * Math.sin(midRad)) - textRenderer.fontHeight / 2;

            if (unlocked) {
                ctx.drawCenteredTextWithShadow(textRenderer, variantDisplayName(i),
                        labelX, labelY, hov ? 0xFFFFFF : 0xDDDDDD);
            } else {
                // Show lock icon + "???"
                ctx.drawCenteredTextWithShadow(textRenderer, "\uD83D\uDD12 ???",
                        labelX, labelY, hov ? 0xAAAAAA : 0x666666);
            }
        }

        // Center disc (decorative, no function)
        drawDisc(ctx, cx, cy, (int) INNER_R, 0xBB1A1A1A);

        // Tooltip for locked variants
        if (hoveredIndex >= 0 && !isVariantUnlocked(hoveredIndex)) {
            String hint = UnlockSyncHelper.getHintText(variantEnumName(hoveredIndex));
            List<Text> tooltip = new ArrayList<>();
            tooltip.add(Text.literal("\u00a7c\u00a7lLocked"));
            for (String line : hint.split("\n")) {
                tooltip.add(Text.literal(line));
            }
            ctx.drawTooltip(textRenderer, tooltip, mouseX, mouseY);
        }

        super.render(ctx, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        int cx = width / 2, cy = height / 2;

        if (button == 0 && hoveredIndex >= 0) {
            if (!isVariantUnlocked(hoveredIndex)) {
                // Play "locked" sound — bass note
                if (client != null && client.player != null) {
                    client.player.playSound(SoundEvents.BLOCK_NOTE_BLOCK_BASS.value(), 0.5f, 0.5f);
                }
                return true; // Consume click but don't send packet
            }
            sendPayload(targetPos, variantEnumName(hoveredIndex));
            close();
            return true;
        }
        return super.mouseClicked(mx, my, button);
    }

    // ── Drawing helpers ───────────────────────────────────────────────────────

    private void drawRing(DrawContext ctx, int cx, int cy, float inner, float outer,
                          float startDeg, float endDeg, int argb) {
        int steps = Math.max(1, (int) (endDeg - startDeg));
        int a = (argb >> 24) & 0xFF, r = (argb >> 16) & 0xFF, g = (argb >> 8) & 0xFF, b = argb & 0xFF;
        Matrix4f mat = ctx.getMatrices().peek().getPositionMatrix();
        VertexConsumer buf = ctx.getVertexConsumers().getBuffer(RenderLayer.getGui());
        for (int s = 0; s < steps; s++) {
            float a1 = (float) Math.toRadians(startDeg + s), a2 = (float) Math.toRadians(startDeg + s + 1);
            float ox1 = cx + outer * (float) Math.cos(a1), oy1 = cy + outer * (float) Math.sin(a1);
            float ox2 = cx + outer * (float) Math.cos(a2), oy2 = cy + outer * (float) Math.sin(a2);
            float ix1 = cx + inner * (float) Math.cos(a1), iy1 = cy + inner * (float) Math.sin(a1);
            float ix2 = cx + inner * (float) Math.cos(a2), iy2 = cy + inner * (float) Math.sin(a2);
            buf.vertex(mat, ox1, oy1, 0).color(r, g, b, a);
            buf.vertex(mat, ox2, oy2, 0).color(r, g, b, a);
            buf.vertex(mat, ix1, iy1, 0).color(r, g, b, a);
            buf.vertex(mat, ox2, oy2, 0).color(r, g, b, a);
            buf.vertex(mat, ix2, iy2, 0).color(r, g, b, a);
            buf.vertex(mat, ix1, iy1, 0).color(r, g, b, a);
        }
    }

    private void drawDisc(DrawContext ctx, int cx, int cy, int radius, int argb) {
        int a = (argb >> 24) & 0xFF, r = (argb >> 16) & 0xFF, g = (argb >> 8) & 0xFF, b = argb & 0xFF;
        Matrix4f mat = ctx.getMatrices().peek().getPositionMatrix();
        VertexConsumer buf = ctx.getVertexConsumers().getBuffer(RenderLayer.getGui());
        for (int s = 0; s < 360; s++) {
            float a1 = (float) Math.toRadians(s), a2 = (float) Math.toRadians(s + 1);
            buf.vertex(mat, cx, cy, 0).color(r, g, b, a);
            buf.vertex(mat, cx + radius * (float) Math.cos(a1), cy + radius * (float) Math.sin(a1), 0).color(r, g, b, a);
            buf.vertex(mat, cx + radius * (float) Math.cos(a2), cy + radius * (float) Math.sin(a2), 0).color(r, g, b, a);
        }
    }

    private static int brighten(int rgb, int amt) {
        return (Math.min(255, ((rgb >> 16) & 0xFF) + amt) << 16)
                | (Math.min(255, ((rgb >> 8) & 0xFF) + amt) << 8)
                | Math.min(255, (rgb & 0xFF) + amt);
    }

    private static int darken(int rgb, int amt) {
        return (Math.max(0, ((rgb >> 16) & 0xFF) - amt) << 16)
                | (Math.max(0, ((rgb >> 8) & 0xFF) - amt) << 8)
                | Math.max(0, (rgb & 0xFF) - amt);
    }
}