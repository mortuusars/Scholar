package io.github.mortuusars.scholar.client.textbox;

import io.github.mortuusars.scholar.Scholar;
import io.github.mortuusars.scholar.client.textbox.internals.RichText;
import io.github.mortuusars.scholar.client.textbox.display.RichTextDisplay;
import io.github.mortuusars.scholar.client.util.HorizontalAlignment;
import io.github.mortuusars.scholar.client.util.Pos2i;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
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

    public RichText getText() {
        return text;
    }

    protected void refreshDisplayCache() {
        display.scheduleRebuild();
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        display.render(guiGraphics, getX(), getY(), mouseX, mouseY, partialTick);
        if (isFocused() && frameTick / 6 % 2 == 0) {
            display.renderCursor(guiGraphics, getX(), getY(), mouseX, mouseY, partialTick);
        }
    }

    public int getCurrentFontColor() {
        return isFocused() ? fontColor : fontUnfocusedColor;
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
