package io.github.mortuusars.scholar;

import com.mojang.logging.LogUtils;
import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {
    public static class Common {
        public static final ModConfigSpec SPEC;

        // Book
        public static final ModConfigSpec.BooleanValue BOOK_CHANGEABLE_AUTHOR;

        // Screen
        public static final ModConfigSpec.BooleanValue IN_HAND_TWO_PAGE_BOOK_SCREEN;
        public static final ModConfigSpec.BooleanValue LECTERN_TWO_PAGE_BOOK_SCREEN;
        public static final ModConfigSpec.BooleanValue SNEAKING_OPENS_VANILLA_BOOK_SCREEN;
        public static final ModConfigSpec.BooleanValue BOOK_SCREEN_PAUSE;
        public static final ModConfigSpec.BooleanValue BOOK_SCREEN_SHOW_DONE_BUTTON;
        public static final ModConfigSpec.BooleanValue EDIT_SCREEN_SHOW_EXTRA_TOOLS;

        // Tooltip
        public static final ModConfigSpec.BooleanValue CHISELED_BOOKSHELF_TOOLTIP;
        public static final ModConfigSpec.BooleanValue LECTERN_TOOLTIP;
        public static final ModConfigSpec.BooleanValue TOOLTIP_REQUIRES_SNEAK;

        // Visuals
        public static final ModConfigSpec.BooleanValue LECTERN_COLORED_BOOK_MODEL;
        public static final ModConfigSpec.BooleanValue BOOK_ENCHANTMENT_GLINT;

        static {
            ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

            builder.push("book");
            BOOK_CHANGEABLE_AUTHOR = builder
                  .comment("Author can be changed when signing a book. Default: true")
                  .define("changeable_author", true);
            {
                builder.push("ui");
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
                            "This setting can be toggled in-game by pressing F1 button (by default) or clicking on question mark in top right corner. Initial value: false")
                      .define("show_extra_tools", false);
                builder.pop();
            }
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

            builder.push("visuals");
            LECTERN_COLORED_BOOK_MODEL = builder
                  .comment("Lectern book rendering reflects the actual book placed on it. Default: true")
                  .define("lectern_colored_book", true);
            BOOK_ENCHANTMENT_GLINT = builder
                  .comment("Written books have an enchantment glint. Default: false, Vanilla: true")
                  .define("written_book_enchantment_glint", false);
            builder.pop();

            SPEC = builder.build();
        }
    }

    public static class Client {
        public static final ModConfigSpec SPEC;

        // Colors
        public static final ModConfigSpec.ConfigValue<String> TEXT_COLOR;
        public static final ModConfigSpec.ConfigValue<String> PAGE_NUMBERS_COLOR;
        public static final ModConfigSpec.ConfigValue<String> ENTER_TITLE_COLOR;
        public static final ModConfigSpec.ConfigValue<String> BY_AUTHOR_COLOR;
        public static final ModConfigSpec.ConfigValue<String> SELECTION_COLOR;
        public static final ModConfigSpec.ConfigValue<String> SELECTION_UNFOCUSED_COLOR;

        static {
            ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

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

            SPEC = builder.build();
        }

        public static int getColor(ModConfigSpec.ConfigValue<String> configValue) {
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
