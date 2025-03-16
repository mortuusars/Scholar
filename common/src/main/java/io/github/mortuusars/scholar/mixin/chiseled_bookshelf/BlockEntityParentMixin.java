package io.github.mortuusars.scholar.mixin.chiseled_bookshelf;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * This class is used so child classes {@link ChiseledBookShelfBlockEntityMixin}
 * could modify methods in a (hopefully) more compatible with other mods way.
 */
@Mixin(BlockEntity.class)
public class BlockEntityParentMixin {
    @ModifyReturnValue(method = "getUpdatePacket", at = @At("RETURN"))
    protected @Nullable Packet<ClientGamePacketListener> onGetUpdatePacket(@Nullable Packet<ClientGamePacketListener> original) {
        return original;
    }

    @ModifyReturnValue(method = "getUpdateTag", at = @At("RETURN"))
    protected CompoundTag onGetUpdateTag(CompoundTag original) {
        return original;
    }
}
