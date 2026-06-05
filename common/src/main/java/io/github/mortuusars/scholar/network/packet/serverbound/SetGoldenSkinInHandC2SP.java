package io.github.mortuusars.scholar.network.packet.serverbound;

import io.github.mortuusars.scholar.Scholar;
import io.github.mortuusars.scholar.network.PacketDirection;
import io.github.mortuusars.scholar.network.packet.Packet;
import io.github.mortuusars.scholar.util.supporter.Supporters;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;

public record SetGoldenSkinInHandC2SP(int slot, boolean golden) implements Packet {
    public static final ResourceLocation ID = Scholar.resource("set_golden_skin_in_hand");

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    public static SetGoldenSkinInHandC2SP fromBuffer(FriendlyByteBuf buffer) {
        return new SetGoldenSkinInHandC2SP(buffer.readInt(), buffer.readBoolean());
    }

    @Override
    public FriendlyByteBuf toBuffer(FriendlyByteBuf buffer) {
        buffer.writeInt(slot);
        buffer.writeBoolean(golden);
        return buffer;
    }

    @Override
    public boolean handle(PacketDirection direction, @Nullable Player player) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            Scholar.LOGGER.error("Cannot handle {} packet: player is not ServerPlayer.", ID);
            return true;
        }

        if (Inventory.isHotbarSlot(slot) || slot == Inventory.SLOT_OFFHAND) {
            ItemStack stack = serverPlayer.getInventory().getItem(slot);
            if (stack.is(Items.WRITABLE_BOOK) && Supporters.hasAccessToGoldenSkin(player.getUUID())) {
                if (!golden) {
                    if (stack.getTag() != null) {
                        stack.getTag().remove(Scholar.NBT.BOOK_GOLDEN);
                    }
                } else {
                    stack.getOrCreateTag().putBoolean(Scholar.NBT.BOOK_GOLDEN, true);
                }
            }
            return true;
        }

        return true;
    }
}
