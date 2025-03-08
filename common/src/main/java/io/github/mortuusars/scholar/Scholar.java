package io.github.mortuusars.scholar;

import com.google.common.base.Preconditions;
import com.mojang.logging.LogUtils;
import io.github.mortuusars.scholar.item.ColoredWritableBookItem;
import io.github.mortuusars.scholar.item.ColoredWrittenBookItem;
import io.github.mortuusars.scholar.menu.LecternSpreadBookEditMenu;
import io.github.mortuusars.scholar.menu.LecternSpreadMenu;
import io.github.mortuusars.scholar.recipe.NbtTransferringRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class Scholar {
    public static final String ID = "scholar";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static ResourceLocation resource(String path) {
        return new ResourceLocation(Scholar.ID, path);
    }

    public static void init() {
        Items.init();
        MenuTypes.init();
        RecipeSerializers.init();
        SoundEvents.init();
    }

    @SuppressWarnings("removal")
    public static class Items {
        public static final Map<DyeColor, Supplier<ColoredWritableBookItem>> COLORED_WRITABLE_BOOKS;
        public static final Map<DyeColor, Supplier<ColoredWrittenBookItem>> COLORED_WRITTEN_BOOKS;

        static {
            COLORED_WRITABLE_BOOKS = new HashMap<>();
            for (DyeColor color : DyeColor.values()) {
                COLORED_WRITABLE_BOOKS.put(color, Register.item(color.getSerializedName() + "_writable_book",
                        () -> new ColoredWritableBookItem(color, new Item.Properties().stacksTo(1))));
            }

            COLORED_WRITTEN_BOOKS = new HashMap<>();
            for (DyeColor color : DyeColor.values()) {
                COLORED_WRITTEN_BOOKS.put(color, Register.item(color.getSerializedName() + "_written_book",
                        () -> new ColoredWrittenBookItem(color, new Item.Properties().stacksTo(16))));
            }
        }

        static void init() { }
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
        public static final Supplier<SoundEvent> BOOK_SIGNED = register("book", "signed");

        @SuppressWarnings("SameParameterValue")
        private static Supplier<SoundEvent> register(String category, String key) {
            Preconditions.checkState(category != null && !category.isEmpty(), "'category' should not be empty.");
            Preconditions.checkState(key != null && !key.isEmpty(), "'key' should not be empty.");
            String path = category + "." + key;
            return Register.soundEvent(path, () -> SoundEvent.createVariableRangeEvent(Scholar.resource(path)));
        }

        static void init() { }
    }
}