package io.github.mortuusars.scholar.menu;

import io.github.mortuusars.scholar.PlatformHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import org.jetbrains.annotations.NotNull;

public class Lectern {
    public static void openBookViewMenu(ServerPlayer player, LecternBlockEntity lecternBlockEntity, ItemStack bookStack) {
        MenuProvider menuProvider = new MenuProvider() {
            @Override
            public @NotNull Component getDisplayName() {
                return bookStack.getHoverName();
            }

            @Override
            public @NotNull AbstractContainerMenu createMenu(int containerId, @NotNull Inventory playerInventory, @NotNull Player player) {
                Container bookAccess = lecternBlockEntity.bookAccess;
                ContainerData dataAccess = lecternBlockEntity.dataAccess;
                return new LecternSpreadMenu(containerId, bookAccess, dataAccess, lecternBlockEntity.getBlockPos());
            }
        };

        PlatformHelper.openMenu(player, menuProvider, buffer -> {
            ItemStack.STREAM_CODEC.encode(buffer, bookStack);
            buffer.writeBlockPos(lecternBlockEntity.getBlockPos());
        });
    }

    public static void openBookEditMenu(ServerPlayer player, LecternBlockEntity lecternBlockEntity, ItemStack bookStack) {
        MenuProvider menuProvider = new MenuProvider() {
            @Override
            public @NotNull Component getDisplayName() {
                return bookStack.getHoverName();
            }

            @Override
            public @NotNull AbstractContainerMenu createMenu(int containerId, @NotNull Inventory playerInventory, @NotNull Player player) {
                Container bookAccess = lecternBlockEntity.bookAccess;
                ContainerData dataAccess = lecternBlockEntity.dataAccess;
                return new LecternSpreadBookEditMenu(containerId, bookAccess, dataAccess, lecternBlockEntity.getBlockPos());
            }
        };

        PlatformHelper.openMenu(player, menuProvider, buffer -> {
            ItemStack.STREAM_CODEC.encode(buffer, bookStack);
            buffer.writeBlockPos(lecternBlockEntity.getBlockPos());
        });
    }
}
