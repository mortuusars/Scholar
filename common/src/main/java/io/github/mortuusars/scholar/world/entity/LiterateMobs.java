package io.github.mortuusars.scholar.world.entity;

import io.github.mortuusars.scholar.Config;
import io.github.mortuusars.scholar.Scholar;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class LiterateMobs {
    public static void populateEquipmentSlots(RandomSource random, DifficultyInstance difficulty, Mob mob) {
        if (mob.getType().is(Scholar.Tags.EntityTypes.LITERATE)
              && random.nextDouble() < Config.Common.LITERATE_MOBS_BOOK_SPAWN_CHANCE.get()) {
            mob.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.BOOK));
            // This can potentially make drop chances of other items higher that they should be,
            // if code down the line will override the main hand slot without changing drop chances.
            // But I don't know of an easy solution to this, without mixing in into each mob individually.
            mob.setDropChance(EquipmentSlot.MAINHAND, (float) Config.Common.LITERATE_MOBS_BOOK_DROP_CHANCE.getAsDouble());
        }
    }
}
