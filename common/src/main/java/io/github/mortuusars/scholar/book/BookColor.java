package io.github.mortuusars.scholar.book;

import io.github.mortuusars.scholar.Scholar;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;

import java.util.Collections;
import java.util.Map;

public class BookColor {
    public static Map<ResourceLocation, Integer> ITEM_COLORS = Collections.emptyMap();
    public static final int DEFAULT = 0xFF99452E;

    public static int of(ItemStack stack, int fallback) {
        if (stack.getTag() != null && stack.getTag().getBoolean(Scholar.NBT.BOOK_GOLDEN)) {
            return 0xFFFFB83C;
        }

        if (stack.getItem() instanceof DyeableLeatherItem dyeable) {
            return dyeable.hasCustomColor(stack)
                  ? dyeable.getColor(stack)
                  : DEFAULT;
        }

        return getDefaultColor(stack, fallback);
    }

    public static int of(ItemStack stack) {
        return of(stack, DEFAULT);
    }

    public static int getDefaultColor(ItemStack stack, int fallback) {
        ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(stack.getItem());
        return ITEM_COLORS.getOrDefault(itemId, fallback);
    }

    public static int getDefaultColor(ItemStack stack) {
        return getDefaultColor(stack, DEFAULT);
    }

    public static int getItemTintColor(ItemStack stack, int tintIndex) {
        if (tintIndex == 1) {
            return of(stack);
        }

        return -1;
    }

    public static void set(ItemStack stack, int color) {
        if (stack.getItem() instanceof DyeableLeatherItem dyeable) {
            dyeable.setColor(stack, color);
        }
    }
}