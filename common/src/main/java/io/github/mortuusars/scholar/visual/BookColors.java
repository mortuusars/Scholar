package io.github.mortuusars.scholar.visual;

import com.google.common.collect.ImmutableMap;
import io.github.mortuusars.scholar.item.IColoredBook;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;

import java.util.*;

public class BookColors {
    public static final int REGULAR = 0x99452e;
    private static final Map<DyeColor, Integer> COLORS = Collections.unmodifiableMap(new LinkedHashMap<>() {{
        put(DyeColor.WHITE, 0xfffafeff);
        put(DyeColor.LIGHT_GRAY, 0xffccccc8);
        put(DyeColor.GRAY, 0xff898d8f);
        put(DyeColor.BLACK, 0xff424244);
        put(DyeColor.BROWN, 0xff875734);
        put(DyeColor.RED, 0xffcf443c);
        put(DyeColor.ORANGE, 0xfff38b35);
        put(DyeColor.YELLOW, 0xfff4d146);
        put(DyeColor.LIME, 0xff98d840);
        put(DyeColor.GREEN, 0xff61951e);
        put(DyeColor.CYAN, 0xff309e9d);
        put(DyeColor.LIGHT_BLUE, 0xff71ccea);
        put(DyeColor.BLUE, 0xff4b52b0);
        put(DyeColor.PURPLE, 0xff8b35b9);
        put(DyeColor.MAGENTA, 0xffc65fbd);
        put(DyeColor.PINK, 0xfff588a7);
    }});

    public static Map<DyeColor, Integer> getSortedColors() {
        return COLORS;
    }

    public static int fromStack(ItemStack stack) {
        if (stack.getItem() instanceof IColoredBook coloredBook)
            return COLORS.get(coloredBook.getColor());

        return REGULAR;
    }
}
