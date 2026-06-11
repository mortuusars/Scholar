package io.github.mortuusars.scholar.fabric;

import io.github.mortuusars.scholar.ScholarClient;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.input.KeyEvent;

public class PlatformHelperClientImpl {
    public static KeyMapping createInsertEmptyPageLeftKeyMapping() {
        return new KeyMapping("key.scholar.insert_empty_page_left", -1, ScholarClient.KeyMappings.SCHOLAR_CATEGORY);
    }

    public static KeyMapping createRemovePageLeftKeyMapping() {
        return new KeyMapping("key.scholar.remove_page_left", -1, ScholarClient.KeyMappings.SCHOLAR_CATEGORY);
    }

    public static KeyMapping createInsertEmptyPageRightKeyMapping() {
        return new KeyMapping("key.scholar.insert_empty_page_right", -1, ScholarClient.KeyMappings.SCHOLAR_CATEGORY);
    }

    public static KeyMapping createRemovePageRightKeyMapping() {
        return new KeyMapping("key.scholar.remove_page_right", -1, ScholarClient.KeyMappings.SCHOLAR_CATEGORY);
    }

    public static boolean matchesWithModifiers(KeyMapping keyMapping, KeyEvent event) {
        return keyMapping.matches(event);
    }
}
