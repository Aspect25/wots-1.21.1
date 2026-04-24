package net.wots.particle;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;

/** Colored expanding ring for variant change effect. */
@Environment(EnvType.CLIENT)
public class VariantRingParticle extends AbstractSolverParticle {
    private final float baseScale;
    protected VariantRingParticle(ClientWorld w, double x, double y, double z, double vx, double vy, double vz, SpriteProvider sp) {
        super(w,x,y,z, VariantParticleColor.r(), VariantParticleColor.g(), VariantParticleColor.b(), sp);
        velocityX=0;velocityY=0.001;velocityZ=0;
        maxAge=15+w.random.nextInt(8); baseScale=0.05f;scale=baseScale; setSpriteForAge(sp);
    }
    @Override public ParticleTextureSheet getType(){return SolverParticleSheets.ADDITIVE_DEPTH;}
    @Override public void buildGeometry(VertexConsumer vc,Camera c,float td){
        float s=scale,a=alpha,r=red,g=green,b=blue;
        scale=s*1.4f;alpha=a*0.4f;red=r*0.5f;green=g*0.5f;blue=b*0.5f;
        super.buildGeometry(vc,c,td);
        scale=s;alpha=a;red=r;green=g;blue=b;super.buildGeometry(vc,c,td);
    }
    @Override public void tick(){
        prevPosX=x;prevPosY=y;prevPosZ=z; if(age++>=maxAge){markDead();return;}
        float life=(float)age/maxAge;
        scale=baseScale+(1f-(1f-life)*(1f-life))*0.7f;
        applySimpleFade(life);
        alpha=life<0.15f?life/0.15f*0.7f:0.7f*(1f-(life-0.15f)/0.85f);
        move(velocityX,velocityY,velocityZ);setSpriteForAge(spriteProvider);
    }
    @Environment(EnvType.CLIENT) public static class Factory implements ParticleFactory<SimpleParticleType>{
        private final SpriteProvider sp; public Factory(SpriteProvider sp){this.sp=sp;}
        @Override public Particle createParticle(SimpleParticleType t,ClientWorld w,double x,double y,double z,double vx,double vy,double vz){
            return new VariantRingParticle(w,x,y,z,vx,vy,vz,sp);}
    }
}
