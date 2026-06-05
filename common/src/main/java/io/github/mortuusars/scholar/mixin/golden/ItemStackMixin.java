package io.github.mortuusars.scholar.mixin.golden;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import io.github.mortuusars.scholar.Scholar;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
    @Shadow
    @Nullable
    public abstract CompoundTag getTag();

    @Inject(method = "getTooltipLines", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getHideFlags()I"))
    private void onGetTooltipLines(Player player, TooltipFlag isAdvanced, CallbackInfoReturnable<List<Component>> cir, @Local List<Component> lines) {
        if (getTag() != null && getTag().getBoolean(Scholar.NBT.BOOK_GOLDEN)) {
            lines.add(Component.translatable("gui.scholar.golden").withStyle(ChatFormatting.GRAY));
        }
    }

    @WrapOperation(method = "getTooltipLines", at = @At(value = "INVOKE",
          target = "Lnet/minecraft/world/item/ItemStack;shouldShowInTooltip(ILnet/minecraft/world/item/ItemStack$TooltipPart;)Z"))
    private boolean onShouldShowInTooltip(int hideFlags, ItemStack.TooltipPart part, Operation<Boolean> original, @Local List<Component> lines) {
        if (part == ItemStack.TooltipPart.DYE && getTag() != null && getTag().getBoolean(Scholar.NBT.BOOK_GOLDEN)) {
            return false;
        }

        return original.call(hideFlags, part);
    }
}
