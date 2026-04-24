package net.wots.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * Holds the pending color for the next variant particle to be created.
 *
 * Usage in VariantChangeParticles:
 *   VariantParticleColor.set(r, g, b);
 *   pm.addParticle(ModParticles.VARIANT_CORE, ...);
 *   // Factory reads the color during createParticle()
 *
 * This is needed because Minecraft's addParticle() for SimpleParticleType
 * has no way to pass custom constructor args. The static field pattern
 * is safe here because particle creation is single-threaded (client render thread).
 */
@Environment(EnvType.CLIENT)
public final class VariantParticleColor {

    private static float r = 1f, g = 1f, b = 1f;

    public static void set(float red, float green, float blue) {
        r = red; g = green; b = blue;
    }

    public static float r() { return r; }
    public static float g() { return g; }
    public static float b() { return b; }

    private VariantParticleColor() {}
}
