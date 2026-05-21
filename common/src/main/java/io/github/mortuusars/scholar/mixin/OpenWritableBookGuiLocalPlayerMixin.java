package io.github.mortuusars.scholar.mixin;

import com.mojang.authlib.GameProfile;
import io.github.mortuusars.scholar.Config;
import io.github.mortuusars.scholar.client.gui.screen.edit.InHandSpreadBookEditScreen;
import io.github.mortuusars.scholar.client.gui.screen.edit.SpreadBookEditScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocalPlayer.class)
public abstract class OpenWritableBookGuiLocalPlayerMixin extends Player {
    public OpenWritableBookGuiLocalPlayerMixin(Level level, GameProfile gameProfile) {
        super(level, gameProfile);
    }

    @Inject(method = "openItemGui", at = @At("HEAD"), cancellable = true)
    private void onOpenItemGui(ItemStack stack, InteractionHand hand, CallbackInfo ci) {
        if (!Config.Common.IN_HAND_TWO_PAGE_BOOK_SCREEN.get()) return;
        if (!stack.is(Items.WRITABLE_BOOK)) return;
        if (Config.Common.SNEAK_OPENS_VANILLA_BOOK_SCREEN.get() && isSecondaryUseActive()) return;

        Minecraft.getInstance().setScreen(new InHandSpreadBookEditScreen(stack, hand));
        ci.cancel();
    }
}
