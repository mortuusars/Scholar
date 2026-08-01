package io.github.mortuusars.scholar.client.gui.widget.textbox;

import com.mojang.blaze3d.platform.InputConstants;
import io.github.mortuusars.scholar.Scholar;
import io.github.mortuusars.scholar.client.gui.widget.textbox.display.FormattedStringDisplayCache;
import io.github.mortuusars.scholar.client.gui.widget.textbox.display.FormattingToolbar;
import io.github.mortuusars.scholar.client.gui.widget.textbox.display.HorizontalAlignment;
import io.github.mortuusars.scholar.client.gui.widget.textbox.display.Line;
import io.github.mortuusars.scholar.client.gui.widget.textbox.text.FormattedString;
import io.github.mortuusars.scholar.client.gui.widget.textbox.text.FormattedStringEditor;
import io.github.mortuusars.scholar.client.gui.widget.textbox.text.Formatting;
import io.github.mortuusars.scholar.client.util.Pos2i;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class TextBox extends AbstractWidget {
    protected final Font font;

    protected FormattedStringEditor editor = new FormattedStringEditor(() ->
            FormattedStringEditor.Validator.fitInDimensions(getFont(), getWidth(), getHeight()));
    protected FormattedStringDisplayCache displayCache = new FormattedStringDisplayCache(editor);

    protected HorizontalAlignment horizontalAlignment = HorizontalAlignment.LEFT;
    protected int fontColor = 0xFF000000;
    protected int fontUnfocusedColor = 0xFF000000;
    protected int selectionColor = 0xFF0000FF;
    protected int selectionUnfocusedColor = 0x880000FF;
    protected Consumer<FormattedString> onTextChanged = text -> { };

    protected FormattingToolbar formattingToolbar = new FormattingToolbar(this);

    protected Pos2i lastClickPos = new Pos2i(0, 0);
    protected long lastActionTime;
    protected boolean canDrag;

    public TextBox(int x, int y, int width, int height) {
        this(Minecraft.getInstance().font, x, y, width, height);
    }

    public TextBox(Font font, int x, int y, int width, int height) {
        super(x, y, width, height, Component.empty());
        this.font = font;
    }

    public Font getFont() {
        return font;
    }

    public FormattedStringEditor getEditor() {
        return editor;
    }

    public FormattedStringDisplayCache getDisplayCache() {
        if (displayCache.shouldUpdate()) {
            displayCache.update(getFont(), getWidth(), getHeight(), getHorizontalAlignment());
        }

        return displayCache;
    }

    public FormattingToolbar getFormattingToolbar() {
        if (formattingToolbar.shouldUpdate()) {
            formattingToolbar.update();
        }
        return formattingToolbar;
    }

    public TextBox setFormattingToolbar(FormattingToolbar formattingToolbar) {
        this.formattingToolbar = formattingToolbar;
        this.formattingToolbar.scheduleUpdate();
        return this;
    }

    public HorizontalAlignment getHorizontalAlignment() {
        return horizontalAlignment;
    }

    public TextBox setHorizontalAlignment(HorizontalAlignment horizontalAlignment) {
        this.horizontalAlignment = horizontalAlignment;
        refreshDisplayCache();
        return this;
    }

    public int getFontColor() {
        return fontColor;
    }

    public TextBox setFontColor(int fontColor) {
        this.fontColor = fontColor;
        refreshDisplayCache();
        return this;
    }

    public int getFontUnfocusedColor() {
        return fontUnfocusedColor;
    }

    public TextBox setFontUnfocusedColor(int fontUnfocusedColor) {
        this.fontUnfocusedColor = fontUnfocusedColor;
        refreshDisplayCache();
        return this;
    }

    public int getSelectionColor() {
        return selectionColor;
    }

    public TextBox setSelectionColor(int selectionColor) {
        this.selectionColor = selectionColor;
        refreshDisplayCache();
        return this;
    }

    public int getSelectionUnfocusedColor() {
        return selectionUnfocusedColor;
    }

    public TextBox setSelectionUnfocusedColor(int selectionUnfocusedColor) {
        this.selectionUnfocusedColor = selectionUnfocusedColor;
        refreshDisplayCache();
        return this;
    }

    public int getCurrentFontColor() {
        return isFocused() ? fontColor : fontUnfocusedColor;
    }

    public int getCurrentSelectionColor() {
        return isFocused() ? selectionColor : selectionUnfocusedColor;
    }

    public TextBox setTextValidator(Predicate<String> validator) {
        getEditor().setValidator(validator);
        return this;
    }

    public Consumer<FormattedString> onTextChanged() {
        return onTextChanged;
    }

    public TextBox setOnTextChanged(Consumer<FormattedString> onTextChanged) {
        this.onTextChanged = onTextChanged;
        return this;
    }

    public TextBox setText(FormattedString text) {
        getEditor().setString(text);
        return this;
    }

    // -- Render


    @Override
    protected void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        FormattedStringDisplayCache displayCache = getDisplayCache();

        renderLines(graphics, mouseX, mouseY, partialTick, displayCache.getLines(), getCurrentFontColor());

        int cursorColor = getCurrentFontColor();
        Formatting currentFormatting = getEditor().getFormattingAtCursor();
        if (currentFormatting.color() != null) {
            cursorColor = currentFormatting.color().getColor();
        }

        renderCursor(graphics, mouseX, mouseY, partialTick, getEditor(), displayCache.getCursor(), cursorColor);
        renderSelection(graphics, mouseX, mouseY, partialTick, displayCache.getSelection(), getCurrentSelectionColor());

        getFormattingToolbar().render(graphics, mouseX, mouseY, partialTick);
    }

    public void renderLines(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick, List<Line> lines, int color) {
        for (Line line : lines) {
            graphics.text(font, line.renderedString(), getX() + line.x(), getY() + line.y(), color, false);
        }
    }

    public void renderSelection(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick, List<Rect2i> selection, int color) {
        for (Rect2i rect : selection) {
            int x0 = getX() + rect.getX();
            int y0 = getY() + rect.getY();
            int x1 = x0 + rect.getWidth();
            int y1 = y0 + rect.getHeight();
            graphics.fill(RenderPipelines.GUI_INVERT, x0, y0, x1, y1, 0xFFFFFFFF);
            graphics.fill(RenderPipelines.GUI_TEXT_HIGHLIGHT, x0, y0, x1, y1, color);
        }
    }

    public void renderCursor(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick, FormattedStringEditor editor, Pos2i cursor, int color) {
        if (!isFocused()) return;
        if (editor.isSelecting()) return;
        if (System.currentTimeMillis() - lastActionTime > 200 && (System.currentTimeMillis() - lastActionTime) % 600 > 300) // Blinking
            return;

        if (editor.isCursorAtEnd()) {
            Line line = getDisplayCache().getLine(getDisplayCache().getLines().size() - 1);
            if (cursor.y + font.lineHeight > getHeight()) {
                int x = line.x() + line.width();
                int y = line.y();
                graphics.text(getFont(), "<", getX() + x, getY() + y,
                        color, false);
            } else {
                graphics.text(getFont(), "_", getX() + cursor.x, getY() + cursor.y,
                        color, false);
            }
        } else {
            graphics.fill(
                    getX() + cursor.x,
                    getY() + cursor.y - 1,
                    getX() + cursor.x + 1,
                    getY() + cursor.y + font.lineHeight,
                    color);
        }
    }

    protected void refreshDisplayCache() {
        displayCache.scheduleUpdate();
        formattingToolbar.scheduleUpdate();
    }

    // -- Input

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (!isFocused() || !isActive() || !visible) return false;

        if (handleKeyPressed(event)) {
            lastActionTime = System.currentTimeMillis();
            onTextChanged().accept(getEditor().getString());
            refreshDisplayCache();
            return true;
        }

        return false;
    }

    protected boolean handleKeyPressed(KeyEvent event) {
        if (getFormattingToolbar().keyPressed(event)) {
            return true;
        } else if (event.key() == InputConstants.KEY_UP) {
            changeLine(-1);
            return true;
        } else if (event.key() == InputConstants.KEY_DOWN) {
            changeLine(1);
            return true;
        } else if (event.key() == InputConstants.KEY_HOME) {
            keyHome();
            return true;
        } else if (event.key() == InputConstants.KEY_END) {
            keyEnd();
            return true;
        }

        return getEditor().keyPressed(event);
    }

    @Override
    public boolean charTyped(CharacterEvent event) {
        if (isFocused() && getEditor().charTyped(event)) {
            lastActionTime = System.currentTimeMillis();
            onTextChanged().accept(getEditor().getString());
            refreshDisplayCache();
            return true;
        }

        return false;
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return super.isMouseOver(mouseX, mouseY) || getFormattingToolbar().isMouseOver(mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean isDoubleClick) {
        if (!isActive() || !visible) return false;

        if (getFormattingToolbar().mouseClicked(event)) {
            refreshDisplayCache();
            onTextChanged().accept(getEditor().getString());
            canDrag = false;
            return true;
        }

        if (isHovered && event.button() == InputConstants.MOUSE_BUTTON_LEFT) {
            long currentTime = System.currentTimeMillis();
            FormattedStringDisplayCache display = getDisplayCache();

            int indexAtMousePos = display.getCharIndexAtPosition(font, (int) (event.x() - getX()), (int) (event.y() - getY()));

            if (Math.abs(lastClickPos.x - (int) event.x()) < 4 && Math.abs(lastClickPos.y - (int) event.y()) < 4 && currentTime - lastActionTime < 250L) {
                if (!getEditor().isSelecting()) {
                    getEditor().selectWord(indexAtMousePos);
                } else {
                    getEditor().selectAll();
                }
            } else {
                getEditor().setCursorPos(indexAtMousePos, Minecraft.getInstance().hasShiftDown());
            }

            refreshDisplayCache();

            lastClickPos = new Pos2i((int) event.x(), (int) event.y());
            lastActionTime = currentTime;
            canDrag = true;
            return true;
        }

        return false;
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double mouseX, double mouseY) {
        if (event.button() == 0 && canDrag) {
            FormattedStringDisplayCache display = getDisplayCache();
            int indexAtMousePos = display.getCharIndexAtPosition(font, (int) (event.x() + mouseX - getX()), (int) (event.y() + mouseY - getY()));
            getEditor().setCursorPos(indexAtMousePos, true);
            refreshDisplayCache();
            return true;
        }
        return false;
    }

    // --

    public void changeLine(int yChange) {
        Pos2i cursor = getDisplayCache().getCursor();
        int x = cursor.x;
        int y = cursor.y + (font.lineHeight * yChange);
        int index = getDisplayCache().getCharIndexAtPosition(font, x, y);

        getEditor().setCursorPos(index, Minecraft.getInstance().hasShiftDown());
    }

    public void keyHome() {
        if (Minecraft.getInstance().hasControlDown()) {
            getEditor().setCursorToStart(Minecraft.getInstance().hasShiftDown());
        } else {
            int cursorPos = getEditor().getCursorPos();
            int lineIndex = getDisplayCache().findLineIndexByCharIndex(cursorPos);
            Line line = getDisplayCache().getLine(lineIndex);
            getEditor().setCursorPos(line.firstCharIndex(), Minecraft.getInstance().hasShiftDown());
        }
    }

    public void keyEnd() {
        if (Minecraft.getInstance().hasControlDown()) {
            getEditor().setCursorToEnd(Minecraft.getInstance().hasShiftDown());
        } else {
            int cursorPos = getEditor().getCursorPos();
            int lineIndex = getDisplayCache().findLineIndexByCharIndex(cursorPos);
            Line line = getDisplayCache().getLine(lineIndex);
            getEditor().setCursorPos(line.lastCharIndex() + 1, Minecraft.getInstance().hasShiftDown());
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
