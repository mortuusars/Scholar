package io.github.mortuusars.scholar.client.gui.widget;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;

import java.util.function.Consumer;
import java.util.function.Function;

public class ToggleImageButton extends ImageButton {
    protected final WidgetSprites onSprites;
    protected final Consumer<Boolean> onToggled;
    protected boolean state;

    public ToggleImageButton(int x, int y, int width, int height, WidgetSprites offSprites, WidgetSprites onSprites,
                             Consumer<Boolean> onToggled) {
        super(x, y, width, height, offSprites, b -> {});
        this.onSprites = onSprites;
        this.onToggled = onToggled;
    }

    public boolean isOn() {
        return state;
    }

    public boolean isOff() {
        return !isOn();
    }

    public void toggle() {
        this.state = !state;
        onToggled.accept(this.state);
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public <T> T mapState(Function<Boolean, T> mappingFunction) {
        return mappingFunction.apply(state);
    }

    @Override
    public void onPress(InputWithModifiers input) {
        toggle();
    }

    @Override
    public void renderContents(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        WidgetSprites sprites = isOn() ? onSprites : this.sprites;
        Identifier identifier = sprites.get(isActive(), isHoveredOrFocused());
        guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, identifier, getX(), getY(), width, height);
    }
}
