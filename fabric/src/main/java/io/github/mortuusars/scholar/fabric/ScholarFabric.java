package io.github.mortuusars.scholar.fabric;

import fuzs.forgeconfigapiport.fabric.api.neoforge.v4.NeoForgeConfigRegistry;
import io.github.mortuusars.scholar.Config;
import io.github.mortuusars.scholar.Scholar;
import io.github.mortuusars.scholar.network.fabric.FabricC2SPackets;
import io.github.mortuusars.scholar.network.fabric.FabricS2CPackets;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.*;
import net.neoforged.fml.config.ModConfig;
import org.jetbrains.annotations.Nullable;

public class ScholarFabric implements ModInitializer {
    // Server field to access when no other objects are available to get it from.
    public static @Nullable MinecraftServer server = null;

    @Override
    public void onInitialize() {
        NeoForgeConfigRegistry.INSTANCE.register(Scholar.ID, ModConfig.Type.COMMON, Config.Common.SPEC);
        NeoForgeConfigRegistry.INSTANCE.register(Scholar.ID, ModConfig.Type.CLIENT, Config.Client.SPEC);

        Scholar.init();

        CauldronInteraction.WATER.map().put(Items.WRITABLE_BOOK, CauldronInteraction.WATER.map().get(Items.LEATHER_HELMET));
        CauldronInteraction.WATER.map().put(Items.WRITTEN_BOOK, CauldronInteraction.WATER.map().get(Items.LEATHER_HELMET));

        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            ScholarFabric.server = server;
        });
        ServerLifecycleEvents.SERVER_STOPPED.register(server -> {
            ScholarFabric.server = null;
        });

        FabricC2SPackets.register();
        FabricS2CPackets.register();
    }
}