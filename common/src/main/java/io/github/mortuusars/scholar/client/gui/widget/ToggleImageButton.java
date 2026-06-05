package io.github.mortuusars.scholar.client.gui.widget;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;
import java.util.function.Function;

public class ToggleImageButton extends ImageButton {
    protected final int onVOffset;
    protected final ResourceLocation texture;
    protected final Consumer<Boolean> onToggled;
    protected boolean state;

    public ToggleImageButton(int x, int y, int width, int height, int u, int v, int onVOffset, ResourceLocation texture,
                             int textureWidth, int textureHeight, Consumer<Boolean> onToggled) {
        super(x, y, width, height, u, v, height, texture, textureWidth, textureHeight, b -> {});
        this.onVOffset = onVOffset;
        this.texture = texture;
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
    public void onPress() {
        toggle();
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderTexture(guiGraphics, texture, getX(), getY(), xTexStart,
              yTexStart + (state ? onVOffset : 0), yDiffTex, width, height, textureWidth, textureHeight);
    }
}
