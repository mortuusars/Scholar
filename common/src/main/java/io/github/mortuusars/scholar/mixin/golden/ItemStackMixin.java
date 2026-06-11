package io.github.mortuusars.scholar.mixin.golden;

import io.github.mortuusars.scholar.Scholar;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponentHolder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.component.TooltipProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin implements DataComponentHolder {
    @Inject(method = "addToTooltip", at = @At("HEAD"), cancellable = true)
    private <T extends TooltipProvider> void onAddToTooltip(DataComponentType<T> component, Item.TooltipContext context,
                                                            TooltipDisplay tooltipDisplay, Consumer<Component> tooltipAdder,
                                                            TooltipFlag tooltipFlag, CallbackInfo ci) {
        if (component == DataComponents.DYED_COLOR && has(Scholar.DataComponents.BOOK_GOLDEN)) {
            tooltipAdder.accept(Component.translatable("gui.scholar.golden").withStyle(ChatFormatting.GRAY));
            ci.cancel();
        }
    }
}
