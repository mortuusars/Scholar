package io.github.mortuusars.scholar;

import com.google.common.base.Preconditions;
import com.mojang.logging.LogUtils;
import io.github.mortuusars.scholar.item.ColoredWritableBookItem;
import io.github.mortuusars.scholar.item.ColoredWrittenBookItem;
import io.github.mortuusars.scholar.menu.LecternSpreadMenu;
import io.github.mortuusars.scholar.recipe.NbtTransferringRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class Scholar {
    public static final String ID = "scholar";

    public static ResourceLocation resource(String path) {
        return new ResourceLocation(Scholar.ID, path);
    }

    public static void init() {
        Items.init();
        MenuTypes.init();
        RecipeSerializers.init();
        SoundEvents.init();

        try {
            String template = """
            {
               "parent": "minecraft:recipes/root",
               "criteria": {
                 "has_book": {
                   "conditions": {
                     "items": [
                       {
                         "items": [
                           "minecraft:book"
                         ]
                       }
                     ]
                   },
                   "trigger": "minecraft:inventory_changed"
                 },
                 "has_the_recipe": {
                   "conditions": {
                     "recipe": "scholar:writable/<COLOR>_writable_book"
                   },
                   "trigger": "minecraft:recipe_unlocked"
                 }
               },
               "requirements": [
                 [
                   "has_book",
                   "has_the_recipe"
                 ]
               ],
               "rewards": {
                 "recipes": [
                   "scholar:writable/<COLOR>_writable_book"
                 ]
               },
               "sends_telemetry_event": false
            }
            """;

            String desktopPath = System.getProperty("user.home") + File.separator + "Desktop";

            for (DyeColor color : DyeColor.values()) {
                String colorReplaced = template.replace("<COLOR>", color.getSerializedName());
//                String writableJson = colorReplaced.replace("<TYPE>", "writable");
//                String writtenJson = colorReplaced.replace("<TYPE>", "written");

                String writableFileName = desktopPath + File.separator + color.getSerializedName() + "_writable_book.json";
                Files.write(Paths.get(writableFileName), colorReplaced.getBytes());

//                String writableFileName = desktopPath + File.separator + color.getSerializedName() + "_writable_book.json";
//                Files.write(Paths.get(writableFileName), writableJson.getBytes());
//
//                String writtenFileName = desktopPath + File.separator + color.getSerializedName() + "_written_book.json";
//                Files.write(Paths.get(writtenFileName), writtenJson.getBytes());
            }
        }
        catch (Exception e) {
            LogUtils.getLogger().error(e.toString());
        }

    }

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
        public static final Supplier<MenuType<LecternSpreadMenu>> LECTERN = Register.menuType("lectern_spread", LecternSpreadMenu::fromBuffer);

        static void init() { }
    }

    public static class RecipeSerializers {
        public static final Supplier<RecipeSerializer<?>> NBT_TRANSFERRING = Register.recipeSerializer("nbt_transferring",
                NbtTransferringRecipe.Serializer::new);
        static void init() { }
    }

    public static class SoundEvents {
        public static final Supplier<SoundEvent> FORMATTING_CLICK = register("book", "formatting_click");
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

