package io.github.mortuusars.scholar.client.gui.screen.edit;

import io.github.mortuusars.scholar.Scholar;
import io.github.mortuusars.scholar.client.gui.Widgets;
import io.github.mortuusars.scholar.client.gui.widget.BookmarkButton;
import io.github.mortuusars.scholar.network.Packets;
import io.github.mortuusars.scholar.network.packet.server.SetBookmarkC2SP;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundEditBookPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;

public class InHandSpreadBookEditScreen extends SpreadBookEditScreen {
    protected final InteractionHand hand;

    protected BookmarkButton bookmarkButton;

    public InHandSpreadBookEditScreen(ItemStack bookStack, InteractionHand hand) {
        super(bookStack);
        this.hand = hand;
        int bookmarkedPage = bookStack.getOrDefault(Scholar.DataComponents.BOOKMARK, 0);
        setPage(bookmarkedPage);
    }

    @Override
    protected void createExtraToolsButtons() {
        super.createExtraToolsButtons();
        bookmarkButton = addRenderableWidget(new BookmarkButton(leftPos + 118, topPos + 2, 20, 20,
              Widgets.threeStateSprites(Scholar.resource("book/bookmark_button_inactive")),
              Widgets.threeStateSprites(Scholar.resource("book/bookmark_button_active")),
              this::pressBookmarkButton,
              Component.translatable("gui.scholar.bookmark")));
    }

    @Override
    protected void updateButtons() {
        super.updateButtons();
        int bookmarkedSpread = bookStack.getOrDefault(Scholar.DataComponents.BOOKMARK, 0) / 2;
        bookmarkButton.visible = currentSpread != 0 || bookmarkedSpread != 0;
        if (bookmarkButton.visible) {
            boolean isAtBookmarkedSpread = bookmarkedSpread == currentSpread;
            bookmarkButton.setExpanded(isAtBookmarkedSpread);
            bookmarkButton.setTooltip(Tooltip.create(
                  Component.translatable("gui.scholar.bookmark." + (isAtBookmarkedSpread ? "remove" : "set"))));
        }
    }

    protected void pressBookmarkButton(Button button) {
        int bookmarkedSpread = bookStack.getOrDefault(Scholar.DataComponents.BOOKMARK, 0) / 2;

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
            bookStack.remove(Scholar.DataComponents.BOOKMARK);
        } else {
            bookStack.set(Scholar.DataComponents.BOOKMARK, newBookmarkedPage);
        }

        int slot = hand == InteractionHand.MAIN_HAND ? player.getInventory().selected : Inventory.SLOT_OFFHAND;
        Packets.sendToServer(new SetBookmarkC2SP(slot, newBookmarkedPage));
    }

    @Override
    protected void sendChanges(@Nullable String title) {
        int slot = hand == InteractionHand.MAIN_HAND ? player.getInventory().selected : Inventory.SLOT_OFFHAND;
        Objects.requireNonNull(minecraft.getConnection()).send(
              new ServerboundEditBookPacket(slot, this.pages, Optional.ofNullable(title)));
    }
}
