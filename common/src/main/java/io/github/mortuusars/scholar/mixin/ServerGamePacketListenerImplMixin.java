package io.github.mortuusars.scholar.mixin;

import io.github.mortuusars.scholar.ScholarBookHandler;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.FilteredText;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.function.UnaryOperator;

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class ServerGamePacketListenerImplMixin {
    @Shadow public ServerPlayer player;
    @Shadow protected abstract void updateBookPages(List<FilteredText> pages, UnaryOperator<String> updater, ItemStack book);

    @Inject(method = "signBook", at = @At("HEAD"), cancellable = true)
    private void onSignBook(FilteredText title, List<FilteredText> pages, int slot, CallbackInfo ci) {
        ItemStack itemStack = this.player.getInventory().getItem(slot);
        if (ScholarBookHandler.handleBookSigning(player, itemStack, title, pages, slot, this::updateBookPages))
            ci.cancel();
    }

    @Inject(method = "updateBookContents", at = @At("HEAD"), cancellable = true)
    private void onUpdateBookContents(List<FilteredText> pages, int slot, CallbackInfo ci) {
        ItemStack itemStack = this.player.getInventory().getItem(slot);
        if (ScholarBookHandler.handleBookContentsUpdating(player, itemStack, pages, slot, this::updateBookPages))
            ci.cancel();
    }
}
