package io.github.mortuusars.scholar.client.gui.screen.view;

import com.google.common.collect.ImmutableList;
import io.github.mortuusars.scholar.Scholar;
import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.WritableBookItem;
import net.minecraft.world.item.WrittenBookItem;
import net.minecraft.world.item.component.WritableBookContent;
import net.minecraft.world.item.component.WrittenBookContent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface BookViewAccess {
    BookViewAccess EMPTY = new BookViewAccess() {
        public int getPageCount() {
            return 0;
        }

        public Component getPageRaw(int i) {
            return CommonComponents.EMPTY;
        }

        @Override
        public boolean isGolden() {
            return false;
        }
    };

    boolean isGolden();
    int getPageCount();
    Component getPageRaw(int pageIndex);
    default int getBookmarkedPage() {
        return 0;
    }
    default void setBookmarkedPage(@Nullable Integer page) {}

    default Component getPage(int pageIndex) {
        return pageIndex >= 0 && pageIndex < getPageCount() ? getPageRaw(pageIndex) : CommonComponents.EMPTY;
    }

    static BookViewAccess fromItem(ItemStack itemStack) {
        if (itemStack.getItem() instanceof WrittenBookItem) {
            return new WrittenBookAccess(itemStack);
        } else if (itemStack.getItem() instanceof WritableBookItem) {
            return new WritableBookAccess(itemStack);
        } else {
            return EMPTY;
        }
    }

    class WritableBookAccess implements BookViewAccess {
        private final List<String> pages;
        private final ItemStack bookStack;

        public WritableBookAccess(ItemStack itemStack) {
            this.pages = readPages(itemStack);
            this.bookStack = itemStack;
        }

        @Override
        public boolean isGolden() {
            return bookStack.has(Scholar.DataComponents.BOOK_GOLDEN);
        }

        private static List<String> readPages(ItemStack itemStack) {
            WritableBookContent content = itemStack.getOrDefault(DataComponents.WRITABLE_BOOK_CONTENT, WritableBookContent.EMPTY);
            return content.getPages(Minecraft.getInstance().isTextFilteringEnabled()).toList();
        }

        public int getPageCount() {
            return 100;
        }

        public Component getPageRaw(int i) {
            return i < pages.size() ? Component.literal(pages.get(i)) : CommonComponents.EMPTY;
        }

        @Override
        public int getBookmarkedPage() {
            return bookStack.getOrDefault(Scholar.DataComponents.BOOKMARK, 0);
        }

        @Override
        public void setBookmarkedPage(@Nullable Integer page) {
            bookStack.set(Scholar.DataComponents.BOOKMARK, page);
        }
    }

    class WrittenBookAccess implements BookViewAccess {
        private final List<Component> pages;
        private final ItemStack bookStack;

        public WrittenBookAccess(ItemStack bookStack) {
            this.pages = readPages(bookStack);
            this.bookStack = bookStack;
        }

        @Override
        public boolean isGolden() {
            return bookStack.has(Scholar.DataComponents.BOOK_GOLDEN);
        }

        private static List<Component> readPages(ItemStack itemStack) {
            WrittenBookContent content = itemStack.getOrDefault(DataComponents.WRITTEN_BOOK_CONTENT, WrittenBookContent.EMPTY);
            return content.getPages(Minecraft.getInstance().isTextFilteringEnabled());
        }

        public int getPageCount() {
            return this.pages.size();
        }

        public @NotNull Component getPageRaw(int i) {
            return i < pages.size() ? pages.get(i) : CommonComponents.EMPTY;
        }

        @Override
        public int getBookmarkedPage() {
            return bookStack.getOrDefault(Scholar.DataComponents.BOOKMARK, 0);
        }

        @Override
        public void setBookmarkedPage(@Nullable Integer page) {
            bookStack.set(Scholar.DataComponents.BOOKMARK, page);
        }
    }
}
