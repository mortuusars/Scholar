package io.github.mortuusars.scholar.client.screen;

import io.github.mortuusars.scholar.Scholar;
import io.github.mortuusars.scholar.client.textbox.AmazingTextBox;
import io.github.mortuusars.scholar.client.util.HorizontalAlignment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class TestScreen extends Screen {

    private AmazingTextBox textBox;

    public TestScreen() {
        super(Component.empty());
    }

    @Override
    public void tick() {
        super.tick();
        textBox.tick();
    }

    @Override
    protected void init() {
        textBox = new AmazingTextBox((width / 2) - 80, (height / 2) - 70, 160, 160);
//        textBox.horizontalAlignment = HorizontalAlignment.CENTER;

        addRenderableWidget(textBox);
        setFocused(textBox);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics);
        guiGraphics.fill((width / 2) - 85, (height / 2) - 75, (width / 2) + 85, (height / 2) + 95, 0xCFEFE9E1);
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        if (Minecraft.getInstance().options.renderDebug) {
            String text = textBox.getText().getText().replace("§", "&");
            try {
                int cursorPos = textBox.getText().getCursorPos();
                text = new StringBuilder(text).insert(cursorPos, "_").toString();
            } catch (Exception e) {
                Scholar.LOGGER.error(e.toString());
            }
            guiGraphics.drawString(font, text, 5, 5, 0xFFFFFFFF);
        }
    }
}
