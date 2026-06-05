package io.github.mortuusars.scholar;

import com.google.common.base.Preconditions;
import com.mojang.logging.LogUtils;
import io.github.mortuusars.scholar.menu.LecternSpreadBookEditMenu;
import io.github.mortuusars.scholar.menu.LecternSpreadMenu;
import io.github.mortuusars.scholar.util.supporter.Supporters;
import io.github.mortuusars.scholar.recipe.NbtTransferringRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.slf4j.Logger;

import java.util.function.Supplier;

public class Scholar {
    public static final String ID = "scholar";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static ResourceLocation resource(String path) {
        return new ResourceLocation(Scholar.ID, path);
    }

    public static void init() {
        MenuTypes.init();
        RecipeSerializers.init();
        SoundEvents.init();

        // Query supporters early, so it will be available right away when needed
        Supporters.query();
    }

    public static class NBT {
        public static final String BOOKMARK = "ScholarBookmark";
        public static final String BOOK_OPEN = "ScholarBookOpen";
        public static final String BOOK_GOLDEN = "ScholarBookGolden";
    }

    public static class MenuTypes {
        public static final Supplier<MenuType<LecternSpreadMenu>> LECTERN_SPREAD_BOOK_VIEW =
                Register.menuType("lectern_spread_book_view", LecternSpreadMenu::fromBuffer);
        public static final Supplier<MenuType<LecternSpreadBookEditMenu>> LECTERN_SPREAD_BOOK_EDIT =
                Register.menuType("lectern_spread_book_edit", LecternSpreadBookEditMenu::fromBuffer);

        static void init() { }
    }

    public static class RecipeSerializers {
        public static final Supplier<RecipeSerializer<?>> NBT_TRANSFERRING = Register.recipeSerializer("nbt_transferring",
                NbtTransferringRecipe.Serializer::new);
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

    public static class LootTables {
        public static final ResourceLocation LITERATE_MOB_BOOK = resource("entities/literate_mob_book");
    }
}