package io.github.mortuusars.scholar.network.packet.serverbound;

import io.github.mortuusars.scholar.Scholar;
import io.github.mortuusars.scholar.network.PacketDirection;
import io.github.mortuusars.scholar.network.packet.Packet;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public record SetBookmarkC2SP(int slot, int page) implements Packet {
    public static final ResourceLocation ID = Scholar.resource("set_bookmark");

    @Override
    public FriendlyByteBuf toBuffer(FriendlyByteBuf buffer) {
        buffer.writeInt(slot);
        buffer.writeInt(page);
        return buffer;
    }

    public static SetBookmarkC2SP fromBuffer(FriendlyByteBuf buffer) {
        return new SetBookmarkC2SP(buffer.readInt(), buffer.readInt());
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public boolean handle(PacketDirection direction, @Nullable Player player) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            Scholar.LOGGER.error("Cannot handle {} packet: player is not ServerPlayer.", ID);
            return true;
        }

        if (Inventory.isHotbarSlot(slot) || slot == Inventory.SLOT_OFFHAND) {
            ItemStack stack = serverPlayer.getInventory().getItem(slot);
            if (page == 0) {
                if (stack.getTag() != null) {
                    stack.getTag().remove(Scholar.NBT.BOOKMARK);
                }
            } else {
                stack.getOrCreateTag().putInt(Scholar.NBT.BOOKMARK, page);
            }
            return true;
        }

        return true;
    }
}
