package io.github.mortuusars.scholar;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.Items;

import java.util.function.Consumer;

public class ScholarClient {
    public static void init() {
        registerItemModelProperties();
    }

    private static void registerItemModelProperties() {
        ItemProperties.register(Items.WRITABLE_BOOK, Scholar.resource("book_golden"),
              (stack, level, entity, seed) -> stack.getTag() != null && stack.getTag().getBoolean(Scholar.NBT.BOOK_GOLDEN) ? 1 : 0);
        ItemProperties.register(Items.WRITTEN_BOOK, Scholar.resource("book_golden"),
              (stack, level, entity, seed) -> stack.getTag() != null && stack.getTag().getBoolean(Scholar.NBT.BOOK_GOLDEN) ? 1 : 0);
    }

    public static class KeyMappings {
        public static KeyMapping toggleBookTools = new KeyMapping("key.scholar.toggle_book_tools",
              InputConstants.KEY_F1, "key.scholar.categories.scholar");
        public static KeyMapping importBook = new KeyMapping("key.scholar.import_book",
              InputConstants.KEY_F6, "key.scholar.categories.scholar");
        public static KeyMapping exportBook = new KeyMapping("key.scholar.export_book",
              InputConstants.KEY_F7, "key.scholar.categories.scholar");
        public static KeyMapping insertEmptyPageLeft = PlatformHelperClient.createInsertEmptyPageLeftKeyMapping();
        public static KeyMapping removePageLeft = PlatformHelperClient.createRemovePageLeftKeyMapping();
        public static KeyMapping insertEmptyPageRight = PlatformHelperClient.createInsertEmptyPageRightKeyMapping();
        public static KeyMapping removePageRight = PlatformHelperClient.createRemovePageRightKeyMapping();

        public static void register(Consumer<KeyMapping> registerFunction) {
            registerFunction.accept(toggleBookTools);
            registerFunction.accept(importBook);
            registerFunction.accept(exportBook);
            registerFunction.accept(insertEmptyPageLeft);
            registerFunction.accept(removePageLeft);
            registerFunction.accept(insertEmptyPageRight);
            registerFunction.accept(removePageRight);
        }

        public static MutableComponent componentForTooltip(KeyMapping keyMapping) {
            return componentForTooltip(keyMapping, ChatFormatting.DARK_GRAY);
        }

        public static MutableComponent componentForTooltip(KeyMapping keyMapping, ChatFormatting formatting) {
            return Component.literal(" " + keyMapping.getTranslatedKeyMessage().getString()).withStyle(formatting);
        }
    }
}
