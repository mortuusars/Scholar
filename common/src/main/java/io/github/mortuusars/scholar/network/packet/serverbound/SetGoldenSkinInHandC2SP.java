package io.github.mortuusars.scholar.network.packet.serverbound;

import io.github.mortuusars.scholar.Scholar;
import io.github.mortuusars.scholar.network.packet.Packet;
import io.github.mortuusars.scholar.util.supporter.Supporters;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

public record SetGoldenSkinInHandC2SP(int slot, boolean golden) implements Packet {
    public static final Identifier ID = Scholar.identifier("set_golden_skin_in_hand");
    public static final Type<SetGoldenSkinInHandC2SP> TYPE = new Type<>(ID);

    public static final StreamCodec<FriendlyByteBuf, SetGoldenSkinInHandC2SP> STREAM_CODEC = StreamCodec.composite(
          ByteBufCodecs.VAR_INT, SetGoldenSkinInHandC2SP::slot,
          ByteBufCodecs.BOOL, SetGoldenSkinInHandC2SP::golden,
          SetGoldenSkinInHandC2SP::new
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
            if (stack.is(Items.WRITABLE_BOOK) && Supporters.hasAccessToGoldenSkin(player.getUUID())) {
                stack.set(Scholar.DataComponents.BOOK_GOLDEN, golden ? Unit.INSTANCE : null);
            }
            return true;
        }

        return true;
    }
}
