package io.github.mortuusars.scholar.menu;

import io.github.mortuusars.scholar.Scholar;
import io.github.mortuusars.scholar.book.Spread;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.SimpleContainerData;
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
}