package io.github.mortuusars.scholar.client.gui.widget.textbox.text;

import net.minecraft.ChatFormatting;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.Objects;

public record Formatting(@Nullable Color color, EnumSet<Format> format) {
    public static final char SECTION_SIGN = ChatFormatting.PREFIX_CODE;
    public static final Reset RESET = new Reset();

    // --

    public static final Formatting EMPTY = new Formatting(null, EnumSet.noneOf(Format.class));

    public static Formatting of(Color color) {
        return new Formatting(color, EnumSet.noneOf(Format.class));
    }

    public static Formatting of(Format format) {
        return new Formatting(null, EnumSet.of(format));
    }

    public static Formatting of(char code) {
        return EMPTY.with(code);
    }

    public static Formatting of(Formatting.Type type) {
        return of(type.getChar());
    }

    // --

    public boolean isEmpty() {
        return color == null && format.isEmpty();
    }

    public Formatting with(Color color) {
        return new Formatting(color, EnumSet.copyOf(format));
    }

    public Formatting with(Format format) {
        EnumSet<Format> formats = EnumSet.copyOf(this.format);
        formats.add(format);
        return new Formatting(color, formats);
    }

    public Formatting with(char code) {
        if (code == RESET.getChar()) {
            return EMPTY;
        }

        @Nullable Color color = Color.fromChar(code);
        if (color != null) {
            return with(color);
        }

        @Nullable Format format = Format.fromChar(code);
        if (format != null) {
            return with(format);
        }

        return this;
    }

    public StringBuilder append(StringBuilder sb) {
        if (color != null) {
            sb.append(SECTION_SIGN).append(color.getChar());
        }

        for (Format format : format) {
            sb.append(SECTION_SIGN).append(format.getChar());
        }

        return sb;
    }

    public Formatting flip(Formatting formatting) {
        if (formatting.isEmpty()) {
            return EMPTY;
        }

        @Nullable Color color = color();
        EnumSet<Format> format = EnumSet.copyOf(format());

        if (formatting.color() != null) {
            color = formatting.color() != color ? formatting.color() : null;
        }

        for (Format f : formatting.format()) {
            if (format.contains(f)) {
                format.remove(f);
            } else {
                format.add(f);
            }
        }

        return new Formatting(color, format);
    }

    public Formatting copy() {
        return new Formatting(color, EnumSet.copyOf(format));
    }

    public enum Color implements Type {
        BLACK("black", '0', 0xFF000000),
        DARK_BLUE("dark_blue", '1', 0xFF0000AA),
        DARK_GREEN("dark_green", '2', 0xFF00AA00),
        DARK_AQUA("dark_aqua", '3', 0xFF00AAAA),
        DARK_RED("dark_red", '4', 0xFFAA0000),
        DARK_PURPLE("dark_purple", '5', 0xFFAA00AA),
        GOLD("gold", '6', 0xFFFFAA00),
        GRAY("gray", '7', 0xFFAAAAAA),
        DARK_GRAY("dark_gray", '8', 0xFF555555),
        BLUE("blue", '9', 0xFF5555FF),
        GREEN("green", 'a', 0xFF55FF55),
        AQUA("aqua", 'b', 0xFF55FFFF),
        RED("red", 'c', 0xFFFF5555),
        LIGHT_PURPLE("light_purple", 'd', 0xFFFF55FF),
        YELLOW("yellow", 'e', 0xFFFFFF55),
        WHITE("white", 'f', 0xFFFFFFFF);

        private final String name;
        private final char c;
        private final int color;

        Color(String name, char c, int color) {
            this.name = name;
            this.c = c;
            this.color = color;
        }

        @Override
        public String getName() {
            return name;
        }

        public char getChar() {
            return c;
        }

        public int getColor() {
            return color;
        }

        public static @Nullable Color fromChar(char c) {
            for (Color value : values()) {
                if (value.c == c) return value;
            }
            return null;
        }

        public @NotNull ChatFormatting asChatFormatting() {
            return Objects.requireNonNull(ChatFormatting.getByCode(getChar()));
        }
    }

    public enum Format implements Type {
        OBFUSCATED("obfuscated", 'k'),
        BOLD("bold", 'l'),
        STRIKETHROUGH("strikethrough", 'm'),
        UNDERLINE("underline", 'n'),
        ITALIC("italic", 'o');

        private final String name;
        private final char c;

        Format(String name, char c) {
            this.name = name;
            this.c = c;
        }

        public String getName() {
            return name;
        }

        public char getChar() {
            return c;
        }

        public @NotNull ChatFormatting asChatFormatting() {
            return Objects.requireNonNull(ChatFormatting.getByCode(getChar()));
        }

        public static @Nullable Format fromChar(char c) {
            for (Format value : values()) {
                if (value.c == c) return value;
            }
            return null;
        }

        public static EnumSet<Format> fromCharAsSet(char c) {
            for (Format value : values()) {
                if (value.c == c) return EnumSet.of(value);
            }
            return EnumSet.noneOf(Format.class);
        }
    }

    public static class Reset implements Type {
        private Reset() {}

        public String getName() {
            return "reset";
        }

        public char getChar() {
            return 'r';
        }

        public StringBuilder append(StringBuilder sb) {
            sb.append(SECTION_SIGN).append(getChar());
            return sb;
        }

        public ChatFormatting asChatFormatting() {
            return ChatFormatting.RESET;
        }
    }

    public interface Type {
        String getName();
        char getChar();
        ChatFormatting asChatFormatting();

        default boolean isColor() {
            return this instanceof Color;
        }

        default boolean isFormat() {
            return this instanceof Format;
        }

        default boolean isReset() {
            return this == RESET;
        }
    }
}