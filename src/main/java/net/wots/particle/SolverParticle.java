package net.wots.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;

/** Purple orbital core orb for Cyn plushie cluster effect. */
@Environment(EnvType.CLIENT)
public class SolverParticle extends AbstractSolverParticle {
    private static final float R = 0.55f, G = 0.12f, B = 0.78f;
    private final float baseScale, phaseOffset, orbitSpeed, orbitRadius;
    private final double originX, originY, originZ;

    protected SolverParticle(ClientLevel w, double x, double y, double z,
                              double vx, double vy, double vz, SpriteSet sp) {
        super(w, x, y, z, R, G, B, sp);
        originX = x; originY = y; originZ = z;
        xd = vx; yd = vy; zd = vz;
        lifetime = 80 + w.getRandom().nextInt(80);
        baseScale = 0.12f + w.getRandom().nextFloat() * 0.10f; quadSize = baseScale;
        phaseOffset = w.getRandom().nextFloat() * (float) Math.PI * 2;
        orbitSpeed = 0.02f + w.getRandom().nextFloat() * 0.03f;
        orbitRadius = 0.15f + w.getRandom().nextFloat() * 0.2f;
        setSpriteFromAge(sp);
    }

    @Override
    protected SingleQuadParticle.Layer getLayer() {
        return SolverParticleSheets.ADDITIVE;
    }

    @Override public void tick() {
        xo=x; yo=y; zo=z;
        if (age++ >= lifetime) { remove(); return; }
        float life = (float)age/lifetime;
        applyColorRamp(life);
        float fadeIn=Math.min(1f,age/(lifetime*0.1f));
        float fadeOut=Math.max(0f,1f-(life-0.65f)/0.35f);
        alpha = Math.min(fadeIn,fadeOut)*0.85f;
        float pulse=1f+(float)Math.sin(age*0.1f+phaseOffset)*0.25f;
        quadSize = baseScale*pulse*(1f-life*0.2f);
        float ang=age*orbitSpeed+phaseOffset;
        xd=(originX+Math.cos(ang)*orbitRadius*(1f+life*0.5f)-x)*0.08;
        yd=(originY+age*0.003-y)*0.08+0.002;
        zd=(originZ+Math.sin(ang)*orbitRadius*(1f+life*0.5f)-z)*0.08;
        move(xd,yd,zd);
        setSpriteFromAge(spriteProvider);
    }

    @Environment(EnvType.CLIENT) public static class Factory implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sp;
        public Factory(SpriteSet sp) { this.sp = sp; }
        @Override public Particle createParticle(SimpleParticleType t, ClientLevel w, double x, double y, double z, double vx, double vy, double vz, RandomSource random) {
            return new SolverParticle(w, x, y, z, vx, vy, vz, sp); }
    }
}
