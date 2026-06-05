package io.github.mortuusars.scholar.mixin.chiseled_bookshelf;

import io.github.mortuusars.scholar.mixin.BlockEntityParentMixin;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.ChiseledBookShelfBlockEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 * Sends stored items to client, so it can tint book properly and display bookshelf contents in a tooltip.
 */
@Mixin(ChiseledBookShelfBlockEntity.class)
public abstract class ChiseledBookShelfBlockEntityMixin extends BlockEntityParentMixin {
    @Shadow
    @Final
    private NonNullList<ItemStack> items;

    @Override
    protected Packet<ClientGamePacketListener> onGetUpdatePacket(@Nullable Packet<ClientGamePacketListener> packet) {
        return ClientboundBlockEntityDataPacket.create((ChiseledBookShelfBlockEntity) (Object) this);
    }

    @Override
    protected CompoundTag onGetUpdateTag(CompoundTag tag) {
        return ContainerHelper.saveAllItems(tag, this.items);
    }
}
