package io.github.mortuusars.scholar.network.packet.serverbound;

import io.github.mortuusars.scholar.Scholar;
import io.github.mortuusars.scholar.network.PacketDirection;
import io.github.mortuusars.scholar.network.packet.Packet;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.WritableBookItem;
import net.minecraft.world.item.WrittenBookItem;

public record StoppedReadingInHandC2SP(int slot) implements Packet {
    public static final ResourceLocation ID = Scholar.resource("stopped_reading_in_hand");
    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public FriendlyByteBuf toBuffer(FriendlyByteBuf buffer) {
        buffer.writeInt(slot);
        return buffer;
    }

    public static StoppedReadingInHandC2SP fromBuffer(FriendlyByteBuf buffer) {
        return new StoppedReadingInHandC2SP(buffer.readInt());
    }

    @Override
    public boolean handle(PacketDirection direction, Player player) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            Scholar.LOGGER.error("Cannot handle {} packet: player is not ServerPlayer.", ID);
            return true;
        }

        if (Inventory.isHotbarSlot(slot) || slot == Inventory.SLOT_OFFHAND) {
            ItemStack stack = serverPlayer.getInventory().getItem(slot);
            if (stack.getItem() instanceof WritableBookItem || stack.getItem() instanceof WrittenBookItem) {
                if (stack.getTag() != null) {
                    stack.getTag().remove(Scholar.NBT.BOOK_OPEN);
                }
                serverPlayer.level().playSound(player, player, SoundEvents.BOOK_PAGE_TURN, SoundSource.PLAYERS, 1, 1);
            }
            return true;
        }

        return true;
    }
}
