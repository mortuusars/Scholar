package io.github.mortuusars.scholar.client.textbox;

import com.mojang.blaze3d.platform.InputConstants;
import io.github.mortuusars.scholar.Scholar;
import io.github.mortuusars.scholar.client.screen.textbox.DisplayCache;
import io.github.mortuusars.scholar.client.textbox.internals.RichLine;
import io.github.mortuusars.scholar.client.textbox.internals.RichText;
import io.github.mortuusars.scholar.client.textbox.display.RichTextDisplay;
import io.github.mortuusars.scholar.client.util.HorizontalAlignment;
import io.github.mortuusars.scholar.client.util.Pos2i;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.StringSplitter;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class AmazingTextBox extends AbstractWidget {
    public final Font font = Minecraft.getInstance().font;
    public int fontColor = 0xFF000000;
    public int fontUnfocusedColor = 0xFF000000;
    public int selectionColor = 0xFF0000FF;
    public int selectionUnfocusedColor = 0x880000FF;
    public HorizontalAlignment horizontalAlignment = HorizontalAlignment.LEFT;

    protected final RichText text = new RichText(text -> text != null
            && getFont().wordWrapHeight(text, width) + (text.endsWith("\n") ? getFont().lineHeight : 0) <= height);

    protected RichTextDisplay display = new RichTextDisplay(this, text);
    protected int lastIndex;
    protected long lastActionTime;

    public AmazingTextBox(int x, int y, int width, int height) {
        super(x, y, width, height, Component.empty());
    }

    public Font getFont() {
        return font;
    }

    public RichText getText() {
        return text;
    }

    public RichTextDisplay getDisplay() {
        return display;
    }

    protected void refreshDisplayCache() {
        display.scheduleRebuild();
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        display.render(guiGraphics, getX(), getY(), mouseX, mouseY, partialTick);
        if (isFocused() && (System.currentTimeMillis() - lastActionTime < 200 || (System.currentTimeMillis() - lastActionTime) % 600 < 300)) {
            display.renderCursor(guiGraphics, getX(), getY(), mouseX, mouseY, partialTick);
        }
    }

    public int getCurrentFontColor() {
        return isFocused() ? fontColor : fontUnfocusedColor;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        try {
            if (isFocused() && (handleKeyPressed(keyCode, scanCode, modifiers) || getText().keyPressed(keyCode))) {
                lastActionTime = System.currentTimeMillis();
                refreshDisplayCache();
                return true;
            }
        } catch (Exception e) {
            Scholar.LOGGER.error("KeyPressed error: ", e);
            return true;
        }
        return false;
    }

    protected boolean handleKeyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == InputConstants.KEY_UP) {
            changeLine(-1);
            return true;
        } else if (keyCode == InputConstants.KEY_DOWN) {
            changeLine(1);
            return true;
        } else if (keyCode == InputConstants.KEY_HOME) {
            keyHome();
            return true;
        } else if (keyCode == InputConstants.KEY_END) {
            keyEnd();
            return true;
        }

        return false;
    }

    public boolean charTyped(char codePoint, int modifiers) {
        if (isFocused() && getText().charTyped(codePoint)) {
            lastActionTime = System.currentTimeMillis();
            refreshDisplayCache();
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (isHovered && visible && isActive() && button == InputConstants.MOUSE_BUTTON_LEFT) {
            long currentTime = Util.getMillis();
            RichTextDisplay display = getDisplay();

            int indexAtMousePos = display.getIndexAtPosition(font, (int) (mouseX - getX()), (int) (mouseY - getY()));

            if (indexAtMousePos == lastIndex && currentTime - lastActionTime < 250L) {
                if (!getText().isSelecting()) {
                    getText().selectWord(indexAtMousePos);
                } else {
                    getText().selectAll();
                }
            } else {
                getText().setCursorPos(indexAtMousePos, Screen.hasShiftDown());
            }

            refreshDisplayCache();

            lastIndex = indexAtMousePos;
            lastActionTime = currentTime;
            return true;
        }

        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (button == 0) {
            RichTextDisplay display = getDisplay();
            int indexAtMousePos = display.getIndexAtPosition(font, (int) (mouseX - getX()), (int) (mouseY - getY()));
            getText().setCursorPos(indexAtMousePos, true);
            refreshDisplayCache();
        }
        return true;
    }

    public void changeLine(int yChange) {
        int cursorPos = getText().getCursorPos();
        int cursorLineIndex = getDisplay().getLineWithCharIndex(cursorPos);
        RichLine cursorLine = getDisplay().getLine(cursorLineIndex);

        int linePos = cursorPos - cursorLine.getFirstCharIndex();

        int newLineIndex = Mth.clamp(cursorLineIndex + yChange, 0, getDisplay().getLines().size() - 1);
        RichLine newLine = getDisplay().getLine(newLineIndex);

        int newCursorPos = Mth.clamp(newLine.getFirstCharIndex() + linePos, newLine.getFirstCharIndex(), newLine.getLastCharIndex() + 1);

        getText().setCursorPos(newCursorPos, Screen.hasShiftDown());
    }

    public void keyHome() {
        if (Screen.hasControlDown()) {
            getText().setCursorToStart(Screen.hasShiftDown());
        } else {
            int cursorPos = getText().getCursorPos();
            int lineIndex = getDisplay().getLineWithCharIndex(cursorPos);
            RichLine line = getDisplay().getLine(lineIndex);
            getText().setCursorPos(line.getFirstCharIndex(), Screen.hasShiftDown());
        }
    }

    public void keyEnd() {
        if (Screen.hasControlDown()) {
            getText().setCursorToEnd(Screen.hasShiftDown());
        } else {
            int cursorPos = getText().getCursorPos();
            int lineIndex = getDisplay().getLineWithCharIndex(cursorPos);
            RichLine line = getDisplay().getLine(lineIndex);
            getText().setCursorPos(line.getLastCharIndex() + 1, Screen.hasShiftDown());
        }
    }

    // --

    @Override
    public @NotNull Component getMessage() {
        return Component.literal(getText().getText());
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
        narrationElementOutput.add(NarratedElementType.TITLE, createNarrationMessage());
    }
}
