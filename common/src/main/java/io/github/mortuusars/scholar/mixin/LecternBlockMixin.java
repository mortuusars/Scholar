package io.github.mortuusars.scholar.mixin;

import io.github.mortuusars.scholar.Config;
import io.github.mortuusars.scholar.PlatformHelper;
import io.github.mortuusars.scholar.menu.LecternSpreadMenu;
import io.github.mortuusars.scholar.visual.BookColors;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.WritableBookItem;
import net.minecraft.world.item.WrittenBookItem;
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
        if (level.getBlockEntity(pos) instanceof LecternBlockEntity lecternBlockEntity
                && player instanceof ServerPlayer serverPlayer
                && (!player.isSecondaryUseActive() || !Config.Common.LECTERN_SNEAK_OPENS_VANILLA_SCREEN.get())
                && scholar$shouldOpenScholarScreenForBook(lecternBlockEntity.getBook())) {
            scholar$openSpreadGUI(serverPlayer, lecternBlockEntity, lecternBlockEntity.getBook());
            player.awardStat(Stats.INTERACT_WITH_LECTERN);
            ci.cancel();
        }
    }

    @Unique
    private boolean scholar$shouldOpenScholarScreenForBook(ItemStack bookStack) {
        return Config.Common.LECTERN_REPLACE_VANILLA_SCREEN.get()
                && (bookStack.getItem() instanceof WritableBookItem || bookStack.getItem() instanceof WrittenBookItem);
    }

    @Unique
    private void scholar$openSpreadGUI(ServerPlayer player, LecternBlockEntity lecternBlockEntity, ItemStack bookStack) {
        int bookColor = BookColors.fromStack(bookStack);
        MenuProvider menuProvider = new MenuProvider() {
            @Override
            public @NotNull Component getDisplayName() {
                return bookStack.getHoverName();
            }

            @Override
            public @NotNull AbstractContainerMenu createMenu(int containerId, @NotNull Inventory playerInventory, @NotNull Player player) {
                LecternBlockEntityAccessor accessor = (LecternBlockEntityAccessor) lecternBlockEntity;
                return new LecternSpreadMenu(containerId, accessor.getBookAccess(), accessor.getDataAccess(), bookColor);
            }
        };

        PlatformHelper.openMenu(player, menuProvider, buffer -> {
            buffer.writeItem(bookStack);
        });
    }
}
