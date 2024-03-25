package io.github.mortuusars.scholar.mixin;

import net.minecraft.client.gui.screens.inventory.BookViewScreen;
import net.minecraft.client.gui.screens.inventory.LecternScreen;
import net.minecraft.world.inventory.LecternMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.WritableBookItem;
import net.minecraft.world.item.WrittenBookItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LecternScreen.class)
public abstract class LecternScreenMixin extends BookViewScreen {
    @Shadow public abstract LecternMenu getMenu();

    /**
     * BookViewScreen.BookAccess#fromItem is hardcoded against Items.WRITABLE_BOOK and Items.WRITTEN_BOOK.
     * To make vanilla LecternScreen work with scholar colored books we need to check item type instead.
     */
    @Inject(method = "bookChanged", at = @At("HEAD"), cancellable = true)
    void onBookChanged(CallbackInfo ci) {
        ItemStack itemStack = getMenu().getBook();
        this.setBookAccess(scholar$fromItem(itemStack));
        ci.cancel();
    }

    @Unique
    private static BookAccess scholar$fromItem(ItemStack stack) {
        if (stack.getItem() instanceof WrittenBookItem) {
            return new WrittenBookAccess(stack);
        }
        if (stack.getItem() instanceof WritableBookItem) {
            return new WritableBookAccess(stack);
        }
        return EMPTY_ACCESS;
    }
}
