package io.github.mortuusars.scholar.client.screen;

import com.google.common.base.Preconditions;
import io.github.mortuusars.scholar.Config;
import io.github.mortuusars.scholar.book.BookColor;
import io.github.mortuusars.scholar.book.Spread;
import io.github.mortuusars.scholar.client.screen.textbox.TextBox;
import io.github.mortuusars.scholar.client.screen.textbox.text.FormattedString;
import io.github.mortuusars.scholar.client.util.RenderUtil;
import io.netty.util.internal.StringUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.toasts.TutorialToast;
import net.minecraft.client.gui.screens.inventory.BookViewScreen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundEditBookPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class SpreadBookEditScreen extends SpreadBookScreen {
    protected final ItemStack bookStack;
    protected final InteractionHand hand;

    protected final List<String> pages = new ArrayList<>();

    protected TextBox rightPageTextBox;
    protected TextBox leftPageTextBox;

    protected ImageButton insertPageLeftButton;
    protected ImageButton removePageLeftButton;
    protected ImageButton insertPageRightButton;
    protected ImageButton removePageRightButton;

    protected boolean bookModified;

    @Nullable
    protected TutorialToast toast;

    public SpreadBookEditScreen(ItemStack bookStack, InteractionHand hand) {
        super(BookColor.of(bookStack));
        this.bookStack = bookStack;
        this.hand = hand;

        showTutorial();
    }

    protected void showTutorial() {
        if (!Config.Client.SHOW_BOOK_EDIT_SCREEN_TUTORIAL.get()) return;

        Component title = Component.translatable("tutorial.scholar.additional_editing_tools.title");
        Component message = Component.translatable("tutorial.scholar.additional_editing_tools.message");

        toast = new TutorialToast(TutorialToast.Icons.RECIPE_BOOK, title, message, false);
        Minecraft.getInstance().getTutorial().addTimedToast(toast, 160);
        Config.Client.SHOW_BOOK_EDIT_SCREEN_TUTORIAL.set(false);
        Config.Client.SHOW_BOOK_EDIT_SCREEN_TUTORIAL.save();
    }

    protected void setupPages(ItemStack bookStack) {
        pages.clear();
        CompoundTag compoundtag = bookStack.getTag();
        if (compoundtag != null) {
            BookViewScreen.loadPages(compoundtag, this.pages::add);
        }

        while (this.pages.size() < 2) {
            this.pages.add("");
        }

        setTextBoxes();
    }

    @Override
    protected void createWidgets() {
        leftPageTextBox = new TextBox(font, leftPos + TEXT_LEFT_X, topPos + TEXT_Y, TEXT_WIDTH, TEXT_HEIGHT)
                .setFontColor(textColor)
                .setFontUnfocusedColor(textColor)
                .setSelectionColor(selectionColor)
                .setSelectionUnfocusedColor(selectionUnfocusedColor)
                .setText(FormattedString.parse(getPageText(Spread.Side.LEFT)))
                .setOnTextChanged(text -> setPageText(Spread.Side.LEFT, text.toString()));
        addRenderableWidget(leftPageTextBox);

        rightPageTextBox = new TextBox(font, leftPos + TEXT_RIGHT_X, topPos + TEXT_Y, TEXT_WIDTH, TEXT_HEIGHT)
                .setFontColor(textColor)
                .setFontUnfocusedColor(textColor)
                .setSelectionColor(selectionColor)
                .setSelectionUnfocusedColor(selectionUnfocusedColor)
                .setText(FormattedString.parse(getPageText(Spread.Side.RIGHT)))
                .setOnTextChanged(text -> setPageText(Spread.Side.RIGHT, text.toString()));
        addRenderableWidget(rightPageTextBox);

        setupPages(bookStack);

        createPageToolButtons();

        createPrevPageButton();
        createNextPageButton();

        ImageButton enterSignModeButton = new ImageButton(leftPos - 24, topPos + 18, 22, 22, 321, 0,
                22, TEXTURE, 512, 512,
                b -> enterSignMode(), Component.translatable("book.signButton"));
        enterSignModeButton.setTooltip(Tooltip.create(Component.translatable("book.signButton")));
        addRenderableWidget(enterSignModeButton);

        createBottomButtons();
    }

    protected void createPageToolButtons() {
        insertPageLeftButton = new ImageButton(leftPos + 112, topPos + 154, 13, 13, 343, 0,
                13, TEXTURE, 512, 512,
                b -> insertEmptyPage(Spread.Side.LEFT), Component.translatable("gui.scholar.insert_empty_page"));
        insertPageLeftButton.setTooltip(Tooltip.create(Component.translatable("gui.scholar.insert_empty_page")));
        addRenderableWidget(insertPageLeftButton);

        removePageLeftButton = new ImageButton(leftPos + 126, topPos + 154, 13, 13, 356, 0,
                13, TEXTURE, 512, 512,
                b -> removePage(Spread.Side.LEFT), Component.translatable("gui.scholar.remove_page"));
        removePageLeftButton.setTooltip(Tooltip.create(Component.translatable("gui.scholar.remove_page")));
        addRenderableWidget(removePageLeftButton);

        insertPageRightButton = new ImageButton(leftPos + 156, topPos + 154, 13, 13, 343, 0,
                13, TEXTURE, 512, 512,
                b -> insertEmptyPage(Spread.Side.RIGHT), Component.translatable("gui.scholar.insert_empty_page"));
        insertPageRightButton.setTooltip(Tooltip.create(Component.translatable("gui.scholar.insert_empty_page")));
        addRenderableWidget(insertPageRightButton);

        removePageRightButton = new ImageButton(leftPos + 170, topPos + 154, 13, 13, 356, 0,
                13, TEXTURE, 512, 512,
                b -> removePage(Spread.Side.RIGHT), Component.translatable("gui.scholar.remove_page"));
        removePageRightButton.setTooltip(Tooltip.create(Component.translatable("gui.scholar.remove_page")));
        addRenderableWidget(removePageRightButton);
    }

    @Override
    protected void toggleBookTools() {
        super.toggleBookTools();

        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.LEVER_CLICK, 1.1f, 0.6f));

        if (toast != null) {
            toast.hide();
            toast = null;
        }
    }

    @Override
    protected void updateButtonVisibility() {
        super.updateButtonVisibility();

        insertPageLeftButton.visible = isToolsVisible();
        insertPageLeftButton.active = pages.size() < 100;
        insertPageRightButton.visible = isToolsVisible();
        insertPageRightButton.active = pages.size() < 100;

        removePageLeftButton.visible = isToolsVisible();
        removePageRightButton.visible = isToolsVisible();
    }

    // -- Render

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics);
        renderBook(guiGraphics, mouseX, mouseY, partialTick);
        renderPageNumbers(guiGraphics, mouseX, mouseY, partialTick, currentSpread);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    protected void renderBook(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        RenderUtil.withColorMultiplied(bookColor, () -> {
            // Cover
            guiGraphics.blit(TEXTURE, (width - BOOK_WIDTH) / 2, (height - BOOK_HEIGHT) / 2, BOOK_WIDTH, BOOK_HEIGHT,
                    0, 0, BOOK_WIDTH, BOOK_HEIGHT, 512, 512);

            // Enter Sign Mode BG
            guiGraphics.blit(TEXTURE, leftPos - 29, topPos + 14, 0, 360,
                    29, 28, 512, 512);
        });

        // Paper
        guiGraphics.blit(TEXTURE, (width - BOOK_WIDTH) / 2, (height - BOOK_HEIGHT) / 2, BOOK_WIDTH, BOOK_HEIGHT,
                0, 180, BOOK_WIDTH, BOOK_HEIGHT, 512, 512);
    }

    // --

    @Override
    protected boolean pageBack() {
        if (super.pageBack()) {
            setTextBoxes();
            return true;
        }
        return false;
    }

    @Override
    protected boolean pageForward() {
        if (super.pageForward()) {
            // Ensure we have pages to display:
            while (this.pages.size() < (currentSpread + 1) * 2) {
                appendEmptyPage();
            }

            setTextBoxes();
            return true;
        }
        return false;
    }

    protected void setTextBoxes() {
        leftPageTextBox.getEditor().setCursorPos(0, false);
        leftPageTextBox.getEditor().setString(FormattedString.parse(getPageText(Spread.Side.LEFT)));
        leftPageTextBox.getDisplayCache().scheduleUpdate();
        rightPageTextBox.getEditor().setCursorPos(0, false);
        rightPageTextBox.getEditor().setString(FormattedString.parse(getPageText(Spread.Side.RIGHT)));
        rightPageTextBox.getDisplayCache().scheduleUpdate();
    }

    protected void enterSignMode() {
        saveChanges(false, null);
        minecraft.setScreen(new BookSigningScreen(this, bookColor, title -> saveChanges(true, title)));
    }

    // --

    protected String getPageText(Spread.Side side) {
        int pageIndex = side.getPageIndexFromSpread(currentSpread);
        return pageIndex >= 0 && pageIndex < this.pages.size() ? this.pages.get(pageIndex) : "";
    }

    protected void setPageText(Spread.Side side, String text) {
        int pageIndex = side.getPageIndexFromSpread(currentSpread);
        if (pageIndex >= 0 && pageIndex < this.pages.size()) {
            this.pages.set(pageIndex, text);
            this.bookModified = true;
        }
    }

    protected void appendEmptyPage() {
        if (this.pages.size() < 100) {
            this.pages.add("");
        }
    }

    protected void insertEmptyPage(Spread.Side side) {
        if (pages.size() == 100) {
            Objects.requireNonNull(Minecraft.getInstance().player).displayClientMessage(
                    Component.translatable("gui.scholar.cannot_insert_page"), false);
            return;
        }

        int pageIndex = side.getPageIndexFromSpread(currentSpread);
        pages.add(pageIndex, "");

        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.BOOK_PAGE_TURN, 1.15f, 0.6f));

        setTextBoxes();
        bookModified = true;
    }

    protected void removePage(Spread.Side side) {
        int pageIndex = side.getPageIndexFromSpread(currentSpread);
        pages.remove(pageIndex);

        while (this.pages.size() < Spread.Side.RIGHT.getPageIndexFromSpread(currentSpread) + 1) {
            this.pages.add("");
        }

        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.BOOK_PAGE_TURN, 0.85f, 0.6f));

        setTextBoxes();
        bookModified = true;
    }

    protected void removeEmptyTrailingPages() {
        ListIterator<String> iterator = this.pages.listIterator(this.pages.size());
        while (iterator.hasPrevious() && iterator.previous().isEmpty()) {
            iterator.remove();
        }
    }

    protected void saveChanges(boolean sign, @Nullable String title) {
        if (bookModified || sign) {
            if (!sign) {
                title = null;
            }
            removeEmptyTrailingPages();
            updateLocalCopy(sign, title);

            sendChanges(title);
        }
    }

    protected void sendChanges(@Nullable String title) {
        int slotId = hand == InteractionHand.MAIN_HAND ? player.getInventory().selected : 40;
        Objects.requireNonNull(minecraft.getConnection()).send(
                new ServerboundEditBookPacket(slotId, this.pages, Optional.ofNullable(title)));
    }

    protected void updateLocalCopy(boolean sign, @Nullable String title) {
        ListTag listTag = new ListTag();
        this.pages.stream().map(StringTag::valueOf).forEach(listTag::add);
        if (!this.pages.isEmpty()) {
            this.bookStack.addTagElement("pages", listTag);
        }

        if (sign) {
            Preconditions.checkState(!StringUtil.isNullOrEmpty(title), "Title cannot be null or empty when signing a book.");
            this.bookStack.addTagElement("author", StringTag.valueOf(player.getGameProfile().getName()));
            this.bookStack.addTagElement("title", StringTag.valueOf(title));
        }
    }

    // --

    @Override
    public void onClose() {
        saveChanges(false, null);
        super.onClose();
    }
}
