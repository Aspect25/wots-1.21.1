package net.wots.particle;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;

/** Purple fast spark for Cyn cluster ambient effect. */
@Environment(EnvType.CLIENT)
public class SolverSparkParticle extends AbstractSolverParticle {
    private static final float R=0.55f, G=0.12f, B=0.78f;
    protected SolverSparkParticle(ClientWorld w, double x, double y, double z, double vx, double vy, double vz, SpriteProvider sp) {
        super(w,x,y,z,R,G,B,sp); maxAge=8+w.random.nextInt(12); scale=0.04f+w.random.nextFloat()*0.04f;
        float a=w.random.nextFloat()*(float)Math.PI*2, s=0.05f+w.random.nextFloat()*0.08f;
        velocityX=Math.cos(a)*s+vx; velocityY=0.03f+w.random.nextFloat()*0.06f+vy; velocityZ=Math.sin(a)*s+vz;
        red=1f;green=0.9f;blue=1f; setSpriteForAge(sp);
    }
    @Override public void buildGeometry(VertexConsumer vc,Camera c,float td){
        super.buildGeometry(vc,c,td);
        float s=scale,a=alpha; scale=s*0.5f;alpha=Math.min(1f,a*1.5f);
        red=Math.min(1f,baseR*0.5f+0.5f);green=Math.min(1f,baseG*0.5f+0.5f);blue=Math.min(1f,baseB*0.5f+0.5f);
        super.buildGeometry(vc,c,td); scale=s;alpha=a;
    }
    @Override public void tick(){
        prevPosX=x;prevPosY=y;prevPosZ=z; if(age++>=maxAge){markDead();return;}
        applySparkColor((float)age/maxAge);
        alpha=age<maxAge*0.7f?1f:(1f-((float)age/maxAge-0.7f)/0.3f);
        velocityY-=0.002;velocityX*=0.92;velocityZ*=0.92;
        move(velocityX,velocityY,velocityZ);setSpriteForAge(spriteProvider);
    }
    @Environment(EnvType.CLIENT) public static class Factory implements ParticleFactory<SimpleParticleType>{
        private final SpriteProvider sp; public Factory(SpriteProvider sp){this.sp=sp;}
        @Override public Particle createParticle(SimpleParticleType t,ClientWorld w,double x,double y,double z,double vx,double vy,double vz){
            return new SolverSparkParticle(w,x,y,z,vx,vy,vz,sp);}
    }
}
