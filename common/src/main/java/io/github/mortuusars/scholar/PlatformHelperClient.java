package io.github.mortuusars.scholar;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.input.KeyEvent;

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
    public static boolean matchesWithModifiers(KeyMapping keyMapping, KeyEvent event) {
        throw new AssertionError();
    }
}
