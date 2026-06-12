package io.github.mortuusars.scholar.fabric;

import io.github.mortuusars.scholar.Register;
import net.fabricmc.fabric.api.menu.v1.ExtendedMenuProvider;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlatformHelperImpl {
    public static <D extends Register.MenuData<D>> void openMenu(ServerPlayer serverPlayer, MenuProvider menuProvider, D data) {
        serverPlayer.openMenu(new ExtendedMenuProvider<>() {
            @Nullable
            @Override
            public AbstractContainerMenu createMenu(int id, @NotNull Inventory inventory, @NotNull Player player) {
                return menuProvider.createMenu(id, inventory, player);
            }

            @Override
            public @NotNull Component getDisplayName() {
                return menuProvider.getDisplayName();
            }

            @Override
            public D getScreenOpeningData(ServerPlayer player) {
                return data;
            }
        });
    }

    public static boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }

    public static boolean isModLoading(String modId) {
        return isModLoaded(modId); // On Fabric, we can use the same method.
    }
}
