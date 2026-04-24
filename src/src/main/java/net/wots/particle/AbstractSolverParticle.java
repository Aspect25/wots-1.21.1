package net.wots.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.SpriteBillboardParticle;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;

/**
 * Base class for all WOTS particles.
 * Provides color ramp helpers and shared rendering config.
 */
@Environment(EnvType.CLIENT)
public abstract class AbstractSolverParticle extends SpriteBillboardParticle {

    protected float baseR, baseG, baseB;
    protected float startR, startG, startB;
    protected float endR, endG, endB;
    protected final SpriteProvider spriteProvider;

    protected AbstractSolverParticle(ClientWorld world, double x, double y, double z,
                                      float r, float g, float b,
                                      SpriteProvider spriteProvider) {
        super(world, x, y, z);
        this.spriteProvider = spriteProvider;
        setBaseColor(r, g, b);
        this.red = startR;
        this.green = startG;
        this.blue = startB;
    }

    protected void setBaseColor(float r, float g, float b) {
        this.baseR = r; this.baseG = g; this.baseB = b;
        this.startR = Math.min(1f, r * 0.5f + 0.5f);
        this.startG = Math.min(1f, g * 0.5f + 0.5f);
        this.startB = Math.min(1f, b * 0.5f + 0.5f);
        this.endR = r * 0.15f;
        this.endG = g * 0.15f;
        this.endB = b * 0.15f;
    }

    /** Bright start → base → dark end. For core orbs. */
    protected void applyColorRamp(float life) {
        if (life < 0.2f) {
            float t = life / 0.2f;
            this.red = lerp(startR, baseR, t);
            this.green = lerp(startG, baseG, t);
            this.blue = lerp(startB, baseB, t);
        } else if (life < 0.6f) {
            float dim = 1f - ((life - 0.2f) / 0.4f) * 0.5f;
            this.red = baseR * dim;
            this.green = baseG * dim;
            this.blue = baseB * dim;
        } else {
            float t = (life - 0.6f) / 0.4f;
            this.red = lerp(baseR * 0.5f, endR, t);
            this.green = lerp(baseG * 0.5f, endG, t);
            this.blue = lerp(baseB * 0.5f, endB, t);
        }
    }

    /** Uniform fade to black. For wisps and rings. */
    protected void applySimpleFade(float life) {
        float fade = 1f - life;
        this.red = baseR * fade;
        this.green = baseG * fade;
        this.blue = baseB * fade;
    }

    /** White-hot → base color → dark. For sparks. */
    protected void applySparkColor(float life) {
        if (life < 0.3f) {
            float t = life / 0.3f;
            this.red = 1f - t * (1f - baseR);
            this.green = 0.95f - t * (0.95f - baseG);
            this.blue = 1f - t * (1f - baseB);
        } else {
            float t = (life - 0.3f) / 0.7f;
            float fade = 1f - t;
            this.red = baseR * fade;
            this.green = baseG * fade;
            this.blue = baseB * fade;
        }
    }

    @Override
    public ParticleTextureSheet getType() {
        return SolverParticleSheets.ADDITIVE;
    }

    @Override
    public int getBrightness(float tint) {
        return 0xF000F0;
    }

    protected static float lerp(float a, float b, float t) {
        return a + (b - a) * t;
    }
}
