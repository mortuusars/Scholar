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
        public static final ForgeConfigSpec.BooleanValue TWO_PAGE_SCREEN;
        public static final ForgeConfigSpec.BooleanValue LECTERN_TWO_PAGE_SCREEN;
        public static final ForgeConfigSpec.BooleanValue SNEAK_OPENS_VANILLA_SCREEN;
        public static final ForgeConfigSpec.BooleanValue SURVIVAL_FORMATTING;

        // QOL
        public static final ForgeConfigSpec.BooleanValue CHISELED_BOOKSHELF_TOOLTIP;
        public static final ForgeConfigSpec.BooleanValue BOOK_ENCHANTMENT_GLINT;

        static {
            ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

            builder.push("BookColoring");
            WRITABLE_BOOK_COLORING = builder
                    .comment("Writable Book (Book and Quill) can be colored like Leather armor. Default: true")
                    .define("WritableBookColoring", true);
            WRITTEN_BOOK_COLORING = builder
                    .comment("Written Book can be colored like Leather armor. Default: false")
                    .define("WrittenBookColoring", false);
            builder.pop();

            builder.push("UI");
            TWO_PAGE_SCREEN = builder
                    .comment("Vanilla book screens will be replaced with a two-paged ones. Default: true")
                    .define("TwoPageScreen", true);
            LECTERN_TWO_PAGE_SCREEN = builder
                    .comment("Lectern book screen will replace the vanilla one. Can be disabled if you want to use different screen for lectern (such as from Amendments). Default: true")
                    .define("LecternTwoPageScreen", true);
            SNEAK_OPENS_VANILLA_SCREEN = builder
                    .comment("Holding sneak while using a book screen will show vanilla screen. Default: false")
                    .define("SneakingOpensVanillaScreen", false);
            SURVIVAL_FORMATTING = builder
                    .comment("Allow inserting formatting symbol (section sign) for players in survival mode." +
                                    "When set to true - hotkey or a button can be used to paste a formatting symbol.",
                            "Default: true")
                    .define("SurvivalFormatting", true);
            builder.pop();

            builder.push("QOL");
            CHISELED_BOOKSHELF_TOOLTIP = builder
                    .comment("Hovering over a slot in a Chiseled Bookshelf will show tooltip of a book that's stored in that slot. Default: true")
                    .define("ChiseledBookshelfTooltip", true);
            BOOK_ENCHANTMENT_GLINT = builder
                    .comment("Written books will have an enchantment glint on them. Default: false")
                    .define("EnchantmentGlint", false);
            builder.pop();

            builder.push("LecternScreen");

            builder.pop();

            builder.push("Misc");

            builder.pop();

            SPEC = builder.build();
        }
    }

    public static class Client {
        public static final ForgeConfigSpec SPEC;

        // UI
        public static final ForgeConfigSpec.BooleanValue SCREEN_PAUSE;
        public static final ForgeConfigSpec.BooleanValue SHOW_DONE_BUTTON;

        // Colors
        public static final ForgeConfigSpec.ConfigValue<String> TEXT_COLOR;
        public static final ForgeConfigSpec.ConfigValue<String> PAGE_NUMBERS_COLOR;
        public static final ForgeConfigSpec.ConfigValue<String> ENTER_TITLE_COLOR;
        public static final ForgeConfigSpec.ConfigValue<String> BY_AUTHOR_COLOR;
        public static final ForgeConfigSpec.ConfigValue<String> SELECTION_COLOR;
        public static final ForgeConfigSpec.ConfigValue<String> SELECTION_UNFOCUSED_COLOR;

        // Misc
        public static final ForgeConfigSpec.BooleanValue SHOW_BOOK_EDIT_SCREEN_TUTORIAL;

        static {
            ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

            {
                builder.push("ui");

                SCREEN_PAUSE = builder
                        .comment("Singleplayer game will be paused when book screen is open.",
                                "Default: false, Vanilla: true")
                        .define("pause", false);

                SHOW_DONE_BUTTON = builder
                        .comment("Show 'Done' button in the Scholar book screen. Default: false")
                        .define("show_done_button", false);

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

            {
                builder.push("misc");

                SHOW_BOOK_EDIT_SCREEN_TUTORIAL = builder
                        .comment("'Press F1 for additional editing tools' toast will be shown when book editing UI is first opened.",
                                "This setting will be set to 'false' automatically after first show. Default: true")
                        .define("show_book_edit_screen_tutorial", true);

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
