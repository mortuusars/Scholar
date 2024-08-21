package io.github.mortuusars.scholar.mixin;

import io.github.mortuusars.scholar.Config;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.WrittenBookItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Adds DyeableLeatherItem interface to written books. This makes it handle most of the coloring stuff automatically.
 */
@Mixin(WrittenBookItem.class)
public abstract class WrittenBookItemMixin implements DyeableLeatherItem {
    @Inject(method = "isFoil", at = @At("HEAD"), cancellable = true)
    private void onIsFoil(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (!Config.Common.BOOK_ENCHANTMENT_GLINT.get())
            cir.setReturnValue(false);
    }
}
