package net.wots.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.util.math.BlockPos;
import org.joml.Vector3f;

import java.util.Random;

/**
 * Spawns variant-change particle effects on the client.
 *
 * When a plushie's variant changes, a burst of colored dust particles
 * spirals outward from the block, using the variant's color.
 */
public class VariantChangeParticles {

    private static final Random RANDOM = new Random();

    /**
     * Spawn a burst of colored particles around a block position.
     * Called when the client receives a VariantChangeParticlePayload.
     *
     * @param pos   Block position of the plushie
     * @param color RGB color from the variant enum (e.g. 0xC94C4C)
     */
    public static void spawnBurst(BlockPos pos, int color) {
        ClientWorld world = MinecraftClient.getInstance().world;
        if (world == null) return;

        float r = ((color >> 16) & 0xFF) / 255f;
        float g = ((color >> 8) & 0xFF) / 255f;
        float b = (color & 0xFF) / 255f;

        // Center of the block
        double cx = pos.getX() + 0.5;
        double cy = pos.getY() + 0.5;
        double cz = pos.getZ() + 0.5;

        // ── Ring burst — particles expand outward in a ring ──────────────────
        int ringCount = 16;
        for (int i = 0; i < ringCount; i++) {
            double angle = (2 * Math.PI / ringCount) * i;
            double radius = 0.3 + RANDOM.nextDouble() * 0.2;

            double px = cx + Math.cos(angle) * radius;
            double pz = cz + Math.sin(angle) * radius;
            double py = cy + (RANDOM.nextDouble() - 0.5) * 0.4;

            // Velocity: push outward + slight upward drift
            double vx = Math.cos(angle) * 0.08 + (RANDOM.nextDouble() - 0.5) * 0.02;
            double vy = 0.03 + RANDOM.nextDouble() * 0.04;
            double vz = Math.sin(angle) * 0.08 + (RANDOM.nextDouble() - 0.5) * 0.02;

            float size = 0.6f + RANDOM.nextFloat() * 0.4f;
            DustParticleEffect dust = new DustParticleEffect(new Vector3f(r, g, b), size);
            world.addParticle(dust, px, py, pz, vx, vy, vz);
        }

        // ── Upward spiral — a few particles drift up through the plushie ─────
        int spiralCount = 8;
        for (int i = 0; i < spiralCount; i++) {
            double angle = (2 * Math.PI / spiralCount) * i + RANDOM.nextDouble() * 0.5;
            double radius = 0.15 + RANDOM.nextDouble() * 0.1;

            double px = cx + Math.cos(angle) * radius;
            double pz = cz + Math.sin(angle) * radius;
            double py = cy - 0.3 + (i / (double) spiralCount) * 0.8;

            double vx = Math.cos(angle) * 0.01;
            double vy = 0.05 + RANDOM.nextDouble() * 0.03;
            double vz = Math.sin(angle) * 0.01;

            float size = 0.4f + RANDOM.nextFloat() * 0.3f;
            // Slightly brighter version of the color for the spiral
            float br = Math.min(1f, r * 1.3f);
            float bg = Math.min(1f, g * 1.3f);
            float bb = Math.min(1f, b * 1.3f);
            DustParticleEffect dust = new DustParticleEffect(new Vector3f(br, bg, bb), size);
            world.addParticle(dust, px, py, pz, vx, vy, vz);
        }

        // ── Sparkle pop — a handful of white particles for contrast ──────────
        for (int i = 0; i < 5; i++) {
            double px = cx + (RANDOM.nextDouble() - 0.5) * 0.6;
            double py = cy + (RANDOM.nextDouble() - 0.5) * 0.8;
            double pz = cz + (RANDOM.nextDouble() - 0.5) * 0.6;
            double vx = (RANDOM.nextDouble() - 0.5) * 0.05;
            double vy = 0.02 + RANDOM.nextDouble() * 0.05;
            double vz = (RANDOM.nextDouble() - 0.5) * 0.05;

            // Mix white with the variant color for sparkle
            float wr = Math.min(1f, r * 0.5f + 0.5f);
            float wg = Math.min(1f, g * 0.5f + 0.5f);
            float wb = Math.min(1f, b * 0.5f + 0.5f);
            DustParticleEffect dust = new DustParticleEffect(new Vector3f(wr, wg, wb), 0.3f);
            world.addParticle(dust, px, py, pz, vx, vy, vz);
        }
    }
}
