package io.github.mortuusars.scholar;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.function.Consumer;

public class ScholarClient {
    public static void init() {
    }

    public static class KeyMappings {
        public static final KeyMapping.Category SCHOLAR_CATEGORY = KeyMapping.Category.register(
              Scholar.resource("key.scholar.categories.scholar"));

        public static KeyMapping toggleBookTools = new KeyMapping("key.scholar.toggle_book_tools",
                InputConstants.KEY_F1, SCHOLAR_CATEGORY);

        public static KeyMapping importBook = new KeyMapping("key.scholar.import_book",
                InputConstants.KEY_F6, SCHOLAR_CATEGORY);

        public static KeyMapping exportBook = new KeyMapping("key.scholar.export_book",
                InputConstants.KEY_F7, SCHOLAR_CATEGORY);

        public static void register(Consumer<KeyMapping> registerFunction) {
            registerFunction.accept(toggleBookTools);
            registerFunction.accept(importBook);
            registerFunction.accept(exportBook);
        }

        public static MutableComponent componentForTooltip(KeyMapping keyMapping) {
            return componentForTooltip(keyMapping, ChatFormatting.DARK_GRAY);
        }

        public static MutableComponent componentForTooltip(KeyMapping keyMapping, ChatFormatting formatting) {
            return Component.literal(" " + keyMapping.getTranslatedKeyMessage().getString()).withStyle(formatting);
        }
    }
}
