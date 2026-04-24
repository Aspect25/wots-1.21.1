package net.wots.particle;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;

/** Colored short-lived wisp for variant change effect. No trail — just a drifting soft glow. */
@Environment(EnvType.CLIENT)
public class VariantWispParticle extends AbstractSolverParticle {
    private final float baseScale, driftAngle;
    protected VariantWispParticle(ClientWorld w, double x, double y, double z, double vx, double vy, double vz, SpriteProvider sp) {
        super(w,x,y,z, VariantParticleColor.r(), VariantParticleColor.g(), VariantParticleColor.b(), sp);
        velocityX=vx;velocityY=vy;velocityZ=vz;
        maxAge=25+w.random.nextInt(20);
        baseScale=0.10f+w.random.nextFloat()*0.08f; scale=baseScale;
        driftAngle=w.random.nextFloat()*(float)Math.PI*2;
        setSpriteForAge(sp);
    }
    @Override public void tick(){
        prevPosX=x;prevPosY=y;prevPosZ=z; if(age++>=maxAge){markDead();return;}
        float life=(float)age/maxAge;
        applySimpleFade(life);
        float fi=Math.min(1f,age/(maxAge*0.15f)),fo=Math.max(0f,1f-(life-0.7f)/0.3f);
        alpha=Math.min(fi,fo)*0.6f;
        scale=baseScale*Math.min(1f,age/10f)*(1f-life*0.3f);
        float ds=0.005f+life*0.003f;
        velocityX=Math.cos(driftAngle+age*0.02f)*ds;
        velocityY=0.002;
        velocityZ=Math.sin(driftAngle+age*0.02f)*ds;
        move(velocityX,velocityY,velocityZ);setSpriteForAge(spriteProvider);
    }
    @Environment(EnvType.CLIENT) public static class Factory implements ParticleFactory<SimpleParticleType>{
        private final SpriteProvider sp; public Factory(SpriteProvider sp){this.sp=sp;}
        @Override public Particle createParticle(SimpleParticleType t,ClientWorld w,double x,double y,double z,double vx,double vy,double vz){
            return new VariantWispParticle(w,x,y,z,vx,vy,vz,sp);}
    }
}
