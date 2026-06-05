package io.github.mortuusars.scholar.network.packet.serverbound;

import io.github.mortuusars.scholar.Config;
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

public record SetCustomAuthorInHandC2SP(int slot, String author) implements Packet {
    public static final ResourceLocation ID = Scholar.resource("set_custom_author_in_hand");

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    public static SetCustomAuthorInHandC2SP fromBuffer(FriendlyByteBuf buffer) {
        return new SetCustomAuthorInHandC2SP(buffer.readInt(), buffer.readUtf());
    }

    @Override
    public FriendlyByteBuf toBuffer(FriendlyByteBuf buffer) {
        buffer.writeInt(slot);
        buffer.writeUtf(author);
        return buffer;
    }

    @Override
    public boolean handle(PacketDirection direction, @Nullable Player player) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            Scholar.LOGGER.error("Cannot handle {} packet: player is not ServerPlayer.", ID);
            return true;
        }

        if (!Config.Common.BOOK_CHANGEABLE_AUTHOR.get()) {
            return true;
        }

        if (author.length() > 128) {
            Scholar.LOGGER.error("Cannot handle {} packet: player {} sent author string that's too long: {}. Max 128..", ID, player, author.length());
            return true;
        }

        if (Inventory.isHotbarSlot(slot) || slot == Inventory.SLOT_OFFHAND) {
            ItemStack stack = serverPlayer.getInventory().getItem(slot);
            stack.getOrCreateTag().putString("author", author);
            return true;
        }

        return true;
    }
}
