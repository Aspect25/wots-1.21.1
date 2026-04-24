package net.wots.particle;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;

/** Colored burst orb for variant change effect. Color set at creation via VariantParticleColor. */
@Environment(EnvType.CLIENT)
public class VariantCoreParticle extends AbstractSolverParticle {
    private final float baseScale, phaseOffset;
    protected VariantCoreParticle(ClientWorld w, double x, double y, double z, double vx, double vy, double vz, SpriteProvider sp) {
        super(w, x, y, z, VariantParticleColor.r(), VariantParticleColor.g(), VariantParticleColor.b(), sp);
        velocityX=vx; velocityY=vy; velocityZ=vz;
        maxAge=20+w.random.nextInt(15);
        baseScale=0.10f+w.random.nextFloat()*0.08f; scale=baseScale;
        phaseOffset=w.random.nextFloat()*(float)Math.PI*2;
        setSpriteForAge(sp);
    }
    @Override public void buildGeometry(VertexConsumer vc, Camera c, float td) {
        float s=scale,a=alpha,sr=red,sg=green,sb=blue;
        scale=s*2.5f;alpha=a*0.15f;red=sr*0.5f;green=sg*0.5f;blue=sb*0.5f;
        super.buildGeometry(vc,c,td);
        scale=s*1.4f;alpha=a*0.4f;red=sr*0.75f;green=sg*0.75f;blue=sb*0.75f;
        super.buildGeometry(vc,c,td);
        scale=s;alpha=a*0.9f;red=sr;green=sg;blue=sb;
        super.buildGeometry(vc,c,td);
        scale=s;alpha=a;red=sr;green=sg;blue=sb;
    }
    @Override public void tick() {
        prevPosX=x;prevPosY=y;prevPosZ=z; if(age++>=maxAge){markDead();return;}
        float life=(float)age/maxAge;
        applyColorRamp(life);
        float fi=Math.min(1f,age/(maxAge*0.1f)),fo=Math.max(0f,1f-(life-0.6f)/0.4f);
        alpha=Math.min(fi,fo)*0.9f;
        scale=baseScale*(1f+(float)Math.sin(age*0.15f+phaseOffset)*0.2f)*(1f-life*0.3f);
        velocityX*=0.95;velocityY=velocityY*0.95+0.002;velocityZ*=0.95;
        move(velocityX,velocityY,velocityZ);setSpriteForAge(spriteProvider);
    }
    @Environment(EnvType.CLIENT) public static class Factory implements ParticleFactory<SimpleParticleType>{
        private final SpriteProvider sp; public Factory(SpriteProvider sp){this.sp=sp;}
        @Override public Particle createParticle(SimpleParticleType t,ClientWorld w,double x,double y,double z,double vx,double vy,double vz){
            return new VariantCoreParticle(w,x,y,z,vx,vy,vz,sp);}
    }
}
