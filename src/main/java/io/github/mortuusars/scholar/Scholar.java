package io.github.mortuusars.scholar;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

@Mod(Scholar.ID)
public class Scholar {
    public static final String ID = "scholar";

    public Scholar() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.Common.SPEC);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.Client.SPEC);

        /*
        give Dev written_book{pages:[
        "{}"
        ]
         */
    }

    public static ResourceLocation resource(String path) {
        return new ResourceLocation(Scholar.ID, path);
    }
}

