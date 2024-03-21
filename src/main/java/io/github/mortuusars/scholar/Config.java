package io.github.mortuusars.scholar;

import com.mojang.logging.LogUtils;
import net.minecraftforge.common.ForgeConfigSpec;


public class Config {
    public static class Common {
        public static final ForgeConfigSpec SPEC;

        public static final ForgeConfigSpec.BooleanValue WRITTEN_BOOK_GLINT_ENABLED;

        static {
            ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

            WRITTEN_BOOK_GLINT_ENABLED = builder
                    .comment("Written books will have an enchantment glint on them. Default: false")
                    .define("WrittenBookEnchantmentGlint", false);

            SPEC = builder.build();
        }
    }

    public static class Client {
        public static final ForgeConfigSpec SPEC;

        public static final ForgeConfigSpec.ConfigValue<String> MAIN_FONT_COLOR;
        public static final ForgeConfigSpec.ConfigValue<String> SECONDARY_FONT_COLOR;

        // EDIT
        public static final ForgeConfigSpec.BooleanValue SCHOLAR_EDIT_SCREEN_ENABLED;
        public static final ForgeConfigSpec.BooleanValue SNEAK_OPENS_VANILLA_EDIT_SCREEN;
//        public static final ForgeConfigSpec.BooleanValue BOOK_EDIT_SCREEN_SHOW_DONE_BUTTON;
        public static final ForgeConfigSpec.BooleanValue BOOK_EDIT_SCREEN_PAUSE;

        // VIEW
        public static final ForgeConfigSpec.BooleanValue SCHOLAR_VIEW_SCREEN_ENABLED;
        public static final ForgeConfigSpec.BooleanValue SNEAK_OPENS_VANILLA_VIEW_SCREEN;
        public static final ForgeConfigSpec.BooleanValue BOOK_VIEW_SCREEN_SHOW_DONE_BUTTON;
        public static final ForgeConfigSpec.BooleanValue BOOK_VIEW_SCREEN_PAUSE;


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

            builder.push("BookEditScreen");
            SCHOLAR_EDIT_SCREEN_ENABLED = builder
                    .comment("Scholar book edit screen will replace the vanilla one. Default: true")
                    .define("ReplaceBookEditScreen", true);

            SNEAK_OPENS_VANILLA_EDIT_SCREEN = builder
                    .comment("Vanilla book edit screen will open when the player is sneaking. Default: false")
                    .define("VanillaBookEditScreenWhenSneaking", false);

//            BOOK_EDIT_SCREEN_SHOW_DONE_BUTTON = builder
//                    .comment("Show 'Done' button in the Scholar book edit screen. Default: false")
//                    .define("ShowDoneButton", false);

            BOOK_EDIT_SCREEN_PAUSE = builder
                    .comment("Singleplayer game will be paused when book edit screen is open.",
                            "This will affect vanilla book edit screen as well",
                            "Set to 'true' to restore vanilla behavior.",
                            "Default: false")
                    .define("BookEditScreenPause", false);
            builder.pop();

            builder.push("BookViewScreen");
            SCHOLAR_VIEW_SCREEN_ENABLED = builder
                    .comment("Scholar book view screen will replace the vanilla one. Default: true")
                    .define("ReplaceBookViewScreen", true);

            SNEAK_OPENS_VANILLA_VIEW_SCREEN = builder
                    .comment("Vanilla book view screen will open when the player is sneaking. Default: false")
                    .define("VanillaBookViewScreenWhenSneaking", false);

            BOOK_VIEW_SCREEN_SHOW_DONE_BUTTON = builder
                    .comment("Show 'Done' button in the Scholar book view screen. Default: false")
                    .define("ShowDoneButton", false);

            BOOK_VIEW_SCREEN_PAUSE = builder
                    .comment("Singleplayer game will be paused when book view screen is open.",
                            "This will affect vanilla book view screen as well",
                            "Set to 'true' to restore vanilla behavior.",
                            "Default: false")
                    .define("BookViewScreenPause", false);
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
