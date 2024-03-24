package io.github.mortuusars.scholar;

import com.google.common.base.Preconditions;
import com.mojang.logging.LogUtils;
import io.github.mortuusars.scholar.item.ColoredWritableBookItem;
import io.github.mortuusars.scholar.item.ColoredWrittenBookItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.WritableBookItem;

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
        SoundEvents.init();

//        try {
//
//
//            String template = """
//            {
//              "parent": "minecraft:item/generated",
//              "textures": {
//                "layer0": "scholar:item/<COLOR>_<TYPE>_book"
//              }
//            }
//            """;
//
//            String desktopPath = System.getProperty("user.home") + File.separator + "Desktop";
//
//            for (DyeColor color : DyeColor.values()) {
//                String colorReplaced = template.replace("<COLOR>", color.getSerializedName());
//                String writableJson = colorReplaced.replace("<TYPE>", "writable");
//                String writtenJson = colorReplaced.replace("<TYPE>", "written");
//
//                String writableFileName = desktopPath + File.separator + color.getSerializedName() + "_writable_book.json";
//                Files.write(Paths.get(writableFileName), writableJson.getBytes());
//
//                String writtenFileName = desktopPath + File.separator + color.getSerializedName() + "_written_book.json";
//                Files.write(Paths.get(writtenFileName), writtenJson.getBytes());
//            }
//        }
//        catch (Exception e) {
//            LogUtils.getLogger().error(e.toString());
//        }

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

//        public static final Supplier<ColoredWritableBookItem> WHITE_WRITABLE_BOOK = Register.item("white_writable_book",
//                () -> new ColoredWritableBookItem(DyeColor.WHITE, new Item.Properties().stacksTo(1)));
//
//        public static final Supplier<ColoredWritableBookItem> ORANGE_WRITABLE_BOOK = Register.item("orange_writable_book",
//                () -> new ColoredWritableBookItem(DyeColor.ORANGE, new Item.Properties().stacksTo(1)));
//
//        public static final Supplier<ColoredWritableBookItem> MAGENTA_WRITABLE_BOOK = Register.item("magenta_writable_book",
//                () -> new ColoredWritableBookItem(DyeColor.MAGENTA, new Item.Properties().stacksTo(1)));
//
//        public static final Supplier<ColoredWritableBookItem> LIGHT_BLUE_WRITABLE_BOOK = Register.item("light_blue_writable_book",
//                () -> new ColoredWritableBookItem(DyeColor.LIGHT_BLUE, new Item.Properties().stacksTo(1)));
//
//        public static final Supplier<ColoredWritableBookItem> YELLOW_WRITABLE_BOOK = Register.item("yellow_writable_book",
//                () -> new ColoredWritableBookItem(DyeColor.YELLOW, new Item.Properties().stacksTo(1)));
//
//        public static final Supplier<ColoredWritableBookItem> LIME_WRITABLE_BOOK = Register.item("lime_writable_book",
//                () -> new ColoredWritableBookItem(DyeColor.LIME, new Item.Properties().stacksTo(1)));
//
//        public static final Supplier<ColoredWritableBookItem> PINK_WRITABLE_BOOK = Register.item("pink_writable_book",
//                () -> new ColoredWritableBookItem(DyeColor.PINK, new Item.Properties().stacksTo(1)));
//
//        public static final Supplier<ColoredWritableBookItem> GRAY_WRITABLE_BOOK = Register.item("gray_writable_book",
//                () -> new ColoredWritableBookItem(DyeColor.GRAY, new Item.Properties().stacksTo(1)));
//
//        public static final Supplier<ColoredWritableBookItem> LIGHT_GRAY_WRITABLE_BOOK = Register.item("light_gray_writable_book",
//                () -> new ColoredWritableBookItem(DyeColor.LIGHT_GRAY, new Item.Properties().stacksTo(1)));
//
//        public static final Supplier<ColoredWritableBookItem> CYAN_WRITABLE_BOOK = Register.item("cyan_writable_book",
//                () -> new ColoredWritableBookItem(DyeColor.CYAN, new Item.Properties().stacksTo(1)));
//
//        public static final Supplier<ColoredWritableBookItem> PURPLE_WRITABLE_BOOK = Register.item("purple_writable_book",
//                () -> new ColoredWritableBookItem(DyeColor.PURPLE, new Item.Properties().stacksTo(1)));
//
//        public static final Supplier<ColoredWritableBookItem> BLUE_WRITABLE_BOOK = Register.item("blue_writable_book",
//                () -> new ColoredWritableBookItem(DyeColor.BLUE, new Item.Properties().stacksTo(1)));
//
//        public static final Supplier<ColoredWritableBookItem> BROWN_WRITABLE_BOOK = Register.item("brown_writable_book",
//                () -> new ColoredWritableBookItem(DyeColor.BROWN, new Item.Properties().stacksTo(1)));
//
//        public static final Supplier<ColoredWritableBookItem> GREEN_WRITABLE_BOOK = Register.item("green_writable_book",
//                () -> new ColoredWritableBookItem(DyeColor.GREEN, new Item.Properties().stacksTo(1)));
//
//        public static final Supplier<ColoredWritableBookItem> RED_WRITABLE_BOOK = Register.item("red_writable_book",
//                () -> new ColoredWritableBookItem(DyeColor.RED, new Item.Properties().stacksTo(1)));
//
//        public static final Supplier<ColoredWritableBookItem> BLACK_WRITABLE_BOOK = Register.item("black_writable_book",
//                () -> new ColoredWritableBookItem(DyeColor.BLACK, new Item.Properties().stacksTo(1)));
//
//        // --
//
//        public static final Supplier<ColoredWrittenBookItem> WHITE_WRITTEN_BOOK = Register.item("white_written_book",
//                () -> new ColoredWrittenBookItem(DyeColor.WHITE, new Item.Properties().stacksTo(16)));
//
//        public static final Supplier<ColoredWrittenBookItem> ORANGE_WRITTEN_BOOK = Register.item("orange_written_book",
//                () -> new ColoredWrittenBookItem(DyeColor.ORANGE, new Item.Properties().stacksTo(16)));
//
//        public static final Supplier<ColoredWrittenBookItem> MAGENTA_WRITTEN_BOOK = Register.item("magenta_written_book",
//                () -> new ColoredWrittenBookItem(DyeColor.MAGENTA, new Item.Properties().stacksTo(16)));
//
//        public static final Supplier<ColoredWrittenBookItem> LIGHT_BLUE_WRITTEN_BOOK = Register.item("light_blue_written_book",
//                () -> new ColoredWrittenBookItem(DyeColor.LIGHT_BLUE, new Item.Properties().stacksTo(16)));
//
//        public static final Supplier<ColoredWrittenBookItem> YELLOW_WRITTEN_BOOK = Register.item("yellow_written_book",
//                () -> new ColoredWrittenBookItem(DyeColor.YELLOW, new Item.Properties().stacksTo(16)));
//
//        public static final Supplier<ColoredWrittenBookItem> LIME_WRITTEN_BOOK = Register.item("lime_written_book",
//                () -> new ColoredWrittenBookItem(DyeColor.LIME, new Item.Properties().stacksTo(16)));
//
//        public static final Supplier<ColoredWrittenBookItem> PINK_WRITTEN_BOOK = Register.item("pink_written_book",
//                () -> new ColoredWrittenBookItem(DyeColor.PINK, new Item.Properties().stacksTo(16)));
//
//        public static final Supplier<ColoredWrittenBookItem> GRAY_WRITTEN_BOOK = Register.item("gray_written_book",
//                () -> new ColoredWrittenBookItem(DyeColor.GRAY, new Item.Properties().stacksTo(16)));
//
//        public static final Supplier<ColoredWrittenBookItem> LIGHT_GRAY_WRITTEN_BOOK = Register.item("light_gray_written_book",
//                () -> new ColoredWrittenBookItem(DyeColor.LIGHT_GRAY, new Item.Properties().stacksTo(16)));
//
//        public static final Supplier<ColoredWrittenBookItem> CYAN_WRITTEN_BOOK = Register.item("cyan_written_book",
//                () -> new ColoredWrittenBookItem(DyeColor.CYAN, new Item.Properties().stacksTo(16)));
//
//        public static final Supplier<ColoredWrittenBookItem> PURPLE_WRITTEN_BOOK = Register.item("purple_written_book",
//                () -> new ColoredWrittenBookItem(DyeColor.PURPLE, new Item.Properties().stacksTo(16)));
//
//        public static final Supplier<ColoredWrittenBookItem> BLUE_WRITTEN_BOOK = Register.item("blue_written_book",
//                () -> new ColoredWrittenBookItem(DyeColor.BLUE, new Item.Properties().stacksTo(16)));
//
//        public static final Supplier<ColoredWrittenBookItem> BROWN_WRITTEN_BOOK = Register.item("brown_written_book",
//                () -> new ColoredWrittenBookItem(DyeColor.BROWN, new Item.Properties().stacksTo(16)));
//
//        public static final Supplier<ColoredWrittenBookItem> GREEN_WRITTEN_BOOK = Register.item("green_written_book",
//                () -> new ColoredWrittenBookItem(DyeColor.GREEN, new Item.Properties().stacksTo(16)));
//
//        public static final Supplier<ColoredWrittenBookItem> RED_WRITTEN_BOOK = Register.item("red_written_book",
//                () -> new ColoredWrittenBookItem(DyeColor.RED, new Item.Properties().stacksTo(16)));
//
//        public static final Supplier<ColoredWrittenBookItem> BLACK_WRITTEN_BOOK = Register.item("black_written_book",
//                () -> new ColoredWrittenBookItem(DyeColor.BLACK, new Item.Properties().stacksTo(16)));

        public static void init() { }
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

        public static void init() { }
    }
}

