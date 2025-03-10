package io.github.mortuusars.scholar.client.screen;

import com.mojang.blaze3d.platform.InputConstants;
import io.github.mortuusars.scholar.Config;
import io.github.mortuusars.scholar.Scholar;
import io.github.mortuusars.scholar.ScholarClient;
import io.github.mortuusars.scholar.client.screen.textbox.TextBox;
import io.github.mortuusars.scholar.client.util.RenderUtil;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public abstract class SpreadBookScreen extends Screen {
    public static final ResourceLocation TEXTURE = Scholar.resource("textures/gui/book.png");
    public static final int BOOK_WIDTH = 295;
    public static final int BOOK_HEIGHT = 180;
    public static final int TEXT_LEFT_X = 22;
    public static final int TEXT_RIGHT_X = 159;
    public static final int TEXT_Y = 21;
    public static final int TEXT_WIDTH = 114;
    public static final int TEXT_HEIGHT = 128;

    @NotNull
    protected final Minecraft minecraft;
    @NotNull
    protected final LocalPlayer player;

    protected int bookColor;
    protected int textColor;
    protected int pageNumbersColor;
    protected int selectionColor;
    protected int selectionUnfocusedColor;

    protected int leftPos;
    protected int topPos;
    protected Button prevPageButton;
    protected Button nextPageButton;

    protected int currentSpread;

    protected boolean toolsVisible;

    public SpreadBookScreen(int bookColor) {
        super(GameNarrator.NO_TITLE);
        this.minecraft = Minecraft.getInstance();
        this.player = Objects.requireNonNull(minecraft.player);
        this.bookColor = bookColor;
        this.textColor = Config.Client.getColor(Config.Client.TEXT_COLOR);
        this.pageNumbersColor = Config.Client.getColor(Config.Client.PAGE_NUMBERS_COLOR);
        this.selectionColor = Config.Client.getColor(Config.Client.SELECTION_COLOR);
        this.selectionUnfocusedColor = Config.Client.getColor(Config.Client.SELECTION_UNFOCUSED_COLOR);
    }

    @Override
    public boolean isPauseScreen() {
        return Config.Client.SCREEN_PAUSE.get();
    }

    protected void init() {
        this.leftPos = (this.width - BOOK_WIDTH) / 2;
        this.topPos = (this.height - BOOK_HEIGHT) / 2;

        createWidgets();
        updateButtonVisibility();
    }

    protected void createWidgets() {
        createPrevPageButton();
        createNextPageButton();
        createBottomButtons();
    }

    protected void createNextPageButton() {
        ImageButton nextPageButton = new ImageButton(leftPos + 270, topPos + 156, 13, 15,
                308, 0, 15, TEXTURE, 512, 512,
                (button) -> pageForward());
        nextPageButton.setTooltip(Tooltip.create(Component.translatable("spectatorMenu.next_page")));
        this.nextPageButton = addRenderableWidget(nextPageButton);
    }

    protected void createPrevPageButton() {
        ImageButton prevPageButton = new ImageButton(leftPos + 12, topPos + 156, 13, 15,
                295, 0, 15, TEXTURE, 512, 512,
                (button) -> pageBack());
        prevPageButton.setTooltip(Tooltip.create(Component.translatable("spectatorMenu.previous_page")));
        this.prevPageButton = addRenderableWidget(prevPageButton);
    }

    protected void createBottomButtons() {
        if (Config.Client.SHOW_DONE_BUTTON.get()) {
            addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, (button) -> onClose())
                    .bounds(this.width / 2 - 60, topPos + BOOK_HEIGHT + 12, 120, 20)
                    .build());
        }
    }

    protected void updateButtonVisibility() {
        prevPageButton.visible = currentSpread > 0;
        nextPageButton.visible = currentSpread < getSpreadCount() - 1;
    }

    protected void toggleBookTools() {
        toolsVisible = !toolsVisible;
        updateButtonVisibility();
    }

    public boolean isToolsVisible() {
        return toolsVisible;
    }

    // -- Book

    public int getPageCount() {
        return 100;
    }

    public int getSpreadCount() {
        return (int)Math.ceil(getPageCount() / 2.0);
    }

    protected boolean pageBack() {
        if (currentSpread > 0) {
            currentSpread--;
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.BOOK_PAGE_TURN, 0.8f));
            updateButtonVisibility();
            return true;
        }
        return false;
    }

    protected boolean pageForward() {
        if (currentSpread < getSpreadCount() - 1) {
            currentSpread++;
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.BOOK_PAGE_TURN, 1f));
            updateButtonVisibility();
            return true;
        }
        return false;
    }

    // -- Render

    protected void renderBook(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        RenderUtil.withColorMultiplied(bookColor, () -> {
            // Cover
            guiGraphics.blit(TEXTURE, (width - BOOK_WIDTH) / 2, (height - BOOK_HEIGHT) / 2, BOOK_WIDTH, BOOK_HEIGHT,
                    0, 0, BOOK_WIDTH, BOOK_HEIGHT, 512, 512);
        });

        // Paper
        guiGraphics.blit(TEXTURE, (width - BOOK_WIDTH) / 2, (height - BOOK_HEIGHT) / 2, BOOK_WIDTH, BOOK_HEIGHT,
                0, 180, BOOK_WIDTH, BOOK_HEIGHT, 512, 512);
    }

    protected void renderPageNumbers(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, int currentSpread) {
        renderLeftPageNumber(guiGraphics, mouseX, mouseY, partialTick, currentSpread, pageNumbersColor);
        renderRightPageNumber(guiGraphics, mouseX, mouseY, partialTick, currentSpread, pageNumbersColor);
    }

    protected void renderLeftPageNumber(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, int currentSpread, int color) {
        String leftPageNumber = Integer.toString(currentSpread * 2 + 1);
        guiGraphics.drawString(font, leftPageNumber, leftPos + 69 + (8 - font.width(leftPageNumber) / 2),
                topPos + 157, color, false);
    }

    protected void renderRightPageNumber(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, int currentSpread, int color) {
        String rightPageNumber = Integer.toString(currentSpread * 2 + 2);
        guiGraphics.drawString(font, rightPageNumber, leftPos + 208 + (8 - font.width(rightPageNumber) / 2),
                topPos + 157, color, false);
    }

    // -- Input

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (ScholarClient.KeyMappings.toggleBookTools.matches(keyCode, scanCode)) {
            toggleBookTools();
            return true;
        }

        if (!(getFocused() instanceof TextBox)) {
            if (Minecraft.getInstance().options.keyInventory.matches(keyCode, scanCode)) {
                this.onClose();
                return true;
            }

            if (keyCode == InputConstants.KEY_LEFT || keyCode == InputConstants.KEY_PAGEUP || Minecraft.getInstance().options.keyLeft.matches(keyCode, scanCode)) {
                pageBack();
                return true;
            }

            if (keyCode == InputConstants.KEY_RIGHT || keyCode == InputConstants.KEY_PAGEDOWN || Minecraft.getInstance().options.keyRight.matches(keyCode, scanCode)) {
                pageForward();
                return true;
            }
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    // --

    protected boolean isHovering(int x, int y, int width, int height, double mouseX, double mouseY) {
        mouseX -= this.leftPos;
        mouseY -= this.topPos;
        return mouseX >= (double)(x - 1) && mouseX < (double)(x + width + 1) && mouseY >= (double)(y - 1) && mouseY < (double)(y + height + 1);
    }

    protected boolean isHoveringOverRightPageNumber(double mouseX, double mouseY) {
        return isHovering(206, 157, 17, 7, mouseX, mouseY);
    }

    protected boolean isHoveringOverLeftPageNumber(double mouseX, double mouseY) {
        return isHovering(66, 157, 17, 7, mouseX, mouseY);
    }

    protected boolean isLeftPage(int pageIndex) {
        return pageIndex % 2 == 0;
    }

    protected boolean isRightPage(int pageIndex) {
        return pageIndex % 2 == 1;
    }
}