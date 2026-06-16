package io.github.mortuusars.scholar.client.gui.screen.edit;

import io.github.mortuusars.scholar.Scholar;
import io.github.mortuusars.scholar.book.BookSignature;
import io.github.mortuusars.scholar.client.gui.widget.BookmarkButton;
import io.github.mortuusars.scholar.client.gui.widget.textbox.TextBox;
import io.github.mortuusars.scholar.network.Packets;
import io.github.mortuusars.scholar.network.packet.serverbound.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundEditBookPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
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
        int bookmarkedPage = bookStack.getTag() != null ? bookStack.getTag().getInt(Scholar.NBT.BOOKMARK) : 0;
        setPage(bookmarkedPage);
        Packets.sendToServer(new StartedReadingInHandC2SP(getBookSlot()));
        Minecraft.getInstance().level.playSound(player, player, SoundEvents.BOOK_PAGE_TURN, SoundSource.PLAYERS, 1, 1);
    }

    @Override
    protected void sendGoldenSkinChange(boolean golden) {
        Packets.sendToServer(new SetGoldenSkinInHandC2SP(getBookSlot(), golden));
    }

    protected int getBookSlot() {
        return hand == InteractionHand.MAIN_HAND ? player.getInventory().selected : Inventory.SLOT_OFFHAND;
    }

    @Override
    protected void createExtraToolsButtons() {
        super.createExtraToolsButtons();
        bookmarkButton = addRenderableWidget(new BookmarkButton(leftPos + 118, topPos + 2, 20, 20,
              423, 0, 60, 512, 512, TEXTURE,
              this::pressBookmarkButton,
              Component.translatable("gui.scholar.bookmark")));
    }

    @Override
    protected void updateButtons() {
        super.updateButtons();
        int bookmarkedSpread = bookStack.getTag() != null ? bookStack.getTag().getInt(Scholar.NBT.BOOKMARK) / 2 : 0;
        bookmarkButton.visible = currentSpread != 0 || bookmarkedSpread != 0;
        if (bookmarkButton.visible) {
            boolean isAtBookmarkedSpread = bookmarkedSpread == currentSpread;
            bookmarkButton.setExpanded(isAtBookmarkedSpread);

            if (getFocused() instanceof TextBox textBox && textBox.getFormattingToolbar().shouldShow()) {
                // Crude fix to prevent two tooltips rendering at the same time.
                bookmarkButton.setTooltip(Tooltip.create(Component.empty()));
            } else {
                bookmarkButton.setTooltip(Tooltip.create(
                  Component.translatable("gui.scholar.bookmark." + (isAtBookmarkedSpread ? "remove" : "set"))));
            }
        }
    }

    protected void pressBookmarkButton(Button button) {
        int bookmarkedSpread = bookStack.getTag() != null ? bookStack.getTag().getInt(Scholar.NBT.BOOKMARK) / 2 : 0;

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
            if (bookStack.getTag() != null) {
                bookStack.getTag().remove(Scholar.NBT.BOOKMARK);
            }
        } else {
            bookStack.getOrCreateTag().putInt(Scholar.NBT.BOOKMARK, newBookmarkedPage);
        }

        int slot = getBookSlot();
        Packets.sendToServer(new SetBookmarkC2SP(slot, newBookmarkedPage));
    }

    @Override
    protected void sendChanges(@Nullable String title) {
        int slot = getBookSlot();
        Objects.requireNonNull(minecraft.getConnection()).send(
              new ServerboundEditBookPacket(slot, this.pages, Optional.ofNullable(title)));
    }

    @Override
    protected void signBook(BookSignature signature) {
        saveChanges(true, signature.title());
        signature.customAuthor().ifPresent(customAuthor ->
              Packets.sendToServer(new SetCustomAuthorInHandC2SP(getBookSlot(), customAuthor)));
        Packets.sendToServer(new StoppedReadingInHandC2SP(getBookSlot()));
    }

    @Override
    public void onClose() {
        super.onClose();
        Packets.sendToServer(new StoppedReadingInHandC2SP(getBookSlot()));
    }
}
