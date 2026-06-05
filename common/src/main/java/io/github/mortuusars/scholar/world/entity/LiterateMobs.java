package io.github.mortuusars.scholar.world.entity;

import io.github.mortuusars.scholar.Config;
import io.github.mortuusars.scholar.Scholar;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

import java.util.List;

public class LiterateMobs {
    public static void populateEquipmentSlots(RandomSource random, DifficultyInstance difficulty, Mob mob) {
        if (mob.level() instanceof ServerLevel serverLevel
              && mob.getType().is(Scholar.Tags.EntityTypes.LITERATE)
              && random.nextDouble() < Config.Common.LITERATE_MOBS_BOOK_SPAWN_CHANCE.get()) {
            double dropChance = Config.Common.LITERATE_MOBS_BOOK_DROP_CHANCE.get();

            ResourceLocation lootTableId = Scholar.LootTables.LITERATE_MOB_BOOK;
            LootTable lootTable = serverLevel.getServer().getLootData().getLootTable(lootTableId);
            LootParams.Builder builder = (new LootParams.Builder(serverLevel))
                  .withParameter(LootContextParams.THIS_ENTITY, mob)
                  .withParameter(LootContextParams.ORIGIN, mob.position());

            LootParams lootParams = builder.create(LootContextParamSets.CHEST);
            List<ItemStack> items = lootTable.getRandomItems(lootParams, mob.getLootTableSeed());

            if (!items.isEmpty()) {
                ItemStack item = Util.getRandom(items, mob.getRandom());
                mob.setItemSlot(EquipmentSlot.MAINHAND, item);
                mob.setDropChance(EquipmentSlot.MAINHAND, (float) dropChance);
            }
        }
    }
}
