package net.wots.client;

import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import com.mojang.blaze3d.platform.InputConstants;
import org.lwjgl.glfw.GLFW;

public class ModKeybindings {

    public static final KeyMapping.Category WOTS_CATEGORY =
            KeyMapping.Category.register(Identifier.fromNamespaceAndPath("wots", "plushies"));

    public static KeyMapping PLUSHIE_HAT_SOUND_KEY;
    public static KeyMapping PLUSHIE_BACK_SOUND_KEY;

    public static void register() {
        PLUSHIE_HAT_SOUND_KEY = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.wots.plushie_hat_sound",   // translation key
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_Z,                // default -- player can rebind
                WOTS_CATEGORY
        ));

        PLUSHIE_BACK_SOUND_KEY = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.wots.plushie_back_sound",  // translation key
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_X,                // default -- player can rebind
                WOTS_CATEGORY
        ));
    }
}
