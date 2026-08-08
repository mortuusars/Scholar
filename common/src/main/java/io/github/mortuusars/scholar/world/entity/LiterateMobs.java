package io.github.mortuusars.scholar.world.entity;

import io.github.mortuusars.scholar.Config;
import io.github.mortuusars.scholar.Scholar;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentTable;
import net.minecraft.world.entity.Mob;

import java.util.Map;

public class LiterateMobs {
    public static void populateEquipmentSlots(RandomSource random, DifficultyInstance difficulty, Mob mob) {
        if (mob.is(Scholar.Tags.EntityTypes.LITERATE)
              && random.nextDouble() < Config.Common.LITERATE_MOBS_BOOK_SPAWN_CHANCE.get()) {
            if (mob.level() instanceof ServerLevel serverLevel) {
                serverLevel.getServer().execute(() -> {
                    float dropChance = (float) Config.Common.LITERATE_MOBS_BOOK_DROP_CHANCE.getAsDouble();
                    mob.equip(new EquipmentTable(Scholar.LootTables.LITERATE_MOB_BOOK,
                          Map.of(EquipmentSlot.MAINHAND, dropChance, EquipmentSlot.OFFHAND, dropChance)));
                });
            }
        }
    }
}
