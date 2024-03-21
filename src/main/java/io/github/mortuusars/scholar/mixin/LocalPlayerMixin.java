package io.github.mortuusars.scholar.mixin;

import com.mojang.authlib.GameProfile;
import io.github.mortuusars.scholar.ScholarBookHandler;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin extends Player {
    public LocalPlayerMixin(Level pLevel, BlockPos pPos, float pYRot, GameProfile pGameProfile) {
        super(pLevel, pPos, pYRot, pGameProfile);
    }

    @Inject(method = "openItemGui", at = @At("HEAD"), cancellable = true)
    private void onOpenItemGui(ItemStack stack, InteractionHand hand, CallbackInfo ci) {
        if (ScholarBookHandler.handleBookOpening(this, hand))
            ci.cancel();
    }
}
