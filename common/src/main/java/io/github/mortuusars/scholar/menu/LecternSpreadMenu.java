package io.github.mortuusars.scholar.menu;

import io.github.mortuusars.scholar.Scholar;
import io.github.mortuusars.scholar.book.Spread;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.LecternMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.WrittenBookItem;
import org.jetbrains.annotations.NotNull;

public class LecternSpreadMenu extends LecternMenu {
    protected final Container lecternContainer;
    protected final BlockPos lecternPos;

    public LecternSpreadMenu(int containerId, Container lectern, ContainerData lecternData, BlockPos lecternPos) {
        super(containerId, lectern, lecternData);
        this.lecternContainer = lectern;
        this.lecternPos = lecternPos;
    }

    public static LecternSpreadMenu fromBuffer(int containerId, Inventory inventory, FriendlyByteBuf buffer) {
        return new LecternSpreadMenu(containerId, new SimpleContainer(buffer.readItem()), new SimpleContainerData(1), buffer.readBlockPos());
    }

    @Override
    public @NotNull MenuType<?> getType() {
        return Scholar.MenuTypes.LECTERN_SPREAD_BOOK_VIEW.get();
    }

    public BlockPos getLecternPos() {
        return lecternPos;
    }

    protected int getPageCount() {
        return WrittenBookItem.getPageCount(getBook());
    }

    protected int getSpreadCount() {
        return Mth.ceil(getPageCount() / 2.0);
    }

    protected int getCurrentSpread() {
        return getPage() / 2;
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
            this.setData(0, newPageIndex);
            return true;
        }

        return super.clickMenuButton(player, buttonId);
    }
}
