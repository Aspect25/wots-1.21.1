package net.wots.client;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class ModKeybindings {

    public static KeyBinding PLUSHIE_HAT_SOUND_KEY;
    public static KeyBinding PLUSHIE_BACK_SOUND_KEY;

    public static void register() {
        PLUSHIE_HAT_SOUND_KEY = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.wots.plushie_hat_sound",   // translation key
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_Z,                // default — player can rebind
                "category.wots.plushies"
        ));

        PLUSHIE_BACK_SOUND_KEY = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.wots.plushie_back_sound",  // translation key
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_X,                // default — player can rebind
                "category.wots.plushies"
        ));
    }
}