package io.github.mortuusars.scholar.client.gui.screen;

import com.mojang.blaze3d.platform.InputConstants;
import io.github.mortuusars.scholar.Config;
import io.github.mortuusars.scholar.Scholar;
import io.github.mortuusars.scholar.ScholarClient;
import io.github.mortuusars.scholar.client.gui.Widgets;
import io.github.mortuusars.scholar.client.gui.widget.textbox.TextBox;
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
import net.minecraft.util.Mth;
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
        return Config.Common.BOOK_SCREEN_PAUSE.get();
    }

    protected void init() {
        this.leftPos = (this.width - BOOK_WIDTH) / 2;
        this.topPos = (this.height - BOOK_HEIGHT) / 2;

        createWidgets();
    }

    protected void createWidgets() {
        createPrevPageButton();
        createNextPageButton();
        createBottomButtons();
    }

    protected void createNextPageButton() {
        ImageButton nextPageButton = new ImageButton(leftPos + 270, topPos + 156, 13, 15,
                308, 0, 15, TEXTURE, 512, 512,
              (button) -> {
                  if (Screen.hasShiftDown()) {
                      pageToEnd();
                  } else {
                      pageForward();
                  }
              });
        nextPageButton.setTooltip(Tooltip.create(Component.translatable("spectatorMenu.next_page")
              .append(CommonComponents.NEW_LINE)
              .append(Component.translatable("gui.scholar.shift_jump_to_end"))));
        this.nextPageButton = addRenderableWidget(nextPageButton);
    }

    protected void createPrevPageButton() {
        ImageButton prevPageButton = new ImageButton(leftPos + 12, topPos + 156, 13, 15,
                295, 0, 15, TEXTURE, 512, 512,
              (button) -> {
                  if (Screen.hasShiftDown()) {
                      pageToStart();
                  } else {
                      pageBack();
                  }
              });
        prevPageButton.setTooltip(Tooltip.create(Component.translatable("spectatorMenu.previous_page")
              .append(CommonComponents.NEW_LINE)
              .append(Component.translatable("gui.scholar.shift_jump_to_start"))));
        this.prevPageButton = addRenderableWidget(prevPageButton);
    }

    protected void createBottomButtons() {
        if (Config.Common.BOOK_SCREEN_SHOW_DONE_BUTTON.get()) {
            addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, (button) -> onClose())
                  .bounds(this.width / 2 - 60, topPos + BOOK_HEIGHT + 12, 120, 20)
                  .build());
        }
    }

    protected void updateButtons() {
        prevPageButton.visible = currentSpread > 0;
        nextPageButton.visible = currentSpread < getSpreadCount() - 1;
    }

    protected void toggleBookTools() {
        boolean currentValue = Config.Common.EDIT_SCREEN_SHOW_EXTRA_TOOLS.get();
        Config.Common.EDIT_SCREEN_SHOW_EXTRA_TOOLS.set(!currentValue);
        Config.Common.EDIT_SCREEN_SHOW_EXTRA_TOOLS.save();
        Config.Client.TUTORIAL_EXTRA_TOOLS.set(false);
        Config.Client.TUTORIAL_EXTRA_TOOLS.save();
        // Implemented in base screen in case some extra functionality would be added in the future.
        // Does nothing in this class currently. Should be implemented in child classes.
    }

    public boolean isToolsVisible() {
        return Config.Common.EDIT_SCREEN_SHOW_EXTRA_TOOLS.get();
    }

    // -- Book

    public int getPageCount() {
        return 100;
    }

    public int getSpreadCount() {
        return (int) Math.ceil(getPageCount() / 2.0);
    }

    public abstract int getLastPage();

    protected abstract int getFirstPageWithContent();

    protected abstract int getLastPageWithContent();

    protected boolean pageBack() {
        if (currentSpread > 0) {
            currentSpread--;
            playPageTurnSound(0.8f);
            return true;
        }
        return false;
    }

    protected boolean pageToStart() {
        if (currentSpread > 0) {
            int firstPageWithContent = getFirstPageWithContent();
            int firstSpreadWithContent = firstPageWithContent / 2;
            setSpread(firstSpreadWithContent < currentSpread ? firstSpreadWithContent : 0);
            playPageTurnSound(0.8f);
            return true;
        }
        return false;
    }

    protected boolean pageForward() {
        if (currentSpread < getSpreadCount() - 1) {
            currentSpread++;
            playPageTurnSound(1f);
            return true;
        }
        return false;
    }

    protected boolean pageToEnd() {
        if (currentSpread < getLastPage() / 2) {
            int lastPageWithContent = getLastPageWithContent();
            int lastSpreadWithContent = lastPageWithContent / 2;
            setSpread(lastSpreadWithContent > currentSpread ? lastSpreadWithContent : getLastPage() / 2);
            playPageTurnSound(1f);
            return true;
        }
        return false;
    }

    public boolean setPage(int pageIndex) {
        pageIndex = Mth.clamp(pageIndex, 0, Math.max(0, getPageCount() - 1));
        int spreadIndex = pageIndex / 2;
        return setSpread(spreadIndex);
    }

    public boolean setSpread(int spreadIndex) {
        spreadIndex = Mth.clamp(spreadIndex, 0, Math.max(0, getSpreadCount() - 1));
        if (spreadIndex != this.currentSpread) {
            this.currentSpread = spreadIndex;
            return true;
        } else {
            return false;
        }
    }

    // -- Render

    protected void renderBook(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        RenderUtil.withColorMultiplied(bookColor, () -> {
            // Cover
            guiGraphics.blit(TEXTURE, leftPos, topPos, BOOK_WIDTH, BOOK_HEIGHT,
                  0, 0, BOOK_WIDTH, BOOK_HEIGHT, 512, 512);
        });

        // Paper
        guiGraphics.blit(TEXTURE, leftPos, topPos, BOOK_WIDTH, BOOK_HEIGHT,
              0, BOOK_HEIGHT, BOOK_WIDTH, BOOK_HEIGHT, 512, 512);
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

    protected void renderTools(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
    }

    // -- Input

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (!(getFocused() instanceof TextBox)) {
            if (Minecraft.getInstance().options.keyInventory.matches(keyCode, scanCode)) {
                this.onClose();
                return true;
            }

            if (keyCode == InputConstants.KEY_LEFT
                  || keyCode == InputConstants.KEY_PAGEUP
                  || Minecraft.getInstance().options.keyLeft.matches(keyCode, scanCode)) {
                if (Screen.hasShiftDown()) {
                    pageToStart();
                } else {
                    pageBack();
                }
                return true;
            }

            if (keyCode == InputConstants.KEY_RIGHT
                  || keyCode == InputConstants.KEY_PAGEDOWN
                  || Minecraft.getInstance().options.keyRight.matches(keyCode, scanCode)) {
                if (Screen.hasShiftDown()) {
                    pageToEnd();
                } else {
                    pageForward();
                }
                return true;
            }
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (super.mouseScrolled(mouseX, mouseY, scrollX, scrollY)) {
            return true;
        }

        if (scrollY > 0) {
            if (Screen.hasShiftDown()) {
                pageToStart();
            } else {
                pageBack();
            }
            return true;
        }

        if (scrollY < 0) {
            if (Screen.hasShiftDown()) {
                pageToEnd();
            } else {
                pageForward();
            }
            return true;
        }

        return false;
    }

    // --

    protected boolean isHovering(int x, int y, int width, int height, double mouseX, double mouseY) {
        mouseX -= this.leftPos;
        mouseY -= this.topPos;
        return mouseX >= (double) (x - 1) && mouseX < (double) (x + width + 1) && mouseY >= (double) (y - 1) && mouseY < (double) (y + height + 1);
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

    protected void playButtonClickSound(float volume, float pitch) {
        Minecraft.getInstance().getSoundManager().play(
              SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK.value(), pitch, volume));
    }

    protected void playButtonClickSound(float pitch) {
        Minecraft.getInstance().getSoundManager().play(
              SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK.value(), pitch, 0.3f));
    }

    protected void playButtonClickSound() {
        Minecraft.getInstance().getSoundManager().play(
              SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK.value(), 1, 0.3f));
    }

    protected void playPageTurnSound(float volume, float pitch) {
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.BOOK_PAGE_TURN, pitch, volume));
    }

    protected void playPageTurnSound(float pitch) {
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.BOOK_PAGE_TURN, pitch, 1f));
    }

    protected void playPageTurnSound() {
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.BOOK_PAGE_TURN, 1f, 1f));
    }
}