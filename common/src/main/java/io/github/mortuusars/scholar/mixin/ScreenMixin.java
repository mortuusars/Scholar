package io.github.mortuusars.scholar.mixin;

import io.github.mortuusars.scholar.Config;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.BookEditScreen;
import net.minecraft.client.gui.screens.inventory.BookViewScreen;
import net.minecraft.client.gui.screens.inventory.LecternScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Screen.class)
public abstract class ScreenMixin {
    @Inject(method = "isPauseScreen", at = @At("HEAD"), cancellable = true)
    private void onIsPauseScreen(CallbackInfoReturnable<Boolean> cir) {
        Screen screen = (Screen) (Object) this;

        if (screen instanceof LecternScreen) {
            cir.setReturnValue(!Config.Client.LECTERN_PAUSE.get());
        }
        if (screen instanceof BookEditScreen) {
            cir.setReturnValue(!Config.Client.WRITABLE_PAUSE.get());
        }
        if (screen instanceof BookViewScreen) {
            cir.setReturnValue(!Config.Client.WRITTEN_PAUSE.get());
        }
    }
}
