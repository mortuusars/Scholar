package io.github.mortuusars.scholar;

import io.github.mortuusars.scholar.item.ColoredWritableBookItem;
import io.github.mortuusars.scholar.screen.SpreadBookEditScreen;
import io.github.mortuusars.scholar.screen.SpreadBookViewScreen;
import io.github.mortuusars.scholar.visual.BookColors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.BookEditScreen;
import net.minecraft.client.gui.screens.inventory.BookViewScreen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.FilteredText;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.WritableBookItem;
import net.minecraft.world.item.WrittenBookItem;
import org.apache.logging.log4j.util.TriConsumer;

import java.util.List;
import java.util.function.UnaryOperator;

public class ScholarBookHandler {
    public static boolean handleBookOpening(Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);

        if (itemStack.getItem() instanceof WritableBookItem) {
            if (Config.Client.WRITABLE_REPLACE_VANILLA_SCREEN.get()
                    && (!Config.Client.WRITABLE_SNEAK_OPENS_VANILLA_SCREEN.get() || !player.isSecondaryUseActive())) {
                Minecraft.getInstance().setScreen(new SpreadBookEditScreen(player, itemStack, hand));
            }
            else
                Minecraft.getInstance().setScreen(new BookEditScreen(player, itemStack, hand));

            return true;
        }

        if (itemStack.getItem() instanceof WrittenBookItem) {
            if (Config.Client.WRITTEN_REPLACE_VANILLA_SCREEN.get()
                    && (!Config.Client.WRITTEN_SNEAK_OPENS_VANILLA_SCREEN.get() || !player.isSecondaryUseActive())) {
                Minecraft.getInstance().setScreen(new SpreadBookViewScreen(new SpreadBookViewScreen.WrittenBookAccess(itemStack), BookColors.fromStack(itemStack)));
            }
            else
                Minecraft.getInstance().setScreen(new BookViewScreen(new BookViewScreen.WrittenBookAccess(itemStack)));

            return true;
        }

        return false;
    }

    public static boolean handleBookSigning(ServerPlayer player, ItemStack editableBook, FilteredText title, List<FilteredText> pages, int slot,
                                            TriConsumer<List<FilteredText>, UnaryOperator<String>, ItemStack> bookPagesUpdater) {
        if (!(editableBook.getItem() instanceof ColoredWritableBookItem writableBookItem))
            return false;

        ItemStack signedBook = writableBookItem.createWrittenBook(editableBook);

        CompoundTag tag = editableBook.getTag();
        if (tag != null)
            signedBook.setTag(tag.copy());

        signedBook.addTagElement("author", StringTag.valueOf(player.getName().getString()));
        if (player.isTextFilteringEnabled()) {
            signedBook.addTagElement("title", StringTag.valueOf(title.filteredOrEmpty()));
        } else {
            signedBook.addTagElement("filtered_title", StringTag.valueOf(title.filteredOrEmpty()));
            signedBook.addTagElement("title", StringTag.valueOf(title.raw()));
        }

        bookPagesUpdater.accept(pages, s -> Component.Serializer.toJson(Component.literal(s)), signedBook);
        player.getInventory().setItem(slot, signedBook);
        return true;
    }

    public static boolean handleBookContentsUpdating(ServerPlayer player, ItemStack editableBook, List<FilteredText> pages,
                                                     int slot, TriConsumer<List<FilteredText>, UnaryOperator<String>, ItemStack> bookPagesUpdater) {
        if (!(editableBook.getItem() instanceof ColoredWritableBookItem))
            return false;

        bookPagesUpdater.accept(pages, UnaryOperator.identity(), editableBook);
        return true;
    }
}
