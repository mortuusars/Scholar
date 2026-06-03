package io.github.mortuusars.scholar.world.entity;

import io.github.mortuusars.scholar.Config;
import io.github.mortuusars.scholar.Scholar;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.EnderMan;
import org.jetbrains.annotations.Nullable;

public class Reading {
    public static @Nullable InteractionHand getOpenedBookHand(LivingEntity entity) {
        if (!Config.Common.BOOK_READING_ANIMATION.get()) return null;
        if (entity instanceof EnderMan) return null; // They cannot hold items as other mobs do.
        if (entity.getMainHandItem().has(Scholar.DataComponents.BOOK_OPEN)) return InteractionHand.MAIN_HAND;
        if (entity.getOffhandItem().has(Scholar.DataComponents.BOOK_OPEN)) return InteractionHand.OFF_HAND;
        return null;
    }
}
