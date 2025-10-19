package io.github.mortuusars.scholar.client.chiseled_bookshelf;

import com.mojang.serialization.Codec;
import io.github.mortuusars.scholar.client.util.HexColor;

import java.util.List;

public record BookshelfDefaultColors(List<Integer> colors) {
    public static final BookshelfDefaultColors VANILLA = new BookshelfDefaultColors(List.of(
          0x1AB8BC,
          0x78B140,
          0xA743E5,
          0xD44A6E,
          0xD57856,
          0x2F6BC6
    ));

    public static final Codec<BookshelfDefaultColors> CODEC =
          Codec.list(HexColor.CODEC).xmap(BookshelfDefaultColors::new, BookshelfDefaultColors::colors);

    public int getDefaultTintForSlot(int slot) {
        if (slot >= 0 && slot < colors.size()) {
            return colors.get(slot);
        }
        return -1;
    }
}
