package io.github.mortuusars.scholar;

import com.mojang.logging.LogUtils;
import net.minecraftforge.common.ForgeConfigSpec;


public class Config {
    public static class Common {
        public static final ForgeConfigSpec SPEC;

        public static final ForgeConfigSpec.BooleanValue WRITABLE_REPLACE_VANILLA_SCREEN;
        public static final ForgeConfigSpec.BooleanValue WRITABLE_SNEAK_OPENS_VANILLA_SCREEN;
        public static final ForgeConfigSpec.BooleanValue WRITABLE_SURVIVAL_FORMATTING;


        public static final ForgeConfigSpec.BooleanValue WRITTEN_REPLACE_VANILLA_SCREEN;
        public static final ForgeConfigSpec.BooleanValue WRITTEN_SNEAK_OPENS_VANILLA_SCREEN;
        public static final ForgeConfigSpec.BooleanValue WRITTEN_GLINT_ENABLED;

        public static final ForgeConfigSpec.BooleanValue LECTERN_REPLACE_VANILLA_SCREEN;
        public static final ForgeConfigSpec.BooleanValue LECTERN_SNEAK_OPENS_VANILLA_SCREEN;

        static {
            ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

            builder.push("WritableBookScreen");
            WRITABLE_REPLACE_VANILLA_SCREEN = builder
                    .comment("Scholar book edit screen will replace the vanilla one. Scholar signing screen will not be available if disabled. Default: true")
                    .define("ReplaceVanillaScreen", true);

            WRITABLE_SNEAK_OPENS_VANILLA_SCREEN = builder
                    .comment("Vanilla book edit screen will open when the player is sneaking. Default: false")
                    .define("SneakingOpensVanillaScreen", false);
            WRITABLE_SURVIVAL_FORMATTING = builder
                    .comment("Allow inserting formatting symbol (section sign) for players in survival mode." +
                                    "When set to true - hotkey or a button can be used to paste a formatting symbol.",
                            "Default: true")
                    .define("SurvivalFormatting", true);
            builder.pop();

            builder.push("WrittenBookScreen");
            WRITTEN_REPLACE_VANILLA_SCREEN = builder
                    .comment("Scholar book view screen will replace the vanilla one. Default: true")
                    .define("ReplaceVanillaScreen", true);

            WRITTEN_SNEAK_OPENS_VANILLA_SCREEN = builder
                    .comment("Vanilla book view screen will open when the player is sneaking. Default: false")
                    .define("SneakingOpensVanillaScreen", false);
            WRITTEN_GLINT_ENABLED = builder
                    .comment("Written books will have an enchantment glint on them. Default: false")
                    .define("EnchantmentGlint", false);
            builder.pop();

            builder.push("LecternScreen");
            LECTERN_REPLACE_VANILLA_SCREEN = builder
                    .comment("Scholar lectern screen will replace the vanilla one. Default: true")
                    .define("ReplaceVanillaScreen", true);

            LECTERN_SNEAK_OPENS_VANILLA_SCREEN = builder
                    .comment("Vanilla lectern screen will open when the player is sneaking. Default: false")
                    .define("SneakingOpensVanillaScreen", false);
            builder.pop();

            SPEC = builder.build();
        }
    }

    public static class Client {
        public static final ForgeConfigSpec SPEC;

        public static final ForgeConfigSpec.ConfigValue<String> MAIN_FONT_COLOR;
        public static final ForgeConfigSpec.ConfigValue<String> SECONDARY_FONT_COLOR;

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
