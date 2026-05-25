package io.github.mortuusars.scholar;

import com.google.common.base.Preconditions;
import com.mojang.logging.LogUtils;
import io.github.mortuusars.scholar.menu.LecternSpreadBookEditMenu;
import io.github.mortuusars.scholar.menu.LecternSpreadMenu;
import net.minecraft.resources.Identifier;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.MenuType;
import org.slf4j.Logger;

import java.util.function.Supplier;

public class Scholar {
    public static final String ID = "scholar";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static Identifier resource(String path) {
        return Identifier.fromNamespaceAndPath(Scholar.ID, path);
    }

    public static void init() {
        DataComponents.init();
        MenuTypes.init();
        RecipeSerializers.init();
        SoundEvents.init();
    }

    public static class DataComponents {
        public static final DataComponentType<Integer> BOOKMARK = Register.dataComponentType("bookmark",
              b -> b.persistent(ExtraCodecs.NON_NEGATIVE_INT).networkSynchronized(ByteBufCodecs.INT));
        public static final DataComponentType<Unit> BOOK_OPEN = Register.dataComponentType("book_open",
              b -> b.persistent(Unit.CODEC).networkSynchronized(StreamCodec.unit(Unit.INSTANCE)));

        static void init() { }
    }

    @SuppressWarnings("NullableProblems")
    public static class MenuTypes {
        public static final Supplier<MenuType<LecternSpreadMenu>> LECTERN_SPREAD_BOOK_VIEW =
                Register.menuType("lectern_spread_book_view", LecternSpreadMenu::fromBuffer);
        public static final Supplier<MenuType<LecternSpreadBookEditMenu>> LECTERN_SPREAD_BOOK_EDIT =
                Register.menuType("lectern_spread_book_edit", LecternSpreadBookEditMenu::fromBuffer);

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
            return Register.soundEvent(path, () -> SoundEvent.createVariableRangeEvent(Scholar.resource(path)));
        }

        static void init() {}
    }

    public static class Tags {
        public static class EntityTypes {
            public static final TagKey<EntityType<?>> LITERATE = TagKey.create(Registries.ENTITY_TYPE, resource("literate"));
        }
    }
}