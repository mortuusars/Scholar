package io.github.mortuusars.scholar.client.screen;

import io.github.mortuusars.scholar.Config;
import io.github.mortuusars.scholar.book.BookColor;
import io.github.mortuusars.scholar.menu.LecternSpreadMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class LecternSpreadBookViewScreen extends SpreadBookViewScreen implements MenuAccess<LecternSpreadMenu> {
    protected final LecternSpreadMenu menu;
    protected final ContainerListener listener = new ContainerListener(){
        @Override
        public void slotChanged(AbstractContainerMenu menu, int dataSlotIndex, ItemStack stack) {
            LecternSpreadBookViewScreen.this.bookChanged();
        }

        @Override
        public void dataChanged(AbstractContainerMenu menu, int dataSlotIndex, int value) {
            if (dataSlotIndex == 0) {
                LecternSpreadBookViewScreen.this.pageChanged();
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
        this.menu.addSlotListener(this.listener);
    }

    @Override
    protected void createBottomButtons() {
        if (player.mayBuild()) {
            if (Config.Client.SHOW_DONE_BUTTON.get()) {
                this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE,
                        button -> this.onClose()).bounds(this.width / 2 - 100, topPos + BOOK_HEIGHT + 12, 98, 20).build());
                this.addRenderableWidget(Button.builder(Component.translatable("lectern.take_book"),
                        button -> this.sendButtonClick(3)).bounds(this.width / 2 + 2, topPos + BOOK_HEIGHT + 12, 98, 20).build());
            }
            else {
                this.addRenderableWidget(Button.builder(Component.translatable("lectern.take_book"),
                        (button) -> this.sendButtonClick(3)).bounds(this.width / 2 - 60, topPos + BOOK_HEIGHT + 12, 120, 20).build());
            }
        } else {
            super.createBottomButtons();
        }
    }

    @Override
    protected boolean pageBack() {
        this.sendButtonClick(1);
        return true;
    }

    @Override
    protected boolean pageForward() {
        this.sendButtonClick(2);
        return true;
    }

    @Override
    public boolean setPage(int pageIndex) {
        if (pageIndex != this.menu.getPage()) {
            this.sendButtonClick(100 + pageIndex);
            return true;
        }
        return false;
    }

    protected void bookChanged() {
        ItemStack itemStack = this.menu.getBook();
        this.setBookAccess(BookViewAccess.fromItem(itemStack));
    }

    protected void pageChanged() {
        this.setPage(this.menu.getPage());
    }

    // --

    @Override
    public void removed() {
        super.removed();
        this.menu.removeSlotListener(this.listener);
    }

    @Override
    public void onClose() {
        player.closeContainer();
        super.onClose();
    }

    protected void sendButtonClick(int pageData) {
        if (Minecraft.getInstance().gameMode != null)
            Minecraft.getInstance().gameMode.handleInventoryButtonClick(this.menu.containerId, pageData);
    }
}
