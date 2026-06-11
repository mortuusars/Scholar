package io.github.mortuusars.scholar.client;

public record BookRenderState(boolean hasBook, boolean isGolden, int coverTintColor) {
    public static final BookRenderState NO_BOOK = new BookRenderState(false, false, 0x00000000);

    public BookRenderState(boolean isGolden, int coverTintColor) {
        this(true, isGolden, isGolden ? 0xFFFFFFFF : coverTintColor);
    }
}
