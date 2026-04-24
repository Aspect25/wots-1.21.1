package net.wots.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

/**
 * Base class for all WOTS particles.
 * Provides color ramp helpers and shared rendering config.
 */
@Environment(EnvType.CLIENT)
public abstract class AbstractSolverParticle extends SingleQuadParticle {

    protected float baseR, baseG, baseB;
    protected float startR, startG, startB;
    protected float endR, endG, endB;
    protected final SpriteSet spriteProvider;

    protected AbstractSolverParticle(ClientLevel world, double x, double y, double z,
                                      float r, float g, float b,
                                      SpriteSet spriteProvider) {
        super(world, x, y, z, spriteProvider.get(world.getRandom()));
        this.spriteProvider = spriteProvider;
        setBaseColor(r, g, b);
        this.rCol = startR;
        this.gCol = startG;
        this.bCol = startB;
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

    /** Bright start -> base -> dark end. For core orbs. */
    protected void applyColorRamp(float life) {
        if (life < 0.2f) {
            float t = life / 0.2f;
            this.rCol = lerp(startR, baseR, t);
            this.gCol = lerp(startG, baseG, t);
            this.bCol = lerp(startB, baseB, t);
        } else if (life < 0.6f) {
            float dim = 1f - ((life - 0.2f) / 0.4f) * 0.5f;
            this.rCol = baseR * dim;
            this.gCol = baseG * dim;
            this.bCol = baseB * dim;
        } else {
            float t = (life - 0.6f) / 0.4f;
            this.rCol = lerp(baseR * 0.5f, endR, t);
            this.gCol = lerp(baseG * 0.5f, endG, t);
            this.bCol = lerp(baseB * 0.5f, endB, t);
        }
    }

    /** Uniform fade to black. For wisps and rings. */
    protected void applySimpleFade(float life) {
        float fade = 1f - life;
        this.rCol = baseR * fade;
        this.gCol = baseG * fade;
        this.bCol = baseB * fade;
    }

    /** White-hot -> base color -> dark. For sparks. */
    protected void applySparkColor(float life) {
        if (life < 0.3f) {
            float t = life / 0.3f;
            this.rCol = 1f - t * (1f - baseR);
            this.gCol = 0.95f - t * (0.95f - baseG);
            this.bCol = 1f - t * (1f - baseB);
        } else {
            float t = (life - 0.3f) / 0.7f;
            float fade = 1f - t;
            this.rCol = baseR * fade;
            this.gCol = baseG * fade;
            this.bCol = baseB * fade;
        }
    }

    @Override
    protected SingleQuadParticle.Layer getLayer() {
        return SolverParticleSheets.ADDITIVE;
    }

    // getBrightness removed in MC 26.1 -- fullbright handled via extract/render-state

    protected static float lerp(float a, float b, float t) {
        return a + (b - a) * t;
    }
}
