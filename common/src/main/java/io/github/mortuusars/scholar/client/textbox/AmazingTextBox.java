package io.github.mortuusars.scholar.client.textbox;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import io.github.mortuusars.scholar.Scholar;
import io.github.mortuusars.scholar.book.Formatting;
import io.github.mortuusars.scholar.client.screen.textbox.DisplayCache;
import io.github.mortuusars.scholar.client.textbox.internals.Text;
import io.github.mortuusars.scholar.client.util.HorizontalAlignment;
import io.github.mortuusars.scholar.client.util.Pos2i;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.font.TextFieldHelper;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AmazingTextBox extends AbstractWidget {
    public final Font font = Minecraft.getInstance().font;
    public int fontColor = 0xFF000000;
    public int fontUnfocusedColor = 0xFF000000;
    public int selectionColor = 0xFF0000FF;
    public int selectionUnfocusedColor = 0x880000FF;

    protected final Text text = new Text(text -> text != null
            && getFont().wordWrapHeight(text, width) + (text.endsWith("\n") ? getFont().lineHeight : 0) <= height);

    protected DisplayCache displayCache = new DisplayCache();
    protected int frameTick;
    protected long lastClickTime;

    public AmazingTextBox(int x, int y, int width, int height) {
        super(x, y, width, height, Component.empty());
    }

    public void tick() {
        ++frameTick;
    }

    public Font getFont() {
        return font;
    }

    protected DisplayCache getDisplayCache() {
        if (displayCache.needsRebuilding) {
            try {
                displayCache.rebuild(font, getText().getText(), getText().getCursorPos(), getText().getSelectionAnchor(),
                        getX(), getY(), getWidth(), getHeight(), HorizontalAlignment.LEFT);
            } catch (Exception e) {
                Scholar.LOGGER.error("Rebuilding Display Cache failed: ", e);
            }
        }
        return displayCache;
    }

    public Text getText() {
        return text;
    }

    protected void refreshDisplayCache() {
        displayCache.needsRebuilding = true;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        DisplayCache displayCache = this.getDisplayCache();
        for (DisplayCache.LineInfo lineInfo : displayCache.lines) {
            guiGraphics.drawString(this.font, lineInfo.asComponent, getX() + lineInfo.x, getY() + lineInfo.y, getCurrentFontColor(), false);
        }
        this.renderHighlight(guiGraphics, displayCache.selectionAreas);
        if (isFocused())
            this.renderCursor(guiGraphics, displayCache.cursorPos, displayCache.cursorAtEnd);
    }

    public int getCurrentFontColor() {
        return isFocused() ? fontColor : fontUnfocusedColor;
    }

    protected void renderHighlight(GuiGraphics guiGraphics, Rect2i[] highlightAreas) {
        for (Rect2i selection : highlightAreas) {
            int x = getX() + selection.getX();
            int y = getY() + selection.getY();
            int x1 = x + selection.getWidth();
            int y1 = y + selection.getHeight();
            guiGraphics.fill(RenderType.guiTextHighlight(), x, y - 1, x1, y1, isFocused() ? selectionColor : selectionUnfocusedColor);
        }
    }

    protected void renderCursor(GuiGraphics guiGraphics, Pos2i cursorPos, boolean isEndOfText) {
        if (this.frameTick / 6 % 2 == 0) {
            cursorPos = convertLocalToScreen(cursorPos);
            if (isEndOfText)
                guiGraphics.drawString(this.font, "_", cursorPos.x, cursorPos.y, getCurrentFontColor(), false);
            else {
                guiGraphics.pose().pushPose();
                guiGraphics.pose().translate(0, 0, 50);
                RenderSystem.disableBlend();
                guiGraphics.fill(cursorPos.x, cursorPos.y - 1, cursorPos.x + 1, cursorPos.y + this.font.lineHeight, getCurrentFontColor());
                guiGraphics.pose().popPose();
            }
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        try {
            if (isFocused() && getText().keyPressed(keyCode)) {
                refreshDisplayCache();
                return true;
            }
        } catch (Exception e) {
            Scholar.LOGGER.error("KeyPressed error: ", e);
            return true;
        }
        return false;
    }

    public boolean charTyped(char codePoint, int modifiers) {
        if (isFocused() && getText().charTyped(codePoint)) {
            refreshDisplayCache();
            return true;
        }
        return false;
    }

    protected Pos2i convertLocalToScreen(Pos2i pos) {
        return new Pos2i(getX() + pos.x, getY() + pos.y);
    }

    protected Pos2i convertScreenToLocal(Pos2i screenPos) {
        return new Pos2i(screenPos.x - getX(), screenPos.y - getY());
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
