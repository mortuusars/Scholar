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
import net.minecraft.world.item.WritableBookItem;
import net.minecraft.world.item.WrittenBookItem;
import org.jetbrains.annotations.NotNull;

public record StoppedReadingInHandC2SP(int slot) implements Packet {
    public static final ResourceLocation ID = Scholar.resource("stopped_reading_in_hand");
    public static final Type<StoppedReadingInHandC2SP> TYPE = new Type<>(ID);

    public static final StreamCodec<FriendlyByteBuf, StoppedReadingInHandC2SP> STREAM_CODEC = StreamCodec.composite(
          ByteBufCodecs.VAR_INT, StoppedReadingInHandC2SP::slot,
          StoppedReadingInHandC2SP::new
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
            if (stack.getItem() instanceof WritableBookItem || stack.getItem() instanceof WrittenBookItem) {
                stack.remove(Scholar.DataComponents.BOOK_OPEN);
            }
            return true;
        }

        return true;
    }
}
