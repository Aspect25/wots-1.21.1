package net.wots.particle;

import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.wots.Wots;

public class ModParticles {
    // Cyn cluster ambient particles (always purple)
    public static final SimpleParticleType SOLVER_PARTICLE = FabricParticleTypes.simple();
    public static final SimpleParticleType SOLVER_WISP = FabricParticleTypes.simple();
    public static final SimpleParticleType SOLVER_SPARK = FabricParticleTypes.simple();
    public static final SimpleParticleType SOLVER_RING = FabricParticleTypes.simple();

    // Variant change burst particles (colored per-variant)
    public static final SimpleParticleType VARIANT_CORE = FabricParticleTypes.simple();
    public static final SimpleParticleType VARIANT_WISP = FabricParticleTypes.simple();
    public static final SimpleParticleType VARIANT_SPARK = FabricParticleTypes.simple();
    public static final SimpleParticleType VARIANT_RING = FabricParticleTypes.simple();

    public static void registerParticles() {
        Registry.register(Registries.PARTICLE_TYPE, Identifier.of(Wots.MOD_ID, "solver_particle"), SOLVER_PARTICLE);
        Registry.register(Registries.PARTICLE_TYPE, Identifier.of(Wots.MOD_ID, "solver_wisp"), SOLVER_WISP);
        Registry.register(Registries.PARTICLE_TYPE, Identifier.of(Wots.MOD_ID, "solver_spark"), SOLVER_SPARK);
        Registry.register(Registries.PARTICLE_TYPE, Identifier.of(Wots.MOD_ID, "solver_ring"), SOLVER_RING);
        Registry.register(Registries.PARTICLE_TYPE, Identifier.of(Wots.MOD_ID, "variant_core"), VARIANT_CORE);
        Registry.register(Registries.PARTICLE_TYPE, Identifier.of(Wots.MOD_ID, "variant_wisp"), VARIANT_WISP);
        Registry.register(Registries.PARTICLE_TYPE, Identifier.of(Wots.MOD_ID, "variant_spark"), VARIANT_SPARK);
        Registry.register(Registries.PARTICLE_TYPE, Identifier.of(Wots.MOD_ID, "variant_ring"), VARIANT_RING);
    }
}
