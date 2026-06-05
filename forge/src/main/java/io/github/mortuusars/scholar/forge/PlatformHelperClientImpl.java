package io.github.mortuusars.scholar.forge;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;

public class PlatformHelperClientImpl {
    public static KeyMapping createInsertEmptyPageLeftKeyMapping() {
        return new KeyMapping("key.scholar.insert_empty_page_left",
              KeyConflictContext.GUI,
              KeyModifier.SHIFT,
              InputConstants.Type.KEYSYM.getOrCreate(InputConstants.KEY_INSERT),
              "key.scholar.categories.scholar");
    }

    public static KeyMapping createRemovePageLeftKeyMapping() {
        return new KeyMapping("key.scholar.remove_page_left",
              KeyConflictContext.GUI,
              KeyModifier.SHIFT,
              InputConstants.Type.KEYSYM.getOrCreate(InputConstants.KEY_DELETE),
              "key.scholar.categories.scholar");
    }

    public static KeyMapping createInsertEmptyPageRightKeyMapping() {
        return new KeyMapping("key.scholar.insert_empty_page_right",
              KeyConflictContext.GUI,
              KeyModifier.ALT,
              InputConstants.Type.KEYSYM.getOrCreate(InputConstants.KEY_INSERT),
              "key.scholar.categories.scholar");
    }

    public static KeyMapping createRemovePageRightKeyMapping() {
        return new KeyMapping("key.scholar.remove_page_right",
              KeyConflictContext.GUI,
              KeyModifier.ALT,
              InputConstants.Type.KEYSYM.getOrCreate(InputConstants.KEY_DELETE),
              "key.scholar.categories.scholar");
    }

    public static boolean matchesWithModifiers(KeyMapping keyMapping, int keyCode, int scancode, int modifiers) {
        return keyMapping.matches(keyCode, scancode) && keyMapping.isConflictContextAndModifierActive();
    }
}
