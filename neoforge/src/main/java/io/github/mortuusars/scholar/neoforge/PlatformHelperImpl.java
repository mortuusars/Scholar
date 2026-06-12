package io.github.mortuusars.scholar.neoforge;

import io.github.mortuusars.scholar.Register;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;

public class PlatformHelperImpl {
    public static <D extends Register.MenuData<D>> void openMenu(ServerPlayer serverPlayer, MenuProvider menuProvider, D data) {
        serverPlayer.openMenu(menuProvider, buffer -> data.streamCodec().encode(buffer, data));
    }

    public static boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }

    public static boolean isModLoading(String modId) {
        return FMLLoader.getCurrent().getLoadingModList().getModFileById(modId) != null;
    }
}
