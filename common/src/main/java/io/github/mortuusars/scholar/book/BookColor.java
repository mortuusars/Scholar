package io.github.mortuusars.scholar.book;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.DyedItemColor;

public class BookColor {
    public static final int DEFAULT = 0xFF99452E;

    public static int of(ItemStack stack) {
        return DyedItemColor.getOrDefault(stack, DEFAULT);
    }

    public static int getItemTintColor(ItemStack stack, int tintIndex) {
        if (tintIndex == 1) {
            return of(stack);
        }

        return -1;
    }

    public static void set(ItemStack stack, int color) {
        stack.set(DataComponents.DYED_COLOR, new DyedItemColor(color, color != DEFAULT));
    }
}