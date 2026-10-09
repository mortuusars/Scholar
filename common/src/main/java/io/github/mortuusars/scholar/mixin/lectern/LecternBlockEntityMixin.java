package io.github.mortuusars.scholar.mixin.lectern;

import io.github.mortuusars.scholar.mixin.BlockEntityParentMixin;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 * Sends stored book to clients, so it can be displayed in a tooltip.
 */
@Mixin(LecternBlockEntity.class)
public abstract class LecternBlockEntityMixin extends BlockEntityParentMixin {
    @Shadow
    ItemStack book;

    @Override
    protected Packet<ClientGamePacketListener> onGetUpdatePacket(@Nullable Packet<ClientGamePacketListener> packet) {
        // Previously it only synched the book when LECTERN_TOOLTIP config option was enabled,
        // but because LECTERN_COLORED_BOOK_MODEL renders the book only if block entity has it,
        // it made the book always invisible if LECTERN_TOOLTIP was disabled.
        return ClientboundBlockEntityDataPacket.create((LecternBlockEntity) (Object) this);
    }

    @Override
    protected CompoundTag onGetUpdateTag(CompoundTag tag) {
        CompoundTag data = saveWithoutMetadata();
        if (book.isEmpty()) {
            // Empty tag (if book is removed) does not trigger the loadAdditional method on the client block entity,
            // and thus the tooltip shows the book even if it's no longer there. So we add placeholder data to trigger an update.
            data.putBoolean("BookIsRemoved", true);
        }
        return data;
    }
}
