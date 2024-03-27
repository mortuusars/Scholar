package io.github.mortuusars.scholar.mixin;

import io.github.mortuusars.scholar.Config;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.WrittenBookItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WrittenBookItem.class)
public abstract class WrittenBookItemMixin {
    @Inject(method = "isFoil", at = @At("HEAD"), cancellable = true)
    private void onIsFoil(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (!Config.Common.WRITTEN_GLINT_ENABLED.get())
            cir.setReturnValue(false);
    }
}
