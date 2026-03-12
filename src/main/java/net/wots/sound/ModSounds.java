package net.wots.sound;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.wots.Wots;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModSounds {
    public static final SoundEvent CHISEL_USE = registerSoundEvent("chisel_use");

    public static final SoundEvent J_NOISE = registerSoundEvent("j_noise");
    public static final SoundEvent CYN_NOISE_1 = registerSoundEvent("cyn_noise_1");
    public static final SoundEvent CYN_NOISE_2 = registerSoundEvent("cyn_noise_2");
    public static final SoundEvent CYN_NOISE_3 = registerSoundEvent("cyn_noise_3");
    public static final SoundEvent CYN_NOISE_4 = registerSoundEvent("cyn_noise_4");
    public static final SoundEvent CYN_NOISE_5 = registerSoundEvent("cyn_noise_5");
    public static final SoundEvent CYN_NOISE_6 = registerSoundEvent("cyn_noise_6");
    public static final SoundEvent FATASS_NOISE = registerSoundEvent("fatass_noise");
    public static final SoundEvent UZI_NOISE = registerSoundEvent("uzi_noise");
    public static final SoundEvent N_NOISE_1 = registerSoundEvent("n_noise_1");
    public static final SoundEvent N_NOISE_2 = registerSoundEvent("n_noise_2");
    public static final SoundEvent N_NOISE_3 = registerSoundEvent("n_noise_3");
    public static final SoundEvent N_NOISE_4 = registerSoundEvent("n_noise_4");
    public static final SoundEvent N_NOISE_5 = registerSoundEvent("n_noise_5");
    public static final SoundEvent N_NOISE_6 = registerSoundEvent("n_noise_6");
    public static final SoundEvent N_NOISE_7 = registerSoundEvent("n_noise_7");
    public static final SoundEvent N_NOISE_8 = registerSoundEvent("n_noise_8");
    public static final SoundEvent N_NOISE_9 = registerSoundEvent("n_noise_9");
    public static final SoundEvent N_NOISE_10 = registerSoundEvent("n_noise_10");
    public static final SoundEvent N_NOISE_11 = registerSoundEvent("n_noise_11");
    public static final SoundEvent N_NOISE_12 = registerSoundEvent("n_noise_12");
    public static final SoundEvent N_NOISE_13 = registerSoundEvent("n_noise_13");
    public static final SoundEvent N_NOISE_14 = registerSoundEvent("n_noise_14");
    public static final SoundEvent N_NOISE_15 = registerSoundEvent("n_noise_15");
    public static final SoundEvent UZI_NOISE_2 = registerSoundEvent("uzi_noise_2");
    public static final SoundEvent UZI_NOISE_3 = registerSoundEvent("uzi_noise_3");
    public static final SoundEvent UZI_NOISE_4 = registerSoundEvent("uzi_noise_4");
    public static final SoundEvent UZI_NOISE_5 = registerSoundEvent("uzi_noise_5");
    public static final SoundEvent UZI_NOISE_6 = registerSoundEvent("uzi_noise_6");
    public static final SoundEvent TRASHED_1 = registerSoundEvent("trashed_1");
    public static final SoundEvent TRASHED_5 = registerSoundEvent("trashed_5");
    public static final SoundEvent TRASHED_10 = registerSoundEvent("trashed_10");
    public static final SoundEvent TRASHED_15 = registerSoundEvent("trashed_15");
    public static final SoundEvent TRASHED_20 = registerSoundEvent("trashed_20");
    public static final SoundEvent TRASHED_25 = registerSoundEvent("trashed_25");
    public static final SoundEvent TRASHED_30 = registerSoundEvent("trashed_30");
    public static final SoundEvent TRASHED_35 = registerSoundEvent("trashed_35");
    public static final SoundEvent TRASHED_40 = registerSoundEvent("trashed_40");
    public static final SoundEvent TRASHED_50 = registerSoundEvent("trashed_50");
    public static final SoundEvent TRASHED_69 = registerSoundEvent("trashed_69");
    public static final SoundEvent TRASHED_70 = registerSoundEvent("trashed_70");








    private static SoundEvent registerSoundEvent(String name) {
        Identifier id = Identifier.of(Wots.MOD_ID, name);
        return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(id));
    }



    public static void registerSounds() {
        Wots.LOGGER.info("Registering Mod Sounds for " + Wots.MOD_ID);
    }
}
