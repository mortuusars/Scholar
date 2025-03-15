package io.github.mortuusars.scholar.mixin.chiseled_bookshelf_tooltip;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChiseledBookShelfBlock;
import net.minecraft.world.level.block.entity.ChiseledBookShelfBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChiseledBookShelfBlock.class)
public abstract class ChiseledBookShelfBlockMixin extends Block {
    public ChiseledBookShelfBlockMixin(Properties properties) {
        super(properties);
    }

    @Inject(method = "addBook", at = @At(value = "RETURN")
            /*target = "Lnet/minecraft/world/level/Level;gameEvent(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/level/gameevent/GameEvent;Lnet/minecraft/core/BlockPos;)V"), cancellable = true*/)
    private static void onAddBook(Level level, BlockPos pos, Player player, ChiseledBookShelfBlockEntity blockEntity,
                                  ItemStack bookStack, int slot, CallbackInfo ci) {
        if (!level.isClientSide) {
            blockEntity.setChanged();
            level.sendBlockUpdated(pos, blockEntity.getBlockState().getBlock().defaultBlockState(), blockEntity.getBlockState(), Block.UPDATE_ALL);
//            ci.cancel();
        } else {
            blockEntity.setItem(slot, bookStack.split(1));
            if (player.isCreative()) {
                bookStack.grow(1);
            }
        }
    }

    @Inject(method = "removeBook", at = @At(value = "RETURN")
            /*target = "Lnet/minecraft/world/level/Level;gameEvent(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/level/gameevent/GameEvent;Lnet/minecraft/core/BlockPos;)V"), cancellable = true*/)
    private static void onRemoveBook(Level level, BlockPos pos, Player player, ChiseledBookShelfBlockEntity blockEntity, int slot, CallbackInfo ci) {
        if (!level.isClientSide) {
            blockEntity.setChanged();
            level.sendBlockUpdated(pos, blockEntity.getBlockState().getBlock().defaultBlockState(), blockEntity.getBlockState(), Block.UPDATE_ALL);
//            ci.cancel();
        } else {
            ItemStack itemStack = blockEntity.removeItem(slot, 1);
//            if (!player.getInventory().add(itemStack)) {
//                player.drop(itemStack, false);
//            }
        }
    }
}
