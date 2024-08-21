package io.github.mortuusars.scholar.fabric;

import fuzs.forgeconfigapiport.api.config.v2.ForgeConfigRegistry;
import io.github.mortuusars.scholar.Config;
import io.github.mortuusars.scholar.Scholar;
import net.fabricmc.api.ModInitializer;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.world.item.*;
import net.minecraftforge.fml.config.ModConfig;

public class ScholarFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        ForgeConfigRegistry.INSTANCE.register(Scholar.ID, ModConfig.Type.COMMON, Config.Common.SPEC);
        ForgeConfigRegistry.INSTANCE.register(Scholar.ID, ModConfig.Type.CLIENT, Config.Client.SPEC);

        Scholar.init();

        if (Config.Common.WRITABLE_BOOK_COLORING.get()) {
            CauldronInteraction.WATER.put(Items.WRITABLE_BOOK, CauldronInteraction.DYED_ITEM);
        }

        if (Config.Common.WRITTEN_BOOK_COLORING.get()) {
            CauldronInteraction.WATER.put(Items.WRITTEN_BOOK, CauldronInteraction.DYED_ITEM);
        }
    }
}