package io.github.mortuusars.scholar.fabric;

import net.minecraft.client.KeyMapping;

public class PlatformHelperClientImpl {
    public static KeyMapping createInsertEmptyPageLeftKeyMapping() {
        return new KeyMapping("key.scholar.insert_empty_page_left", -1, "key.scholar.categories.scholar");
    }

    public static KeyMapping createRemovePageLeftKeyMapping() {
        return new KeyMapping("key.scholar.remove_page_left", -1, "key.scholar.categories.scholar");
    }

    public static KeyMapping createInsertEmptyPageRightKeyMapping() {
        return new KeyMapping("key.scholar.insert_empty_page_right", -1, "key.scholar.categories.scholar");
    }

    public static KeyMapping createRemovePageRightKeyMapping() {
        return new KeyMapping("key.scholar.remove_page_right", -1, "key.scholar.categories.scholar");
    }

    public static boolean matchesWithModifiers(KeyMapping keyMapping, int keyCode, int scancode, int modifiers) {
        return keyMapping.matches(keyCode, scancode);
    }
}
