package io.github.mortuusars.scholar.neoforge;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.LoadingModList;

import java.util.function.Consumer;

public class PlatformHelperImpl {
    public static void openMenu(ServerPlayer serverPlayer, MenuProvider menuProvider, Consumer<RegistryFriendlyByteBuf> extraDataWriter) {
        serverPlayer.openMenu(menuProvider, extraDataWriter);
    }

    public static boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }

    public static boolean isModLoading(String modId) {
        return LoadingModList.get().getModFileById(modId) != null;
    }
}
