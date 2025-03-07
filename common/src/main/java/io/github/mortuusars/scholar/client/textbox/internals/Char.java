package io.github.mortuusars.scholar.client.textbox.internals;

import net.minecraft.client.gui.Font;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public record Char(char character, @Nullable Formatting.Color color, EnumSet<Formatting.Format> format) {
    public static final Char EMPTY = new Char('\u0000', null, EnumSet.noneOf(Formatting.Format.class));

    public Char(char c) {
        this(c, null, EnumSet.noneOf(Formatting.Format.class));
    }

    public Char(int c) {
        this((char) c);
    }

    public boolean formattingMatches(Char other) {
        return color == other.color && format.equals(other.format);
    }

    public boolean hasFormatting() {
        return color != null || !format.isEmpty();
    }

    public StringBuilder appendFormatting(StringBuilder sb) {
        if (color != null) {
            sb.append(Formatting.SECTION_SIGN).append(color.getChar());
        }

        for (Formatting.Format format : format) {
            sb.append(Formatting.SECTION_SIGN).append(format.getChar());
        }

        return sb;
    }

    public Char applyFormatting(Formatting formatting) {
        if (formatting == Formatting.RESET) {
            return new Char(character);
        }

        @Nullable Formatting.Color color = color();
        EnumSet<Formatting.Format> format = format();

        if (formatting.isColor()) {
            Formatting.Color newColor = (Formatting.Color) formatting;
            color = newColor != color ? newColor : null;
        }

        if (formatting.isFormat()) {
            format = EnumSet.copyOf(format);
            Formatting.Format newFormat = (Formatting.Format) formatting;

            if (format.contains(newFormat)) {
                format.remove(newFormat);
            } else {
                format.add(newFormat);
            }
        }

        return new Char(character, color, format);
    }

    public int getWidth(Font font, boolean ignoreNewLine) {
        return ignoreNewLine && character == '\n' ? 0 : font.width(toStringWithFormatting());
    }

    public String toStringWithFormatting() {
        StringBuilder sb = new StringBuilder();
        appendFormatting(sb);
        sb.append(character);
        if (hasFormatting()) {
            sb.append(Formatting.SECTION_SIGN).append(Formatting.RESET.getChar());
        }
        return sb.toString();
    }

    public Char applyFormattingFrom(Char other) {
        return new Char(character, other.color, other.format);
    }
}
