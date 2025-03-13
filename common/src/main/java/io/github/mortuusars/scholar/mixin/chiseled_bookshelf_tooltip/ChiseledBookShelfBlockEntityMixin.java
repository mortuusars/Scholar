package io.github.mortuusars.scholar.mixin.chiseled_bookshelf_tooltip;

import io.github.mortuusars.scholar.Config;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.ChiseledBookShelfBlockEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 * Sends stored items to client, so it can display bookshelf contents in a tooltip.
 */
@Mixin(ChiseledBookShelfBlockEntity.class)
public abstract class ChiseledBookShelfBlockEntityMixin extends BlockEntityParentMixin {
    @Shadow @Final private NonNullList<ItemStack> items;

    @Override
    protected Packet<ClientGamePacketListener> onGetUpdatePacket(Packet<ClientGamePacketListener> original) {
        ChiseledBookShelfBlockEntity blockEntity = (ChiseledBookShelfBlockEntity) (Object) this;
        return Config.Common.CHISELED_BOOKSHELF_TOOLTIP.get()
                ? ClientboundBlockEntityDataPacket.create(blockEntity)
                : super.onGetUpdatePacket(original);
    }

    @Override
    protected CompoundTag onGetUpdateTag(CompoundTag original) {
        if (Config.Common.CHISELED_BOOKSHELF_TOOLTIP.get()) {
            ContainerHelper.saveAllItems(original, this.items);
        }

        return original;
    }
}
