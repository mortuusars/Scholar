package io.github.mortuusars.scholar.client.screen.textbox.display;

import com.mojang.blaze3d.platform.InputConstants;
import io.github.mortuusars.scholar.Scholar;
import io.github.mortuusars.scholar.client.screen.textbox.TextBox;
import io.github.mortuusars.scholar.client.screen.textbox.text.Char;
import io.github.mortuusars.scholar.client.screen.textbox.text.Formatting;
import io.github.mortuusars.scholar.client.util.Pos2i;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class FormattingToolbar {
    public static final ResourceLocation TEXTURE = Scholar.resource("textures/gui/formatting_toolbar.png");
    protected final TextBox textBox;
    protected Positioner positioner = Positioner.ABOVE_SELECTION;
    protected int width, height, x, y;
    protected boolean visible = true;
    protected boolean shouldUpdate = true;

    protected final List<FormattingButton> buttons = new ArrayList<>();

    public FormattingToolbar(TextBox textBox) {
        this.textBox = textBox;
        setup();
    }

    protected void setup() {
        buttons.clear();

        int x = 0;

        buttons.add(new FormattingButton(Formatting.Format.BOLD, new Rect2i(x, 0, 12, 15), new Pos2i(x, 0)));
        x += 11;
        buttons.add(new FormattingButton(Formatting.Format.ITALIC, new Rect2i(x, 0, 12, 15), new Pos2i(x, 0)));
        x += 11;
        buttons.add(new FormattingButton(Formatting.Format.UNDERLINE, new Rect2i(x, 0, 12, 15), new Pos2i(x, 0)));
        x += 11;
        buttons.add(new FormattingButton(Formatting.Format.STRIKETHROUGH, new Rect2i(x, 0, 12, 15), new Pos2i(x, 0)));
        x += 11;
        buttons.add(new FormattingButton(Formatting.Format.OBFUSCATED, new Rect2i(x, 0, 12, 15), new Pos2i(x, 0)));
        x += 12;

        buttons.add(new FormattingButton(Formatting.RESET, new Rect2i(x - 8, -13, 19, 28), new Pos2i(x, 0)));

        x += 11;

        for (Formatting.Color color : Arrays.stream(Formatting.Color.values()).limit(8).toList()) {
            buttons.add(new FormattingButton(color, new Rect2i(x, 0, 7, 7), new Pos2i(x + 8, 0)));
            x += 6;
        }

        x += 1;

        for (Formatting.Color color : Arrays.stream(Formatting.Color.values()).skip(8).toList()) {
            buttons.add(new FormattingButton(color, new Rect2i(x - 49, 6, 7, 9), new Pos2i(x + 8, 0)));
            x += 6;
        }

        width = buttons.stream().mapToInt(button -> button.area().getX() + button.area.getWidth()).max().orElse(0);
        height = buttons.stream().mapToInt(button -> button.area().getY() + button.area.getHeight()).max().orElse(0);
    }

    // --

    public TextBox getTextBox() {
        return textBox;
    }

    public Positioner getPositioner() {
        return positioner;
    }

    public FormattingToolbar setPositioner(Positioner positioner) {
        this.positioner = positioner;
        return this;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public boolean isVisible() {
        return visible;
    }

    public FormattingToolbar setVisible(boolean visible) {
        this.visible = visible;
        return this;
    }

    // --

    public boolean shouldUpdate() {
        return shouldUpdate;
    }

    public void scheduleUpdate() {
        shouldUpdate = true;
    }

    public void update() {
        Pos2i pos = positioner.position(this, getTextBox());
        x = pos.x;
        y = pos.y;

        for (FormattingButton button : buttons) {
            if (button.formatting() == Formatting.RESET) continue;
            button.highlighted = getTextBox().getEditor().getSelectedSpan().stream()
                    .allMatch(c -> c.hasFormatting(button.formatting()));
        }

        this.shouldUpdate = false;
    }

    // -- Render

    public boolean shouldShow() {
        return getTextBox().isFocused() && getTextBox().getEditor().isSelecting();
    }

    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (!isVisible() || !shouldShow()) return;

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0, 0, 500);

        @Nullable FormattingButton hoveredButton = null;

        for (FormattingButton button : buttons) {
            int vOffset = 0;
            if (button.disabled()) {
                vOffset = button.area.getHeight() * 2;
            } else if (button.highlighted()) {
                vOffset = button.area.getHeight();
            }

            if (hoveredButton == null && button.isHovering(mouseX - x, mouseY - y)) {
                hoveredButton = button;

                if (!button.disabled()) {
                    vOffset = button.area.getHeight();
                }
            }

            guiGraphics.blit(TEXTURE, x + button.area.getX(), y + button.area.getY(),
                    button.uv.x, button.uv.y + vOffset, button.area.getWidth(), button.area.getHeight());
        }

        if (hoveredButton != null) {
            String hotkey = Character.toString(hoveredButton.formatting().getChar()).toUpperCase();
            MutableComponent component = Component.translatable(
                            "gui.scholar.formatting." + hoveredButton.formatting.getName())
                    .append(" §8Alt+" + hotkey);
            guiGraphics.renderTooltip(Minecraft.getInstance().font, component, mouseX, mouseY + 20);
        }

        guiGraphics.pose().popPose();
    }

    // -- Input

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return false;
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (isVisible() && shouldShow() && button == InputConstants.MOUSE_BUTTON_LEFT) {
            for (FormattingButton formattingButton : buttons) {
                if (formattingButton.isHovering((int) (mouseX - x), (int) (mouseY - y))) {
                    getTextBox().getEditor().applyFormatting(Formatting.of(formattingButton.formatting()));
                    Minecraft.getInstance().getSoundManager().play(
                            SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK.value(), 1, 0.3f));
                    return true;
                }
            }
        }

        return false;
    }

    public interface Positioner {
        Pos2i position(FormattingToolbar toolbar, TextBox textBox);

        Positioner ABOVE_SELECTION = (toolbar, textBox) -> {
            int selectionStartY = textBox.getDisplayCache()
                    .getSelection()
                    .stream()
                    .mapToInt(Rect2i::getY)
                    .min()
                    .orElse(0);

            int x = textBox.getX() + HorizontalAlignment.CENTER.align(textBox.getWidth(), toolbar.getWidth());
            int y = textBox.getY() + selectionStartY - textBox.getFont().lineHeight - toolbar.getHeight();
            return new Pos2i(x, y);
        };
    }

    public static class FormattingButton {
        private final Formatting.Type formatting;
        private final Rect2i area;
        private final Pos2i uv;
        private boolean disabled;
        private boolean highlighted;

        public FormattingButton(Formatting.Type formatting, Rect2i area, Pos2i uv) {
            this.formatting = formatting;
            this.area = area;
            this.uv = uv;
        }

        public Formatting.Type formatting() {
            return formatting;
        }

        public Rect2i area() {
            return area;
        }

        public Pos2i uv() {
            return uv;
        }

        public boolean disabled() {
            return disabled;
        }

        public boolean highlighted() {
            return highlighted;
        }

        public boolean isHovering(int mouseX, int mouseY) {
            return (mouseX >= area.getX() && mouseX < area.getX() + area.getWidth())
                    && (mouseY >= area.getY() && mouseY < area.getY() + area.getHeight());
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (FormattingButton) obj;
            return Objects.equals(this.formatting, that.formatting) &&
                    Objects.equals(this.area, that.area) &&
                    Objects.equals(this.uv, that.uv) &&
                    this.disabled == that.disabled &&
                    this.highlighted == that.highlighted;
        }

        @Override
        public int hashCode() {
            return Objects.hash(formatting, area, uv, disabled, highlighted);
        }

        @Override
        public String toString() {
            return "FormattingButton[" +
                    "formatting=" + formatting + ", " +
                    "area=" + area + ", " +
                    "uv=" + uv + ", " +
                    "disabled=" + disabled + ", " +
                    "highlighted=" + highlighted + ']';
        }
    }
}
