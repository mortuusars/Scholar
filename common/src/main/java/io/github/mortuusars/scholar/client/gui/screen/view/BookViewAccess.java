package io.github.mortuusars.scholar.client.gui.screen.view;

import com.google.common.collect.ImmutableList;
import io.github.mortuusars.scholar.Scholar;
import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
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
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.IntFunction;

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
    default int getBookmarkedPage() {
        return 0;
    }
    default void setBookmarkedPage(@Nullable Integer page) {}

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

    static List<String> loadPages(CompoundTag compoundTag) {
        ImmutableList.Builder<String> builder = ImmutableList.builder();
        Objects.requireNonNull(builder);
        loadPages(compoundTag, builder::add);
        return builder.build();
    }

    static void loadPages(CompoundTag compoundTag, Consumer<String> consumer) {
        ListTag listTag = compoundTag.getList("pages", 8).copy();
        IntFunction<String> intFunction;
        if (Minecraft.getInstance().isTextFilteringEnabled() && compoundTag.contains("filtered_pages", 10)) {
            CompoundTag compoundTag2 = compoundTag.getCompound("filtered_pages");
            intFunction = (ix) -> {
                String string = String.valueOf(ix);
                return compoundTag2.contains(string) ? compoundTag2.getString(string) : listTag.getString(ix);
            };
        } else {
            Objects.requireNonNull(listTag);
            intFunction = listTag::getString;
        }

        for (int i = 0; i < listTag.size(); ++i) {
            consumer.accept(intFunction.apply(i));
        }
    }

    class WritableBookAccess implements BookViewAccess {
        private final List<String> pages;
        private final ItemStack bookStack;

        public WritableBookAccess(ItemStack itemStack) {
            this.pages = readPages(itemStack);
            this.bookStack = itemStack;
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
