package io.github.mortuusars.scholar.client.gui.widget;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class BookmarkButton extends ImageButton {
    protected int expandedVOffset;
    protected final ResourceLocation texture;
    protected boolean expanded;

    public BookmarkButton(int x, int y, int width, int height, int u, int v, int expandedVOffset,
                          int textureWidth, int textureHeight, ResourceLocation texture, OnPress onPress, Component message) {
        super(x, y, width, height, u, v, height, texture, textureWidth, textureHeight, onPress, message);
        this.expandedVOffset = expandedVOffset;
        this.texture = texture;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderTexture(guiGraphics, texture, getX(), getY(), xTexStart, yTexStart + (isExpanded() ? expandedVOffset : 0), yDiffTex,
              width, height, textureWidth, textureHeight);
    }
}
