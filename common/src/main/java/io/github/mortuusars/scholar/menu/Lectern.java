package io.github.mortuusars.scholar.menu;

import io.github.mortuusars.scholar.PlatformHelper;
import io.github.mortuusars.scholar.Register;
import io.github.mortuusars.scholar.mixin.accessor.LecternBlockEntityAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
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
                Container bookAccess = lecternBlockEntity instanceof LecternBlockEntityAccessor accessor ? accessor.getBookAccess() : new SimpleContainer(1);
                ContainerData dataAccess = lecternBlockEntity instanceof LecternBlockEntityAccessor accessor ? accessor.getDataAccess() : new SimpleContainerData(1);
                return new LecternSpreadMenu(containerId, bookAccess, dataAccess, lecternBlockEntity.getBlockPos());
            }
        };

        PlatformHelper.openMenu(player, menuProvider, new Lectern.Data(lecternBlockEntity.getBlockPos(), bookStack));
    }

    public static void openBookEditMenu(ServerPlayer player, LecternBlockEntity lecternBlockEntity, ItemStack bookStack) {
        MenuProvider menuProvider = new MenuProvider() {
            @Override
            public @NotNull Component getDisplayName() {
                return bookStack.getHoverName();
            }

            @Override
            public @NotNull AbstractContainerMenu createMenu(int containerId, @NotNull Inventory playerInventory, @NotNull Player player) {
                Container bookAccess = lecternBlockEntity instanceof LecternBlockEntityAccessor accessor ? accessor.getBookAccess() : new SimpleContainer(1);
                ContainerData dataAccess = lecternBlockEntity instanceof LecternBlockEntityAccessor accessor ? accessor.getDataAccess() : new SimpleContainerData(1);
                return new LecternSpreadBookEditMenu(containerId, bookAccess, dataAccess, lecternBlockEntity.getBlockPos());
            }
        };

        PlatformHelper.openMenu(player, menuProvider, new Lectern.Data(lecternBlockEntity.getBlockPos(), bookStack));
    }

    public record Data(BlockPos pos, ItemStack book) implements Register.MenuData<Data> {
        public static final StreamCodec<RegistryFriendlyByteBuf, Data> STREAM_CODEC = StreamCodec.composite(
              BlockPos.STREAM_CODEC, Data::pos,
              ItemStack.STREAM_CODEC, Data::book,
              Data::new
        );

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, Data> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
