package io.github.mortuusars.scholar.integration;

import io.github.mortuusars.scholar.PlatformHelper;

public class Mods {
    public static final Mod MCBV = new Mod("lolmcbv");
    public static final Mod WOODWORKS = new Mod("woodworks");
    public static final Mod WOODSTER = new Mod("woodster");

    public record Mod(String id) {
        public boolean isLoaded() {
            return PlatformHelper.isModLoaded(id);
        }

        public boolean isLoading() {
            return PlatformHelper.isModLoading(id);
        }
    }
}
