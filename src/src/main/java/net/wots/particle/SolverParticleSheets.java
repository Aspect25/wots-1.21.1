package net.wots.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.ParticleTextureSheet;

/**
 * Solver particle render sheets.
 *
 * Using vanilla PARTICLE_SHEET_TRANSLUCENT as the base —
 * the layered buildGeometry() calls + self-lit getBrightness()
 * already handle the glow effect. Additive blending can be
 * revisited later if needed.
 */
@Environment(EnvType.CLIENT)
public final class SolverParticleSheets {

    public static final ParticleTextureSheet ADDITIVE = ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    public static final ParticleTextureSheet ADDITIVE_DEPTH = ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;

    private SolverParticleSheets() {}
}
