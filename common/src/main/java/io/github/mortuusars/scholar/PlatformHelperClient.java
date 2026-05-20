package io.github.mortuusars.scholar;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.client.KeyMapping;

public class PlatformHelperClient {
    @ExpectPlatform
    public static KeyMapping createInsertEmptyPageLeftKeyMapping() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static KeyMapping createRemovePageLeftKeyMapping() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static KeyMapping createInsertEmptyPageRightKeyMapping() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static KeyMapping createRemovePageRightKeyMapping() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean matchesWithModifiers(KeyMapping keyMapping, int keyCode, int scancode, int modifiers) {
        throw new AssertionError();
    }
}
