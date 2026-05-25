package io.github.mortuusars.scholar.book;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.DyedItemColor;

public class BookColor {
    public static final int DEFAULT = 0xFF99452E;

    public static int of(ItemStack stack) {
        return DyedItemColor.getOrDefault(stack, getDefaultColor(stack));
    }

    public static int getDefaultColor(ItemStack stack) {
        if (stack.is(Items.BOOK)) {
            return 0xFF9C763E;
        }
        return DEFAULT;
    }

    public static void set(ItemStack stack, int color) {
        if (color == DEFAULT) {
            stack.remove(DataComponents.DYED_COLOR);
        } else {
            stack.set(DataComponents.DYED_COLOR, new DyedItemColor(color));
        }
    }
}