package io.github.mortuusars.scholar.book;

import io.github.mortuusars.scholar.Scholar;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.DyedItemColor;

import java.util.Collections;
import java.util.Map;

public class BookColor {
    public static Map<Identifier, Integer> ITEM_COLORS = Collections.emptyMap();
    public static final int DEFAULT = 0xFF99452E;

    public static int of(ItemStack stack, int fallback) {
        if (stack.has(Scholar.DataComponents.BOOK_GOLDEN)) {
            return 0xFFFFB83C;
        }
        return DyedItemColor.getOrDefault(stack, getDefaultColor(stack, fallback));
    }

    public static int of(ItemStack stack) {
        return of(stack, DEFAULT);
    }

    public static int getDefaultColor(ItemStack stack, int fallback) {
        Identifier key = BuiltInRegistries.ITEM.getKey(stack.getItem());
        return ITEM_COLORS.getOrDefault(key, fallback);
    }

    public static int getDefaultColor(ItemStack stack) {
        return getDefaultColor(stack, DEFAULT);
    }

    public static void set(ItemStack stack, int color) {
        if (color == DEFAULT) {
            stack.remove(DataComponents.DYED_COLOR);
        } else {
            stack.set(DataComponents.DYED_COLOR, new DyedItemColor(color));
        }
    }
}