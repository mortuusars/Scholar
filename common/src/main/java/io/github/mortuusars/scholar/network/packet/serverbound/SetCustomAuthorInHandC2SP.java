package io.github.mortuusars.scholar.network.packet.serverbound;

import io.github.mortuusars.scholar.Config;
import io.github.mortuusars.scholar.Scholar;
import io.github.mortuusars.scholar.network.packet.Packet;
import net.minecraft.core.component.DataComponents;
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
import net.minecraft.world.item.component.WrittenBookContent;
import org.jetbrains.annotations.NotNull;

public record SetCustomAuthorInHandC2SP(int slot, String author) implements Packet {
    public static final ResourceLocation ID = Scholar.resource("set_custom_author_in_hand");
    public static final Type<SetCustomAuthorInHandC2SP> TYPE = new Type<>(ID);

    public static final StreamCodec<FriendlyByteBuf, SetCustomAuthorInHandC2SP> STREAM_CODEC = StreamCodec.composite(
          ByteBufCodecs.VAR_INT, SetCustomAuthorInHandC2SP::slot,
          ByteBufCodecs.stringUtf8(128), SetCustomAuthorInHandC2SP::author,
          SetCustomAuthorInHandC2SP::new
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

        if (!Config.Common.BOOK_CHANGEABLE_AUTHOR.get()) {
            return true;
        }

        if (Inventory.isHotbarSlot(slot) || slot == Inventory.SLOT_OFFHAND) {
            ItemStack stack = serverPlayer.getInventory().getItem(slot);
            if (stack.get(DataComponents.WRITTEN_BOOK_CONTENT) instanceof WrittenBookContent content) {
                stack.set(DataComponents.WRITTEN_BOOK_CONTENT, new WrittenBookContent(content.title(),
                      author, content.generation(), content.pages(), content.resolved()));
            }
            return true;
        }

        return true;
    }
}
