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

@Mixin(ArmorDyeRecipe.class)
public class ArmorDyeRecipeMixin {
    @Inject(method = "matches(Lnet/minecraft/world/inventory/CraftingContainer;Lnet/minecraft/world/level/Level;)Z", at = @At("HEAD"), cancellable = true)
    private void onMatches(CraftingContainer container, Level level, CallbackInfoReturnable<Boolean> cir) {
        for (ItemStack stack : container.getItems()) {
            if (stack.getItem() instanceof WritableBookItem && !Config.Common.WRITABLE_BOOK_COLORING.get()) {
                cir.setReturnValue(false);
                return;
            }

            if (stack.getItem() instanceof WrittenBookItem && !Config.Common.WRITTEN_BOOK_COLORING.get()) {
                cir.setReturnValue(false);
                return;
            }
        }
    }

    @Inject(method = "assemble(Lnet/minecraft/world/inventory/CraftingContainer;Lnet/minecraft/core/RegistryAccess;)Lnet/minecraft/world/item/ItemStack;", at = @At("HEAD"), cancellable = true)
    private void onAssemble(CraftingContainer container, RegistryAccess registryAccess, CallbackInfoReturnable<ItemStack> cir) {
        for (ItemStack stack : container.getItems()) {
            if (stack.getItem() instanceof WritableBookItem && !Config.Common.WRITABLE_BOOK_COLORING.get()) {
                cir.setReturnValue(ItemStack.EMPTY);
                return;
            }

            if (stack.getItem() instanceof WrittenBookItem && !Config.Common.WRITTEN_BOOK_COLORING.get()) {
                cir.setReturnValue(ItemStack.EMPTY);
                return;
            }
        }
    }
}
