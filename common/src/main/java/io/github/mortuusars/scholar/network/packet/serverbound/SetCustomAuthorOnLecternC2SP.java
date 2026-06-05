package io.github.mortuusars.scholar.network.packet.serverbound;

import io.github.mortuusars.scholar.Config;
import io.github.mortuusars.scholar.Scholar;
import io.github.mortuusars.scholar.network.PacketDirection;
import io.github.mortuusars.scholar.network.packet.Packet;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import org.jetbrains.annotations.Nullable;

public record SetCustomAuthorOnLecternC2SP(BlockPos lecternPos, String author) implements Packet {
    public static final ResourceLocation ID = Scholar.resource("set_custom_author_on_lectern");

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    public static SetCustomAuthorOnLecternC2SP fromBuffer(FriendlyByteBuf buffer) {
        return new SetCustomAuthorOnLecternC2SP(buffer.readBlockPos(), buffer.readUtf());
    }

    @Override
    public FriendlyByteBuf toBuffer(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(lecternPos);
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

        if (!(player.level().getBlockEntity(lecternPos) instanceof LecternBlockEntity lecternBlockEntity)) {
            Scholar.LOGGER.error("Cannot update lectern book: no lectern block entity at [{}]", lecternPos.toShortString());
            return false;
        }

        if (author.length() > 128) {
            Scholar.LOGGER.error("Cannot handle {} packet: player {} sent author string that's too long: {}. Max 128..", ID, player, author.length());
            return true;
        }

        ItemStack book = lecternBlockEntity.getBook();
        if (!book.is(Items.WRITTEN_BOOK)) {
            Scholar.LOGGER.error("Cannot set signing author on a lectern book at [{}]: book is not a written book but '{}'",
                  lecternPos.toShortString(), book);
            return false;
        }

        book.getOrCreateTag().putString("author", author);
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

        return true;
    }
}