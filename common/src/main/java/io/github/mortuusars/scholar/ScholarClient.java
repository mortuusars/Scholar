package io.github.mortuusars.scholar;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;

import java.util.function.Consumer;

public class ScholarClient {
    public static void init() {
    }

    public static class KeyMappings {
        public static KeyMapping toggleBookTools = new KeyMapping("key.scholar.toggle_book_tools",
                InputConstants.KEY_F1, "key.scholar.categories.scholar");

        public static void register(Consumer<KeyMapping> registerFunction) {
            registerFunction.accept(toggleBookTools);
        }
    }
}
