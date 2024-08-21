package io.github.mortuusars.scholar.mixin;

import io.github.mortuusars.scholar.Config;
import io.github.mortuusars.scholar.PlatformHelper;
import io.github.mortuusars.scholar.menu.LecternSpreadMenu;
import io.github.mortuusars.scholar.book.BookColor;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LecternBlock;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LecternBlock.class)
public abstract class LecternBlockMixin {
    @Inject(method = "openScreen", at = @At(value = "HEAD"), cancellable = true)
    private void openScreen(Level level, BlockPos pos, Player player, CallbackInfo ci) {
        if (!Config.Common.TWO_PAGE_SCREEN.get() || !Config.Common.LECTERN_TWO_PAGE_SCREEN.get()) {
            return;
        }

        if (Config.Common.SNEAK_OPENS_VANILLA_SCREEN.get() && player.isSecondaryUseActive()) {
            return;
        }

        if (!(player instanceof ServerPlayer serverPlayer)
                || !(level.getBlockEntity(pos) instanceof LecternBlockEntity lecternBlockEntity)) {
            return;
        }

        ItemStack bookStack = lecternBlockEntity.getBook();
        if (!bookStack.is(Items.WRITABLE_BOOK) && !bookStack.is(Items.WRITTEN_BOOK)) {
            return;
        }

        scholar$openGUI(serverPlayer, lecternBlockEntity, lecternBlockEntity.getBook());
        player.awardStat(Stats.INTERACT_WITH_LECTERN);
        ci.cancel();
    }

    @Unique
    private void scholar$openGUI(ServerPlayer player, LecternBlockEntity lecternBlockEntity, ItemStack bookStack) {
        int bookColor = BookColor.get(bookStack);
        MenuProvider menuProvider = new MenuProvider() {
            @Override
            public @NotNull Component getDisplayName() {
                return bookStack.getHoverName();
            }

            @Override
            public @NotNull AbstractContainerMenu createMenu(int containerId, @NotNull Inventory playerInventory, @NotNull Player player) {
                Container bookAccess = lecternBlockEntity.bookAccess;
                ContainerData dataAccess = lecternBlockEntity.dataAccess;
                return new LecternSpreadMenu(containerId, bookAccess, dataAccess, bookColor);
            }
        };

        PlatformHelper.openMenu(player, menuProvider, buffer -> {
            buffer.writeItem(bookStack);
        });
    }
}
