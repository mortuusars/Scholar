package io.github.mortuusars.scholar.menu;

import io.github.mortuusars.scholar.Lectern;
import io.github.mortuusars.scholar.Scholar;
import io.github.mortuusars.scholar.book.Spread;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.WritableBookItem;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import org.jetbrains.annotations.NotNull;

public class LecternSpreadBookEditMenu extends LecternSpreadMenu {
    public LecternSpreadBookEditMenu(int containerId, Container lectern, ContainerData lecternData, BlockPos lecternPos) {
        super(containerId, lectern, lecternData, lecternPos);
    }

    public static LecternSpreadBookEditMenu fromBuffer(int containerId, Inventory inventory, FriendlyByteBuf buffer) {
        return new LecternSpreadBookEditMenu(containerId, new SimpleContainer(buffer.readItem()),
                new SimpleContainerData(1), buffer.readBlockPos());
    }

    @Override
    public @NotNull MenuType<?> getType() {
        return Scholar.MenuTypes.LECTERN_SPREAD_BOOK_EDIT.get();
    }

    @Override
    protected int getPageCount() {
        return 100;
    }

    @Override
    protected int getSpreadCount() {
        return 50;
    }

    @Override
    public boolean clickMenuButton(Player player, int buttonId) {
        if (buttonId == BUTTON_PREV_PAGE || buttonId == BUTTON_NEXT_PAGE) {
            int currentSpreadIndex = getCurrentSpread();
            int newSpreadIndex = currentSpreadIndex + (buttonId == BUTTON_PREV_PAGE ? -1 : 1);

            if (newSpreadIndex < 0 || newSpreadIndex > getSpreadCount() - 1) {
                return true;
            }

            int newPageIndex = Spread.Side.LEFT.getPageIndexFromSpread(newSpreadIndex);
            if (player.level().getBlockEntity(getLecternPos()) instanceof LecternBlockEntity lecternBlockEntity) {
                lecternBlockEntity.pageCount = Math.min(100, Spread.Side.RIGHT.getPageIndexFromSpread(newSpreadIndex) + 2);
            }

            this.setData(0, newPageIndex);
            return true;
        }

        return super.clickMenuButton(player, buttonId);
    }

    @Override
    public void removed(Player player) {
        super.removed(player);

        // If another player(s) is viewing the same lectern - transfer editing mode to him.
        // If Writable Book is signed, other players would not have their screens closed, however.
        // But that's not a problem, they can't screw up anything anyway.
        // Their view will be the same as if they'd opened Written Book.

        if (player instanceof ServerPlayer serverPlayer
                && player.level().getBlockEntity(getLecternPos()) instanceof LecternBlockEntity be
                && be.hasBook()) {
            serverPlayer.serverLevel().players().stream()
                    .filter(pl -> !pl.equals(serverPlayer)
                            && pl.containerMenu instanceof LecternSpreadMenu lecternMenu
                            && lecternMenu.getLecternPos().equals(getLecternPos()))
                    .findFirst()
                    .ifPresent(pl -> {
                        pl.closeContainer();
                        if (be.getBook().getItem() instanceof WritableBookItem) {
                            Lectern.openBookEditMenu(pl, be, be.getBook());
                        }
                    });
        }
    }
}