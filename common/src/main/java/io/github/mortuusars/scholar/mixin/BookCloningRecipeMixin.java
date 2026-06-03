package io.github.mortuusars.scholar.mixin;

import io.github.mortuusars.scholar.Scholar;
import io.github.mortuusars.scholar.book.BookColor;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.WrittenBookContent;
import net.minecraft.world.item.crafting.BookCloningRecipe;
import net.minecraft.world.item.crafting.CraftingInput;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BookCloningRecipe.class)
public class BookCloningRecipeMixin {
    // 'BookCloningRecipe#matches' may need to be changed as well. But it seems to work fine without it.

    /**
     * Purpose of this mixin is to disallow writable books of different colors to be in a craft.
     * And set the color of a result book to color of writable book.
     * -
     * This mixin basically overrides whole method which may cause compatibility issues. But it would require more mixins to change it in specific parts.
     * Mods that modify this recipe should be very rare anyway.
     */
    @Inject(method = "assemble(Lnet/minecraft/world/item/crafting/CraftingInput;Lnet/minecraft/core/HolderLookup$Provider;)Lnet/minecraft/world/item/ItemStack;",
            at = @At("HEAD"), cancellable = true)
    private void onAssemble(CraftingInput input, HolderLookup.Provider registries, CallbackInfoReturnable<ItemStack> cir) {
        ItemStack inputBook = ItemStack.EMPTY;
        @Nullable Integer resultColor = null;
        int copies = 0;

        for (int slot = 0; slot < input.size(); ++slot) {
            ItemStack stack = input.getItem(slot);
            if (stack.isEmpty()) {
                continue;
            }

            if (stack.is(Items.WRITTEN_BOOK)) {
                if (!inputBook.isEmpty()) {
                    cir.setReturnValue(ItemStack.EMPTY);
                    return; // multiple written books as inputs are not allowed
                }

                inputBook = stack;
                continue;
            }

            if (stack.is(Items.WRITABLE_BOOK)) {
                // Since result book will get writable book color - books of different colors are not allowed.
                int color = BookColor.of(stack);
                if (resultColor == null) {
                    resultColor = color;
                }
                else if (resultColor != color) {
                    cir.setReturnValue(ItemStack.EMPTY);
                    return;
                }

                ++copies;
                continue;
            }
            cir.setReturnValue(ItemStack.EMPTY);
            return;
        }

        if (inputBook.isEmpty() || copies < 1) {
            cir.setReturnValue(ItemStack.EMPTY);
            return; // Cannot be copied
        }

        @Nullable WrittenBookContent content = inputBook.getOrDefault(DataComponents.WRITTEN_BOOK_CONTENT, WrittenBookContent.EMPTY).tryCraftCopy();
        if (content == null) {
            cir.setReturnValue(ItemStack.EMPTY);
            return; // Cannot be copied
        }

        ItemStack resultStack = inputBook.copyWithCount(copies);
        resultStack.set(DataComponents.WRITTEN_BOOK_CONTENT, content);
        resultStack.remove(Scholar.DataComponents.BOOK_GOLDEN);
        BookColor.set(resultStack, resultColor);
        cir.setReturnValue(resultStack);
    }
}
