package io.github.mortuusars.scholar.client.textbox.internals;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;

public class FormattedString extends ArrayList<Char> implements CharSequence {
    public FormattedString() {
        super();
    }

    public FormattedString(@NotNull Collection<? extends Char> chars) {
        super(chars);
    }

    public static FormattedString parse(String string) {
        ArrayList<Char> chars = new ArrayList<>();

        @Nullable Formatting.Color currentColor = null;
        EnumSet<Formatting.Format> currentFormat = EnumSet.noneOf(Formatting.Format.class);

        boolean grabbingFormattingChar = false;

        for (int i = 0; i < string.length(); i++) {
            char c = string.charAt(i);

            if (c == Formatting.SECTION_SIGN) {
                grabbingFormattingChar = true;
            } else if (grabbingFormattingChar) {
                if (c == Formatting.RESET.getChar()) {
                    currentColor = null;
                    currentFormat = EnumSet.noneOf(Formatting.Format.class);
                } else {
                    currentColor = Formatting.Color.fromChar(c);
                    currentFormat = Formatting.Format.fromCharAsSet(c);
                }

                grabbingFormattingChar = false;
            } else {
                chars.add(new Char(c, currentColor, currentFormat));
            }
        }

        return new FormattedString(chars);
    }

    // -- CharSequence

    @Override
    public int length() {
        return size();
    }

    @Override
    public char charAt(int index) {
        return get(index).character();
    }

    public FormattedString subString(int start, int end) {
        return new FormattedString(subList(start, end));
    }

    @NotNull
    @Override
    public CharSequence subSequence(int start, int end) {
        return subString(start, end);
    }

    // --

    @Override
    public @NotNull String toString() {
        return toString(false);
    }

    public @NotNull String toString(boolean skipNewLines) {
        if (isEmpty()) return "";

        StringBuilder sb = new StringBuilder();

        Char previousChar = Char.EMPTY;

        for (Char character : this) {
            if (skipNewLines && character.character() == '\n') continue;

            if (!character.hasSameFormatting(previousChar)) {
                if (previousChar.hasFormatting()) {
                    sb.append(Formatting.SECTION_SIGN).append(Formatting.RESET.getChar());
                }
                character.appendFormatting(sb);
            }

            sb.append(character.character());
            previousChar = character;
        }

        Char lastChar = get(size() - 1);
        if (lastChar.hasFormatting()) {
            sb.append(Formatting.SECTION_SIGN).append(Formatting.RESET.getChar());
        }

        return sb.toString();
    }

    public @NotNull String toStringWithoutFormatting() {
        return toStringWithoutFormatting(false);
    }

    public @NotNull String toStringWithoutFormatting(boolean skipNewLines) {
        if (isEmpty()) return "";

        StringBuilder sb = new StringBuilder();

        for (Char character : this) {
            if (skipNewLines && character.character() == '\n') continue;
            sb.append(character.character());
        }

        return sb.toString();
    }
}