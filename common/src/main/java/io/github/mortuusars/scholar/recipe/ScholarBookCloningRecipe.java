package io.github.mortuusars.scholar.recipe;

import io.github.mortuusars.scholar.Scholar;
import io.github.mortuusars.scholar.item.ColoredWritableBookItem;
import io.github.mortuusars.scholar.item.ColoredWrittenBookItem;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.BookCloningRecipe;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Vanilla BookCloningRecipe is hardcoded to use vanilla writable/written book.
 * To support other books (colored) we need to check for type instead.
 */
public class ScholarBookCloningRecipe extends BookCloningRecipe {
    public ScholarBookCloningRecipe(ResourceLocation id, CraftingBookCategory category) {
        super(id, category);
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return Scholar.RecipeSerializers.BOOK_CLONING.get();
    }

    @Override
    public boolean matches(CraftingContainer inv, Level level) {
        int i = 0;
        ItemStack itemStack = ItemStack.EMPTY;
        for (int j = 0; j < inv.getContainerSize(); ++j) {
            ItemStack itemStack2 = inv.getItem(j);
            if (itemStack2.isEmpty()) continue;
            if (itemStack2.getItem() instanceof WrittenBookItem) {
                if (!itemStack.isEmpty()) {
                    return false;
                }
                itemStack = itemStack2;
                continue;
            }
            if (itemStack2.getItem() instanceof WritableBookItem) {
                ++i;
                continue;
            }
            return false;
        }
        return !itemStack.isEmpty() && itemStack.hasTag() && i > 0;
    }

    @Override
    public @NotNull ItemStack assemble(CraftingContainer container, RegistryAccess registryAccess) {
        int writableBooksCount = 0;
        ItemStack firstWritableBook = ItemStack.EMPTY;
        ItemStack itemStack = ItemStack.EMPTY;
        for (int j = 0; j < container.getContainerSize(); ++j) {
            ItemStack itemStack2 = container.getItem(j);
            if (itemStack2.isEmpty()) continue;
            if (itemStack2.getItem() instanceof WrittenBookItem) {
                if (!itemStack.isEmpty()) {
                    return ItemStack.EMPTY;
                }
                itemStack = itemStack2;
                continue;
            }
            if (itemStack2.getItem() instanceof WritableBookItem) {
                // When copying to multiple books we need to make sure they are all same type:
                if (firstWritableBook.isEmpty())
                    firstWritableBook = itemStack2;
                else if (itemStack2.getItem() != firstWritableBook.getItem())
                    return ItemStack.EMPTY;

                ++writableBooksCount;
                continue;
            }
            return ItemStack.EMPTY;
        }

        if (itemStack.isEmpty() || itemStack.getTag() == null || writableBooksCount < 1 || WrittenBookItem.getGeneration(itemStack) >= 2)
            return ItemStack.EMPTY;

        ItemStack resultBook = firstWritableBook.getItem() instanceof ColoredWritableBookItem coloredBook ?
                coloredBook.createWrittenBook(firstWritableBook) : new ItemStack(Items.WRITTEN_BOOK);
        resultBook.setCount(writableBooksCount);

        CompoundTag compoundTag = itemStack.getTag().copy();
        compoundTag.putInt("generation", WrittenBookItem.getGeneration(itemStack) + 1);
        resultBook.setTag(compoundTag);
        return resultBook;
    }
}
