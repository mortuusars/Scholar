package io.github.mortuusars.scholar.util;

import com.mojang.blaze3d.systems.RenderSystem;

public class RenderUtil {
    public static void withColorMultiplied(int color, Runnable code) {
        int red = color >> 16 & 0xFF;
        int green = color >> 8 & 0xFF;
        int blue = color & 0xFF;

        RenderSystem.setShaderColor(red / 255f, green / 255f, blue / 255f, 1.0f);
        code.run();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
    }
}
