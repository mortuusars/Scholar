package io.github.mortuusars.scholar.mixin.chiseled_bookshelf;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ChiseledBookShelfBlock;
import net.minecraft.world.level.block.entity.ChiseledBookShelfBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Purpose of this mixin is to update bookshelf immediately on the client,
 * because due to some weird order of updates (probably), tint colors are updated earlier than items are synched -
 * thus color of the new placed book will not be correct.
 */
@Mixin(ChiseledBookShelfBlock.class)
public abstract class ChiseledBookShelfBlockMixin {
    @Inject(method = "addBook", at = @At(value = "RETURN"))
    private static void onAddBook(Level level, BlockPos pos, Player player, ChiseledBookShelfBlockEntity blockEntity,
                                  ItemStack bookStack, int slot, CallbackInfo ci) {
        if (level.isClientSide) {
            if (BuiltInRegistries.BLOCK.getKey(blockEntity.getBlockState().getBlock()).getNamespace().equals("apotheosis")) {
                return; // Fix crash with apotheosis shelf. Scholar doesn't support them anyway currently.
            }

            blockEntity.setItem(slot, bookStack.split(1));
            if (player.isCreative()) {
                bookStack.grow(1);
            }
        }
    }
}
