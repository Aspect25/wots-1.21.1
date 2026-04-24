package net.wots.sound;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.Registry;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.resources.Identifier;
import net.wots.Wots;

public class ModSounds {

    // -------------------------------------------------------------------------
    // Existing sounds (your originals — untouched)
    // -------------------------------------------------------------------------
    public static final SoundEvent CHISEL_USE = registerSoundEvent("chisel_use");

    public static final SoundEvent J_NOISE = registerSoundEvent("j_noise");
    public static final SoundEvent CYN_NOISE_1 = registerSoundEvent("cyn_noise_1");
    public static final SoundEvent CYN_NOISE_2 = registerSoundEvent("cyn_noise_2");
    public static final SoundEvent CYN_NOISE_3 = registerSoundEvent("cyn_noise_3");
    public static final SoundEvent CYN_NOISE_4 = registerSoundEvent("cyn_noise_4");
    public static final SoundEvent CYN_NOISE_5 = registerSoundEvent("cyn_noise_5");
    public static final SoundEvent CYN_NOISE_6 = registerSoundEvent("cyn_noise_6");
    public static final SoundEvent CYN_NOISE_7 = registerSoundEvent("cyn_noise_7");
    public static final SoundEvent CYN_NOISE_8 = registerSoundEvent("cyn_noise_8");
    public static final SoundEvent CYN_NOISE_9 = registerSoundEvent("cyn_noise_9");
    public static final SoundEvent CYN_NOISE_10 = registerSoundEvent("cyn_noise_10");
    public static final SoundEvent CYN_NOISE_11 = registerSoundEvent("cyn_noise_11");


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
    public static final SoundEvent GABE_LINE_1  = registerSoundEvent("gabe_line_1");
    public static final SoundEvent GABE_LINE_2  = registerSoundEvent("gabe_line_2");
    public static final SoundEvent GABE_LINE_3  = registerSoundEvent("gabe_line_3");
    public static final SoundEvent GABE_LINE_4  = registerSoundEvent("gabe_line_4");
    public static final SoundEvent GABE_LINE_5  = registerSoundEvent("gabe_line_5");
    public static final SoundEvent GABE_LINE_6  = registerSoundEvent("gabe_line_6");
    public static final SoundEvent GABE_LINE_7  = registerSoundEvent("gabe_line_7");
    public static final SoundEvent GABE_LINE_8  = registerSoundEvent("gabe_line_8");
    public static final SoundEvent GABE_LINE_9  = registerSoundEvent("gabe_line_9");
    public static final SoundEvent GABE_LINE_10 = registerSoundEvent("gabe_line_10");
    public static final SoundEvent GABE_LINE_11 = registerSoundEvent("gabe_line_11");
    public static final SoundEvent GABE_LINE_12 = registerSoundEvent("gabe_line_12");
    public static final SoundEvent GABE_LINE_13 = registerSoundEvent("gabe_line_13");
    public static final SoundEvent GABE_LINE_14 = registerSoundEvent("gabe_line_14");
    public static final SoundEvent GABE_LINE_15 = registerSoundEvent("gabe_line_15");
    public static final SoundEvent GABE_LINE_16 = registerSoundEvent("gabe_line_16");
    public static final SoundEvent GABE_LINE_17 = registerSoundEvent("gabe_line_17");
    public static final SoundEvent GABE_LINE_18 = registerSoundEvent("gabe_line_18");
    public static final SoundEvent GABE_LINE_19 = registerSoundEvent("gabe_line_19");
    public static final SoundEvent GABE_LINE_20 = registerSoundEvent("gabe_line_20");

    // -------------------------------------------------------------------------
    // Duckler sounds
    // -------------------------------------------------------------------------
    public static final SoundEvent DUCKLER_BREAK = registerSoundEvent("duckler_break");
    public static final SoundEvent DUCKLER_STEP  = registerSoundEvent("duckler_step");
    public static final SoundEvent DUCKLER_PLACE = registerSoundEvent("duckler_place");
    public static final SoundEvent DUCKLER_HIT   = registerSoundEvent("duckler_hit");
    public static final SoundEvent DUCKLER_FALL  = registerSoundEvent("duckler_fall");

    // -------------------------------------------------------------------------
    // Plush sounds
    // -------------------------------------------------------------------------
    public static final SoundEvent PLUSH_BREAK = registerSoundEvent("plush_break");
    public static final SoundEvent PLUSH_STEP  = registerSoundEvent("plush_step");
    public static final SoundEvent PLUSH_PLACE = registerSoundEvent("plush_place");
    public static final SoundEvent PLUSH_HIT   = registerSoundEvent("plush_hit");
    public static final SoundEvent PLUSH_FALL  = registerSoundEvent("plush_fall");

    // -------------------------------------------------------------------------
    // TV sounds
    // -------------------------------------------------------------------------
    public static final SoundEvent TV_BREAK = registerSoundEvent("tv_break");
    public static final SoundEvent TV_STEP  = registerSoundEvent("tv_step");
    public static final SoundEvent TV_PLACE = registerSoundEvent("tv_place");
    public static final SoundEvent TV_HIT   = registerSoundEvent("tv_hit");
    public static final SoundEvent TV_FALL  = registerSoundEvent("tv_fall");
    public static final SoundEvent TV_USE   = registerSoundEvent("tv_use");

    // -------------------------------------------------------------------------
    // Uzi plush voice lines (per-variant)
    // -------------------------------------------------------------------------
    // Default
    public static final SoundEvent UZI_PLUSH_DEFAULT_1  = registerSoundEvent("uzi_plush_default_1");
    public static final SoundEvent UZI_PLUSH_DEFAULT_2  = registerSoundEvent("uzi_plush_default_2");
    public static final SoundEvent UZI_PLUSH_DEFAULT_3  = registerSoundEvent("uzi_plush_default_3");
    public static final SoundEvent UZI_PLUSH_DEFAULT_4  = registerSoundEvent("uzi_plush_default_4");
    public static final SoundEvent UZI_PLUSH_DEFAULT_5  = registerSoundEvent("uzi_plush_default_5");
    public static final SoundEvent UZI_PLUSH_DEFAULT_6  = registerSoundEvent("uzi_plush_default_6");
    public static final SoundEvent UZI_PLUSH_DEFAULT_7  = registerSoundEvent("uzi_plush_default_7");
    public static final SoundEvent UZI_PLUSH_DEFAULT_8  = registerSoundEvent("uzi_plush_default_8");
    public static final SoundEvent UZI_PLUSH_DEFAULT_9  = registerSoundEvent("uzi_plush_default_9");
    public static final SoundEvent UZI_PLUSH_DEFAULT_10 = registerSoundEvent("uzi_plush_default_10");
    // Sadge
    public static final SoundEvent UZI_PLUSH_SADGE_1 = registerSoundEvent("uzi_plush_sadge_1");
    public static final SoundEvent UZI_PLUSH_SADGE_2 = registerSoundEvent("uzi_plush_sadge_2");
    public static final SoundEvent UZI_PLUSH_SADGE_3 = registerSoundEvent("uzi_plush_sadge_3");
    // Scared AF
    public static final SoundEvent UZI_PLUSH_SCAREDAF_1 = registerSoundEvent("uzi_plush_scaredaf_1");
    public static final SoundEvent UZI_PLUSH_SCAREDAF_2 = registerSoundEvent("uzi_plush_scaredaf_2");
    public static final SoundEvent UZI_PLUSH_SCAREDAF_3 = registerSoundEvent("uzi_plush_scaredaf_3");
    // Spooked
    public static final SoundEvent UZI_PLUSH_SPOOKED_1 = registerSoundEvent("uzi_plush_spooked_1");
    public static final SoundEvent UZI_PLUSH_SPOOKED_2 = registerSoundEvent("uzi_plush_spooked_2");
    public static final SoundEvent UZI_PLUSH_SPOOKED_3 = registerSoundEvent("uzi_plush_spooked_3");
    // Traumatized
    public static final SoundEvent UZI_PLUSH_TRAUMATIZED_1 = registerSoundEvent("uzi_plush_traumatized_1");
    public static final SoundEvent UZI_PLUSH_TRAUMATIZED_2 = registerSoundEvent("uzi_plush_traumatized_2");
    public static final SoundEvent UZI_PLUSH_TRAUMATIZED_3 = registerSoundEvent("uzi_plush_traumatized_3");
    // Unamused
    public static final SoundEvent UZI_PLUSH_UNAMUSED_1 = registerSoundEvent("uzi_plush_unamused_1");
    public static final SoundEvent UZI_PLUSH_UNAMUSED_2 = registerSoundEvent("uzi_plush_unamused_2");
    public static final SoundEvent UZI_PLUSH_UNAMUSED_3 = registerSoundEvent("uzi_plush_unamused_3");
    // Angy
    public static final SoundEvent UZI_PLUSH_ANGY_1 = registerSoundEvent("uzi_plush_angy_1");
    public static final SoundEvent UZI_PLUSH_ANGY_2 = registerSoundEvent("uzi_plush_angy_2");
    public static final SoundEvent UZI_PLUSH_ANGY_3 = registerSoundEvent("uzi_plush_angy_3");
    // Angy AF
    public static final SoundEvent UZI_PLUSH_ANGYAF_1 = registerSoundEvent("uzi_plush_angyaf_1");
    public static final SoundEvent UZI_PLUSH_ANGYAF_2 = registerSoundEvent("uzi_plush_angyaf_2");
    public static final SoundEvent UZI_PLUSH_ANGYAF_3 = registerSoundEvent("uzi_plush_angyaf_3");
    // Drunk
    public static final SoundEvent UZI_PLUSH_DRUNK_1 = registerSoundEvent("uzi_plush_drunk_1");
    public static final SoundEvent UZI_PLUSH_DRUNK_2 = registerSoundEvent("uzi_plush_drunk_2");
    public static final SoundEvent UZI_PLUSH_DRUNK_3 = registerSoundEvent("uzi_plush_drunk_3");
    public static final SoundEvent UZI_PLUSH_DRUNK_4 = registerSoundEvent("uzi_plush_drunk_4");
    public static final SoundEvent UZI_PLUSH_DRUNK_5 = registerSoundEvent("uzi_plush_drunk_5");
    // Happy
    public static final SoundEvent UZI_PLUSH_HAPPY_1 = registerSoundEvent("uzi_plush_happy_1");
    public static final SoundEvent UZI_PLUSH_HAPPY_2 = registerSoundEvent("uzi_plush_happy_2");
    public static final SoundEvent UZI_PLUSH_HAPPY_3 = registerSoundEvent("uzi_plush_happy_3");
    // Worried AF
    public static final SoundEvent UZI_PLUSH_WORRIEDAF_1 = registerSoundEvent("uzi_plush_worriedaf_1");
    public static final SoundEvent UZI_PLUSH_WORRIEDAF_2 = registerSoundEvent("uzi_plush_worriedaf_2");
    public static final SoundEvent UZI_PLUSH_WORRIEDAF_3 = registerSoundEvent("uzi_plush_worriedaf_3");
    // Worried
    public static final SoundEvent UZI_PLUSH_WORRIED_1 = registerSoundEvent("uzi_plush_worried_1");
    public static final SoundEvent UZI_PLUSH_WORRIED_2 = registerSoundEvent("uzi_plush_worried_2");
    public static final SoundEvent UZI_PLUSH_WORRIED_3 = registerSoundEvent("uzi_plush_worried_3");
    // Oh No
    public static final SoundEvent UZI_PLUSH_OHNO_1 = registerSoundEvent("uzi_plush_ohno_1");
    public static final SoundEvent UZI_PLUSH_OHNO_2 = registerSoundEvent("uzi_plush_ohno_2");
    public static final SoundEvent UZI_PLUSH_OHNO_3 = registerSoundEvent("uzi_plush_ohno_3");
    // Smirk
    public static final SoundEvent UZI_PLUSH_SMIRK_1 = registerSoundEvent("uzi_plush_smirk_1");
    public static final SoundEvent UZI_PLUSH_SMIRK_2 = registerSoundEvent("uzi_plush_smirk_2");
    public static final SoundEvent UZI_PLUSH_SMIRK_3 = registerSoundEvent("uzi_plush_smirk_3");

    // -------------------------------------------------------------------------
    // Misc sounds
    // -------------------------------------------------------------------------
    public static final SoundEvent STATIC      = registerSoundEvent("static");
    public static final SoundEvent DUCK_SOUND  = registerSoundEvent("duck_sound");
    public static final SoundEvent UZI_NOISE_7 = registerSoundEvent("uzi_noise_7");

    // -------------------------------------------------------------------------
    // Broadcast sounds (Collection Broadcast & Gifting)
    // -------------------------------------------------------------------------
    public static final SoundEvent COLLECTION_DISCOVER = registerSoundEvent("collection_discover");
    public static final SoundEvent GIFT_SEND           = registerSoundEvent("gift_send");
    public static final SoundEvent GIFT_DENY           = registerSoundEvent("gift_deny");

    // -------------------------------------------------------------------------
    // Doll voice lines (VA records in Russian, subtitles show English)
    // Add .ogg files to assets/wots/sounds/doll/ when VA delivers them.
    // -------------------------------------------------------------------------
    public static final SoundEvent DOLL_DEFAULT_1     = registerSoundEvent("doll_default_1");
    public static final SoundEvent DOLL_DEFAULT_2     = registerSoundEvent("doll_default_2");
    public static final SoundEvent DOLL_DEFAULT_3     = registerSoundEvent("doll_default_3");
    public static final SoundEvent DOLL_HAPPY_1       = registerSoundEvent("doll_happy_1");
    public static final SoundEvent DOLL_ANGY_1        = registerSoundEvent("doll_angy_1");
    public static final SoundEvent DOLL_UNAMUSED_1    = registerSoundEvent("doll_unamused_1");
    public static final SoundEvent DOLL_WORRIED_1     = registerSoundEvent("doll_worried_1");
    public static final SoundEvent DOLL_SADGE_1       = registerSoundEvent("doll_sadge_1");
    public static final SoundEvent DOLL_TRAUMATIZED_1 = registerSoundEvent("doll_traumatized_1");
    public static final SoundEvent DOLL_SPOOKED_1     = registerSoundEvent("doll_spooked_1");
    public static final SoundEvent DOLL_SCAREDAF_1    = registerSoundEvent("doll_scaredaf_1");
    public static final SoundEvent DOLL_DRUNK_1       = registerSoundEvent("doll_drunk_1");
    public static final SoundEvent DOLL_ANGYAF_1      = registerSoundEvent("doll_angyaf_1");
    public static final SoundEvent DOLL_WORRIEDAF_1   = registerSoundEvent("doll_worriedaf_1");
    public static final SoundEvent DOLL_OHNO_1        = registerSoundEvent("doll_ohno_1");
    public static final SoundEvent DOLL_SMIRK_1       = registerSoundEvent("doll_smirk_1");

    // =========================================================================
    // Block Sound Groups
    // SoundType(volume, pitch, breakSound, stepSound, placeSound, hitSound, fallSound)
    // =========================================================================

    public static final SoundType DUCKLER_SOUND_GROUP = new SoundType(
            1.0f, 1.0f,
            DUCKLER_BREAK,
            DUCKLER_STEP,
            DUCKLER_PLACE,
            DUCKLER_HIT,
            DUCKLER_FALL
    );

    public static final SoundType PLUSH_SOUND_GROUP = new SoundType(
            1.0f, 1.0f,
            PLUSH_BREAK,
            PLUSH_STEP,
            PLUSH_PLACE,
            PLUSH_HIT,
            PLUSH_FALL
    );
    public static final SoundType CYN_SOUND_GROUP = new SoundType(
            1.0f, 1.0f,
            CYN_NOISE_11,
            PLUSH_STEP,
            CYN_NOISE_9,
            PLUSH_HIT,
            PLUSH_FALL
    );

    public static final SoundType TV_SOUND_GROUP = new SoundType(
            1.0f, 1.0f,
            TV_BREAK,
            TV_STEP,
            TV_PLACE,
            TV_HIT,
            TV_FALL
    );

    // =========================================================================
    // Helpers
    // =========================================================================

    private static SoundEvent registerSoundEvent(String name) {
        Identifier id = Identifier.fromNamespaceAndPath(Wots.MOD_ID, name);
        return Registry.register(BuiltInRegistries.SOUND_EVENT, id, SoundEvent.createVariableRangeEvent(id));
    }

    public static void registerModSounds() {
        // Loading this class is enough — all statics self-register on access.
        // Call this from your ModInitializer to ensure early registration.
    }
}