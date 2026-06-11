package io.github.mortuusars.scholar.client.gui.screen.view;

import io.github.mortuusars.scholar.Scholar;
import io.github.mortuusars.scholar.client.gui.Widgets;
import io.github.mortuusars.scholar.client.gui.widget.BookmarkButton;
import io.github.mortuusars.scholar.network.Packets;
import io.github.mortuusars.scholar.network.packet.serverbound.SetBookmarkC2SP;
import io.github.mortuusars.scholar.network.packet.serverbound.StartedReadingInHandC2SP;
import io.github.mortuusars.scholar.network.packet.serverbound.StoppedReadingInHandC2SP;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;

public class InHandSpreadBookViewScreen extends SpreadBookViewScreen {
    protected final InteractionHand hand;
    protected BookmarkButton bookmarkButton;

    public InHandSpreadBookViewScreen(BookViewAccess bookAccess, int bookColor, InteractionHand hand) {
        super(bookAccess, bookColor);
        this.hand = hand;
        setPage(getBookAccess().getBookmarkedPage());
        Packets.sendToServer(new StartedReadingInHandC2SP(getBookSlot()));
        Minecraft.getInstance().level.playSound(player, player, SoundEvents.BOOK_PAGE_TURN, SoundSource.PLAYERS, 1, 1);
    }

    protected int getBookSlot() {
        return hand == InteractionHand.MAIN_HAND ? player.getInventory().getSelectedSlot() : Inventory.SLOT_OFFHAND;
    }

    @Override
    protected void createWidgets() {
        super.createWidgets();
        bookmarkButton = addRenderableWidget(new BookmarkButton(leftPos + 118, topPos + 2, 20, 20,
              Widgets.threeStateSprites(Scholar.resource("book/bookmark_button_inactive")),
              Widgets.threeStateSprites(Scholar.resource("book/bookmark_button_active")),
              this::pressBookmarkButton,
              Component.translatable("gui.scholar.bookmark")));
    }

    @Override
    protected void updateButtons() {
        super.updateButtons();
        int bookmarkedSpread = getBookAccess().getBookmarkedPage() / 2;
        bookmarkButton.visible = currentSpread != 0 || bookmarkedSpread != 0;
        if (bookmarkButton.visible) {
            boolean isAtBookmarkedSpread = bookmarkedSpread == currentSpread;
            bookmarkButton.setExpanded(isAtBookmarkedSpread);
            bookmarkButton.setTooltip(Tooltip.create(
                  Component.translatable("gui.scholar.bookmark." + (isAtBookmarkedSpread ? "remove" : "set"))));
        }
    }

    protected void pressBookmarkButton(Button button) {
        int bookmarkedSpread = getBookAccess().getBookmarkedPage() / 2;

        int newBookmarkedPage;

        if (bookmarkedSpread == currentSpread) {
            if (currentSpread == 0) {
                return;
            }
            newBookmarkedPage = 0;
        } else {
            newBookmarkedPage = currentSpread * 2;
        }

        if (newBookmarkedPage == 0) {
            getBookAccess().setBookmarkedPage(null);
        } else {
            getBookAccess().setBookmarkedPage(newBookmarkedPage);
        }

        int slot = hand == InteractionHand.MAIN_HAND ? player.getInventory().getSelectedSlot() : Inventory.SLOT_OFFHAND;
        Packets.sendToServer(new SetBookmarkC2SP(slot, newBookmarkedPage));
    }

    @Override
    public void onClose() {
        super.onClose();
        Packets.sendToServer(new StoppedReadingInHandC2SP(getBookSlot()));
    }
}
