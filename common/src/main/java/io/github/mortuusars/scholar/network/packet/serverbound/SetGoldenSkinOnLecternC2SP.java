package io.github.mortuusars.scholar.network.packet.serverbound;

import io.github.mortuusars.scholar.Config;
import io.github.mortuusars.scholar.Scholar;
import io.github.mortuusars.scholar.network.PacketDirection;
import io.github.mortuusars.scholar.network.packet.Packet;
import io.github.mortuusars.scholar.util.supporter.Supporters;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.LecternBlockEntity;

public record SetGoldenSkinOnLecternC2SP(BlockPos lecternPos, boolean golden) implements Packet {
    public static final ResourceLocation ID = Scholar.resource("set_golden_skin_on_lectern");

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    public static SetGoldenSkinOnLecternC2SP fromBuffer(FriendlyByteBuf buffer) {
        return new SetGoldenSkinOnLecternC2SP(buffer.readBlockPos(), buffer.readBoolean());
    }

    @Override
    public FriendlyByteBuf toBuffer(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(lecternPos);
        buffer.writeBoolean(golden);
        return buffer;
    }

    @Override
    public boolean handle(PacketDirection direction, Player player) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            Scholar.LOGGER.error("Cannot handle {} packet: player is not ServerPlayer.", ID);
            return true;
        }

        if (!(serverPlayer.level().getBlockEntity(lecternPos) instanceof LecternBlockEntity lecternBlockEntity)) {
            Scholar.LOGGER.error("Cannot update lectern book: no lectern block entity at [{}]", lecternPos.toShortString());
            return false;
        }

        ItemStack book = lecternBlockEntity.getBook();

        if (book.is(Items.WRITABLE_BOOK) && Supporters.hasAccessToGoldenSkin(player.getUUID())) {
            if (!golden) {
                if (book.getTag() != null) {
                    book.getTag().remove(Scholar.NBT.BOOK_GOLDEN);
                }
            } else {
                book.getOrCreateTag().putBoolean(Scholar.NBT.BOOK_GOLDEN, true);
            }
            lecternBlockEntity.setChanged();

            // Manually sending the block data to clients because it doesn't happen when block is changed for some reason
            if (Config.Common.LECTERN_TOOLTIP.get()) {
                var bePacket = lecternBlockEntity.getUpdatePacket();
                serverPlayer.serverLevel().players().forEach(pl -> {
                    if (lecternPos.distSqr(pl.blockPosition()) < 128) {
                        pl.connection.send(bePacket);
                    }
                });
            }
        }

        return true;
    }
}
