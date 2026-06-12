package io.github.mortuusars.scholar;

import com.google.common.base.Preconditions;
import com.mojang.logging.LogUtils;
import io.github.mortuusars.scholar.menu.Lectern;
import io.github.mortuusars.scholar.menu.LecternSpreadBookEditMenu;
import io.github.mortuusars.scholar.menu.LecternSpreadMenu;
import net.minecraft.resources.Identifier;
import io.github.mortuusars.scholar.util.supporter.Supporters;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.storage.loot.LootTable;
import org.slf4j.Logger;

import java.util.function.Supplier;

public class Scholar {
    public static final String ID = "scholar";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static Identifier identifier(String path) {
        return Identifier.fromNamespaceAndPath(Scholar.ID, path);
    }

    public static void init() {
        DataComponents.init();
        MenuTypes.init();
        RecipeSerializers.init();
        SoundEvents.init();

        // Query supporters early, so it will be available right away when needed
        Supporters.query();
    }

    public static class DataComponents {
        public static final DataComponentType<Integer> BOOKMARK = Register.dataComponentType("bookmark",
              b -> b.persistent(ExtraCodecs.NON_NEGATIVE_INT).networkSynchronized(ByteBufCodecs.INT));
        public static final DataComponentType<Unit> BOOK_OPEN = Register.dataComponentType("book_open",
              b -> b.persistent(Unit.CODEC).networkSynchronized(StreamCodec.unit(Unit.INSTANCE)));
        public static final DataComponentType<Unit> BOOK_GOLDEN = Register.dataComponentType("book_golden",
              b -> b.persistent(Unit.CODEC).networkSynchronized(StreamCodec.unit(Unit.INSTANCE)));

        static void init() { }
    }

    public static class MenuTypes {
        public static final Supplier<MenuType<LecternSpreadMenu>> LECTERN_SPREAD_BOOK_VIEW =
              Register.menuType("lectern_spread_book_view", LecternSpreadMenu::fromNetwork, Lectern.Data.STREAM_CODEC);
        public static final Supplier<MenuType<LecternSpreadBookEditMenu>> LECTERN_SPREAD_BOOK_EDIT =
              Register.menuType("lectern_spread_book_edit", LecternSpreadBookEditMenu::fromNetwork, Lectern.Data.STREAM_CODEC);

        static void init() { }
    }

    public static class RecipeSerializers {
        static void init() { }
    }

    public static class SoundEvents {
        public static final Supplier<SoundEvent> BOOK_SIGNED = register("ui", "book_signed");
        public static final Supplier<SoundEvent> SCRIBBLE = register("ui", "scribble");
        public static final Supplier<SoundEvent> INK = register("ui", "ink");

        @SuppressWarnings("SameParameterValue")
        private static Supplier<SoundEvent> register(String category, String key) {
            Preconditions.checkState(category != null && !category.isEmpty(), "'category' should not be empty.");
            Preconditions.checkState(key != null && !key.isEmpty(), "'key' should not be empty.");
            String path = category + "." + key;
            return Register.soundEvent(path, () -> SoundEvent.createVariableRangeEvent(Scholar.identifier(path)));
        }

        static void init() {}
    }

    public static class Tags {
        public static class EntityTypes {
            public static final TagKey<EntityType<?>> LITERATE = TagKey.create(Registries.ENTITY_TYPE, identifier("literate"));
        }
    }

    public static class LootTables {
        public static final ResourceKey<LootTable> LITERATE_MOB_BOOK = ResourceKey.create(Registries.LOOT_TABLE, identifier("entities/literate_mob_book"));
    }
}