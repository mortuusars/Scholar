package io.github.mortuusars.scholar;

import io.github.mortuusars.scholar.item.ColoredWritableBookItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.FilteredText;
import net.minecraft.world.item.ItemStack;
import org.apache.logging.log4j.util.TriConsumer;

import java.util.List;
import java.util.function.UnaryOperator;

public class BookHandlerServer {
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
