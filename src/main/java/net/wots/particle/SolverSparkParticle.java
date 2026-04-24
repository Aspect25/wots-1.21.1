package net.wots.particle;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;

/** Purple fast spark for Cyn cluster ambient effect. */
@Environment(EnvType.CLIENT)
public class SolverSparkParticle extends AbstractSolverParticle {
    private static final float R=0.55f, G=0.12f, B=0.78f;
    protected SolverSparkParticle(ClientLevel w, double x, double y, double z, double vx, double vy, double vz, SpriteSet sp) {
        super(w,x,y,z,R,G,B,sp); lifetime=8+w.getRandom().nextInt(12); quadSize=0.04f+w.getRandom().nextFloat()*0.04f;
        float a=w.getRandom().nextFloat()*(float)Math.PI*2, s=0.05f+w.getRandom().nextFloat()*0.08f;
        xd=Math.cos(a)*s+vx; yd=0.03f+w.getRandom().nextFloat()*0.06f+vy; zd=Math.sin(a)*s+vz;
        rCol=1f;gCol=0.9f;bCol=1f; setSpriteFromAge(sp);
    }
    @Override public void tick(){
        xo=x;yo=y;zo=z; if(age++>=lifetime){remove();return;}
        applySparkColor((float)age/lifetime);
        alpha=age<lifetime*0.7f?1f:(1f-((float)age/lifetime-0.7f)/0.3f);
        yd-=0.002;xd*=0.92;zd*=0.92;
        move(xd,yd,zd);setSpriteFromAge(spriteProvider);
    }
    @Environment(EnvType.CLIENT) public static class Factory implements ParticleProvider<SimpleParticleType>{
        private final SpriteSet sp; public Factory(SpriteSet sp){this.sp=sp;}
        @Override public Particle createParticle(SimpleParticleType t,ClientLevel w,double x,double y,double z,double vx,double vy,double vz,RandomSource random){
            return new SolverSparkParticle(w,x,y,z,vx,vy,vz,sp);}
    }
}
