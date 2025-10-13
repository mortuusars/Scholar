package io.github.mortuusars.scholar.menu;

import io.github.mortuusars.scholar.Scholar;
import io.github.mortuusars.scholar.book.Spread;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
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
import net.minecraft.world.item.component.WritableBookContent;
import net.minecraft.world.item.component.WrittenBookContent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LecternSpreadMenu extends LecternMenu {
    protected final Container lecternContainer;
    protected final BlockPos lecternPos;

    public LecternSpreadMenu(int containerId, Container lectern, ContainerData lecternData, BlockPos lecternPos) {
        super(containerId, lectern, lecternData);
        this.lecternContainer = lectern;
        this.lecternPos = lecternPos;
    }

    public static LecternSpreadMenu fromBuffer(int containerId, Inventory inventory, RegistryFriendlyByteBuf buffer) {
        return new LecternSpreadMenu(containerId, new SimpleContainer(ItemStack.STREAM_CODEC.decode(buffer)), new SimpleContainerData(1), buffer.readBlockPos());
    }

    @Override
    public @NotNull MenuType<?> getType() {
        return Scholar.MenuTypes.LECTERN_SPREAD_BOOK_VIEW.get();
    }

    public BlockPos getLecternPos() {
        return lecternPos;
    }

    protected int getPageCount() {
        @Nullable WrittenBookContent content = getBook().get(DataComponents.WRITTEN_BOOK_CONTENT);
        if (content != null) {
            return content.pages().size();
        }

        @Nullable WritableBookContent writableContent = getBook().get(DataComponents.WRITABLE_BOOK_CONTENT);
        if (writableContent != null) {
            return writableContent.pages().size();
        }

        return 0;
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
