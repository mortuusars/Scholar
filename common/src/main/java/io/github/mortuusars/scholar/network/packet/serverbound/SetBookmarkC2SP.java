package io.github.mortuusars.scholar.network.packet.serverbound;

import io.github.mortuusars.scholar.Scholar;
import io.github.mortuusars.scholar.network.packet.Packet;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public record SetBookmarkC2SP(int slot, int page) implements Packet {
    public static final ResourceLocation ID = Scholar.resource("set_bookmark");
    public static final Type<SetBookmarkC2SP> TYPE = new Type<>(ID);

    public static final StreamCodec<FriendlyByteBuf, SetBookmarkC2SP> STREAM_CODEC = StreamCodec.composite(
          ByteBufCodecs.VAR_INT, SetBookmarkC2SP::slot,
          ByteBufCodecs.VAR_INT, SetBookmarkC2SP::page,
          SetBookmarkC2SP::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    public boolean handle(PacketFlow direction, Player player) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            Scholar.LOGGER.error("Cannot handle {} packet: player is not ServerPlayer.", ID);
            return true;
        }

        if (Inventory.isHotbarSlot(slot) || slot == Inventory.SLOT_OFFHAND) {
            ItemStack stack = serverPlayer.getInventory().getItem(slot);
            if (page == 0) {
                stack.remove(Scholar.DataComponents.BOOKMARK);
            } else {
                stack.set(Scholar.DataComponents.BOOKMARK, page);
            }
            return true;
        }

        return true;
    }
}
