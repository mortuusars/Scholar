package io.github.mortuusars.scholar.client.gui.widget.textbox.text;

import net.minecraft.client.gui.Font;

//TODO: Rename to Codepoint or Symbol or something
public record Char(int codepoint, Formatting formatting) {
    public static final Char EMPTY = new Char('\u0000', Formatting.EMPTY);

    public Char(char c) {
        this(c, Formatting.EMPTY);
    }

    public Char(int c) {
        this((char) c);
    }

    public boolean hasFormatting() {
        return !formatting.isEmpty();
    }

    public boolean hasFormatting(Formatting.Type type) {
        if (type.isColor()) return type.equals(formatting.color());
        else if (type.isFormat()) return formatting.format().contains(((Formatting.Format) type));
        return false;
    }

    public Char withFormatting(Formatting.Color color) {
        return new Char(codepoint, formatting.with(color));
    }

    public Char withFormatting(Formatting.Format format) {
        return new Char(codepoint, formatting.with(format));
    }

    public Char withFormatting(Formatting formatting) {
        return new Char(codepoint, formatting);
    }

    public Char flipFormatting(Formatting formatting) {
        return new Char(codepoint, formatting().flip(formatting));
    }

    public int getWidth(Font font, boolean ignoreNewLine) {
        return ignoreNewLine && (codepoint == '\n') ? 0 : font.width(toStringWithFormatting());
    }

    public boolean isZeroWidth() {
        int type = Character.getType(codepoint);

        return codepoint == 0x200D // ZWJ
              || codepoint == 0xFE0F // variation selector
              || (type == Character.NON_SPACING_MARK)
              || (type == Character.COMBINING_SPACING_MARK)
              || (type == Character.ENCLOSING_MARK);
    }

    public String toStringWithFormatting() {
        if (!hasFormatting()) {
            return new String(Character.toChars(codepoint));
        }

        StringBuilder sb = new StringBuilder();
        formatting.append(sb);
        sb.appendCodePoint(codepoint);
        if (hasFormatting()) {
            Formatting.RESET.append(sb);
        }
        return sb.toString();
    }
}