package io.github.mortuusars.scholar.neoforge;

import com.mojang.blaze3d.platform.InputConstants;
import io.github.mortuusars.scholar.ScholarClient;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.input.KeyEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.client.settings.KeyModifier;

public class PlatformHelperClientImpl {
    public static KeyMapping createInsertEmptyPageLeftKeyMapping() {
        return new KeyMapping("key.scholar.insert_empty_page_left",
              KeyConflictContext.GUI,
              KeyModifier.SHIFT,
              InputConstants.Type.KEYSYM.getOrCreate(InputConstants.KEY_INSERT),
              ScholarClient.KeyMappings.SCHOLAR_CATEGORY);
    }

    public static KeyMapping createRemovePageLeftKeyMapping() {
        return new KeyMapping("key.scholar.remove_page_left",
              KeyConflictContext.GUI,
              KeyModifier.SHIFT,
              InputConstants.Type.KEYSYM.getOrCreate(InputConstants.KEY_DELETE),
              ScholarClient.KeyMappings.SCHOLAR_CATEGORY);
    }

    public static KeyMapping createInsertEmptyPageRightKeyMapping() {
        return new KeyMapping("key.scholar.insert_empty_page_right",
              KeyConflictContext.GUI,
              KeyModifier.ALT,
              InputConstants.Type.KEYSYM.getOrCreate(InputConstants.KEY_INSERT),
              ScholarClient.KeyMappings.SCHOLAR_CATEGORY);
    }

    public static KeyMapping createRemovePageRightKeyMapping() {
        return new KeyMapping("key.scholar.remove_page_right",
              KeyConflictContext.GUI,
              KeyModifier.ALT,
              InputConstants.Type.KEYSYM.getOrCreate(InputConstants.KEY_DELETE),
              ScholarClient.KeyMappings.SCHOLAR_CATEGORY);
    }

    public static boolean matchesWithModifiers(KeyMapping keyMapping, KeyEvent event) {
        return keyMapping.matches(event) && keyMapping.isConflictContextAndModifierActive();
    }
}
