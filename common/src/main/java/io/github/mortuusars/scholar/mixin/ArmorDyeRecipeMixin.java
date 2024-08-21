package io.github.mortuusars.scholar.mixin;

import io.github.mortuusars.scholar.Config;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.ArmorDyeRecipe;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Since we made WritableBookItem and WrittenBookItem to be DyeableLeatherItem, this recipe will automatically match them if crafted.
 * So we need to disable 'matches' and 'assemble' if coloring was disabled in config.
 * Also, all books are now dyeable, not just vanilla ones.
 * This may cause unwanted behaviour for mods that add their variants of Writable and Written books.
 */
@Mixin(ArmorDyeRecipe.class)
public class ArmorDyeRecipeMixin {
    @Inject(method = "matches(Lnet/minecraft/world/inventory/CraftingContainer;Lnet/minecraft/world/level/Level;)Z", at = @At("HEAD"), cancellable = true)
    private void onMatches(CraftingContainer container, Level level, CallbackInfoReturnable<Boolean> cir) {
        for (ItemStack stack : container.getItems()) {
            if (stack.is(Items.WRITABLE_BOOK) && !Config.Common.WRITABLE_BOOK_COLORING.get()) {
                cir.setReturnValue(false);
                return;
            }

            if (stack.is(Items.WRITTEN_BOOK) && !Config.Common.WRITTEN_BOOK_COLORING.get()) {
                cir.setReturnValue(false);
                return;
            }
        }
    }

    @Inject(method = "assemble(Lnet/minecraft/world/inventory/CraftingContainer;Lnet/minecraft/core/RegistryAccess;)Lnet/minecraft/world/item/ItemStack;", at = @At("HEAD"), cancellable = true)
    private void onAssemble(CraftingContainer container, RegistryAccess registryAccess, CallbackInfoReturnable<ItemStack> cir) {
        for (ItemStack stack : container.getItems()) {
            if (stack.is(Items.WRITABLE_BOOK) && !Config.Common.WRITABLE_BOOK_COLORING.get()) {
                cir.setReturnValue(ItemStack.EMPTY);
                return;
            }

            if (stack.is(Items.WRITTEN_BOOK) && !Config.Common.WRITTEN_BOOK_COLORING.get()) {
                cir.setReturnValue(ItemStack.EMPTY);
                return;
            }
        }
    }
}
