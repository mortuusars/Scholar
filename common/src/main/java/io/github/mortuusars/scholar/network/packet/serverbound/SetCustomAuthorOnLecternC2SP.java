package io.github.mortuusars.scholar.network.packet.serverbound;

import io.github.mortuusars.scholar.Config;
import io.github.mortuusars.scholar.Scholar;
import io.github.mortuusars.scholar.network.packet.Packet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.WrittenBookContent;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import org.jetbrains.annotations.NotNull;

public record SetCustomAuthorOnLecternC2SP(BlockPos lecternPos, String author) implements Packet {
    public static final ResourceLocation ID = Scholar.resource("set_custom_author_on_lectern");
    public static final Type<SetCustomAuthorOnLecternC2SP> TYPE = new Type<>(ID);

    public static final StreamCodec<FriendlyByteBuf, SetCustomAuthorOnLecternC2SP> STREAM_CODEC = StreamCodec.composite(
          BlockPos.STREAM_CODEC, SetCustomAuthorOnLecternC2SP::lecternPos,
          ByteBufCodecs.stringUtf8(128), SetCustomAuthorOnLecternC2SP::author,
          SetCustomAuthorOnLecternC2SP::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    public boolean handle(PacketFlow direction, Player player) {
        if (!(player.level().getBlockEntity(lecternPos) instanceof LecternBlockEntity lecternBlockEntity)) {
            Scholar.LOGGER.error("Cannot update lectern book: no lectern block entity at [{}]", lecternPos.toShortString());
            return false;
        }

        if (!Config.Common.BOOK_CHANGEABLE_AUTHOR.get()) {
            return true;
        }

        ItemStack book = lecternBlockEntity.getBook();
        if (!book.is(Items.WRITTEN_BOOK)) {
            Scholar.LOGGER.error("Cannot set signing author on a lectern book at [{}]: book is not a written book but '{}'",
                  lecternPos.toShortString(), book);
            return false;
        }

        if (book.get(DataComponents.WRITTEN_BOOK_CONTENT) instanceof WrittenBookContent content) {
            book.set(DataComponents.WRITTEN_BOOK_CONTENT, new WrittenBookContent(content.title(),
                  author, content.generation(), content.pages(), content.resolved()));
        }

        return true;
    }
}
