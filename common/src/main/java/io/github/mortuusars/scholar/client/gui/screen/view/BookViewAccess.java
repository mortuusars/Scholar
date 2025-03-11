package io.github.mortuusars.scholar.client.gui.screen.view;

import com.google.common.collect.ImmutableList;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.WritableBookItem;
import net.minecraft.world.item.WrittenBookItem;
import org.jetbrains.annotations.NotNull;

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

        public WritableBookAccess(ItemStack itemStack) {
            this.pages = readPages(itemStack);
        }

        private static List<String> readPages(ItemStack itemStack) {
            CompoundTag compoundTag = itemStack.getTag();
            return (compoundTag != null ? loadPages(compoundTag) : ImmutableList.of());
        }

        public int getPageCount() {
            return this.pages.size();
        }

        public FormattedText getPageRaw(int i) {
            return FormattedText.of(this.pages.get(i));
        }
    }

    class WrittenBookAccess implements BookViewAccess {
        private final List<String> pages;

        public WrittenBookAccess(ItemStack itemStack) {
            this.pages = readPages(itemStack);
        }

        private static List<String> readPages(ItemStack itemStack) {
            CompoundTag compoundTag = itemStack.getTag();
            return (WrittenBookItem.makeSureTagIsValid(compoundTag) ?
                    loadPages(compoundTag) :
                    ImmutableList.of(Component.Serializer.toJson(Component.translatable("book.invalid.tag")
                            .withStyle(ChatFormatting.DARK_RED))));
        }

        public int getPageCount() {
            return this.pages.size();
        }

        public @NotNull FormattedText getPageRaw(int i) {
            String string = this.pages.get(i);

            try {
                FormattedText formattedText = Component.Serializer.fromJson(string);
                if (formattedText != null) {
                    return formattedText;
                }
            } catch (Exception ignored) {
            }

            return FormattedText.of(string);
        }
    }
}
