package io.github.mortuusars.scholar.fabric;

import fuzs.forgeconfigapiport.api.config.v2.ForgeConfigRegistry;
import io.github.mortuusars.scholar.Config;
import io.github.mortuusars.scholar.Scholar;
import net.fabricmc.api.ModInitializer;
import net.minecraftforge.fml.config.ModConfig;

public class ScholarFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        ForgeConfigRegistry.INSTANCE.register(Scholar.ID, ModConfig.Type.COMMON, Config.Common.SPEC);
        ForgeConfigRegistry.INSTANCE.register(Scholar.ID, ModConfig.Type.CLIENT, Config.Client.SPEC);

        Scholar.init();
    }
}