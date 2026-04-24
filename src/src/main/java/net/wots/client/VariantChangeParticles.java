package net.wots.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.wots.particle.ModParticles;
import net.wots.particle.VariantParticleColor;

import java.util.Random;

/**
 * Spawns variant-change particle effects using dedicated Variant* particles.
 * Color is set via VariantParticleColor before each addParticle call —
 * the factory reads it at construction time, so it's baked into the particle.
 */
public class VariantChangeParticles {

    private static final Random RNG = new Random();

    public static void spawnBurst(BlockPos pos, int color) {
        ClientWorld world = MinecraftClient.getInstance().world;
        if (world == null) return;
        ParticleManager pm = MinecraftClient.getInstance().particleManager;

        float r = ((color >> 16) & 0xFF) / 255f;
        float g = ((color >> 8) & 0xFF) / 255f;
        float b = (color & 0xFF) / 255f;

        double cx = pos.getX() + 0.5, cy = pos.getY() + 0.5, cz = pos.getZ() + 0.5;

        // ── Ring pulse ───────────────────────────────────────────────────
        VariantParticleColor.set(r, g, b);
        pm.addParticle(ModParticles.VARIANT_RING, cx, cy, cz, 0, 0, 0);

        // ── Core orbs ────────────────────────────────────────────────────
        VariantParticleColor.set(
                Math.min(1f, r * 1.2f + 0.15f),
                Math.min(1f, g * 1.2f + 0.15f),
                Math.min(1f, b * 1.2f + 0.15f));
        for (int i = 0; i < 12; i++) {
            double angle = (2 * Math.PI / 12) * i;
            double rad = 0.25 + RNG.nextDouble() * 0.15;
            pm.addParticle(ModParticles.VARIANT_CORE,
                    cx + Math.cos(angle) * rad,
                    cy + (RNG.nextDouble() - 0.5) * 0.4,
                    cz + Math.sin(angle) * rad,
                    Math.cos(angle) * 0.06, 0.02 + RNG.nextDouble() * 0.03, Math.sin(angle) * 0.06);
        }

        // ── Wisps ────────────────────────────────────────────────────────
        VariantParticleColor.set(r * 0.7f, g * 0.5f, b * 0.8f);
        for (int i = 0; i < 6; i++) {
            double angle = (2 * Math.PI / 6) * i + RNG.nextDouble() * 0.5;
            double rad = 0.15 + RNG.nextDouble() * 0.1;
            pm.addParticle(ModParticles.VARIANT_WISP,
                    cx + Math.cos(angle) * rad,
                    cy - 0.2 + RNG.nextDouble() * 0.5,
                    cz + Math.sin(angle) * rad,
                    Math.cos(angle) * 0.003, 0.004, Math.sin(angle) * 0.003);
        }

        // ── Sparks ───────────────────────────────────────────────────────
        VariantParticleColor.set(r, g, b);
        for (int i = 0; i < 10; i++) {
            double angle = RNG.nextDouble() * Math.PI * 2;
            double speed = 0.04 + RNG.nextDouble() * 0.06;
            pm.addParticle(ModParticles.VARIANT_SPARK,
                    cx + (RNG.nextDouble() - 0.5) * 0.3,
                    cy + (RNG.nextDouble() - 0.3) * 0.5,
                    cz + (RNG.nextDouble() - 0.5) * 0.3,
                    Math.cos(angle) * speed, 0.03 + RNG.nextDouble() * 0.05, Math.sin(angle) * speed);
        }

        // ── White sparkle pops ───────────────────────────────────────────
        VariantParticleColor.set(
                Math.min(1f, r * 0.3f + 0.7f),
                Math.min(1f, g * 0.3f + 0.7f),
                Math.min(1f, b * 0.3f + 0.7f));
        for (int i = 0; i < 5; i++) {
            pm.addParticle(ModParticles.VARIANT_SPARK,
                    cx + (RNG.nextDouble() - 0.5) * 0.5,
                    cy + (RNG.nextDouble() - 0.5) * 0.7,
                    cz + (RNG.nextDouble() - 0.5) * 0.5,
                    (RNG.nextDouble() - 0.5) * 0.04, 0.02 + RNG.nextDouble() * 0.04, (RNG.nextDouble() - 0.5) * 0.04);
        }
    }
}
