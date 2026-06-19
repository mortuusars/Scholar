package io.github.mortuusars.scholar.world.entity;

import io.github.mortuusars.scholar.Config;
import io.github.mortuusars.scholar.Scholar;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class LiterateMobs {
    public static void populateEquipmentSlots(RandomSource random, DifficultyInstance difficulty, Mob mob) {
        if (mob.level() instanceof ServerLevel serverLevel
              && mob.getType().is(Scholar.Tags.EntityTypes.LITERATE)
              && random.nextDouble() < Config.Common.LITERATE_MOBS_BOOK_SPAWN_CHANCE.get()) {
            float dropChance = (float) Config.Common.LITERATE_MOBS_BOOK_DROP_CHANCE.getAsDouble();
            LootParams params = new LootParams.Builder(serverLevel).withParameter(LootContextParams.ORIGIN, mob.position()).withParameter(LootContextParams.THIS_ENTITY, mob).create(LootContextParamSets.EQUIPMENT);
            equip(mob, random,
                  Scholar.LootTables.LITERATE_MOB_BOOK, Map.of(EquipmentSlot.MAINHAND, dropChance, EquipmentSlot.OFFHAND, dropChance),
                  params);
        }
    }

    private static void equip(Mob mob, RandomSource random, ResourceKey<LootTable> table, Map<EquipmentSlot, Float> slotDropChances, LootParams params) {
        if (table.equals(BuiltInLootTables.EMPTY)) return;

        LootTable lootTable = params.getLevel().getServer().reloadableRegistries().getLootTable(table);
        if (lootTable == LootTable.EMPTY) return;

        List<ItemStack> list = new ArrayList<>();
        lootTable.getRandomItems(new LootContext.Builder(params).withOptionalRandomSource(random).create(Optional.empty()), list::add);
        List<EquipmentSlot> excludedSlots = new ArrayList<>();

        for (ItemStack itemStack : list) {
            EquipmentSlot equipmentSlot = mob.resolveSlot(itemStack, excludedSlots);
            if (equipmentSlot != null) {
                ItemStack itemStack2 = equipmentSlot.limit(itemStack);
                mob.setItemSlot(equipmentSlot, itemStack2);
                @Nullable Float chance = slotDropChances.get(equipmentSlot);
                if (chance != null) {
                    mob.setDropChance(equipmentSlot, chance);
                }

                excludedSlots.add(equipmentSlot);
            }
        }
    }
}
