package io.github.mortuusars.scholar.client.gui.widget.textbox.display;

import io.github.mortuusars.scholar.client.gui.widget.textbox.text.FormattedString;
import io.github.mortuusars.scholar.client.gui.widget.textbox.text.Char;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;

public class Line {
    protected Font font;
    protected FormattedString string;
    protected int firstCharIndex;
    protected int lastCharIndex;
    protected String renderedString;
    protected int width, x, y;

    public Line(Font font, FormattedString string, int firstCharIndex, int lastCharIndex, String renderedString, int width, int x, int y) {
        this.font = font;
        this.string = string;
        this.firstCharIndex = firstCharIndex;
        this.lastCharIndex = lastCharIndex;
        this.renderedString = renderedString;
        this.width = width;
        this.x = x;
        this.y = y;
    }

    public static Line empty() {
        return new Line(Minecraft.getInstance().font, new FormattedString(), 0, 0, "", 0, 0, 0);
    }

    public Font font() {
        return font;
    }

    public FormattedString getString() {
        return string;
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

    // --

    public boolean isEmpty() {
        return getString().isEmpty();
    }

    public boolean endsWithNewLine() {
        return !getString().isEmpty() && getString().get(getString().size() - 1).codepoint() == '\n';
    }

    public int widthToIndex(Font font, int index) {
        int endIndex = Math.min(index, string.size());
        return font.width(string.subString(0, endIndex));
    }

    public int indexAtWidth(Font font, int width) {
        if (width <= 0 || string.isEmpty()) return firstCharIndex();

        int currentWidth = 0;

        for (int i = 0; i < string.size(); i++) {
            Char character = string.get(i);
            int charWidth = character.getWidth(font, false);
            currentWidth += charWidth;

            if (currentWidth - charWidth / 2 > width) {
                return firstCharIndex() + i;
            }
        }

        return string.get(string.length() - 1).codepoint() == '\n' ? lastCharIndex() : lastCharIndex() + 1;
    }

    @Override
    public String toString() {
        return getString().toStringWithoutFormatting();
    }
}