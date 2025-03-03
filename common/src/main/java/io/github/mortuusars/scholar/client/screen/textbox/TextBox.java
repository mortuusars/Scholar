package io.github.mortuusars.scholar.client.screen.textbox;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import io.github.mortuusars.scholar.Scholar;
import io.github.mortuusars.scholar.book.Formatting;
import io.github.mortuusars.scholar.client.util.HorizontalAlignment;
import io.github.mortuusars.scholar.client.util.Pos2i;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.StringSplitter;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.font.TextFieldHelper;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class TextBox extends AbstractWidget {
    public final Font font;
    public Supplier<String> textGetter;
    public Consumer<String> textSetter;
    public Predicate<String> textValidator = text -> text != null
            && getFont().wordWrapHeight(text, width) + (text.endsWith("\n") ? getFont().lineHeight : 0) <= height;

    public HorizontalAlignment horizontalAlignment = HorizontalAlignment.LEFT;
    public int fontColor = 0xFF000000;
    public int fontUnfocusedColor = 0xFF000000;
    public int selectionColor = 0xFF0000FF;
    public int selectionUnfocusedColor = 0x880000FF;

    public final FormattableTextFieldHelper textFieldHelper;
    protected DisplayCache displayCache = new DisplayCache();
    protected int frameTick;
    protected long lastClickTime;
    protected int lastIndex = -1;

    public TextBox(@NotNull Font font, int x, int y, int width, int height,
                   Supplier<String> textGetter, Consumer<String> textSetter) {
        super(x, y, width, height, Component.empty());
        this.font = font;
        this.textGetter = textGetter;
        this.textSetter = textSetter;
        textFieldHelper = new FormattableTextFieldHelper(this::getText, this::setText,
                TextFieldHelper.createClipboardGetter(Minecraft.getInstance()),
                TextFieldHelper.createClipboardSetter(Minecraft.getInstance()),
                this::validateText);
    }

    public void tick() {
        ++frameTick;
    }

    public Font getFont() {
        return font;
    }

    public @NotNull String getText() {
        return textGetter.get();
    }

    public TextBox setText(@NotNull String text) {
        textSetter.accept(text);
        getTextHandler().setCursorPos(getTextHandler().getCursorPos());
        getTextHandler().setSelectionPos(getTextHandler().getSelectionPos());
        refreshDisplayCache();
        return this;
    }

    protected boolean validateText(String text) {
        return textValidator.test(text);
    }

    public void setHeight(int height) {
        this.height = height;
        refreshDisplayCache();
    }

    public FormattableTextFieldHelper getTextHandler() {
        return textFieldHelper;
    }

    public int getCurrentFontColor() {
        return isFocused() ? fontColor : fontUnfocusedColor;
    }

    public TextBox setFontColor(int fontColor, int fontUnfocusedColor) {
        this.fontColor = fontColor;
        this.fontUnfocusedColor = fontUnfocusedColor;
        refreshDisplayCache();
        return this;
    }

    public TextBox setSelectionColor(int selectionColor, int selectionUnfocusedColor) {
        this.selectionColor = selectionColor;
        this.selectionUnfocusedColor = selectionUnfocusedColor;
        refreshDisplayCache();
        return this;
    }

    public void setCursorToEnd() {
        textFieldHelper.setCursorToEnd();
        refreshDisplayCache();
    }

    public void refresh() {
        refreshDisplayCache();
    }

    protected DisplayCache getDisplayCache() {
        if (displayCache.needsRebuilding) {
            try {
                displayCache.rebuild(font, getText(), textFieldHelper.getCursorPos(), textFieldHelper.getSelectionPos(),
                        getX(), getY(), getWidth(), getHeight(), horizontalAlignment);
            } catch (Exception e) {
                Scholar.LOGGER.error("Rebuilding Display Cache failed: ", e);
            }
        }
        return displayCache;
    }

    protected void refreshDisplayCache() {
        displayCache.needsRebuilding = true;
    }

    protected Pos2i convertLocalToScreen(Pos2i pos) {
        return new Pos2i(getX() + pos.x, getY() + pos.y);
    }

    protected Pos2i convertScreenToLocal(Pos2i screenPos) {
        return new Pos2i(screenPos.x - getX(), screenPos.y - getY());
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
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
        narrationElementOutput.add(NarratedElementType.TITLE, createNarrationMessage());
    }

    @Override
    public @NotNull Component getMessage() {
        return Component.literal(getText());
    }

    boolean suppressNextCharTyped = false;

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        try {
            if (isFocused() && handleKeyPressed(keyCode, scanCode, modifiers)) {
                refreshDisplayCache();
                suppressNextCharTyped = true;
                return true;
            }
        } catch (Exception e) {
            Scholar.LOGGER.error("KeyPressed error: ", e);
            return true;
        }
        suppressNextCharTyped = false;
        return false;
    }

    protected boolean handleKeyPressed(int keyCode, int scanCode, int modifiers) {
        if (Screen.hasAltDown()) {
            @Nullable Formatting formatting = switch (keyCode) {
                case InputConstants.KEY_L -> Formatting.BOLD;
                case InputConstants.KEY_O -> Formatting.ITALIC;
                case InputConstants.KEY_N -> Formatting.UNDERLINE;
                case InputConstants.KEY_M -> Formatting.STRIKETHROUGH;
                case InputConstants.KEY_K -> Formatting.OBFUSCATED;
                case InputConstants.KEY_0 -> Formatting.BLACK;
                case InputConstants.KEY_1 -> Formatting.DARK_BLUE;
                case InputConstants.KEY_2 -> Formatting.DARK_GREEN;
                case InputConstants.KEY_3 -> Formatting.DARK_AQUA;
                case InputConstants.KEY_4 -> Formatting.DARK_RED;
                case InputConstants.KEY_5 -> Formatting.DARK_PURPLE;
                case InputConstants.KEY_6 -> Formatting.GOLD;
                case InputConstants.KEY_7 -> Formatting.GRAY;
                case InputConstants.KEY_8 -> Formatting.DARK_GRAY;
                case InputConstants.KEY_9 -> Formatting.BLUE;
                case InputConstants.KEY_A -> Formatting.GREEN;
                case InputConstants.KEY_B -> Formatting.AQUA;
                case InputConstants.KEY_C -> Formatting.RED;
                case InputConstants.KEY_D -> Formatting.LIGHT_PURPLE;
                case InputConstants.KEY_E -> Formatting.YELLOW;
                case InputConstants.KEY_F -> Formatting.WHITE;
                case InputConstants.KEY_R -> Formatting.RESET;
                default -> null;
            };

            if (formatting != null) {
                if (formatting == Formatting.RESET) {
                    //noinspection DataFlowIssue
                    setText(ChatFormatting.stripFormatting(getText()));
                }
                else {
                    getTextHandler().applyFormattingToSelection(formatting);
                }
                return true;
            }
        }

        TextFieldHelper.CursorStep cursorStep = Screen.hasControlDown() ? TextFieldHelper.CursorStep.WORD : TextFieldHelper.CursorStep.CHARACTER;
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
        } else if (keyCode == InputConstants.KEY_BACKSPACE) {
            textFieldHelper.removeFromCursor(-1, cursorStep);
            return true;
        } else if (keyCode == InputConstants.KEY_DELETE) {
            textFieldHelper.removeFromCursor(1, cursorStep);
            return true;
        } else if (keyCode == InputConstants.KEY_RETURN || keyCode == InputConstants.KEY_NUMPADENTER) {
            textFieldHelper.insertText(CommonComponents.NEW_LINE.getString());
            return true;
        }

        return textFieldHelper.keyPressed(keyCode);
    }

    public boolean charTyped(char codePoint, int modifiers) {
        if (suppressNextCharTyped) return true;
        if (isFocused() && getTextHandler().charTyped(codePoint)) {
            refreshDisplayCache();
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (isHovered && visible && isActive() && button == 0) {
            long currentTime = Util.getMillis();
            DisplayCache displayCache = getDisplayCache();
            int index = displayCache.getIndexAtPosition(font, convertScreenToLocal(new Pos2i((int) mouseX, (int) mouseY)));

            if (index >= 0) {
                if (index == lastIndex && currentTime - lastClickTime < 250L) {
                    if (!textFieldHelper.isSelecting()) {
                        selectWord(index);
                    } else {
                        textFieldHelper.selectAll();
                    }
                } else {
                    textFieldHelper.setCursorPos(index, Screen.hasShiftDown());
                }
                refreshDisplayCache();
            }

            lastIndex = index;
            lastClickTime = currentTime;
            return true;
        }

        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (button == 0) {
            DisplayCache displayCache = this.getDisplayCache();
            int index = displayCache.getIndexAtPosition(this.font, this.convertScreenToLocal(new Pos2i((int) mouseX, (int) mouseY)));
            this.textFieldHelper.setCursorPos(index, true);
            this.refreshDisplayCache();
        }
        return true;
    }

    protected void selectWord(int index) {
        String string = this.getText();
        this.textFieldHelper.setSelectionRange(StringSplitter.getWordPosition(string, -1, index, false),
                StringSplitter.getWordPosition(string, 1, index, false));
    }

    protected void changeLine(int yChange) {
        int cursorPos = this.textFieldHelper.getCursorPos();
        int line = this.getDisplayCache().changeLine(cursorPos, yChange);
        this.textFieldHelper.setCursorPos(line, Screen.hasShiftDown());
    }

    protected void keyHome() {
        if (Screen.hasControlDown()) {
            this.textFieldHelper.setCursorToStart(Screen.hasShiftDown());
        } else {
            int cursorIndex = this.textFieldHelper.getCursorPos();
            int lineStartIndex = this.getDisplayCache().findLineStart(cursorIndex);
            this.textFieldHelper.setCursorPos(lineStartIndex, Screen.hasShiftDown());
        }
    }

    protected void keyEnd() {
        if (Screen.hasControlDown()) {
            this.textFieldHelper.setCursorToEnd(Screen.hasShiftDown());
        } else {
            DisplayCache displayCache = this.getDisplayCache();
            int cursorIndex = this.textFieldHelper.getCursorPos();
            int lineEndIndex = displayCache.findLineEnd(cursorIndex);
            this.textFieldHelper.setCursorPos(lineEndIndex, Screen.hasShiftDown());
        }
    }
}
