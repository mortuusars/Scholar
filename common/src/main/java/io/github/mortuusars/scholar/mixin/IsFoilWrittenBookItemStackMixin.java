package io.github.mortuusars.scholar.mixin;

import io.github.mortuusars.scholar.Config;
import net.minecraft.core.component.DataComponentHolder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public abstract class IsFoilWrittenBookItemStackMixin implements DataComponentHolder, ItemInstance {
    @Shadow public abstract Item getItem();

    @Inject(method = "hasFoil", at = @At("HEAD"), cancellable = true)
    private void onHasFoil(CallbackInfoReturnable<Boolean> cir) {
        if (is(Items.WRITTEN_BOOK) && Config.Common.WRITTEN_BOOK_ENCHANTMENT_GLINT.isFalse()) {
            cir.setReturnValue(getItem().isFoil((ItemStack)(Object)this));
        }
    }
}
