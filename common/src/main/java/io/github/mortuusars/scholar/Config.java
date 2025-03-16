package io.github.mortuusars.scholar;

import com.mojang.logging.LogUtils;
import net.minecraftforge.common.ForgeConfigSpec;


public class Config {
    public static class Common {
        public static final ForgeConfigSpec SPEC;
        // Coloring
        public static final ForgeConfigSpec.BooleanValue WRITABLE_BOOK_COLORING;
        public static final ForgeConfigSpec.BooleanValue WRITTEN_BOOK_COLORING;

        // UI
        public static final ForgeConfigSpec.BooleanValue IN_HAND_TWO_PAGE_BOOK_SCREEN;
        public static final ForgeConfigSpec.BooleanValue LECTERN_TWO_PAGE_BOOK_SCREEN;
        public static final ForgeConfigSpec.BooleanValue SNEAK_OPENS_VANILLA_BOOK_SCREEN;

        // Misc
        public static final ForgeConfigSpec.BooleanValue CHISELED_BOOKSHELF_COLORS;
        public static final ForgeConfigSpec.BooleanValue CHISELED_BOOKSHELF_TOOLTIP;
        public static final ForgeConfigSpec.BooleanValue BOOK_ENCHANTMENT_GLINT;

        static {
            ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

            builder.push("ui");
            IN_HAND_TWO_PAGE_BOOK_SCREEN = builder
                    .comment("Vanilla book view/edit screens will be replaced with Scholar's two-paged view/edit screens. Default: true")
                    .define("in_hand_two_page_book_screens", true);
            LECTERN_TWO_PAGE_BOOK_SCREEN = builder
                    .comment("Vanilla lectern book view screen will be replaced with Scholar's two-paged view/edit screens.",
                            "Can be disabled if you want to use different screen for lectern (such as from Amendments (but Scholar now has lectern editing, so...)). Default: true")
                    .define("lectern_two_page_book_screens", true);
            SNEAK_OPENS_VANILLA_BOOK_SCREEN = builder
                    .comment("Holding sneak while opening a book screen will show vanilla screen. Default: false")
                    .define("sneaking_opens_vanilla_book_screen", false);
            builder.pop();

            builder.push("book_coloring");
            WRITABLE_BOOK_COLORING = builder
                    .comment("Writable Book (Book and Quill) can be colored like Leather armor. Default: true")
                    .define("writable_book_coloring", true);
            WRITTEN_BOOK_COLORING = builder
                    .comment("Written Book can be colored like Leather armor. Default: false")
                    .define("written_book_coloring", false);
            builder.pop();

            builder.push("misc");
            CHISELED_BOOKSHELF_COLORS = builder
                    .comment("Colored books in Chiseled Bookshelf will have correct colors displayed on the block. Default: true",
                            "Note: resourepacks that modify Chiseled Bookshelf may break the coloring.",
                            "Note 2: even if this setting is disabled - bookshelf will not look quite the same if you look closely.",
                            "To restore fully - overwrite bookshelf slot models added by Scholar using a resourcepack.")
                    .define("chiseled_bookshelf_colors", true);
            CHISELED_BOOKSHELF_TOOLTIP = builder
                    .comment("Hovering over a slot in a Chiseled Bookshelf will show tooltip of a book that's stored in that slot. Default: true")
                    .define("chiseled_bookshelf_tooltip", true);
            BOOK_ENCHANTMENT_GLINT = builder
                    .comment("Written books will have an enchantment glint on them. Default: false")
                    .define("written_book_enchantment_glint", false);
            builder.pop();

            SPEC = builder.build();
        }
    }

    public static class Client {
        public static final ForgeConfigSpec SPEC;

        // UI
        public static final ForgeConfigSpec.BooleanValue SCREEN_PAUSE;
        public static final ForgeConfigSpec.BooleanValue SHOW_DONE_BUTTON;
        public static final ForgeConfigSpec.BooleanValue EDIT_SCREEN_SHOW_EXTRA_TOOLS;

        // Colors
        public static final ForgeConfigSpec.ConfigValue<String> TEXT_COLOR;
        public static final ForgeConfigSpec.ConfigValue<String> PAGE_NUMBERS_COLOR;
        public static final ForgeConfigSpec.ConfigValue<String> ENTER_TITLE_COLOR;
        public static final ForgeConfigSpec.ConfigValue<String> BY_AUTHOR_COLOR;
        public static final ForgeConfigSpec.ConfigValue<String> SELECTION_COLOR;
        public static final ForgeConfigSpec.ConfigValue<String> SELECTION_UNFOCUSED_COLOR;

        static {
            ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

            {
                builder.push("ui");

                SCREEN_PAUSE = builder
                        .comment("Singleplayer game will be paused when book edit/view screen is open.",
                                "Default: false, Vanilla: true")
                        .define("book_screen_pause", false);

                SHOW_DONE_BUTTON = builder
                        .comment("Show 'Done' button in the Scholar book screens. Default: false")
                        .define("book_screen_show_done_button", false);

                EDIT_SCREEN_SHOW_EXTRA_TOOLS = builder
                        .comment("Additional tool buttons will be shown in book edit screen.",
                                "This setting can be toggled in-game by pressing F1 button (by default) or clicking on question mark in top right corner. Initial value: false")
                        .define("book_edit_screen_show_extra_tools", false);

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
            }

            SPEC = builder.build();
        }

        public static int getColor(ForgeConfigSpec.ConfigValue<String> configValue) {
            String hexString = configValue.get();
            try {
                // Can't parse straight to int because of how integers are interpreted
                // 0xFFFFFFFF will throw for example
                long longValue = Long.parseLong(hexString, 16);
                return (int)longValue;
            }
            catch (Exception e) {
                String configValuePath = String.join(".", configValue.getPath());
                LogUtils.getLogger().error("Value '{}' is not valid for {}. Default value will be used.\n{}",
                        hexString, configValuePath, e.getMessage());
                return (int)Long.parseLong(configValue.getDefault(), 16); // Default shouldn't fail
            }
        }
    }
}
