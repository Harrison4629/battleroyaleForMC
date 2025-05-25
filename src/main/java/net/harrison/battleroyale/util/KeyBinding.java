package net.harrison.battleroyale.util;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.settings.KeyConflictContext;
import org.lwjgl.glfw.GLFW;

public class KeyBinding {
    public static final String KEY_CATEGORY = "key.categories.battleroyale";
    public static final String KEY_BIND_STOP_PHASING = "key.battleroyale.stop_phasing";


    public static final KeyMapping STOP_PHASING_KEY = new KeyMapping(KEY_BIND_STOP_PHASING, KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_SPACE, KEY_CATEGORY);

}
