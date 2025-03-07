package io.github.mortuusars.scholar.client.textbox.internals;

import net.minecraft.ChatFormatting;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

public interface Formatting {
    char SECTION_SIGN = '§';

    String getName();
    char getChar();

    default boolean isReset() {
        return this == RESET;
    }

    default boolean isColor() {
        return false;
    }

    default boolean isFormat() {
        return false;
    }

    default ChatFormatting toChatFormatting() {
        return ChatFormatting.getByCode(getChar());
    }

    Formatting RESET = new Formatting() {
        @Override
        public String getName() {
            return "reset";
        }

        @Override
        public char getChar() {
            return 'r';
        }
    };

    enum Color implements Formatting {
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
        WHITE("white", 'f');

        private final String name;
        private final char c;

        Color(String name, char c) {
            this.name = name;
            this.c = c;
        }

        public String getName() {
            return name;
        }

        @Override
        public char getChar() {
            return c;
        }

        @Override
        public boolean isColor() {
            return true;
        }

        public static @Nullable Color fromChar(char c) {
            for (Color value : values()) {
                if (value.c == c) return value;
            }
            return null;
        }
    }

    enum Format implements Formatting {
        OBFUSCATED("obfuscated", 'k'),
        BOLD("bold", 'l'),
        STRIKETHROUGH("strikethrough", 'm'),
        UNDERLINE("underline", 'n'),
        ITALIC("reset", 'o');

        private final String name;
        private final char c;

        Format(String name, char c) {
            this.name = name;
            this.c = c;
        }

        public String getName() {
            return name;
        }

        @Override
        public char getChar() {
            return c;
        }

        @Override
        public boolean isFormat() {
            return true;
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
}