package io.github.mortuusars.scholar.book;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public enum Formatting {
    OBFUSCATED("obfuscated", 'k', false),
    BOLD("bold", 'l', false),
    STRIKETHROUGH("strikethrough", 'm', false),
    UNDERLINE("underline", 'n', false),
    ITALIC("reset", 'o', false),

    BLACK("black", '0'),
    DARK_BLUE("dark_blue", '1'),
    DARK_GREEN("dark_green", '2'),
    DARK_AQUA("dark_aqua", '3'),
    DARK_RED("dark_red", '4'),
    DARK_PURPLE("dark_purple", '5'),
    GOLD("gold", '6'),
    GRAY("gray", '7'),
    DARK_GRAY("dark_gray", '8'),
    BLUE("blue", '9'),
    GREEN("green", 'a'),
    AQUA("aqua", 'b'),
    RED("red", 'c'),
    LIGHT_PURPLE("light_purple", 'd'),
    YELLOW("yellow", 'e'),
    WHITE("white", 'f'),

    RESET("reset", 'r', false);

    public static final String SECTION_SIGN = "§";
    public static final char SECTION_SIGN_CHAR = '§';

    private final char chr;
    private final String charString;
    private final String fullCode;
    private final String name;
    private final boolean isColor;

    Formatting(String name, char chr) {
        this(name, chr, true);
    }

    Formatting(String name, char chr, boolean isColor) {
        this.chr = chr;
        this.charString = "" + chr;
        this.fullCode = SECTION_SIGN + chr;
        this.name = name;
        this.isColor = isColor;
    }

    public String getName() {
        return name;
    }

    public char getChar() {
        return chr;
    }

    public String getCharString() {
        return charString;
    }

    public boolean isColor() {
        return isColor;
    }

    public boolean matches(String str) {
        return toString().equals(str);
    }

    // --

    public static @Nullable Formatting byCode(char c) {
        for (Formatting formatting : values()) {
            if (formatting.chr == c) {
                return formatting;
            }
        }
        return null;
    }

    public static @Nullable Formatting byFullCode(String code) {
        for (Formatting formatting : values()) {
            if (formatting.matches(code)) {
                return formatting;
            }
        }
        return null;
    }

    public static boolean isFormattingAt(String text, int index) {
        if (index < 0 || index >= text.length() - 1) return false;
        return byFullCode(text.substring(index, index + 2)) != null;
    }

    public static @NotNull List<Formatting> getLastFormattingBefore(String text, int index) {
        if (index < 0) return Collections.emptyList();

        int end = Math.min(text.length() - 1, index - 2);

        @Nullable Formatting format = null;

        for (int i = end; i >= 0; i--) {
            @Nullable Formatting formatting = getCodeAt(text, i);

            if (formatting == null) continue;
            if (formatting == RESET) return format != null ? List.of(format) : Collections.emptyList();
            if (formatting.isColor()) return format != null ? List.of(formatting, format) : List.of(formatting);
            if (format != null) return List.of(format);
            else format = formatting;
        }

        return format != null ? List.of(format) : Collections.emptyList();
    }

    public static @Nullable Formatting getCodeAt(String text, int index) {
        if (index < 0 || index >= text.length() - 1) return null;
        return text.charAt(index) == SECTION_SIGN_CHAR ? byCode(text.charAt(index + 1)) : null;
    }


    @Override
    public String toString() {
        return fullCode;
    }
}
