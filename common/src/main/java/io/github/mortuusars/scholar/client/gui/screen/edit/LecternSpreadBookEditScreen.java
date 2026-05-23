package io.github.mortuusars.scholar.client.gui.screen.edit;

import io.github.mortuusars.scholar.Config;
import io.github.mortuusars.scholar.book.BookColor;
import io.github.mortuusars.scholar.book.BookSignature;
import io.github.mortuusars.scholar.client.gui.screen.SpreadBookScreen;
import io.github.mortuusars.scholar.menu.LecternSpreadBookEditMenu;
import io.github.mortuusars.scholar.network.Packets;
import io.github.mortuusars.scholar.network.packet.server.LecternEditBookC2SP;
import io.github.mortuusars.scholar.network.packet.server.SetCustomAuthorOnLecternC2SP;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Optional;

public class LecternSpreadBookEditScreen extends SpreadBookEditScreen implements MenuAccess<LecternSpreadBookEditMenu> {
    protected final LecternSpreadBookEditMenu menu;
    protected final ContainerListener listener = new ContainerListener() {
        @Override
        public void slotChanged(AbstractContainerMenu menu, int dataSlotIndex, ItemStack stack) {
            LecternSpreadBookEditScreen.this.bookChanged(stack);
        }

        @Override
        public void dataChanged(AbstractContainerMenu menu, int dataSlotIndex, int pageIndex) {
            if (dataSlotIndex == 0) {
                LecternSpreadBookEditScreen.this.updatePage(pageIndex);
            }
        }
    };

    public LecternSpreadBookEditScreen(LecternSpreadBookEditMenu lecternSpreadMenu, Inventory inventory, Component component) {
        super(lecternSpreadMenu.getBook());
        this.menu = lecternSpreadMenu;
    }

    @Override
    public @NotNull LecternSpreadBookEditMenu getMenu() {
        return menu;
    }

    @Override
    protected void init() {
        super.init();
        getMenu().addSlotListener(listener);
    }

    @Override
    protected void createBottomButtons() {
        if (player.mayBuild()) {
            if (Config.Common.BOOK_SCREEN_SHOW_DONE_BUTTON.get()) {
                this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE,
                        button -> this.onClose()).bounds(this.width / 2 - 100, topPos + SpreadBookScreen.BOOK_HEIGHT + 12, 98, 20).build());
                this.addRenderableWidget(Button.builder(Component.translatable("lectern.take_book"),
                        button -> this.sendButtonClick(3)).bounds(this.width / 2 + 2, topPos + SpreadBookScreen.BOOK_HEIGHT + 12, 98, 20).build());
            } else {
                this.addRenderableWidget(Button.builder(Component.translatable("lectern.take_book"),
                        (button) -> this.sendButtonClick(3)).bounds(this.width / 2 - 60, topPos + SpreadBookScreen.BOOK_HEIGHT + 12, 120, 20).build());
            }
        } else {
            super.createBottomButtons();
        }
    }

    @Override
    protected boolean pageBack() {
        this.sendButtonClick(LecternMenu.BUTTON_PREV_PAGE);

        getHistory().add(
              () -> this.sendButtonClick(LecternMenu.BUTTON_PREV_PAGE),
              () -> this.sendButtonClick(LecternMenu.BUTTON_NEXT_PAGE)
        );

        return true;
    }

    @Override
    protected boolean pageForward() {
        this.sendButtonClick(LecternMenu.BUTTON_NEXT_PAGE);

        getHistory().add(
              () -> this.sendButtonClick(LecternMenu.BUTTON_NEXT_PAGE),
              () -> this.sendButtonClick(LecternMenu.BUTTON_PREV_PAGE)
        );

        return true;
    }

    protected void bookChanged(ItemStack stack) {
        bookColor = BookColor.of(stack);
        setupPages(stack);
    }

    protected void updatePage(int pageIndex) {
        int newSpreadIndex = pageIndex / 2;
        if (currentSpread != newSpreadIndex) {
            currentSpread = newSpreadIndex;

            // Ensure we have pages to display:
            while (this.pages.size() < (currentSpread + 1) * 2) {
                appendEmptyPage();
            }

            setTextBoxes();
            updateButtons();
        }
        saveChanges(false, null);
    }

    @Override
    protected void sendChanges(@Nullable String title) {
        removeEmptyTrailingPages();
        // Copying 'pages' list to not cause ConcurrentModificationException when packet is encoded:
        Packets.sendToServer(new LecternEditBookC2SP(getMenu().getLecternPos(), new ArrayList<>(pages), Optional.ofNullable(title)));
    }

    @Override
    protected void signBook(BookSignature signature) {
        saveChanges(true, signature.title());
        signature.customAuthor().ifPresent(customAuthor ->
              Packets.sendToServer(new SetCustomAuthorOnLecternC2SP(getMenu().getLecternPos(), customAuthor)));
    }

    // --

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderPageTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderLeftPageNumber(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, int currentSpread, int color) {
        if (isRightPage(getMenu().getPage()) && isHoveringOverLeftPageNumber(mouseX, mouseY)) {
            color = textColor;
        }
        super.renderLeftPageNumber(guiGraphics, mouseX, mouseY, partialTick, currentSpread, color);
    }

    @Override
    protected void renderRightPageNumber(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, int currentSpread, int color) {
        if (isLeftPage(getMenu().getPage()) && isHoveringOverRightPageNumber(mouseX, mouseY)) {
            color = textColor;
        }
        super.renderRightPageNumber(guiGraphics, mouseX, mouseY, partialTick, currentSpread, color);
    }

    protected void renderPageTooltip(GuiGraphics guiGraphics, int x, int y) {
        int page = getMenu().getPage();

        if ((isRightPage(page) && isHoveringOverLeftPageNumber(x, y))
                || (isLeftPage(page) && isHoveringOverRightPageNumber(x, y))) {
            guiGraphics.renderTooltip(font, Component.translatable("gui.scholar.lectern.set_current_page"), x, y);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int page = getMenu().getPage();
        if (isRightPage(page) && isHoveringOverLeftPageNumber(mouseX, mouseY)) {
            sendButtonClick(LecternMenu.BUTTON_PAGE_JUMP_RANGE_START + page - 1);
        } else if (isLeftPage(page) && isHoveringOverRightPageNumber(mouseX, mouseY)) {
            sendButtonClick(LecternMenu.BUTTON_PAGE_JUMP_RANGE_START + page + 1);
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    // --

    @Override
    public void setFocused(@Nullable GuiEventListener focused) {
        super.setFocused(focused);
        saveChanges(false, null);
    }

    @Override
    public void removed() {
        super.removed();
        getMenu().removeSlotListener(listener);
    }

    @Override
    public void onClose() {
        super.onClose();
        player.closeContainer();
    }

    // --

    protected void sendButtonClick(int buttonId) {
        if (Minecraft.getInstance().gameMode != null) {
            saveChanges(false, null);
            Minecraft.getInstance().gameMode.handleInventoryButtonClick(this.menu.containerId, buttonId);
        }
    }
}
