package io.github.mortuusars.scholar.mixin.lectern;

import io.github.mortuusars.scholar.Config;
import io.github.mortuusars.scholar.mixin.BlockEntityParentMixin;
import net.minecraft.core.HolderLookup;
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
    private ItemStack book;

    @Override
    protected Packet<ClientGamePacketListener> onGetUpdatePacket(@Nullable Packet<ClientGamePacketListener> packet) {
        return Config.Common.LECTERN_TOOLTIP.get()
              ? ClientboundBlockEntityDataPacket.create((LecternBlockEntity) (Object) this)
              : super.onGetUpdatePacket(packet);
    }

    @Override
    protected CompoundTag onGetUpdateTag(CompoundTag tag, HolderLookup.Provider registries) {
        if (Config.Common.LECTERN_TOOLTIP.get()) {
            CompoundTag data = saveCustomOnly(registries);
            if (book.isEmpty()) {
                // Empty tag (if book is removed) does not trigger the loadAdditional method on the client block entity,
                // and thus the tooltip shows the book even if it's no longer there. So we add placeholder data to trigger an update.
                data.putBoolean("BookIsRemoved", true);
            }
            return data;
        }

        return tag;
    }
}
