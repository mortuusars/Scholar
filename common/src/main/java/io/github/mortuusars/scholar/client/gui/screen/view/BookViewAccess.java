package io.github.mortuusars.scholar.client.gui.screen.view;

import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.WritableBookItem;
import net.minecraft.world.item.WrittenBookItem;
import net.minecraft.world.item.component.WritableBookContent;
import net.minecraft.world.item.component.WrittenBookContent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface BookViewAccess {
    BookViewAccess EMPTY = new BookViewAccess() {
        public int getPageCount() {
            return 0;
        }

        public FormattedText getPageRaw(int i) {
            return FormattedText.EMPTY;
        }
    };

    int getPageCount();
    FormattedText getPageRaw(int pageIndex);

    default FormattedText getPage(int pageIndex) {
        return pageIndex >= 0 && pageIndex < getPageCount() ? getPageRaw(pageIndex) : FormattedText.EMPTY;
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

        public WritableBookAccess(ItemStack itemStack) {
            this.pages = readPages(itemStack);
        }

        private static List<String> readPages(ItemStack itemStack) {
            WritableBookContent content = itemStack.getOrDefault(DataComponents.WRITABLE_BOOK_CONTENT, WritableBookContent.EMPTY);
            return content.getPages(Minecraft.getInstance().isTextFilteringEnabled()).toList();
        }

        public int getPageCount() {
            return 100;
        }

        public FormattedText getPageRaw(int i) {
            return i >= pages.size() ? FormattedText.EMPTY : FormattedText.of(this.pages.get(i));
        }
    }

    class WrittenBookAccess implements BookViewAccess {
        private final List<Component> pages;

        public WrittenBookAccess(ItemStack itemStack) {
            this.pages = readPages(itemStack);
        }

        private static List<Component> readPages(ItemStack itemStack) {
            WrittenBookContent content = itemStack.getOrDefault(DataComponents.WRITTEN_BOOK_CONTENT, WrittenBookContent.EMPTY);
            return content.getPages(Minecraft.getInstance().isTextFilteringEnabled());
        }

        public int getPageCount() {
            return this.pages.size();
        }

        public @NotNull FormattedText getPageRaw(int i) {
            return pages.get(i);
        }
    }
}
