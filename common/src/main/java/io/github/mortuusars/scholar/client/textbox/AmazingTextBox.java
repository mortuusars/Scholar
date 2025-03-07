package io.github.mortuusars.scholar.client.textbox;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import io.github.mortuusars.scholar.Scholar;
import io.github.mortuusars.scholar.client.textbox.display.FormattedStringDisplayCache;
import io.github.mortuusars.scholar.client.textbox.display.Line;
import io.github.mortuusars.scholar.client.textbox.internals.FormattedStringEditor;
import io.github.mortuusars.scholar.client.util.HorizontalAlignment;
import io.github.mortuusars.scholar.client.util.Pos2i;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AmazingTextBox extends AbstractWidget {
    public final Font font = Minecraft.getInstance().font;
    public int fontColor = 0xFF000000;
    public int fontUnfocusedColor = 0xFF000000;
    public int selectionColor = 0xFF0000FF;
    public int selectionUnfocusedColor = 0x880000FF;
    public HorizontalAlignment horizontalAlignment = HorizontalAlignment.LEFT;

    protected FormattedStringEditor editor = new FormattedStringEditor(() ->
            FormattedStringEditor.Validator.fitInDimensions(getFont(), getWidth(), getHeight()));
    protected FormattedStringDisplayCache displayCache = new FormattedStringDisplayCache(editor);

    protected int lastIndex;
    protected long lastActionTime;

    public AmazingTextBox(int x, int y, int width, int height) {
        super(x, y, width, height, Component.empty());
    }

    public Font getFont() {
        return font;
    }

    public FormattedStringEditor getEditor() {
        return editor;
    }

    public HorizontalAlignment getHorizontalAlignment() {
        return horizontalAlignment;
    }

    public int getCurrentFontColor() {
        return isFocused() ? fontColor : fontUnfocusedColor;
    }

    public int getCurrentSelectionColor() {
        return isFocused() ? selectionColor : selectionUnfocusedColor;
    }

    // -- Render

    public FormattedStringDisplayCache getDisplayCache() {
        if (displayCache.shouldUpdate()) {
            displayCache.update(getFont(), getWidth(), getHeight(), getHorizontalAlignment());
        }

        return displayCache;
    }

    protected void refreshDisplayCache() {
        displayCache.scheduleUpdate();
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        FormattedStringDisplayCache displayCache = getDisplayCache();

        renderLines(guiGraphics, mouseX, mouseY, partialTick, displayCache.getLines(), getCurrentFontColor());
        renderCursor(guiGraphics, mouseX, mouseY, partialTick, getEditor(), displayCache.getCursor(), getCurrentFontColor());
        renderSelection(guiGraphics, mouseX, mouseY, partialTick, displayCache.getSelection(), getCurrentSelectionColor());
    }

    public void renderLines(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, List<Line> lines, int color) {
        for (Line line : lines) {
            guiGraphics.drawString(font, line.renderedString(), getX() + line.x(), getY() + line.y(), color, false);
        }
    }

    public void renderSelection(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, List<Rect2i> selection, int color) {
        for (Rect2i rect : selection) {
            int x0 = getX() + rect.getX();
            int y0 = getY() + rect.getY();
            int x1 = x0 + rect.getWidth();
            int y1 = y0 + rect.getHeight();
            guiGraphics.fill(RenderType.guiTextHighlight(), x0, y0, x1, y1, color);
        }
    }

    public void renderCursor(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, FormattedStringEditor editor, Pos2i cursor, int color) {
        if (!isFocused()) return;
        if (editor.isSelecting()) return;
        if (System.currentTimeMillis() - lastActionTime > 200 && (System.currentTimeMillis() - lastActionTime) % 600 > 300) // Blinking
            return;

        if (editor.isCursorAtEnd()) {
            guiGraphics.drawString(getFont(), "_", getX() + cursor.x, getY() + cursor.y,
                    getCurrentFontColor(), false);
        } else {
            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(0, 0, 50);
            RenderSystem.disableBlend();
            guiGraphics.fill(
                    getX() + cursor.x,
                    getY() + cursor.y - 1,
                    getX() + cursor.x + 1,
                    getY() + cursor.y + font.lineHeight,
                    color);
            guiGraphics.pose().popPose();
        }
    }

    // -- Input

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        try {
            if (isFocused() && (handleKeyPressed(keyCode, scanCode, modifiers) || getEditor().keyPressed(keyCode))) {
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
        if (isFocused() && getEditor().charTyped(codePoint)) {
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
            FormattedStringDisplayCache display = getDisplayCache();

            int indexAtMousePos = display.getCharIndexAtPosition(font, (int) (mouseX - getX()), (int) (mouseY - getY()));

            if (indexAtMousePos == lastIndex && currentTime - lastActionTime < 250L) {
                if (!getEditor().isSelecting()) {
                    getEditor().selectWord(indexAtMousePos);
                } else {
                    getEditor().selectAll();
                }
            } else {
                getEditor().setCursorPos(indexAtMousePos, Screen.hasShiftDown());
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
            FormattedStringDisplayCache display = getDisplayCache();
            int indexAtMousePos = display.getCharIndexAtPosition(font, (int) (mouseX - getX()), (int) (mouseY - getY()));
            getEditor().setCursorPos(indexAtMousePos, true);
            refreshDisplayCache();
        }
        return true;
    }

    // --

    public void changeLine(int yChange) {
        int cursorPos = getEditor().getCursorPos();
        int cursorLineIndex = getDisplayCache().findLineIndexByCharIndex(cursorPos);
        Line cursorLine = getDisplayCache().getLine(cursorLineIndex);

        int linePos = cursorPos - cursorLine.firstCharIndex();

        int newLineIndex = Mth.clamp(cursorLineIndex + yChange, 0, getDisplayCache().getLines().size() - 1);
        Line newLine = getDisplayCache().getLine(newLineIndex);

        int newCursorPos = Mth.clamp(newLine.firstCharIndex() + linePos, newLine.firstCharIndex(), newLine.lastCharIndex() + 1);

        getEditor().setCursorPos(newCursorPos, Screen.hasShiftDown());
    }

    public void keyHome() {
        if (Screen.hasControlDown()) {
            getEditor().setCursorToStart(Screen.hasShiftDown());
        } else {
            int cursorPos = getEditor().getCursorPos();
            int lineIndex = getDisplayCache().findLineIndexByCharIndex(cursorPos);
            Line line = getDisplayCache().getLine(lineIndex);
            getEditor().setCursorPos(line.firstCharIndex(), Screen.hasShiftDown());
        }
    }

    public void keyEnd() {
        if (Screen.hasControlDown()) {
            getEditor().setCursorToEnd(Screen.hasShiftDown());
        } else {
            int cursorPos = getEditor().getCursorPos();
            int lineIndex = getDisplayCache().findLineIndexByCharIndex(cursorPos);
            Line line = getDisplayCache().getLine(lineIndex);
            getEditor().setCursorPos(line.lastCharIndex() + 1, Screen.hasShiftDown());
        }
    }

    // --

    @Override
    public @NotNull Component getMessage() {
        return Component.literal(getEditor().getString().toStringWithoutFormatting());
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
        narrationElementOutput.add(NarratedElementType.TITLE, createNarrationMessage());
    }
}
