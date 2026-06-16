package io.github.mortuusars.scholar.world.entity;

import io.github.mortuusars.scholar.Config;
import io.github.mortuusars.scholar.Scholar;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.WritableBookItem;
import net.minecraft.world.item.WrittenBookItem;
import org.jetbrains.annotations.Nullable;

public class Reading {
    public static @Nullable InteractionHand getOpenedBookHand(LivingEntity entity) {
        if (!Config.Common.BOOK_READING_ANIMATION.get()) return null;
        if (entity instanceof EnderMan) return null; // They cannot hold items as other mobs do.
        if (entity.getMainHandItem().has(Scholar.DataComponents.BOOK_OPEN)) return InteractionHand.MAIN_HAND;
        if (entity.getOffhandItem().has(Scholar.DataComponents.BOOK_OPEN)) return InteractionHand.OFF_HAND;
        return null;
    }

    public static @Nullable InteractionHand getOpenedBookHand(ItemStack leftHandStack, ItemStack rightHandStack, boolean rightHanded) {
        if (!Config.Common.BOOK_READING_ANIMATION.get()) return null;
        ItemStack mainHandStack = rightHanded ? rightHandStack : leftHandStack;
        ItemStack offHandStack = !rightHanded ? rightHandStack : leftHandStack;
        if (mainHandStack.has(Scholar.DataComponents.BOOK_OPEN)) return InteractionHand.MAIN_HAND;
        if (offHandStack.has(Scholar.DataComponents.BOOK_OPEN)) return InteractionHand.OFF_HAND;
        return null;
    }

    public static void closeAllBooksInInventory(ServerPlayer player) {
        player.getInventory().getNonEquipmentItems().forEach(stack -> {
            if (stack.getItem() instanceof WritableBookItem || stack.getItem() instanceof WrittenBookItem) {
                stack.remove(Scholar.DataComponents.BOOK_OPEN);
            }
        });
    }
}
