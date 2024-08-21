package io.github.mortuusars.scholar.mixin;

import io.github.mortuusars.scholar.book.BookColor;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.WrittenBookItem;
import net.minecraft.world.item.crafting.BookCloningRecipe;
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
    @Inject(method = "assemble(Lnet/minecraft/world/inventory/CraftingContainer;Lnet/minecraft/core/RegistryAccess;)Lnet/minecraft/world/item/ItemStack;",
            at = @At("HEAD"), cancellable = true)
    private void onAssemble(CraftingContainer container, RegistryAccess registryAccess, CallbackInfoReturnable<ItemStack> cir) {
        ItemStack inputBook = ItemStack.EMPTY;
        @Nullable Integer resultColor = null;
        int copies = 0;
        for (int slot = 0; slot < container.getContainerSize(); ++slot) {
            ItemStack stack = container.getItem(slot);
            if (stack.isEmpty()) {
                continue;
            }

            if (stack.is(Items.WRITTEN_BOOK)) {
                if (!inputBook.isEmpty()) {
                    cir.setReturnValue(ItemStack.EMPTY);
                    return;
                }
                inputBook = stack;
                continue;
            }

            if (stack.is(Items.WRITABLE_BOOK)) {
                // Since result book will get writable book color - books of different colors are not allowed.
                int color = BookColor.get(stack);
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

        if (inputBook.isEmpty() || inputBook.getTag() == null || copies < 1 || WrittenBookItem.getGeneration(inputBook) >= 2) {
            cir.setReturnValue(ItemStack.EMPTY);
            return;
        }

        ItemStack resultStack = new ItemStack(Items.WRITTEN_BOOK, copies);
        CompoundTag inputBookTag = inputBook.getTag().copy();
        inputBookTag.putInt("generation", WrittenBookItem.getGeneration(inputBook) + 1);
        resultStack.setTag(inputBookTag);
        BookColor.set(resultStack, resultColor);
        cir.setReturnValue(resultStack);
    }
}
