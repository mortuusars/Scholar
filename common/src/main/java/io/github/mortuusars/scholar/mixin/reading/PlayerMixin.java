package io.github.mortuusars.scholar.mixin.reading;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import io.github.mortuusars.scholar.Scholar;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Player.class)
public class PlayerMixin {
    @ModifyReturnValue(method = "drop(Lnet/minecraft/world/item/ItemStack;ZZ)Lnet/minecraft/world/entity/item/ItemEntity;", at = @At("RETURN"))
    private ItemEntity onDrop(@Nullable ItemEntity original) {
        if (original != null && original.getItem().getTag() != null) {
            original.getItem().getTag().remove(Scholar.NBT.BOOK_OPEN);
        }
        return original;
    }
}
