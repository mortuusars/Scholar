package io.github.mortuusars.scholar;

import com.mojang.logging.LogUtils;
import net.minecraftforge.common.ForgeConfigSpec;


public class Config {
    public static class Common {
        public static final ForgeConfigSpec SPEC;
        //TODO: Move some to client config?
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

        public static final ForgeConfigSpec.ConfigValue<String> MAIN_FONT_COLOR;
        public static final ForgeConfigSpec.ConfigValue<String> SECONDARY_FONT_COLOR;
        public static final ForgeConfigSpec.ConfigValue<String> ENTER_TITLE_FONT_COLOR;
        public static final ForgeConfigSpec.ConfigValue<String> BY_AUTHOR_FONT_COLOR;

        public static final ForgeConfigSpec.BooleanValue WRITABLE_SHOW_DONE_BUTTON;
        public static final ForgeConfigSpec.BooleanValue WRITABLE_PAUSE;

        public static final ForgeConfigSpec.BooleanValue WRITTEN_SHOW_DONE_BUTTON;
        public static final ForgeConfigSpec.BooleanValue WRITTEN_PAUSE;

        public static final ForgeConfigSpec.BooleanValue LECTERN_SHOW_DONE_BUTTON;
        public static final ForgeConfigSpec.BooleanValue LECTERN_PAUSE;

        static {
            ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

            String defaultMainFontColor = "FF7B593D";
            MAIN_FONT_COLOR = builder
                    .comment("Color of the book text. Default: " + defaultMainFontColor)
                    .define("MainFontColor", defaultMainFontColor);

            String defaultSecondaryFontColor = "FFEFE4CA";
            SECONDARY_FONT_COLOR = builder
                    .comment("Color of the secondary text (page numbers, etc). Default: " + defaultSecondaryFontColor)
                    .define("SecondaryFontColor", defaultSecondaryFontColor);

            String defaultEnterTitleFontColor = "FFF5EBD0";
            ENTER_TITLE_FONT_COLOR = builder
                    .comment("Color of the 'Enter Book Title' text on a signing screen. Default: " + defaultEnterTitleFontColor)
                    .define("EnterTitleFontColor", defaultEnterTitleFontColor);

            String defaultByAuthorFontColor = "FFC7B496";
            BY_AUTHOR_FONT_COLOR = builder
                    .comment("Color of the 'by <author>' text on a signing screen. Default: " + defaultByAuthorFontColor)
                    .define("ByAuthorFontColor", defaultByAuthorFontColor);


            builder.push("WritableBookScreen");
            WRITABLE_SHOW_DONE_BUTTON = builder
                    .comment("Show 'Done' button in the Scholar book edit screen. Default: false")
                    .define("ShowDoneButton", false);

            WRITABLE_PAUSE = builder
                    .comment("Singleplayer game will be paused when book edit screen is open.",
                            "This will affect vanilla book edit screen as well",
                            "Set to 'true' to restore vanilla behavior.",
                            "Default: false")
                    .define("Pause", false);
            builder.pop();

            builder.push("WrittenBookScreen");
            WRITTEN_SHOW_DONE_BUTTON = builder
                    .comment("Show 'Done' button in the Scholar book view screen. Default: false")
                    .define("ShowDoneButton", false);

            WRITTEN_PAUSE = builder
                    .comment("Singleplayer game will be paused when book view screen is open.",
                            "This will affect vanilla book view screen as well",
                            "Set to 'true' to restore vanilla behavior.",
                            "Default: false")
                    .define("Pause", false);
            builder.pop();

            builder.push("LecternScreen");
            LECTERN_SHOW_DONE_BUTTON = builder
                    .comment("Show 'Done' button in the Scholar lectern screen. Default: false")
                    .define("ShowDoneButton", false);

            LECTERN_PAUSE = builder
                    .comment("Singleplayer game will be paused when lectern screen is open.",
                            "This will affect vanilla lectern screen as well",
                            "Set to 'true' to restore vanilla behavior.",
                            "Default: false")
                    .define("Pause", false);
            builder.pop();

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
