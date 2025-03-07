package io.github.mortuusars.scholar.client.textbox.internals;

import com.mojang.blaze3d.platform.InputConstants;
import io.github.mortuusars.scholar.Scholar;
import net.minecraft.ChatFormatting;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.StringSplitter;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public class Text {
    protected Predicate<String> validator;

    protected ArrayList<Char> chars = new ArrayList<>();
    protected String text = "";
    protected int cursorPos;
    protected int selectionAnchor;

    public Text(Predicate<String> validator) {
        this.validator = validator;
    }

    public ArrayList<Char> getChars() {
        return chars;
    }

    public List<Char> getView(int start, int end) {
        return chars.subList(start, end);
    }

    public List<Char> getSelectionView() {
        if (!isSelecting()) return Collections.emptyList();
        return getView(getSelectionStart(), getSelectionEnd());
    }

    public int length() {
        return chars.size();
    }

    public boolean isEmpty() {
        return chars.isEmpty();
    }

    public String getText() {
        return text;
    }

    public int getCursorPos() {
        return cursorPos;
    }

    public boolean isCursorAtEnd() {
        return getCursorPos() == length();
    }

    public int getSelectionAnchor() {
        return selectionAnchor;
    }

    public int getSelectionStart() {
        return Math.min(getCursorPos(), getSelectionAnchor());
    }

    public int getSelectionEnd() {
        return Math.max(getCursorPos(), getSelectionAnchor());
    }

    public boolean isSelecting() {
        return getCursorPos() != getSelectionAnchor();
    }

    public void setCursorPos(int index) {
        setCursorPos(index, true);
    }

    public void setCursorPos(int index, boolean keepSelection) {
        cursorPos = clampIndex(index);
        resetSelectionIfNeeded(keepSelection);
    }

    public void setSelectionAnchor(int index) {
        selectionAnchor = clampIndex(index);
    }

    public void setSelectionRange(int start, int end) {
        setSelectionAnchor(start);
        setCursorPos(end, true);
    }

    public void setCursorToStart(boolean keepSelection) {
        setCursorPos(0, keepSelection);
    }

    public void setCursorToEnd(boolean keepSelection) {
        setCursorPos(chars.size(), keepSelection);
    }

    public void selectWord(int index) {
        setSelectionRange(
                StringSplitter.getWordPosition(toStringWithoutFormatting(chars), -1, index, true),
                StringSplitter.getWordPosition(toStringWithoutFormatting(chars), 1, index, true));
    }

    public void moveCursorBy(int direction, boolean keepSelection, CursorStep cursorStep) {
        switch (cursorStep) {
            case CHARACTER -> this.moveCursorByChars(direction, keepSelection);
            case WORD -> this.moveCursorByWords(direction, keepSelection);
        }
    }

    public void moveCursorByChars(int direction, boolean keepSelection) {
        if (!keepSelection && isSelecting()) {
            setCursorPos(direction < 0 ? getSelectionStart() : getSelectionEnd(), false);
        } else {
            setCursorPos(Util.offsetByCodepoints(toStringWithoutFormatting(chars), getCursorPos(), direction), keepSelection);
        }
    }

    public void moveCursorByWords(int direction, boolean keepSelection) {
        if (!keepSelection && isSelecting()) {
            setCursorPos(direction < 0 ? getSelectionStart() : getSelectionEnd(), false);
        } else {
            setCursorPos(StringSplitter.getWordPosition(toStringWithoutFormatting(chars), direction, getCursorPos(), true), keepSelection);
        }
    }

    protected void refreshCursor(boolean keepSelection) {
        setCursorPos(getCursorPos(), keepSelection);
    }

    protected void resetSelectionIfNeeded(boolean keepSelection) {
        if (!keepSelection) {
            setSelectionAnchor(getCursorPos());
        }
    }

    // --

    private boolean suppressNextCharTyped = false;

    public boolean charTyped(char character) {
        if (!suppressNextCharTyped && SharedConstants.isAllowedChatCharacter(character)) {
            insertTextAtCursor(Character.toString(character));
        }
        return true;
    }

    public boolean keyPressed(int key) {
        if (onKeyPressed(key)) {
            suppressNextCharTyped = true;
            return true;
        }
        suppressNextCharTyped = false;
        return false;
    }

    private boolean onKeyPressed(int key) {
        if (Screen.isSelectAll(key)) {
            selectAll();
            return true;
        }
        if (Screen.isCopy(key)) {
            copy();
            return true;
        }
        if (Screen.isPaste(key)) {
            paste();
            return true;
        }
        if (Screen.isCut(key)) {
            cut();
            return true;
        }
        CursorStep cursorStep = Screen.hasControlDown() ? CursorStep.WORD : CursorStep.CHARACTER;
        if (key == InputConstants.KEY_BACKSPACE) {
            removeFromCursor(-1, cursorStep);
            return true;
        }
        if (key == InputConstants.KEY_DELETE) {
            removeFromCursor(1, cursorStep);
            return true;
        } else {
            if (key == InputConstants.KEY_LEFT) {
                moveCursorBy(-1, Screen.hasShiftDown(), cursorStep);
                return true;
            }
            if (key == InputConstants.KEY_RIGHT) {
                moveCursorBy(1, Screen.hasShiftDown(), cursorStep);
                return true;
            }
            if (key == InputConstants.KEY_HOME) {
                setCursorToStart(Screen.hasShiftDown());
                return true;
            }
            if (key == InputConstants.KEY_END) {
                setCursorToEnd(Screen.hasShiftDown());
                return true;
            }
            if (key == InputConstants.KEY_RETURN || key == InputConstants.KEY_NUMPADENTER) {
                insertTextAtCursor("\n");
                return true;
            }
        }

        if (Screen.hasAltDown()) {
            @Nullable Formatting formatting = switch (key) {
                case InputConstants.KEY_L -> Formatting.Format.BOLD;
                case InputConstants.KEY_O -> Formatting.Format.ITALIC;
                case InputConstants.KEY_N -> Formatting.Format.UNDERLINE;
                case InputConstants.KEY_M -> Formatting.Format.STRIKETHROUGH;
                case InputConstants.KEY_K -> Formatting.Format.OBFUSCATED;
                case InputConstants.KEY_0 -> Formatting.Color.BLACK;
                case InputConstants.KEY_1 -> Formatting.Color.DARK_BLUE;
                case InputConstants.KEY_2 -> Formatting.Color.DARK_GREEN;
                case InputConstants.KEY_3 -> Formatting.Color.DARK_AQUA;
                case InputConstants.KEY_4 -> Formatting.Color.DARK_RED;
                case InputConstants.KEY_5 -> Formatting.Color.DARK_PURPLE;
                case InputConstants.KEY_6 -> Formatting.Color.GOLD;
                case InputConstants.KEY_7 -> Formatting.Color.GRAY;
                case InputConstants.KEY_8 -> Formatting.Color.DARK_GRAY;
                case InputConstants.KEY_9 -> Formatting.Color.BLUE;
                case InputConstants.KEY_A -> Formatting.Color.GREEN;
                case InputConstants.KEY_B -> Formatting.Color.AQUA;
                case InputConstants.KEY_C -> Formatting.Color.RED;
                case InputConstants.KEY_D -> Formatting.Color.LIGHT_PURPLE;
                case InputConstants.KEY_E -> Formatting.Color.YELLOW;
                case InputConstants.KEY_F -> Formatting.Color.WHITE;
                case InputConstants.KEY_R -> Formatting.RESET;
                default -> null;
            };

            if (formatting != null) {
                if (isSelecting()) {
                    getSelectionView().replaceAll(c -> c.applyFormatting(formatting));
                    text = toString(chars);
                } else if (formatting == Formatting.RESET) {
                    getView(0, chars.size()).replaceAll(c -> c.applyFormatting(Formatting.RESET));
                    text = toString(chars);
                }
                return true;
            }
        }

        return false;
    }

    public void cut() {
        copy();
        removeSelectedText();
    }

    public void paste() {
        try {
            String text = Minecraft.getInstance().keyboardHandler.getClipboard();
            insertTextAtCursor(Objects.requireNonNull(ChatFormatting.stripFormatting(text)).replaceAll("\\r", ""));
        } catch (Exception e) {
            Scholar.LOGGER.error("Text Paste error: ", e);
        }
    }

    public void copy() {
        Minecraft.getInstance().keyboardHandler.setClipboard(getSelectedString());
    }

    public void selectAll() {
        setSelectionAnchor(0);
        setCursorPos(chars.size(), true);
    }

    // --

    public void insertTextAtCursor(String text) {
        if (isSelecting()) {
            removeSelectedText();
        }

        ArrayList<Char> newChars = new ArrayList<>(chars);
        newChars.addAll(getCursorPos(), text.chars().mapToObj(Char::new).toList());

        String string = toString(newChars);
        if (validator.test(string)) {
            this.chars = newChars;
            this.text = string;
            setCursorPos(getCursorPos() + text.length(), false);
        }
    }

    public void removeFromCursor(int direction, CursorStep step) {
        switch (step) {
            case CHARACTER: {
                this.removeCharsFromCursor(direction);
                break;
            }
            case WORD: {
                this.removeWordsFromCursor(direction);
            }
        }
    }

    public void removeWordsFromCursor(int direction) {
        int charsCount = StringSplitter.getWordPosition(toStringWithoutFormatting(chars), direction, this.cursorPos, true);
        this.removeCharsFromCursor(charsCount - this.cursorPos);
    }

    public void removeCharsFromCursor(int direction) {
        String string = toStringWithoutFormatting(chars);
        if (!string.isEmpty()) {
            if (isSelecting()) {
                removeSelectedText();
            } else {
                int cursor = getCursorPos();
                int removePos = Util.offsetByCodepoints(string, cursor, direction);
                int start = Math.min(removePos, cursor);
                int end = Math.max(removePos, cursor);

                chars.subList(start, end).clear();
                text = toString(chars);

                setCursorPos(start, false);
            }
        }
    }

    public void removeSelectedText() {
        if (!isSelecting()) return;

        int start = getSelectionStart();
        int end = getSelectionEnd();
        chars.subList(start, end).clear();
        text = toString(chars);

        setCursorPos(start, false);
    }

    public String toStringWithoutFormatting(List<Char> chars) {
        if (chars.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        for (Char character : chars) {
            sb.append(character.character());
        }
        return sb.toString();
    }

    public String getSelectedString() {
        if (!isSelecting()) return "";
        return text.substring(getSelectionStart(), getSelectionEnd());
    }

    public static String toString(List<Char> chars) {
        if (chars.isEmpty()) return "";

        StringBuilder sb = new StringBuilder();

        Char previousChar = Char.EMPTY;

        for (Char character : chars) {
            if (!character.formattingMatches(previousChar)) {
                if (previousChar.hasFormatting()) {
                    sb.append(Formatting.SECTION_SIGN).append(Formatting.RESET.getChar());
                }
                character.appendFormatting(sb);
            }

            sb.append(character.character());
            previousChar = character;
        }

        Char lastChar = chars.get(chars.size() - 1);
        if (lastChar.hasFormatting()) {
            sb.append(Formatting.SECTION_SIGN).append(Formatting.RESET.getChar());
        }

        return sb.toString();
    }

    // --

    protected int clampIndex(int index) {
        return Mth.clamp(index, 0, chars.size());
    }

    public enum CursorStep {
        CHARACTER,
        WORD;
    }
}
