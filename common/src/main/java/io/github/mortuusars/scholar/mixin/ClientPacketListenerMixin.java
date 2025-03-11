package io.github.mortuusars.scholar.mixin;

import io.github.mortuusars.scholar.Config;
import io.github.mortuusars.scholar.client.gui.screen.view.BookViewAccess;
import io.github.mortuusars.scholar.book.BookColor;
import io.github.mortuusars.scholar.client.gui.screen.view.SpreadBookViewScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundOpenBookPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public abstract class ClientPacketListenerMixin {
    @Inject(method = "handleOpenBook", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/player/LocalPlayer;getItemInHand(Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/item/ItemStack;"),
            cancellable = true)
    private void handleOpenBook(ClientboundOpenBookPacket clientboundOpenBookPacket, CallbackInfo ci) {
        InteractionHand hand = clientboundOpenBookPacket.getHand();
        Player player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }

        ItemStack stack = player.getItemInHand(hand);

        if (!Config.Common.TWO_PAGE_SCREEN.get() || !stack.is(Items.WRITTEN_BOOK)) {
            return;
        }

        if (Config.Common.SNEAK_OPENS_VANILLA_SCREEN.get() && player.isSecondaryUseActive()) {
            return;
        }

        Minecraft.getInstance().setScreen(new SpreadBookViewScreen(BookViewAccess.fromItem(stack), BookColor.of(stack)));
        ci.cancel();
    }
}