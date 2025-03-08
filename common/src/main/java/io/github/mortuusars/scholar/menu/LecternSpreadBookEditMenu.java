package io.github.mortuusars.scholar.menu;

import io.github.mortuusars.scholar.Scholar;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.SimpleContainerData;
import org.jetbrains.annotations.NotNull;

public class LecternSpreadBookEditMenu extends LecternSpreadMenu {
    public LecternSpreadBookEditMenu(int containerId, Container lectern, ContainerData lecternData) {
        super(containerId, lectern, lecternData);
    }

    public static LecternSpreadBookEditMenu fromBuffer(int containerId, Inventory inventory, FriendlyByteBuf buffer) {
        return new LecternSpreadBookEditMenu(containerId, new SimpleContainer(buffer.readItem()), new SimpleContainerData(1));
    }

    @Override
    public @NotNull MenuType<?> getType() {
        return Scholar.MenuTypes.LECTERN_SPREAD_BOOK_EDIT.get();
    }
}
