package net.wots.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;

/** Purple orbital core orb for Cyn plushie cluster effect. */
@Environment(EnvType.CLIENT)
public class SolverParticle extends AbstractSolverParticle {
    private static final float R = 0.55f, G = 0.12f, B = 0.78f;
    private final float baseScale, phaseOffset, orbitSpeed, orbitRadius;
    private final double originX, originY, originZ;

    protected SolverParticle(ClientWorld w, double x, double y, double z,
                              double vx, double vy, double vz, SpriteProvider sp) {
        super(w, x, y, z, R, G, B, sp);
        originX = x; originY = y; originZ = z;
        velocityX = vx; velocityY = vy; velocityZ = vz;
        maxAge = 80 + w.random.nextInt(80);
        baseScale = 0.12f + w.random.nextFloat() * 0.10f; scale = baseScale;
        phaseOffset = w.random.nextFloat() * (float) Math.PI * 2;
        orbitSpeed = 0.02f + w.random.nextFloat() * 0.03f;
        orbitRadius = 0.15f + w.random.nextFloat() * 0.2f;
        setSpriteForAge(sp);
    }

    @Override public void buildGeometry(VertexConsumer vc, Camera cam, float td) {
        float s=scale, a=alpha, sr=red, sg=green, sb=blue;
        scale=s*2.8f; alpha=a*0.15f; red=sr*0.5f; green=sg*0.5f; blue=sb*0.5f;
        super.buildGeometry(vc,cam,td);
        scale=s*1.6f; alpha=a*0.4f; red=sr*0.75f; green=sg*0.75f; blue=sb*0.75f;
        super.buildGeometry(vc,cam,td);
        scale=s; alpha=a*0.9f; red=sr; green=sg; blue=sb;
        super.buildGeometry(vc,cam,td);
        scale=s; alpha=a; red=sr; green=sg; blue=sb;
    }

    @Override public void tick() {
        prevPosX=x; prevPosY=y; prevPosZ=z;
        if (age++ >= maxAge) { markDead(); return; }
        float life = (float)age/maxAge;
        applyColorRamp(life);
        float fadeIn=Math.min(1f,age/(maxAge*0.1f));
        float fadeOut=Math.max(0f,1f-(life-0.65f)/0.35f);
        alpha = Math.min(fadeIn,fadeOut)*0.85f;
        float pulse=1f+(float)Math.sin(age*0.1f+phaseOffset)*0.25f;
        scale = baseScale*pulse*(1f-life*0.2f);
        float ang=age*orbitSpeed+phaseOffset;
        velocityX=(originX+Math.cos(ang)*orbitRadius*(1f+life*0.5f)-x)*0.08;
        velocityY=(originY+age*0.003-y)*0.08+0.002;
        velocityZ=(originZ+Math.sin(ang)*orbitRadius*(1f+life*0.5f)-z)*0.08;
        move(velocityX,velocityY,velocityZ);
        setSpriteForAge(spriteProvider);
    }

    @Environment(EnvType.CLIENT) public static class Factory implements ParticleFactory<SimpleParticleType> {
        private final SpriteProvider sp;
        public Factory(SpriteProvider sp) { this.sp = sp; }
        @Override public Particle createParticle(SimpleParticleType t, ClientWorld w, double x, double y, double z, double vx, double vy, double vz) {
            return new SolverParticle(w, x, y, z, vx, vy, vz, sp); }
    }
}
