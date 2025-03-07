package io.github.mortuusars.scholar.client.textbox.display;

import io.github.mortuusars.scholar.Scholar;
import io.github.mortuusars.scholar.client.textbox.internals.Char;
import io.github.mortuusars.scholar.client.textbox.internals.Text;
import io.github.mortuusars.scholar.client.util.HorizontalAlignment;
import io.github.mortuusars.scholar.client.util.Pos2i;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.Rect2i;

import java.util.*;

public class TextDisplayCache {
    protected Text text;

    protected Font font;
    protected int width = 0;
    protected int height = 0;
    protected HorizontalAlignment alignment;

    protected Pos2i cursor = new Pos2i(0, 0);
    protected ArrayList<Line> lines = new ArrayList<>();
    protected ArrayList<Rect2i> selection = new ArrayList<>();

    boolean shouldUpdate = true;

    public TextDisplayCache(Text text) {
        this.text = text;
    }

    // --

    public boolean shouldUpdate() {
        return shouldUpdate;
    }

    public void scheduleUpdate() {
        shouldUpdate = true;
    }

    public void updateIfNeeded(Font font, int width, int height, HorizontalAlignment alignment) {
        if (shouldUpdate) {
            update(font, width, height, alignment);
        }
    }

    public void update(Font font, int width, int height, HorizontalAlignment alignment) {
        this.font = font;
        this.width = width;
        this.height = height;
        this.alignment = alignment;

        updateLines();
        updateCursor();
        updateSelectionAreas();

        this.shouldUpdate = false;
    }

    protected void updateLines() {
        lines.clear();
        lines.addAll(splitLines(text.getChars(), font, width, alignment));
    }

    protected void updateCursor() {
        boolean isCursorAtTextEnd = text.isCursorAtEnd();
        Line lastLine = lines.get(lines.size() - 1);

        if (isCursorAtTextEnd && lastLine.isEmpty()) {
            cursor = new Pos2i(lastLine.x - font.width("_") / 2, lastLine.y);
            return;
        }

        boolean endsOnNewLine;
        try {
            endsOnNewLine = text.getChars().get(lastLine.lastCharIndex()).character() == '\n';
        } catch (Exception e) {
            Scholar.LOGGER.error("ends on new line: ", e);
            return;
        }

        if (isCursorAtTextEnd && endsOnNewLine) {
            cursor = new Pos2i(lastLine.x + lastLine.width / 2  - font.width("_") / 2, lastLine.y + font.lineHeight);
            return;
        }

        int cursorLineIndex = findLineIndexByCharIndex(text.getCursorPos());
        Line line = this.lines.get(cursorLineIndex);
        int lineCursorIndex = text.getCursorPos() - line.firstCharIndex();
        int x = line.x + line.widthToIndex(font, lineCursorIndex);
        int y = line.y;
        cursor = new Pos2i(x, y);
    }

    protected void updateSelectionAreas() {
        selection.clear();

        if (!text.isSelecting()) return;

        int selectionStartIndex = text.getSelectionStart();
        int selectionEndIndex = text.getSelectionEnd();
        int lineAtStart = findLineIndexByCharIndex(selectionStartIndex);
        int lineAtEnd = findLineIndexByCharIndex(selectionEndIndex);

        for (int lineIndex = lineAtStart; lineIndex <= lineAtEnd; lineIndex++) {
            Line line = lines.get(lineIndex);
            int firstCharIndex = line.firstCharIndex();

            int firstLineChar = Math.max(selectionStartIndex - firstCharIndex, 0);
            int lastLineChar = Math.min(selectionEndIndex - 1 - firstCharIndex, line.chars().size() - 1);
            if (lastLineChar > firstLineChar && line.chars().get(line.chars().size() - 1).character() == '\n') {
                lastLineChar -= 1;
            }

            int x = line.x + line.widthToIndex(font, firstLineChar);

//            if (line.renderedString.isEmpty()) {
//
//            }

            int y = line.y;
            int width = line.width(font, firstLineChar, lastLineChar);
            int height = font.lineHeight;

            selection.add(new Rect2i(x, y, width, height));
        }
    }

    // --

    public List<Line> getLines() {
        return lines;
    }

    public Line getLine(int line) {
        return lines.get(line);
    }

    public ArrayList<Rect2i> getSelection() {
        return selection;
    }

    public Pos2i getCursor() {
        return cursor;
    }

    public int findLineIndexByCharIndex(int index) {
        for (int i = 0; i < lines.size(); i++) {
            Line line = lines.get(i);
            if (line.firstCharIndex() <= index && index <= line.lastCharIndex()) {
                return i;
            }
        }
        return Math.max(0, lines.size() - 1);
    }

    public int getCharIndexAtPosition(Font font, int x, int y) {
        int lineIndex = y / font.lineHeight;

        if (lineIndex < 0) {
            return 0;
        } else if (lineIndex >= lines.size()) {
            return text.length();
        }

        Line line = getLine(lineIndex);
        int lineWidth = line.width(font, 0, line.chars().size() - 1);
        return line.indexAtWidth(font, x);
    }

    // --

    public List<Line> splitLines(List<Char> chars, Font font, int width, HorizontalAlignment alignment) {
        ArrayList<Line> lines = new ArrayList<>();

        if (chars.isEmpty()) {
            int x = alignment.align(width, 0);
            int y = 0;
            lines.add(new Line(font, Collections.emptyList(), 0, 0, "", 0, x, y));
            return lines;
        }

        int i = 0;
        while (i < chars.size()) {
            int firstCharIndex = i;
            int lineWidth = 0;

            while (i < chars.size()) {
                Char character = chars.get(i);
                int charWidth = character.getWidth(font);

                if (lineWidth + charWidth > width) {
                    break;
                }

                lineWidth += charWidth;
                i++;

                if (character.character() == '\n') {
                    break;
                }
            }

            List<Char> lineChars = chars.subList(firstCharIndex, i);
            String renderedString = Text.toString(lineChars).replace("\n", "");
            int x = alignment.align(width, lineWidth);
            int y = lines.size() * font.lineHeight;

            lines.add(new Line(font, lineChars, firstCharIndex, i - 1, renderedString, lineWidth, x, y));
        }

        Scholar.LOGGER.info("------");

        for (Line line : lines) {
            Scholar.LOGGER.info("{}< First:{}, Last:{}", text.toStringWithoutFormatting(line.chars), line.firstCharIndex(), line.lastCharIndex());
        }

        return lines;
    }

//    public List<Line> splitLines(List<Char> chars, Font font, int width, HorizontalAlignment alignment) {
//        ArrayList<Line> lines = new ArrayList<>();
//
//        if (chars.isEmpty()) {
//            int x = alignment.align(width, 0);
//            int y = 0;
//            lines.add(new Line(font, Collections.emptyList(), 0, 0, "", 0, x, y));
//            return lines;
//        }
//
//        int firstCharIndex = 0;
//        int lineCharsCount = 0;
//        int lineWidth = 0;
//
//        for (Char character : chars) {
//            boolean isNewLine = character.character() == '\n';
//
//            if (isNewLine) {
//                lineCharsCount++;
//
//                List<Char> lineChars = chars.subList(firstCharIndex, firstCharIndex + lineCharsCount);
//                int lastCharIndex = firstCharIndex + Math.max(0, lineChars.size() - 1);
//                String renderedString = Text.toString(lineChars).replace("\n", "");
//                int x = alignment.align(width, lineWidth);
//                int y = lines.size() * font.lineHeight;
//                lines.add(new Line(font, lineChars, firstCharIndex, lastCharIndex, renderedString, lineWidth, x, y));
//
//                firstCharIndex += lineCharsCount;
//                lineCharsCount = 0;
//                lineWidth = 0;
//                continue;
//            }
//
//            int charWidth = character.getWidth(font);
//            boolean exceedsWidth = lineWidth + charWidth >= width;
//
//            if (exceedsWidth) {
//                List<Char> lineChars = chars.subList(firstCharIndex, firstCharIndex + lineCharsCount);
//                int lastCharIndex = firstCharIndex + Math.max(0, lineChars.size() - 1);
//                String renderedString = Text.toString(lineChars).replace("\n", "");
//                int x = alignment.align(width, lineWidth);
//                int y = lines.size() * font.lineHeight;
//                lines.add(new Line(font, lineChars, firstCharIndex, lastCharIndex, renderedString, lineWidth, x, y));
//
//                firstCharIndex += lineCharsCount;
//                lineCharsCount = 0;
//                lineWidth = 0;
//            }
//
//            lineCharsCount++;
//            lineWidth += charWidth;
//        }
//
//        if (lineCharsCount > 0) {
//            List<Char> lineChars = chars.subList(firstCharIndex, firstCharIndex + lineCharsCount);
//            int lastCharIndex = firstCharIndex + Math.max(0, lineChars.size() - 1);
//            String renderedString = Text.toString(lineChars).replace("\n", "");
//            int x = alignment.align(width, lineWidth);
//            int y = lines.size() * font.lineHeight;
//            lines.add(new Line(font, lineChars, firstCharIndex, lastCharIndex, renderedString, lineWidth, x, y));
//        }
//
//        Scholar.LOGGER.info("------");
//
//        for (Line line : lines) {
//            Scholar.LOGGER.info("{}< First:{}, Last:{}", text.toStringWithoutFormatting(line.chars), line.firstCharIndex(), line.lastCharIndex());
//        }
//
//        return lines;
//    }
}