package io.github.mortuusars.scholar.mixin.literate_mobs;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import io.github.mortuusars.scholar.Scholar;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Entity.class)
public class EntityMixin {
    @ModifyReturnValue(method = "spawnAtLocation(Lnet/minecraft/world/item/ItemStack;F)Lnet/minecraft/world/entity/item/ItemEntity;",
          at = @At("RETURN"))
    private ItemEntity onSpawnAtLocation(ItemEntity original) {
        if (original != null) {
            original.getItem().remove(Scholar.DataComponents.BOOK_OPEN);
        }
        return original;
    }
}
