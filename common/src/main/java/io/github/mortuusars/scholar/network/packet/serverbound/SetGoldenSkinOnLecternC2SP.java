package io.github.mortuusars.scholar.network.packet.serverbound;

import io.github.mortuusars.scholar.Config;
import io.github.mortuusars.scholar.Scholar;
import io.github.mortuusars.scholar.network.packet.Packet;
import io.github.mortuusars.scholar.util.supporter.Supporters;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record SetGoldenSkinOnLecternC2SP(BlockPos lecternPos, boolean golden) implements Packet {
    public static final Identifier ID = Scholar.resource("set_golden_skin_on_lectern");
    public static final Type<SetGoldenSkinOnLecternC2SP> TYPE = new Type<>(ID);

    public static final StreamCodec<FriendlyByteBuf, SetGoldenSkinOnLecternC2SP> STREAM_CODEC = StreamCodec.composite(
          BlockPos.STREAM_CODEC, SetGoldenSkinOnLecternC2SP::lecternPos,
          ByteBufCodecs.BOOL, SetGoldenSkinOnLecternC2SP::golden,
          SetGoldenSkinOnLecternC2SP::new
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

        if (!(player.level().getBlockEntity(lecternPos) instanceof LecternBlockEntity lecternBlockEntity)) {
            Scholar.LOGGER.error("Cannot update lectern book: no lectern block entity at [{}]", lecternPos.toShortString());
            return false;
        }

        ItemStack book = lecternBlockEntity.getBook();
        if (book.is(Items.WRITABLE_BOOK) && Supporters.hasAccessToGoldenSkin(player.getUUID())) {
            book.set(Scholar.DataComponents.BOOK_GOLDEN, golden ? Unit.INSTANCE : null);
            lecternBlockEntity.setChanged();

            if (Config.Common.LECTERN_TOOLTIP.get()) {
                List<ServerPlayer> players = serverPlayer.level().players();
                var packet = lecternBlockEntity.getUpdatePacket();
                if (packet != null) {
                    players.forEach(pl -> pl.connection.send(packet));
                }
            }
        }

        return true;
    }
}
