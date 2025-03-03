package io.github.mortuusars.scholar.client.screen.textbox;

import io.github.mortuusars.scholar.book.Formatting;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.font.TextFieldHelper;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class FormattableTextFieldHelper extends TextFieldHelper {
    public FormattableTextFieldHelper(Supplier<String> getMessage,
                                      Consumer<String> setMessage,
                                      Supplier<String> getClipboard,
                                      Consumer<String> setClipboard,
                                      Predicate<String> stringValidator) {
        super(getMessage, setMessage, getClipboard, setClipboard, stringValidator);
    }

    public String getText() {
        return getMessageFn.get();
    }

    public void setText(String text) {
        setMessageFn.accept(text);
        setSelectionPos(getSelectionPos());
        setCursorPos(getCursorPos(), true);
    }

    public int getSelectionStart() {
        return Math.min(selectionPos, cursorPos);
    }

    public int getSelectionEnd() {
        return Math.max(selectionPos, cursorPos);
    }

//    public @Nullable Formatting getFormattingAt(int index) {
//        String text = getText();
//        for (int i = 0; i < index; i++) {
//            if (Formatting.isFormattingAt(text, i)) {
//
//            }
//        }
//    }

//    public boolean isSectionFormatted(int start, int end) {

//    }

    public void applyFormatting(int startIndex, int endIndex, Formatting formatting) {
        if (startIndex == endIndex) return;

        List<Formatting> formats = Formatting.getLastFormattingBefore(getText(), startIndex);
        if (!formats.isEmpty()) {
            insertText(startIndex, Formatting.RESET.getCharString());
            startIndex += 2;
            endIndex += 2;

            int splitEndIndex = endIndex;
            for (Formatting format : formats) {
                insertText(splitEndIndex, format.getCharString());
                splitEndIndex += 2;
            }
        }

        insertText(startIndex, formatting.toString());
        insertText(endIndex + 2, Formatting.RESET.toString());
    }

    public void applyFormattingToSelection(Formatting formatting) {
        int start = getSelectionStart();
        int end = getSelectionEnd();

        applyFormatting(start, end, formatting);
        setSelectionPos(start);
        setCursorPos(end + 4, true);
    }

    // --

    public void insertText(int index, String insertedText) {
        String text = getMessageFn.get();
        text = new StringBuilder(text).insert(index, insertedText).toString();
        if (this.stringValidator.test(text)) {
            this.setMessageFn.accept(text);
            setCursorPos(this.cursorPos + insertedText.length(), false);
        }
    }
}
