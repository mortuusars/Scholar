package io.github.mortuusars.scholar.client.textbox.display;

import io.github.mortuusars.scholar.client.textbox.internals.Char;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class Line implements CharSequence {
    protected Font font;
    protected List<Char> chars;
    protected int firstCharIndex;
    protected int lastCharIndex;
    protected String renderedString;
    protected int width, x, y;

    public Line(Font font, List<Char> chars, int firstCharIndex, int lastCharIndex, String renderedString, int width, int x, int y) {
        this.font = font;
        this.chars = chars;
        this.firstCharIndex = firstCharIndex;
        this.lastCharIndex = lastCharIndex;
        this.renderedString = renderedString;
        this.width = width;
        this.x = x;
        this.y = y;
    }

    public static Line empty() {
        return new Line(Minecraft.getInstance().font, Collections.emptyList(), 0, 0, "", 0, 0, 0);
    }

    public Font font() {
        return font;
    }

    public List<Char> chars() {
        return chars;
    }

    public int firstCharIndex() {
        return firstCharIndex;
    }

    public int lastCharIndex() {
        return lastCharIndex;
    }

    public String renderedString() {
        return renderedString;
    }

    public int width() {
        return width;
    }

    public int x() {
        return x;
    }

    public int y() {
        return y;
    }

    // CharSequence

    @Override
    public int length() {
        return 0;
    }

    @Override
    public char charAt(int index) {
        return 0;
    }

    @NotNull
    @Override
    public CharSequence subSequence(int start, int end) {
        return new Line(font, chars.subList(start, end), start, end - 1, );
    }

    // --

    public boolean isEmpty() {
        return chars().isEmpty();
    }

    public int width(Font font, int first, int last) {
        int width = 0;
        for (int i = first; i <= last; i++) {
            width += chars.get(i).getWidth(font);
        }
        return width;
    }

    public int widthToIndex(Font font, int index) {
        int width = 0;
        int endIndex = Math.min(index, chars.size());
        for (int i = 0; i < endIndex; i++) {
            width += chars.get(i).getWidth(font);
        }
        return width;
    }

    public int indexAtWidth(Font font, int width) {
        if (width <= 0) return firstCharIndex();

        int currentWidth = 0;

        for (int i = 0; i < chars.size(); i++) {
            Char character = chars.get(i);
            int charWidth = character.getWidth(font);
            currentWidth += charWidth;

            if (currentWidth - charWidth / 2 > width) {
                return firstCharIndex() + i;
            }
        }

        return lastCharIndex() + 1;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        return
    }
}