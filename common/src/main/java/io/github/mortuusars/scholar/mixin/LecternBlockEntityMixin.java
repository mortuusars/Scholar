package io.github.mortuusars.scholar.mixin;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.WritableBookItem;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LecternBlockEntity.class)
public class LecternBlockEntityMixin {

//    @Shadow private int pageCount;
//
//    @Inject(method = "setBook(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/player/Player;)V",
//            at = @At(value = "INVOKE",
//                    target = "Lnet/minecraft/world/level/block/entity/LecternBlockEntity;setChanged()V"))
//    private void onSetBook(ItemStack stack, Player player, CallbackInfo ci) {
//        if (stack.getItem() instanceof WritableBookItem writableBook) {
//            this.pageCount = Wri
//        }
//    }
}
