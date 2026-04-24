package net.wots.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;

/** Purple trailing wisp for Cyn cluster ambient effect. */
@Environment(EnvType.CLIENT)
public class SolverWispParticle extends AbstractSolverParticle {
    private static final float R=0.45f, G=0.08f, B=0.65f;
    private static final int TL=12;
    private final double[] tX=new double[TL], tY=new double[TL], tZ=new double[TL];
    private int tHead=0, tSize=0;
    private final float baseScale, phaseOffset, driftAngle;

    protected SolverWispParticle(ClientWorld w, double x, double y, double z,
                                  double vx, double vy, double vz, SpriteProvider sp) {
        super(w, x, y, z, R, G, B, sp);
        velocityX=vx; velocityY=vy; velocityZ=vz;
        maxAge=100+w.random.nextInt(100);
        baseScale=0.10f+w.random.nextFloat()*0.08f; scale=baseScale;
        phaseOffset=w.random.nextFloat()*(float)Math.PI*2;
        driftAngle=w.random.nextFloat()*(float)Math.PI*2;
        for(int i=0;i<TL;i++){tX[i]=x;tY[i]=y;tZ[i]=z;}
        setSpriteForAge(sp);
    }

    @Override public void buildGeometry(VertexConsumer vc, Camera cam, float td) {
        float s=scale,a=alpha;
        scale=s*2f; alpha=a*0.2f; super.buildGeometry(vc,cam,td);
        scale=s; alpha=a*0.7f; super.buildGeometry(vc,cam,td);
        scale=s; alpha=a;
        if(tSize<2)return;
        Vec3d cp=cam.getPos(); float uMn=getMinU(),uMx=getMaxU(),vMn=getMinV(); int li=getBrightness(td);
        for(int i=1;i<tSize;i++){
            int idx=(tHead-i+TL)%TL, prev=(tHead-i+1+TL)%TL;
            float tf=1f-(float)i/tSize; float ta=alpha*tf*tf*0.5f; float tw=scale*tf*0.6f;
            if(ta<0.01f)continue;
            double mx=(tX[idx]+tX[prev])*0.5-cp.x, my=(tY[idx]+tY[prev])*0.5-cp.y, mz=(tZ[idx]+tZ[prev])*0.5-cp.z;
            int cr=(int)(red*tf*255)&0xFF, cg=(int)(green*tf*255)&0xFF, cb=(int)(blue*tf*255)&0xFF, ca=(int)(ta*255)&0xFF;
            Vector3f up=cam.getVerticalPlane(), rt=cam.getHorizontalPlane(); float hw=tw*0.5f;
            vc.vertex((float)mx-rt.x()*hw-up.x()*hw,(float)my-rt.y()*hw-up.y()*hw,(float)mz-rt.z()*hw-up.z()*hw).texture(uMx,uMx).color(cr,cg,cb,ca).light(li);
            vc.vertex((float)mx-rt.x()*hw+up.x()*hw,(float)my-rt.y()*hw+up.y()*hw,(float)mz-rt.z()*hw+up.z()*hw).texture(uMx,vMn).color(cr,cg,cb,ca).light(li);
            vc.vertex((float)mx+rt.x()*hw+up.x()*hw,(float)my+rt.y()*hw+up.y()*hw,(float)mz+rt.z()*hw+up.z()*hw).texture(uMn,vMn).color(cr,cg,cb,ca).light(li);
            vc.vertex((float)mx+rt.x()*hw-up.x()*hw,(float)my+rt.y()*hw-up.y()*hw,(float)mz+rt.z()*hw-up.z()*hw).texture(uMn,uMx).color(cr,cg,cb,ca).light(li);
        }
    }

    @Override public void tick() {
        prevPosX=x;prevPosY=y;prevPosZ=z;
        tX[tHead]=x;tY[tHead]=y;tZ[tHead]=z; tHead=(tHead+1)%TL; if(tSize<TL)tSize++;
        if(age++>=maxAge){markDead();return;}
        float life=(float)age/maxAge;
        applySimpleFade(life);
        float fi=Math.min(1f,age/(maxAge*0.15f)), fo=Math.max(0f,1f-(life-0.7f)/0.3f);
        alpha=Math.min(fi,fo)*0.6f;
        scale=baseScale*Math.min(1f,age/20f)*(1f-life*0.3f);
        float ang=driftAngle+age*0.018f, ds=0.004f+life*0.003f;
        velocityX=Math.cos(ang)*ds+Math.sin(age*0.05f+phaseOffset)*0.0015;
        velocityY=0.0015+Math.sin(age*0.035f)*0.0008;
        velocityZ=Math.sin(ang)*ds+Math.cos(age*0.05f+phaseOffset)*0.0015;
        move(velocityX,velocityY,velocityZ); setSpriteForAge(spriteProvider);
    }

    @Environment(EnvType.CLIENT) public static class Factory implements ParticleFactory<SimpleParticleType> {
        private final SpriteProvider sp; public Factory(SpriteProvider sp){this.sp=sp;}
        @Override public Particle createParticle(SimpleParticleType t,ClientWorld w,double x,double y,double z,double vx,double vy,double vz){
            return new SolverWispParticle(w,x,y,z,vx,vy,vz,sp);}
    }
}
