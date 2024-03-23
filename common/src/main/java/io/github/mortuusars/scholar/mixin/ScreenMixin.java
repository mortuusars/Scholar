package io.github.mortuusars.scholar.mixin;

import io.github.mortuusars.scholar.Config;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.BookEditScreen;
import net.minecraft.client.gui.screens.inventory.BookViewScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Screen.class)
public abstract class ScreenMixin {
    @Inject(method = "isPauseScreen", at = @At("HEAD"), cancellable = true)
    private void onIsPauseScreen(CallbackInfoReturnable<Boolean> cir) {
        Screen screen = (Screen) (Object) this;

        if (screen instanceof BookEditScreen && !Config.Client.BOOK_EDIT_SCREEN_PAUSE.get()) {
            cir.setReturnValue(false);
        }
        else if (screen instanceof BookViewScreen && !Config.Client.BOOK_VIEW_SCREEN_PAUSE.get()) {
            cir.setReturnValue(false);
        }
    }
}
