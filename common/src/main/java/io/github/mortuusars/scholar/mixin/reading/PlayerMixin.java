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
    @ModifyReturnValue(method = "drop", at = @At("RETURN"))
    private ItemEntity onDrop(@Nullable ItemEntity original) {
        if (original != null) {
            original.getItem().remove(Scholar.DataComponents.BOOK_OPEN);
        }
        return original;
    }
}
