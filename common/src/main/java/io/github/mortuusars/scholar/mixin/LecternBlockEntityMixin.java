package io.github.mortuusars.scholar.mixin;

import io.github.mortuusars.scholar.item.IColoredBook;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LecternBlockEntity.class)
public class LecternBlockEntityMixin {
    @Shadow
    ItemStack book;

    @Inject(method = "hasBook", at = @At("HEAD"), cancellable = true)
    private void onHasBook(CallbackInfoReturnable<Boolean> cir) {
        if (book.getItem() instanceof IColoredBook)
            cir.setReturnValue(true);
    }
}
