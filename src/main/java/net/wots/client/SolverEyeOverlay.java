package net.wots.client;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.wots.item.SolverEyeItem;

/**
 * Renders a subtle pulsing yellow-gold screen tint when the player
 * holds the Solver Eye in their offhand at night.
 *
 * Design notes:
 * - Alpha range: 4–28 out of 255. Barely perceptible but definitely there.
 *   We want "something feels off", not "screen is covered in yellow paint".
 * - Pulse period: ~3 seconds. Slow and organic.
 * - Color: #CCCC00 (desaturated Solver gold). Matches the show's Solver
 *   signature colour without being garish.
 * - Only active during Minecraft night (timeOfDay 13000–23000).
 *   During the day the item is just a normal item.
 */
public class SolverEyeOverlay {

    /** Solver signature colour without alpha (RRGGBB). */
    private static final int SOLVER_COLOR_RGB = 0x00CCCC00;

    /** Pulse speed — milliseconds per full sin cycle. */
    private static final double PULSE_MS = 3000.0;

    /** Minimum and maximum alpha (0–255). Keep both low. */
    private static final int ALPHA_MIN = 4;
    private static final int ALPHA_MAX = 28;

    public static void register() {
        HudRenderCallback.EVENT.register(SolverEyeOverlay::onHudRender);
    }

    private static void onHudRender(DrawContext drawContext, net.minecraft.client.render.RenderTickCounter tickCounter) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.world == null) return;
        if (client.options.hudHidden) return;

        // Check offhand
        ItemStack offhand = client.player.getOffHandStack();
        if (!(offhand.getItem() instanceof SolverEyeItem)) return;

        // Only at night
        long timeOfDay = client.world.getTimeOfDay() % 24000L;
        if (timeOfDay < 13000L || timeOfDay > 23000L) return;

        // Compute pulse alpha
        double pulse = Math.sin(System.currentTimeMillis() / PULSE_MS * Math.PI * 2.0);
        // pulse is -1 to 1; remap to 0-1
        double t = (pulse + 1.0) / 2.0;
        int alpha = (int) Math.round(ALPHA_MIN + t * (ALPHA_MAX - ALPHA_MIN));

        // Build ARGB colour — Java's fill() expects ARGB packed int
        int color = (alpha << 24) | SOLVER_COLOR_RGB;

        int w = client.getWindow().getScaledWidth();
        int h = client.getWindow().getScaledHeight();
        drawContext.fill(0, 0, w, h, color);
    }
}
