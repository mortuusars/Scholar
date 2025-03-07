package io.github.mortuusars.scholar.client.screen;

import io.github.mortuusars.scholar.Scholar;
import io.github.mortuusars.scholar.client.textbox.AmazingTextBox;
import io.github.mortuusars.scholar.client.textbox.internals.FormattedString;
import io.github.mortuusars.scholar.client.util.HorizontalAlignment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class TestScreen extends Screen {
    private AmazingTextBox textBoxLeft;
    private AmazingTextBox textBoxCenter;
    private AmazingTextBox textBoxRight;

    public TestScreen() {
        super(Component.empty());
    }

    @Override
    protected void init() {
        textBoxLeft = new AmazingTextBox((width / 2) - 250, (height / 2) - 70, 140, 160);
        textBoxLeft.getEditor().setString(FormattedString.parse("§lbold§r\n§4colored§r"));
        addRenderableWidget(textBoxLeft);

        textBoxCenter = new AmazingTextBox((width / 2) - 70, (height / 2) - 70, 140, 160);
        textBoxCenter.getEditor().setString(FormattedString.parse("§lbold§r\n§4colored§r"));
        textBoxCenter.setHorizontalAlignment(HorizontalAlignment.CENTER);
        addRenderableWidget(textBoxCenter);

        textBoxRight = new AmazingTextBox((width / 2) + 110, (height / 2) - 70, 140, 160);
        textBoxRight.getEditor().setString(FormattedString.parse("§lbold§r\n§4colored§r"));
        textBoxRight.setHorizontalAlignment(HorizontalAlignment.RIGHT);
        addRenderableWidget(textBoxRight);

        setFocused(textBoxCenter);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics);
        guiGraphics.fill((width / 2) - 255, (height / 2) - 75, (width / 2) - 105, (height / 2) + 95, 0xCFEFE9E1);
        guiGraphics.fill((width / 2) - 75, (height / 2) - 75, (width / 2) + 75, (height / 2) + 95, 0xCFEFE9E1);
        guiGraphics.fill((width / 2) + 105, (height / 2) - 75, (width / 2) + 255, (height / 2) + 95, 0xCFEFE9E1);
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        if (Minecraft.getInstance().options.renderDebug) {
            String text = textBoxCenter.getEditor().getString().toStringWithoutFormatting().replace("§", "&");
            try {
                int cursorPos = textBoxCenter.getEditor().getCursorPos();
                text = new StringBuilder(text).insert(cursorPos, "_").toString();
            } catch (Exception e) {
                Scholar.LOGGER.error(e.toString());
            }
            guiGraphics.drawString(font, text, 5, 5, 0xFFFFFFFF);
        }
    }
}
