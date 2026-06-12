package io.github.mortuusars.scholar.client.gui.widget;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

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
    public void extractContents(GuiGraphicsExtractor graphics, int i, int j, float f) {
        WidgetSprites sprites = expanded ? expandedSprites : this.sprites;
        Identifier sprite = sprites.get(isActive(), isHoveredOrFocused());
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, getX(), getY(), width, height);
    }
}
