package io.github.mortuusars.scholar.mixin.lectern;

import io.github.mortuusars.scholar.Config;
import io.github.mortuusars.scholar.menu.LecternMenus;
import io.github.mortuusars.scholar.menu.LecternSpreadBookEditMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LecternBlock;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Bumped priority to override Amendments lectern screen. If player has installed Scholar, they probably want it everywhere anyway.
 * If not - it can be disabled in config.
 */
@Mixin(value = LecternBlock.class, priority = 990)
public abstract class LecternBlockMixin {
    @Inject(method = "openScreen", at = @At(value = "HEAD"), cancellable = true)
    private void openScreen(Level level, BlockPos pos, Player player, CallbackInfo ci) {
        if (!(player instanceof ServerPlayer serverPlayer)) return;
        if (!(level.getBlockEntity(pos) instanceof LecternBlockEntity lecternBlockEntity)) return;
        if (!Config.Common.LECTERN_TWO_PAGE_BOOK_SCREEN.get()) return;
        if (Config.Common.SNEAK_OPENS_VANILLA_BOOK_SCREEN.get() && player.isSecondaryUseActive()) return;

        ItemStack bookStack = lecternBlockEntity.getBook();

        if (bookStack.is(Items.WRITABLE_BOOK)) {
            boolean hasPlayerEditing = serverPlayer.serverLevel().players().stream()
                    .anyMatch(pl -> pl.containerMenu instanceof LecternSpreadBookEditMenu lecternMenu
                            && lecternMenu.getLecternPos().equals(pos));

            if (hasPlayerEditing) {
                LecternMenus.openBookViewMenu(serverPlayer, lecternBlockEntity, bookStack);
            } else {
                LecternMenus.openBookEditMenu(serverPlayer, lecternBlockEntity, bookStack);
            }

            player.awardStat(Stats.INTERACT_WITH_LECTERN);
            ci.cancel();
        }

        if (bookStack.is(Items.WRITTEN_BOOK)) {
            LecternMenus.openBookViewMenu(serverPlayer, lecternBlockEntity, bookStack);
            player.awardStat(Stats.INTERACT_WITH_LECTERN);
            ci.cancel();
        }
    }
}
