package io.github.mortuusars.scholar.menu;

import io.github.mortuusars.scholar.Scholar;
import io.github.mortuusars.scholar.mixin.LecternBlockEntityAccessor;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;

public class LecternSpreadMenu extends LecternMenu {
    private final ContainerData lecternData;
    private final int bookColor;

    public LecternSpreadMenu(int containerId, Container lectern, ContainerData lecternData, int bookColor) {
        super(containerId, lectern, lecternData);
        this.lecternData = lecternData;
        this.bookColor = bookColor;

        // Corrects page to the closest even number (down)
        if (getPage() % 2 != 0) {
            int correctedPage = Mth.clamp(getPage() - 1, 0, 98);
            setData(0, correctedPage);
        }
    }

    public int getBookColor() {
        return bookColor;
    }

    public static LecternSpreadMenu fromBuffer(int containerId, Inventory inventory, FriendlyByteBuf buffer) {
        BlockPos lecternPos = buffer.readBlockPos();
        ItemStack bookStack = buffer.readItem();
        int bookColor = buffer.readInt();
        Player player = inventory.player;
        BlockEntity blockEntity = player.level().getBlockEntity(lecternPos);
        if (blockEntity instanceof LecternBlockEntityAccessor lectern) {
            return new LecternSpreadMenu(containerId, new SimpleContainer(bookStack), new SimpleContainerData(1), bookColor);
        } else
            throw new IllegalStateException("Cannot access lectern block entity.");
    }

    @Override
    public @NotNull MenuType<?> getType() {
        return Scholar.MenuTypes.LECTERN.get();
    }

    @Override
    public boolean clickMenuButton(Player player, int buttonId) {
        if (buttonId == 1) {
            int pageIndex = this.lecternData.get(0);
            int pageOnNextSpread = pageIndex - 2;
            pageOnNextSpread = pageOnNextSpread + (pageOnNextSpread % 2 == 0 ? 0 : 1);
            this.setData(0, Mth.clamp(pageOnNextSpread, 0, 98));
            return true;
        }

        if (buttonId == 2) {
            int pageIndex = this.lecternData.get(0);
            int pageOnNextSpread = pageIndex + 2;
            pageOnNextSpread = pageOnNextSpread + (pageOnNextSpread % 2 == 0 ? 0 : 1);
            this.setData(0, Mth.clamp(pageOnNextSpread, 0, 98));
            return true;
        }

        return super.clickMenuButton(player, buttonId);
    }

    @Override
    public int getPage() {
        return super.getPage();
    }

    @Override
    public boolean stillValid(Player player) {
        return super.stillValid(player);
    }
}
