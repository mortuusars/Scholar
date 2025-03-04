package io.github.mortuusars.scholar.client.textbox.internals;

import net.minecraft.client.gui.Font;

import java.util.Collections;
import java.util.List;

public class RichLine {
    public static final RichLine EMPTY = new RichLine(Collections.emptyList(), 0);

    private final List<Char> chars;
    protected int firstCharIndex;
    protected int lastCharIndex;
    protected String string;

    public RichLine(List<Char> chars, int firstCharIndex) {
        this.chars = chars;
        this.firstCharIndex = firstCharIndex;
        this.lastCharIndex = firstCharIndex + Math.max(0, chars.size() - 1);
        this.string = RichText.toString(chars).replace("\n", "");
    }

    public List<Char> getChars() {
        return chars;
    }

    public int getFirstCharIndex() {
        return firstCharIndex;
    }

    public int getLastCharIndex() {
        return lastCharIndex;
    }

    public String getRenderedString() {
        return string;
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

//    public void render(GuiGraphics guiGraphics, int x, int y, int color) {
//        render(guiGraphics, x, y, color, false);
//    }
//
//    public void render(GuiGraphics guiGraphics, int x, int y, int color, boolean shadow) {
//        guiGraphics.drawString(font, string, x + getX(), y + getY(), color, false);
//    }
}
