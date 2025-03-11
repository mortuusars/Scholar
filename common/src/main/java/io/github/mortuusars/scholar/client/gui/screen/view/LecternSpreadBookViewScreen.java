package io.github.mortuusars.scholar.client.gui.screen.view;

import io.github.mortuusars.scholar.Config;
import io.github.mortuusars.scholar.book.BookColor;
import io.github.mortuusars.scholar.menu.LecternSpreadMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.inventory.LecternMenu;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class LecternSpreadBookViewScreen extends SpreadBookViewScreen implements MenuAccess<LecternSpreadMenu> {
    protected final LecternSpreadMenu menu;
    protected final ContainerListener listener = new ContainerListener() {
        @Override
        public void slotChanged(AbstractContainerMenu menu, int dataSlotIndex, ItemStack stack) {
            LecternSpreadBookViewScreen.this.bookChanged(stack);
        }

        @Override
        public void dataChanged(AbstractContainerMenu menu, int dataSlotIndex, int pageIndex) {
            if (dataSlotIndex == 0) {
                LecternSpreadBookViewScreen.this.updatePage(pageIndex);
            }
        }
    };

    public LecternSpreadBookViewScreen(LecternSpreadMenu lecternSpreadMenu, Inventory inventory, Component component) {
        super(BookViewAccess.fromItem(lecternSpreadMenu.getBook()), BookColor.of(lecternSpreadMenu.getBook()));
        this.menu = lecternSpreadMenu;
    }

    @Override
    public @NotNull LecternSpreadMenu getMenu() {
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
            if (Config.Client.SHOW_DONE_BUTTON.get()) {
                this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE,
                        button -> this.onClose()).bounds(this.width / 2 - 100, topPos + BOOK_HEIGHT + 12, 98, 20).build());
                this.addRenderableWidget(Button.builder(Component.translatable("lectern.take_book"),
                        button -> this.sendButtonClick(3)).bounds(this.width / 2 + 2, topPos + BOOK_HEIGHT + 12, 98, 20).build());
            } else {
                this.addRenderableWidget(Button.builder(Component.translatable("lectern.take_book"),
                        (button) -> this.sendButtonClick(3)).bounds(this.width / 2 - 60, topPos + BOOK_HEIGHT + 12, 120, 20).build());
            }
        } else {
            super.createBottomButtons();
        }
    }

    @Override
    protected boolean pageBack() {
        this.sendButtonClick(LecternMenu.BUTTON_PREV_PAGE);
        return true;
    }

    @Override
    protected boolean pageForward() {
        this.sendButtonClick(LecternMenu.BUTTON_NEXT_PAGE);
        return true;
    }

    @Override
    public boolean setPage(int pageIndex) {
        if (pageIndex != getMenu().getPage()) {
            sendPageIndex(pageIndex);
            return true;
        }
        return false;
    }

    protected void bookChanged(ItemStack stack) {
        this.setBookAccess(BookViewAccess.fromItem(stack));
    }

    protected void updatePage(int pageIndex) {
        super.setPage(pageIndex);
    }

    // --

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
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
        int page = getMenu().getPage();
        if (page < getPageCount() - 1 && isLeftPage(page) && isHoveringOverRightPageNumber(mouseX, mouseY)) {
            color = textColor;
        }
        super.renderRightPageNumber(guiGraphics, mouseX, mouseY, partialTick, currentSpread, color);
    }

    protected void renderPageTooltip(GuiGraphics guiGraphics, int x, int y) {
        int page = getMenu().getPage();
        if ((isRightPage(page) && isHoveringOverLeftPageNumber(x, y))
                || (page < getPageCount() - 1 && isLeftPage(page) && isHoveringOverRightPageNumber(x, y))) {
            guiGraphics.renderTooltip(font, Component.translatable("gui.scholar.lectern.set_current_page"), x, y);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int page = getMenu().getPage();
        if (isRightPage(page) && isHoveringOverLeftPageNumber(mouseX, mouseY)) {
            sendButtonClick(LecternMenu.BUTTON_PAGE_JUMP_RANGE_START + page - 1);
        } else if (page < getPageCount() - 1 && isLeftPage(page) && isHoveringOverRightPageNumber(mouseX, mouseY)) {
            sendButtonClick(LecternMenu.BUTTON_PAGE_JUMP_RANGE_START + page + 1);
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    // --

    @Override
    public void removed() {
        super.removed();
        getMenu().removeSlotListener(listener);
    }

    @Override
    public void onClose() {
        player.closeContainer();
        super.onClose();
    }

    // --

    protected void sendPageIndex(int pageIndex) {
        sendButtonClick(100 + pageIndex);
    }

    protected void sendButtonClick(int buttonId) {
        if (Minecraft.getInstance().gameMode != null) {
            Minecraft.getInstance().gameMode.handleInventoryButtonClick(this.menu.containerId, buttonId);
        }
    }
}
