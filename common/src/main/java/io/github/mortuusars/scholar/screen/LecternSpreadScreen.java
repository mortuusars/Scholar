package io.github.mortuusars.scholar.screen;

import io.github.mortuusars.scholar.Config;
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

public class LecternSpreadScreen extends SpreadBookViewScreen implements MenuAccess<LecternSpreadMenu> {
    protected final LecternSpreadMenu menu;
    protected final ContainerListener listener = new ContainerListener(){

        @Override
        public void slotChanged(AbstractContainerMenu containerToSend, int dataSlotIndex, ItemStack stack) {
            LecternSpreadScreen.this.bookChanged();
        }

        @Override
        public void dataChanged(AbstractContainerMenu containerMenu, int dataSlotIndex, int value) {
            if (dataSlotIndex == 0) {
                LecternSpreadScreen.this.pageChanged();
            }
        }
    };

    public LecternSpreadScreen(LecternSpreadMenu lecternSpreadMenu, Inventory inventory, Component component) {
        super(SpreadBookViewScreen.BookAccess.fromItem(lecternSpreadMenu.getBook()), lecternSpreadMenu.getBookColor());
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
    public void onClose() {
        closeScreen();
        super.onClose();
    }

    @Override
    public void removed() {
        super.removed();
        this.menu.removeSlotListener(this.listener);
    }

    @Override
    protected void createMenuControls() {
        if (Minecraft.getInstance().player != null && Minecraft.getInstance().player.mayBuild()) {
            if (Config.Client.LECTERN_SHOW_DONE_BUTTON.get()) {
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
            super.createMenuControls();
        }
    }

    @Override
    protected void pageBack() {
        this.sendButtonClick(1);
    }

    @Override
    protected void pageForward() {
        this.sendButtonClick(2);
    }

    @Override
    protected boolean forcePage(int spreadNum) {
        if (spreadNum != this.menu.getPage()) {
            this.sendButtonClick(100 + spreadNum);
            return true;
        }
        return false;
    }

    private void sendButtonClick(int pageData) {
        if (Minecraft.getInstance().gameMode != null)
            Minecraft.getInstance().gameMode.handleInventoryButtonClick(this.menu.containerId, pageData);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    void bookChanged() {
        ItemStack itemStack = this.menu.getBook();
        this.setBookAccess(SpreadBookViewScreen.BookAccess.fromItem(itemStack));
    }

    void pageChanged() {
        this.setPage(this.menu.getPage());
    }

    @Override
    protected void closeScreen() {
        if (Minecraft.getInstance().player != null)
            Minecraft.getInstance().player.closeContainer();
    }
}
