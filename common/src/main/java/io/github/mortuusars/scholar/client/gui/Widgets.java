package io.github.mortuusars.scholar.client.gui;

import io.github.mortuusars.scholar.Scholar;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.resources.Identifier;

public class Widgets {
    public static final WidgetSprites PREVIOUS_PAGE_SPRITES =
            threeStateSprites(Scholar.resource("book/previous_page"));
    public static final WidgetSprites NEXT_PAGE_SPRITES =
            threeStateSprites(Scholar.resource("book/next_page"));

    public static WidgetSprites threeStateSprites(Identifier base) {
        return new WidgetSprites(base,
                Identifier.fromNamespaceAndPath(base.getNamespace(), base.getPath() + "_disabled"),
                Identifier.fromNamespaceAndPath(base.getNamespace(), base.getPath() + "_highlighted"));
    }

    public static WidgetSprites normalAndHighlightedSprites(Identifier base) {
        return new WidgetSprites(base, base,
                Identifier.fromNamespaceAndPath(base.getNamespace(), base.getPath() + "_highlighted"));
    }
}