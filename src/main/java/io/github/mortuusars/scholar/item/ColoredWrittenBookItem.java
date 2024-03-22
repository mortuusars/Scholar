package io.github.mortuusars.scholar.item;

import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.WrittenBookItem;

public class ColoredWrittenBookItem extends WrittenBookItem implements IColoredBook {
    private final DyeColor color;

    public ColoredWrittenBookItem(DyeColor color, Properties properties) {
        super(properties);
        this.color = color;
    }

    @Override
    public DyeColor getColor() {
        return color;
    }
}
