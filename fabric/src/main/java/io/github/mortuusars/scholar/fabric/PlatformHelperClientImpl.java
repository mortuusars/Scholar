package io.github.mortuusars.scholar.fabric;

import com.mojang.blaze3d.platform.InputConstants;
import io.github.mortuusars.scholar.ScholarClient;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.input.KeyEvent;

public class PlatformHelperClientImpl {
    public static KeyMapping createInsertEmptyPageLeftKeyMapping() {
        return new FixedKeyMapping("key.scholar.insert_empty_page_left", InputConstants.UNKNOWN.getValue(), ScholarClient.KeyMappings.SCHOLAR_CATEGORY);
    }

    public static KeyMapping createRemovePageLeftKeyMapping() {
        return new FixedKeyMapping("key.scholar.remove_page_left", InputConstants.UNKNOWN.getValue(), ScholarClient.KeyMappings.SCHOLAR_CATEGORY);
    }

    public static KeyMapping createInsertEmptyPageRightKeyMapping() {
        return new FixedKeyMapping("key.scholar.insert_empty_page_right", InputConstants.UNKNOWN.getValue(), ScholarClient.KeyMappings.SCHOLAR_CATEGORY);
    }

    public static KeyMapping createRemovePageRightKeyMapping() {
        return new FixedKeyMapping("key.scholar.remove_page_right", InputConstants.UNKNOWN.getValue(), ScholarClient.KeyMappings.SCHOLAR_CATEGORY);
    }

    public static boolean matchesWithModifiers(KeyMapping keyMapping, KeyEvent event) {
        return keyMapping.matches(event);
    }
}
