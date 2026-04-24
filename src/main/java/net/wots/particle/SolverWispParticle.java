package net.wots.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;

/** Purple trailing wisp for Cyn cluster ambient effect. */
@Environment(EnvType.CLIENT)
public class SolverWispParticle extends AbstractSolverParticle {
    private static final float R=0.45f, G=0.08f, B=0.65f;
    private final float baseScale, phaseOffset, driftAngle;

    protected SolverWispParticle(ClientLevel w, double x, double y, double z,
                                  double vx, double vy, double vz, SpriteSet sp) {
        super(w, x, y, z, R, G, B, sp);
        xd=vx; yd=vy; zd=vz;
        lifetime=100+w.getRandom().nextInt(100);
        baseScale=0.10f+w.getRandom().nextFloat()*0.08f; quadSize=baseScale;
        phaseOffset=w.getRandom().nextFloat()*(float)Math.PI*2;
        driftAngle=w.getRandom().nextFloat()*(float)Math.PI*2;
        setSpriteFromAge(sp);
    }

    @Override public void tick() {
        xo=x;yo=y;zo=z;
        if(age++>=lifetime){remove();return;}
        float life=(float)age/lifetime;
        applySimpleFade(life);
        float fi=Math.min(1f,age/(lifetime*0.15f)), fo=Math.max(0f,1f-(life-0.7f)/0.3f);
        alpha=Math.min(fi,fo)*0.6f;
        quadSize=baseScale*Math.min(1f,age/20f)*(1f-life*0.3f);
        float ang=driftAngle+age*0.018f, ds=0.004f+life*0.003f;
        xd=Math.cos(ang)*ds+Math.sin(age*0.05f+phaseOffset)*0.0015;
        yd=0.0015+Math.sin(age*0.035f)*0.0008;
        zd=Math.sin(ang)*ds+Math.cos(age*0.05f+phaseOffset)*0.0015;
        move(xd,yd,zd); setSpriteFromAge(spriteProvider);
    }

    @Environment(EnvType.CLIENT) public static class Factory implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sp; public Factory(SpriteSet sp){this.sp=sp;}
        @Override public Particle createParticle(SimpleParticleType t,ClientLevel w,double x,double y,double z,double vx,double vy,double vz,RandomSource random){
            return new SolverWispParticle(w,x,y,z,vx,vy,vz,sp);}
    }
}
