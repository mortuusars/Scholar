package io.github.mortuusars.scholar;

import com.mojang.logging.LogUtils;
import net.minecraftforge.common.ForgeConfigSpec;

public class Config {
    public static class Common {
        public static final ForgeConfigSpec SPEC;

        // Book
        public static final ForgeConfigSpec.BooleanValue BOOK_CHANGEABLE_AUTHOR;

        // Reading
        public static final ForgeConfigSpec.BooleanValue BOOK_READING_ANIMATION;
        public static final ForgeConfigSpec.BooleanValue BOOK_READING_HIDE_OFFHAND_ITEM;

        // Literate Mobs
        public static final ForgeConfigSpec.DoubleValue LITERATE_MOBS_BOOK_SPAWN_CHANCE;
        public static final ForgeConfigSpec.DoubleValue LITERATE_MOBS_BOOK_DROP_CHANCE;
        public static final ForgeConfigSpec.DoubleValue LITERATE_MOBS_BOOK_READING_CHANCE;

        // Screen
        public static final ForgeConfigSpec.BooleanValue IN_HAND_TWO_PAGE_BOOK_SCREEN;
        public static final ForgeConfigSpec.BooleanValue LECTERN_TWO_PAGE_BOOK_SCREEN;
        public static final ForgeConfigSpec.BooleanValue SNEAKING_OPENS_VANILLA_BOOK_SCREEN;
        public static final ForgeConfigSpec.BooleanValue BOOK_SCREEN_PAUSE;
        public static final ForgeConfigSpec.BooleanValue BOOK_SCREEN_SHOW_DONE_BUTTON;
        public static final ForgeConfigSpec.BooleanValue EDIT_SCREEN_SHOW_EXTRA_TOOLS;

        // Tooltip
        public static final ForgeConfigSpec.BooleanValue CHISELED_BOOKSHELF_TOOLTIP;
        public static final ForgeConfigSpec.BooleanValue LECTERN_TOOLTIP;
        public static final ForgeConfigSpec.BooleanValue TOOLTIP_REQUIRES_SNEAK;

        // Visuals
        public static final ForgeConfigSpec.BooleanValue WRITABLE_BOOK_COLORING;
        public static final ForgeConfigSpec.BooleanValue WRITTEN_BOOK_COLORING;
        public static final ForgeConfigSpec.BooleanValue LECTERN_COLORED_BOOK_MODEL;
        public static final ForgeConfigSpec.BooleanValue WRITTEN_BOOK_ENCHANTMENT_GLINT;

        // Integration
        public static final ForgeConfigSpec.BooleanValue JEI_DYEING_RECIPES;
        public static final ForgeConfigSpec.BooleanValue JEI_DYEING_RECIPES_ONLY_BOOKS;

        static {
            ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

            builder.push("book");
            BOOK_CHANGEABLE_AUTHOR = builder
                  .comment("Author can be changed when signing a book. Default: true")
                  .define("changeable_author", true);
            {
                builder.push("book_coloring");
                WRITABLE_BOOK_COLORING = builder
                      .comment("Writable Book (Book and Quill) can be colored like Leather armor. Default: true")
                      .define("writable_book_coloring", true);
                WRITTEN_BOOK_COLORING = builder
                      .comment("Written Book can be colored like Leather armor. Default: false")
                      .define("written_book_coloring", false);
                builder.pop();
            }

            {
                builder.push("screen");
                IN_HAND_TWO_PAGE_BOOK_SCREEN = builder
                      .comment("Scholar two-paged in-hand book view/edit screens will replace vanilla screens. Default: true")
                      .define("replace_screens_in_hand", true);
                LECTERN_TWO_PAGE_BOOK_SCREEN = builder
                      .comment("Scholar two-paged lectern book view/edit screens will replace vanilla screens. Default: true")
                      .define("replace_screens_on_lectern", true);
                SNEAKING_OPENS_VANILLA_BOOK_SCREEN = builder
                      .comment("Holding sneak while opening a book screen will show vanilla screen. Default: false")
                      .define("sneaking_opens_vanilla_screen", false);
                BOOK_SCREEN_PAUSE = builder
                      .comment("Singleplayer game will be paused when book edit/view screen is open.",
                            "Default: false, Vanilla: true")
                      .define("pause_game", false);
                BOOK_SCREEN_SHOW_DONE_BUTTON = builder
                      .comment("Show 'Done' button in the Scholar book screens. Default: false")
                      .define("show_done_button", false);
                EDIT_SCREEN_SHOW_EXTRA_TOOLS = builder
                      .comment("Additional tool buttons will be shown in book edit screen.",
                            "This setting can be toggled in-game by pressing F1 button (by default) or clicking on the button in top right corner. Initial value: false")
                      .define("show_extra_tools", false);
                builder.pop();
            }

            builder.pop();

            builder.push("reading");
            BOOK_READING_ANIMATION = builder
                  .comment("Holding a book with 'scholar:book_open' component will render the full book model and show a reading animation. Default: true")
                  .define("animation", true);
            BOOK_READING_HIDE_OFFHAND_ITEM = builder
                  .comment("Item in the opposite hand will be hidden when the reading pose is active. Default: true")
                  .define("hide_offhand_item", true);
            builder.pop();

            builder.push("literate_mobs");
            LITERATE_MOBS_BOOK_SPAWN_CHANCE = builder
                  .comment("Chance of the mob spawning with a book in hand.")
                  .defineInRange("book_spawn_chance", 0.05, 0, 1);
            LITERATE_MOBS_BOOK_DROP_CHANCE = builder
                  .comment("Chance of the book dropping from the mob spawned with a book in hand.")
                  .defineInRange("book_drop_chance", 0.5, 0, 1);
            LITERATE_MOBS_BOOK_READING_CHANCE = builder
                  .comment("Chance of the mob reading the book it's holding. Lower value = more time between reads.")
                  .defineInRange("book_reading_chance", 0.005, 0, 1);
            builder.pop();

            builder.push("visuals");
            LECTERN_COLORED_BOOK_MODEL = builder
                  .comment("Lectern book rendering reflects the actual book placed on it. Default: true")
                  .define("lectern_colored_book", true);
            WRITTEN_BOOK_ENCHANTMENT_GLINT = builder
                  .comment("Written books have an enchantment glint. Default: false, Vanilla: true")
                  .define("written_book_enchantment_glint", false);
            builder.pop();

            builder.push("tooltip");
            CHISELED_BOOKSHELF_TOOLTIP = builder
                  .comment("Hovering over a slot in a Chiseled Bookshelf will show tooltip of a book that's stored in that slot. Default: true")
                  .define("chiseled_bookshelf_tooltip", true);
            LECTERN_TOOLTIP = builder
                  .comment("Hovering over a Lectern will show tooltip of a book that's placed on it. Default: true")
                  .define("lectern_tooltip", true);
            TOOLTIP_REQUIRES_SNEAK = builder
                  .comment("Sneaking is required for tooltip to show. Default: false")
                  .define("requires_sneak", false);
            builder.pop();

            builder.push("integration");
            {
                builder.push("jei");
                JEI_DYEING_RECIPES = builder
                      .comment("Item dyeing recipes for 'minecraft:dyeable' items will be added to JEI. Default: true")
                      .define("jei_dyeing_recipes", true);
                JEI_DYEING_RECIPES_ONLY_BOOKS = builder
                      .comment("Item dyeing recipes added to JEI will shown only books, ignoring other dyeable items. Default: false")
                      .define("jei_dyeing_recipes_only_books", false);
                builder.pop();
            }
            builder.pop();

            SPEC = builder.build();
        }
    }

    public static class Client {
        public static final ForgeConfigSpec SPEC;

        // Colors
        public static final ForgeConfigSpec.ConfigValue<String> TEXT_COLOR;
        public static final ForgeConfigSpec.ConfigValue<String> PAGE_NUMBERS_COLOR;
        public static final ForgeConfigSpec.ConfigValue<String> ENTER_TITLE_COLOR;
        public static final ForgeConfigSpec.ConfigValue<String> BY_AUTHOR_COLOR;
        public static final ForgeConfigSpec.ConfigValue<String> SELECTION_COLOR;
        public static final ForgeConfigSpec.ConfigValue<String> SELECTION_UNFOCUSED_COLOR;

        public static final ForgeConfigSpec.BooleanValue TUTORIAL_EXTRA_TOOLS;

        static {
            ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

            builder.push("ui");

            {
                builder.push("colors");

                String defaultMainFontColor = "FF7B593D";
                TEXT_COLOR = builder
                      .comment("Color of the book text. Default: " + defaultMainFontColor)
                      .define("text_color", defaultMainFontColor);

                String pageNumberFontColor = "FFEFE4CA";
                PAGE_NUMBERS_COLOR = builder
                      .comment("Color of the page numbers. Default: " + pageNumberFontColor)
                      .define("page_numbers_color", pageNumberFontColor);

                String defaultEnterTitleFontColor = "FFF5EBD0";
                ENTER_TITLE_COLOR = builder
                      .comment("Color of the 'Enter Book Title' text in the signing screen. Default: " + defaultEnterTitleFontColor)
                      .define("enter_title_color", defaultEnterTitleFontColor);

                String defaultByAuthorFontColor = "FFC7B496";
                BY_AUTHOR_COLOR = builder
                      .comment("Color of the 'by <author>' text in the signing screen. Default: " + defaultByAuthorFontColor)
                      .define("by_author_color", defaultByAuthorFontColor);

                String selectionColor = "FF664488";
                SELECTION_COLOR = builder
                      .comment("Color of the selection. Default: " + selectionColor)
                      .define("selection_color", selectionColor);

                String selectionUnfocusedColor = "FF827B88";
                SELECTION_UNFOCUSED_COLOR = builder
                      .comment("Color of the selection when text box is not focused. Default: " + selectionUnfocusedColor)
                      .define("selection_unfocused_color", selectionUnfocusedColor);

                builder.pop();
            }

            builder.pop();

            {
                builder
                      .comment("Settings in this category are automatically updated by the mod itself. " +
                            "You don't need to change them, unless the desire is irresistible.")
                      .push("tutorial");
                TUTORIAL_EXTRA_TOOLS = builder
                      .comment("Extra tools button is flashing red to grab attention until the player toggles it.")
                      .define("extra_tools", true);
                builder.pop();
            }

            SPEC = builder.build();
        }

        public static int getColor(ForgeConfigSpec.ConfigValue<String> configValue) {
            String hexString = configValue.get();
            try {
                // Can't parse straight to int because of how integers are interpreted
                // 0xFFFFFFFF will throw for example
                long longValue = Long.parseLong(hexString, 16);
                return (int) longValue;
            } catch (Exception e) {
                String configValuePath = String.join(".", configValue.getPath());
                LogUtils.getLogger().error("Value '{}' is not valid for {}. Default value will be used.\n{}",
                      hexString, configValuePath, e.getMessage());
                return (int) Long.parseLong(configValue.getDefault(), 16); // Default shouldn't fail
            }
        }
    }
}
