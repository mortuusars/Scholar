package io.github.mortuusars.scholar.book;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.*;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class BookColor {
    public static final BookColor DEFAULT = new BookColor(0x99452E, null);
    private final int color;
    @Nullable
    private final DyeColor dyeColor;

    public BookColor(int color, @Nullable DyeColor dyeColor) {
        this.color = color;
        this.dyeColor = dyeColor;
    }

    public int getValue() {
        return color;
    }

    public Optional<DyeColor> getDyeColor() {
        return Optional.ofNullable(dyeColor);
    }

    public ItemStack createWritableBook() {
        ItemStack itemStack = new ItemStack(Items.WRITABLE_BOOK);
        set(itemStack, this);
        return itemStack;
    }

    public static final List<BookColor> COLORS = Collections.synchronizedList(new ArrayList<>());

    static {
        COLORS.add(new BookColor(0xFAFEFF, DyeColor.WHITE));
        COLORS.add(new BookColor(0xCCCCC8, DyeColor.LIGHT_GRAY));
        COLORS.add(new BookColor(0x898D8F, DyeColor.GRAY));
        COLORS.add(new BookColor(0x424244, DyeColor.BLACK));
        COLORS.add(new BookColor(0x875734, DyeColor.BROWN));
        COLORS.add(new BookColor(0xCF443C, DyeColor.RED));
        COLORS.add(new BookColor(0xF38B35, DyeColor.ORANGE));
        COLORS.add(new BookColor(0xF4D146, DyeColor.YELLOW));
        COLORS.add(new BookColor(0x98D840, DyeColor.LIME));
        COLORS.add(new BookColor(0x61951E, DyeColor.GREEN));
        COLORS.add(new BookColor(0x309E9D, DyeColor.CYAN));
        COLORS.add(new BookColor(0x71CCEA, DyeColor.LIGHT_BLUE));
        COLORS.add(new BookColor(0x4B52B0, DyeColor.BLUE));
        COLORS.add(new BookColor(0x8B35B9, DyeColor.PURPLE));
        COLORS.add(new BookColor(0xC65FBD, DyeColor.MAGENTA));
        COLORS.add(new BookColor(0xF588A7, DyeColor.PINK));
    }

    public static int getTintColor(ItemStack stack, int tintIndex) {
        if (tintIndex == 0) {
            return of(stack);
        }

        return -1;
    }

    public static final String TAG_COLOR = "color";
    public static final String TAG_DISPLAY = "display";

    public static boolean hasCustomColor(ItemStack stack) {
        return stack.getItem() instanceof DyeableLeatherItem dyeableLeatherItem && dyeableLeatherItem.hasCustomColor(stack);
    }

    public static int of(ItemStack stack) {
        // Cannot use DyeableLeatherItem#getColor because it has different default color.
        CompoundTag compoundTag = stack.getTagElement(TAG_DISPLAY);
        if (compoundTag != null && compoundTag.contains(TAG_COLOR, Tag.TAG_ANY_NUMERIC)) {
            return compoundTag.getInt(TAG_COLOR);
        }
        return DEFAULT.getValue();
    }

    public static void clear(ItemStack stack) {
        if (stack.getItem() instanceof DyeableLeatherItem dyeableLeatherItem) {
            dyeableLeatherItem.clearColor(stack);
        }
    }

    public static void set(ItemStack stack, BookColor color) {
        set(stack, color.getValue());
    }

    public static void set(ItemStack stack, int color) {
        if (stack.getItem() instanceof DyeableLeatherItem dyeableLeatherItem) {
            dyeableLeatherItem.setColor(stack, color);
        }
    }
}
