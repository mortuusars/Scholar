package io.github.mortuusars.scholar.client.gui.widget;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class BookmarkButton extends ImageButton {
    protected final WidgetSprites expandedSprites;
    protected boolean expanded;

    public BookmarkButton(int x, int y, int width, int height, WidgetSprites defaultSprites, WidgetSprites expandedSprites, OnPress onPress, Component message) {
        super(x, y, width, height, defaultSprites, onPress, message);
        this.expandedSprites = expandedSprites;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        WidgetSprites sprites = expanded ? expandedSprites : this.sprites;
        ResourceLocation texture = sprites.get(isActive(), isHoveredOrFocused());
        guiGraphics.blitSprite(texture, getX(), getY(), width, height);
    }
}
