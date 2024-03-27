package io.github.mortuusars.scholar.menu;

import io.github.mortuusars.scholar.Scholar;
import io.github.mortuusars.scholar.util.BookHelper;
import io.github.mortuusars.scholar.visual.BookColors;
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
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class LecternSpreadMenu extends LecternMenu {
    public static final int PREV_PAGE_ID = 1;
    public static final int NEXT_PAGE_ID = 2;

    private final int bookColor;
    private final int spreads;

    public LecternSpreadMenu(int containerId, Container lectern, ContainerData lecternData, int bookColor) {
        super(containerId, lectern, lecternData);
        this.bookColor = bookColor;
        this.spreads = (int) Math.ceil(BookHelper.getPageCount(getBook()) / 2f);

        // Corrects page to the closest even number (down)
        if (getPage() % 2 != 0) {
            int correctedPage = Mth.clamp(getPage() - 1, 0, 98);
            setData(0, correctedPage);
        }
    }

    public static LecternSpreadMenu fromBuffer(int containerId, Inventory inventory, FriendlyByteBuf buffer) {
        ItemStack bookStack = buffer.readItem();
        int bookColor = BookColors.fromStack(bookStack);
        return new LecternSpreadMenu(containerId, new SimpleContainer(bookStack), new SimpleContainerData(1), bookColor);
    }

    public int getBookColor() {
        return bookColor;
    }

    @Override
    public @NotNull MenuType<?> getType() {
        return Scholar.MenuTypes.LECTERN.get();
    }

    @Override
    public boolean clickMenuButton(Player player, int buttonId) {
        if (buttonId == PREV_PAGE_ID || buttonId == NEXT_PAGE_ID) {
            int currentSpread = getCurrentSpread();

            int change = buttonId == PREV_PAGE_ID ? -1 : 1;

            int newSpreadIndex = currentSpread + change;

            if (newSpreadIndex < 0 || newSpreadIndex + 1 > spreads)
                return true;

            int newPageIndex = newSpreadIndex * 2;
            this.setData(0, newPageIndex);
            return true;
        }

        return super.clickMenuButton(player, buttonId);
    }

    protected int getCurrentSpread() {
        return getPage() / 2;
    }
}
