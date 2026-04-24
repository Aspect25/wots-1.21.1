package net.wots.particle;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;

/** Purple expanding ring for Cyn cluster ambient effect. */
@Environment(EnvType.CLIENT)
public class SolverRingParticle extends AbstractSolverParticle {
    private static final float R=0.65f, G=0.15f, B=0.90f;
    private final float baseScale;
    protected SolverRingParticle(ClientLevel w, double x, double y, double z, double vx, double vy, double vz, SpriteSet sp) {
        super(w,x,y,z,R,G,B,sp); xd=0;yd=0.001;zd=0;
        lifetime=25+w.getRandom().nextInt(15); baseScale=0.05f;quadSize=baseScale; setSpriteFromAge(sp);
    }
    @Override protected SingleQuadParticle.Layer getLayer(){return SolverParticleSheets.ADDITIVE_DEPTH;}
    @Override public void tick(){
        xo=x;yo=y;zo=z; if(age++>=lifetime){remove();return;}
        float life=(float)age/lifetime;
        quadSize=baseScale+(1f-(1f-life)*(1f-life))*0.7f;
        applySimpleFade(life);
        alpha=life<0.15f?life/0.15f*0.7f:0.7f*(1f-(life-0.15f)/0.85f);
        move(xd,yd,zd);setSpriteFromAge(spriteProvider);
    }
    @Environment(EnvType.CLIENT) public static class Factory implements ParticleProvider<SimpleParticleType>{
        private final SpriteSet sp; public Factory(SpriteSet sp){this.sp=sp;}
        @Override public Particle createParticle(SimpleParticleType t,ClientLevel w,double x,double y,double z,double vx,double vy,double vz,RandomSource random){
            return new SolverRingParticle(w,x,y,z,vx,vy,vz,sp);}
    }
}
