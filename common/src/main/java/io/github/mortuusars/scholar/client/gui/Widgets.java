package io.github.mortuusars.scholar.client.gui;

import io.github.mortuusars.scholar.Scholar;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.resources.ResourceLocation;

public class Widgets {
    public static final WidgetSprites PREVIOUS_PAGE_SPRITES =
            threeStateSprites(Scholar.resource("book/previous_page"));
    public static final WidgetSprites NEXT_PAGE_SPRITES =
            threeStateSprites(Scholar.resource("book/next_page"));

    public static WidgetSprites threeStateSprites(ResourceLocation base) {
        return new WidgetSprites(base,
                ResourceLocation.fromNamespaceAndPath(base.getNamespace(), base.getPath() + "_disabled"),
                ResourceLocation.fromNamespaceAndPath(base.getNamespace(), base.getPath() + "_highlighted"));
    }

    public static WidgetSprites normalAndHighlightedSprites(ResourceLocation base) {
        return new WidgetSprites(base, base,
                ResourceLocation.fromNamespaceAndPath(base.getNamespace(), base.getPath() + "_highlighted"));
    }
}