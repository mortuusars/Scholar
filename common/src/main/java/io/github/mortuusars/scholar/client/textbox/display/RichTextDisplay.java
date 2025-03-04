package io.github.mortuusars.scholar.client.textbox.display;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.mortuusars.scholar.client.textbox.AmazingTextBox;
import io.github.mortuusars.scholar.client.textbox.internals.Char;
import io.github.mortuusars.scholar.client.textbox.internals.RichLine;
import io.github.mortuusars.scholar.client.textbox.internals.RichText;
import io.github.mortuusars.scholar.client.util.HorizontalAlignment;
import io.github.mortuusars.scholar.client.util.Pos2i;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.client.renderer.RenderType;

import java.util.*;

public class RichTextDisplay {
    protected AmazingTextBox textBox;
    protected RichText richText;
    protected Font font;

    boolean needsRebuilding;

    protected int width;
    protected int height;
    protected Pos2i cursor = new Pos2i(0, 0);
    protected ArrayList<RichLine> lines = new ArrayList<>();
    protected ArrayList<Rect2i> selection = new ArrayList<>();

    public RichTextDisplay(AmazingTextBox textBox, RichText richText) {
        this.textBox = textBox;
        this.richText = richText;
        this.font = textBox.getFont();
    }

    public void scheduleRebuild() {
        needsRebuilding = true;
    }

    public void rebuild() {
        this.needsRebuilding = false;

        lines.clear();

        Font font = textBox.getFont();
        int width = textBox.getWidth();

        if (richText.isEmpty()) {
            cursor = new Pos2i(textBox.horizontalAlignment.align(width, font.width("_")), 0);
            lines.add(RichLine.EMPTY);
            selection.clear();
            return;
        }

        lines.addAll(splitLines(richText.getChars(), font, width));

        if (lines.isEmpty()) {
            lines.add(RichLine.EMPTY);
        }

        boolean isCursorAtTextEnd = richText.getCursorPos() == richText.length();
        RichLine lastLine = lines.get(lines.size() - 1);
        boolean endsOnNewLine = richText.getChars().get(lastLine.getLastCharIndex()).character() == '\n';

        if (isCursorAtTextEnd && endsOnNewLine) {
            cursor = new Pos2i(textBox.horizontalAlignment.align(this.width, font.width("_")), this.lines.size() * font.lineHeight);
        } else {
            int cursorLineIndex = getLineWithCharIndex(richText.getCursorPos());
            RichLine line = this.lines.get(cursorLineIndex);
            int lineCursorIndex = richText.getCursorPos() - line.getFirstCharIndex();
            int x = line.widthToIndex(font, lineCursorIndex);
            int y = cursorLineIndex * font.lineHeight;
            cursor = new Pos2i(x, y);
        }

        refreshSelectionAreas();
    }

    public void render(GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY, float partialTick) {
        if (needsRebuilding) {
            rebuild();
            needsRebuilding = false;
        }

        Font font = textBox.getFont();
        HorizontalAlignment alignment = textBox.horizontalAlignment;
        int color = textBox.getCurrentFontColor();

        for (int i = 0; i < lines.size(); i++) {
            RichLine line = lines.get(i);
            int lineWidth = font.width(line.getRenderedString());
            int lineX = x + alignment.align(textBox.getWidth(), lineWidth);
            int lineY = y + i * font.lineHeight;
            guiGraphics.drawString(font, line.getRenderedString(), lineX, lineY, color, false);
        }

        this.renderSelection(guiGraphics, x, y, alignment);
    }

    public void renderSelection(GuiGraphics guiGraphics, int x, int y, HorizontalAlignment alignment) {
        for (Rect2i rect : selection) {
            int x0 = x + alignment.align(textBox.getWidth(), rect.getWidth()) + rect.getX();
            int y0 = y + rect.getY();
            int x1 = x0 + rect.getWidth();
            int y1 = y0 + rect.getHeight();
            guiGraphics.fill(RenderType.guiTextHighlight(), x0, y0, x1, y1,
                    textBox.isFocused() ? textBox.selectionColor : textBox.selectionUnfocusedColor);
        }
    }

    public void renderCursor(GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY, float partialTick) {
        if (richText.isSelecting()) return;

        if (needsRebuilding) {
            rebuild();
            needsRebuilding = false;
        }

        if (richText.getCursorPos() == richText.length())
            guiGraphics.drawString(textBox.getFont(), "_", x + cursor.x, y + cursor.y, textBox.getCurrentFontColor(), false);
        else {
            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(0, 0, 50);
            RenderSystem.disableBlend();
            guiGraphics.fill(x + cursor.x, y + cursor.y - 1, x + cursor.x + 1, y + cursor.y + textBox.getFont().lineHeight, textBox.getCurrentFontColor());
            guiGraphics.pose().popPose();
        }
    }

    public List<RichLine> splitLines(List<Char> chars, Font font, int width) {
        ArrayList<RichLine> lines = new ArrayList<>();

        int firstCharIndex = 0;
        int lineCharsCount = 0;
        int lineWidth = 0;

        for (Char character : chars) {
            boolean isNewLine = character.character() == '\n';

            if (isNewLine) {
                lineCharsCount++;
                lines.add(new RichLine(chars.subList(firstCharIndex, firstCharIndex + lineCharsCount), firstCharIndex));
                firstCharIndex += lineCharsCount;
                lineCharsCount = 0;
                lineWidth = 0;
                continue;
            }

            int charWidth = character.getWidth(font);
            boolean exceedsWidth = lineWidth + charWidth >= width;

            if (exceedsWidth) {
                lines.add(new RichLine(chars.subList(firstCharIndex, firstCharIndex + lineCharsCount), firstCharIndex));
                firstCharIndex += lineCharsCount;
                lineCharsCount = 0;
                lineWidth = 0;
            }

            lineCharsCount++;
            lineWidth += charWidth;
        }

        if (lineCharsCount > 0) {
            lines.add(new RichLine(chars.subList(firstCharIndex, firstCharIndex + lineCharsCount), firstCharIndex));
        }

//        Scholar.LOGGER.info("------");
//
//        for (RichLine line : lines) {
//            Scholar.LOGGER.info("{}< First:{}, Last:{}", line.getRenderedString(), line.getFirstCharIndex(), line.getLastCharIndex());
//        }

        return lines;
    }

    public int getLineWithCharIndex(int index) {
        for (int i = 0; i < lines.size(); i++) {
            RichLine line = lines.get(i);
            if (line.getFirstCharIndex() <= index && index <= line.getLastCharIndex()) {
                return i;
            }
        }
        return Math.max(0, lines.size() - 1);
    }

    protected void refreshSelectionAreas() {
        selection.clear();

        if (!richText.isSelecting()) return;

        int selectionStartIndex = richText.getSelectionStart();
        int selectionEndIndex = richText.getSelectionEnd();
        int lineAtStart = getLineWithCharIndex(selectionStartIndex);
        int lineAtEnd = getLineWithCharIndex(selectionEndIndex);

        for (int lineIndex = lineAtStart; lineIndex <= lineAtEnd; lineIndex++) {
            RichLine line = lines.get(lineIndex);
            int firstCharIndex = line.getFirstCharIndex();

            int firstLineChar = Math.max(selectionStartIndex - firstCharIndex, 0);
            int lastLineChar = Math.min(selectionEndIndex - 1 - firstCharIndex, line.getChars().size() - 1);

            int x = line.widthToIndex(font, firstLineChar);
            int y = lineIndex * font.lineHeight;
            int width = line.width(font, firstLineChar, lastLineChar);
            int height = font.lineHeight;

            selection.add(new Rect2i(x, y, width, height));
        }
    }

    public RichLine getLine(int line) {
        return lines.get(line);
    }

    public List<RichLine> getLines() {
        return lines;
    }
}