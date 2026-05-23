package io.github.mortuusars.scholar.mixin;

import io.github.mortuusars.scholar.Config;
import io.github.mortuusars.scholar.client.gui.screen.view.BookViewAccess;
import io.github.mortuusars.scholar.book.BookColor;
import io.github.mortuusars.scholar.client.gui.screen.view.InHandSpreadBookViewScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundOpenBookPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public abstract class HandleOpenBookClientPacketListenerMixin {
    @Inject(method = "handleOpenBook", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/player/LocalPlayer;getItemInHand(Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/item/ItemStack;"),
            cancellable = true)
    private void handleOpenBook(ClientboundOpenBookPacket packet, CallbackInfo ci) {
        if (Minecraft.getInstance().player == null) return;
        if (!Config.Common.IN_HAND_TWO_PAGE_BOOK_SCREEN.get()) return;
        if (Config.Common.SNEAKING_OPENS_VANILLA_BOOK_SCREEN.get() && Minecraft.getInstance().player.isSecondaryUseActive()) return;

        InteractionHand hand = packet.getHand();
        ItemStack stack = Minecraft.getInstance().player.getItemInHand(hand);

        if (!stack.is(Items.WRITTEN_BOOK)) return;

        Minecraft.getInstance().setScreen(new InHandSpreadBookViewScreen(
              BookViewAccess.fromItem(stack),
              BookColor.of(stack),
              packet.getHand()));

        ci.cancel();
    }
}