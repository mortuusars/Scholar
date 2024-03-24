package io.github.mortuusars.scholar.item;

import io.github.mortuusars.scholar.Scholar;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.WritableBookItem;

public class ColoredWritableBookItem extends WritableBookItem implements IColoredBook {
    private final DyeColor color;

    public ColoredWritableBookItem(DyeColor color, Properties properties) {
        super(properties);
        this.color = color;
    }

    @Override
    public DyeColor getColor() {
        return color;
    }

    public ItemStack createWrittenBook(ItemStack writableBook) {
        return new ItemStack(Scholar.Items.COLORED_WRITTEN_BOOKS.get(getColor()).get());
    }
}
