package io.github.mortuusars.scholar.screen;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.InputConstants;
import io.github.mortuusars.scholar.Config;
import io.github.mortuusars.scholar.Scholar;
import io.github.mortuusars.scholar.screen.textbox.TextBox;
import io.github.mortuusars.scholar.util.RenderUtil;
import io.github.mortuusars.scholar.visual.BookColors;
import io.github.mortuusars.scholar.visual.Formatting;
import io.netty.util.internal.StringUtil;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.BookViewScreen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundEditBookPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.Optional;

public class SpreadBookEditScreen extends Screen {

    private TextBox leftPageTextBox;
    private TextBox rightPageTextBox;

    public enum Side {
        LEFT,
        RIGHT;

        public int getSideIndex() {
            return ordinal();
        }

        public int getPageIndexFromSpread(int spreadIndex) {
            return spreadIndex * 2 + getSideIndex();
        }
    }

    public static final ResourceLocation TEXTURE = Scholar.resource("textures/gui/book.png");

    public static final int BOOK_WIDTH = 295;
    public static final int BOOK_HEIGHT = 180;

    public static final int TEXT_LEFT_X = 23;
    public static final int TEXT_RIGHT_X = 158;
    public static final int TEXT_Y = 21;
    public static final int TEXT_WIDTH = 114;
    public static final int TEXT_HEIGHT = 128;

    public static final int SELECTION_COLOR = 0xFF664488;
    public static final int SELECTION_UNFOCUSED_COLOR = 0xFF775599;

    protected final Player owner;
    protected final ItemStack bookStack;
    protected final int bookColor;
    protected final InteractionHand hand;
    protected final int mainFontColor;
    protected final int secondaryFontColor;

    protected final List<String> pages = Lists.newArrayList();

    protected int leftPos;
    protected int topPos;
    protected Button nextButton;
    protected Button prevButton;
    protected Button enterSignModeButton;

    protected int currentSpread;
    protected boolean isModified;

    public SpreadBookEditScreen(Player owner, ItemStack bookStack, InteractionHand hand) {
        super(GameNarrator.NO_TITLE);
        this.owner = owner;
        this.bookStack = bookStack;
        this.bookColor = BookColors.fromStack(bookStack);
        this.hand = hand;
        this.mainFontColor = Config.Client.getColor(Config.Client.MAIN_FONT_COLOR);
        this.secondaryFontColor = Config.Client.getColor(Config.Client.SECONDARY_FONT_COLOR);

        CompoundTag compoundtag = bookStack.getTag();
        if (compoundtag != null)
            BookViewScreen.loadPages(compoundtag, this.pages::add);

        while (this.pages.size() < 2) {
            this.pages.add("");
        }
    }

    @Override
    public boolean isPauseScreen() {
        return Config.Client.BOOK_EDIT_SCREEN_PAUSE.get();
    }

    @Override
    public void tick() {
        leftPageTextBox.tick();
        rightPageTextBox.tick();
    }

    protected void init() {
        this.leftPos = (this.width - BOOK_WIDTH) / 2;
        this.topPos = (this.height - BOOK_HEIGHT) / 2;

        leftPageTextBox = new TextBox(font, leftPos + TEXT_LEFT_X, topPos + TEXT_Y, TEXT_WIDTH, TEXT_HEIGHT,
                () -> getPageText(Side.LEFT), text -> setPageText(Side.LEFT, text))
                .setFontColor(mainFontColor, mainFontColor)
                .setSelectionColor(SELECTION_COLOR, SELECTION_UNFOCUSED_COLOR);

        addRenderableWidget(leftPageTextBox);

        rightPageTextBox = new TextBox(font, leftPos + TEXT_RIGHT_X, topPos + TEXT_Y, TEXT_WIDTH, TEXT_HEIGHT,
                () -> getPageText(Side.RIGHT), text -> setPageText(Side.RIGHT, text))
                .setFontColor(mainFontColor, mainFontColor)
                .setSelectionColor(SELECTION_COLOR, SELECTION_UNFOCUSED_COLOR);

        addRenderableWidget(rightPageTextBox);

        ImageButton prevButton = new ImageButton(leftPos + 12, topPos + 156, 13, 15,
                295, 0, 15, TEXTURE, 512, 512,
                (button) -> this.pageBack());
        prevButton.setTooltip(Tooltip.create(Component.translatable("spectatorMenu.previous_page")));
        this.prevButton = addRenderableWidget(prevButton);

        ImageButton nextButton = new ImageButton(leftPos + 270, topPos + 156, 13, 15,
                308, 0, 15, TEXTURE, 512, 512,
                (button) -> this.pageForward());
        nextButton.setTooltip(Tooltip.create(Component.translatable("spectatorMenu.next_page")));
        this.nextButton = addRenderableWidget(nextButton);

        this.enterSignModeButton = new ImageButton(leftPos - 24, topPos + 18, 22, 22, 321, 0,
                22, TEXTURE, 512, 512,
                b -> enterSignMode(), Component.translatable("book.signButton"));
        this.enterSignModeButton.setTooltip(Tooltip.create(Component.translatable("book.signButton")));
        addRenderableWidget(this.enterSignModeButton);

        this.updateButtonVisibility();

        this.createMenuControls();
    }

    protected void createMenuControls() {
        if (Config.Client.BOOK_EDIT_SCREEN_SHOW_DONE_BUTTON.get()) {
            this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE,
                    (button) -> this.onClose()).bounds(this.width / 2 - 60, topPos + BOOK_HEIGHT + 12, 120, 20).build());
        }
    }

    protected void enterSignMode() {
        Objects.requireNonNull(minecraft).setScreen(new BookSigningScreen(this, bookColor));
    }

    private void updateButtonVisibility() {
        this.prevButton.visible = this.currentSpread > 0;
        this.nextButton.visible = this.currentSpread < 49; // 100 pages max
    }

    private void clearDisplayCacheAfterPageChange() {
        leftPageTextBox.setCursorToEnd();
        rightPageTextBox.setCursorToEnd();
    }

    protected void pageBack() {
        if (this.currentSpread > 0) {
            this.currentSpread--;
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.BOOK_PAGE_TURN, 0.8f));

            this.updateButtonVisibility();
            this.clearDisplayCacheAfterPageChange();
        }
    }

    protected void pageForward() {
        if (this.currentSpread < 49) {
            this.currentSpread++;
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.BOOK_PAGE_TURN, 1f));
            while (this.pages.size() < (currentSpread + 1) * 2)
                appendPageToBook();

            this.updateButtonVisibility();
            this.clearDisplayCacheAfterPageChange();
        }
    }

    private void appendPageToBook() {
        if (this.pages.size() < 100) {
            this.pages.add("");
            this.isModified = true;
        }
    }

    protected String getPageText(Side side) {
        int pageIndex = side.getPageIndexFromSpread(currentSpread);
        return pageIndex >= 0 && pageIndex < this.pages.size() ? this.pages.get(pageIndex) : "";
    }

    protected void setPageText(Side side, String text) {
        int pageIndex = side.getPageIndexFromSpread(currentSpread);
        if (pageIndex >= 0 && pageIndex < this.pages.size()) {
            this.pages.set(pageIndex, text);
            this.isModified = true;
        }
    }

    public void saveChanges(boolean sign, @Nullable String title) {
        if (this.isModified || sign) {
            if (!sign)
                title = null;
            this.eraseEmptyTrailingPages();
            this.updateLocalCopy(sign, title);
            int slotId = this.hand == InteractionHand.MAIN_HAND ? this.owner.getInventory().selected : 40;

            Objects.requireNonNull(Minecraft.getInstance().getConnection()).send(
                    new ServerboundEditBookPacket(slotId, this.pages, Optional.ofNullable(title)));
        }
    }

    protected void eraseEmptyTrailingPages() {
        ListIterator<String> iterator = this.pages.listIterator(this.pages.size());
        while (iterator.hasPrevious() && iterator.previous().isEmpty()) {
            iterator.remove();
        }
    }

    protected void updateLocalCopy(boolean sign, @Nullable String title) {
        ListTag listTag = new ListTag();
        this.pages.stream().map(StringTag::valueOf).forEach(listTag::add);
        if (!this.pages.isEmpty()) {
            this.bookStack.addTagElement("pages", listTag);
        }

        if (sign) {
            Preconditions.checkState(!StringUtil.isNullOrEmpty(title), "Title cannot be null or empty when signing a book.");
            this.bookStack.addTagElement("author", StringTag.valueOf(this.owner.getGameProfile().getName()));
            this.bookStack.addTagElement("title", StringTag.valueOf(title));
        }

    }

    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics);

        RenderUtil.withColorMultiplied(bookColor, () -> {
            // Cover
            guiGraphics.blit(TEXTURE, (width - BOOK_WIDTH) / 2, (height - BOOK_HEIGHT) / 2, BOOK_WIDTH, BOOK_HEIGHT,
                    0, 0, BOOK_WIDTH, BOOK_HEIGHT, 512, 512);

            // Enter Sign Mode BG
            guiGraphics.blit(TEXTURE, leftPos - 29, topPos + 14, 0, 360,
                    29, 28, 512, 512);
        });

        // Pages
        guiGraphics.blit(TEXTURE, (width - BOOK_WIDTH) / 2, (height - BOOK_HEIGHT) / 2, BOOK_WIDTH, BOOK_HEIGHT,
                0, 180, BOOK_WIDTH, BOOK_HEIGHT, 512, 512);

        drawPageNumbers(guiGraphics, currentSpread);

        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    protected void drawPageNumbers(GuiGraphics guiGraphics, int currentSpreadIndex) {
        String leftPageNumber = Integer.toString(currentSpreadIndex * 2 + 1);
        guiGraphics.drawString(font, leftPageNumber, leftPos + 69 + (8 - font.width(leftPageNumber) / 2),
                topPos + 157, secondaryFontColor, false);

        String rightPageNumber = Integer.toString(currentSpreadIndex * 2 + 2);
        guiGraphics.drawString(font, rightPageNumber, leftPos + 208 + (8 - font.width(rightPageNumber) / 2),
                topPos + 157, secondaryFontColor, false);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (getFocused() instanceof TextBox textBox
                && Screen.hasControlDown()
                && Screen.hasShiftDown()
                && keyCode == InputConstants.KEY_F) {
            textBox.textFieldHelper.insertText("§");
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(Scholar.SoundEvents.FORMATTING_CLICK.get(), 1f, 0.5f));
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

    @Override
    public boolean charTyped(char pCodePoint, int pModifiers) {
        boolean handled = super.charTyped(pCodePoint, pModifiers);

        if (handled && getFocused() instanceof TextBox textBox) {
            onTextBoxCharTyped(textBox);
        }

        return handled;
    }

    private static void onTextBoxCharTyped(TextBox textBox) {
        // Plays a sound when formatting code char is typed:

        int cursorPos = textBox.textFieldHelper.getCursorPos();
        String text = textBox.getText();

        if (cursorPos < 2 || cursorPos > text.length())
            return;

        int sectionSymbolIndex = cursorPos - 2;
        int formattingCharIndex = sectionSymbolIndex + 1;
        String enteredFormattingCode = text.substring(sectionSymbolIndex, formattingCharIndex + 1);

        for (Formatting formatting : Formatting.values()) {
            if (formatting.getCode().equals(enteredFormattingCode)) {
                Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(Scholar.SoundEvents.FORMATTING_CLICK.get(), 1f, 0.5f));
                return;
            }
        }
    }

    @Override
    public void onClose() {
        saveChanges(false, null);
        super.onClose();
    }
}
