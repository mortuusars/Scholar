package io.github.mortuusars.scholar.network.packet.serverbound;

import io.github.mortuusars.scholar.Config;
import io.github.mortuusars.scholar.Scholar;
import io.github.mortuusars.scholar.network.packet.Packet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.Filterable;
import net.minecraft.server.network.FilteredText;
import net.minecraft.server.network.TextFilter;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.WritableBookContent;
import net.minecraft.world.item.component.WrittenBookContent;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.Consumer;

/**
 * Similar to {@link net.minecraft.network.protocol.game.ServerboundEditBookPacket} but for lecterns.
 * Contains logic copied from {@link net.minecraft.server.network.ServerGamePacketListenerImpl}, which is not ideal, but hopefully it'll not cause any issues.
 */
public record LecternEditBookC2SP(BlockPos lecternPos, List<String> pages, Optional<String> title) implements Packet {
    public static final Identifier ID = Scholar.resource("lectern_edit_book");
    public static final Type<LecternEditBookC2SP> TYPE = new Type<>(ID);

    public static final int TITLE_MAX_CHARS = 128;
    public static final int PAGE_MAX_CHARS = 8192;
    public static final int MAX_PAGES_COUNT = 200;

    public static final StreamCodec<FriendlyByteBuf, LecternEditBookC2SP> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, LecternEditBookC2SP::lecternPos,
            ByteBufCodecs.stringUtf8(PAGE_MAX_CHARS).apply(ByteBufCodecs.list(MAX_PAGES_COUNT)), LecternEditBookC2SP::pages,
            ByteBufCodecs.optional(ByteBufCodecs.stringUtf8(TITLE_MAX_CHARS)), LecternEditBookC2SP::title,
            LecternEditBookC2SP::new
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
            Scholar.LOGGER.error("Cannot update lectern book: no lectern block entity at lecternPos '{}'", lecternPos);
            return false;
        }

        ArrayList<String> bookPages = new ArrayList<>();
        Optional<String> title = title();
        title.ifPresent(bookPages::add);
        pages().stream().limit(100L).forEach(bookPages::add);
        Consumer<List<FilteredText>> consumer = title.isPresent()
                ? list -> signBook(serverPlayer, list.getFirst(), list.subList(1, list.size()), lecternBlockEntity)
                : list -> updateBookContents(serverPlayer, list, lecternBlockEntity);
        filterTextPacket(serverPlayer,bookPages).thenAccept(consumer);

        return true;
    }

    private void updateBookContents(ServerPlayer player, List<FilteredText> pages, LecternBlockEntity lecternBlockEntity) {
        ItemStack itemStack = lecternBlockEntity.getBook();
        if (!itemStack.is(Items.WRITABLE_BOOK)) return;

        List<Filterable<String>> list = pages.stream().map((FilteredText text) -> filterableFromOutgoing(player, text)).toList();
        itemStack.set(DataComponents.WRITABLE_BOOK_CONTENT, new WritableBookContent(list));
    }

    private void signBook(ServerPlayer player, FilteredText title, List<FilteredText> pages, LecternBlockEntity lecternBlockEntity) {
        ItemStack itemStack = lecternBlockEntity.getBook();
        if (!itemStack.is(Items.WRITABLE_BOOK)) return;

        ItemStack writtenBookStack = itemStack.transmuteCopy(Items.WRITTEN_BOOK);
        writtenBookStack.remove(DataComponents.WRITABLE_BOOK_CONTENT);
        List<Filterable<Component>> list = pages.stream()
                .map((filteredText) -> this.filterableFromOutgoing(player, filteredText)
                        .map(Component::literal)
                        .map(c -> ((Component) c)))
                .toList();
        writtenBookStack.set(DataComponents.WRITTEN_BOOK_CONTENT,
                new WrittenBookContent(this.filterableFromOutgoing(player, title), player.getName().getString(), 0, list, true));
        lecternBlockEntity.setBook(writtenBookStack, player);

        if (Config.Common.LECTERN_TOOLTIP.get()) {
            // Wanna hear about how frustrating minecraft is sometimes?
            // Without this sh*t block entity just refuses to update no matter what I try.
            // blockEntity.setChanged, level.sendBlockUpdated, level.setBlock - nothing works.
            // And because of that the old book is still showing in the tooltip.
            // 40 minutes of my life has been broadcasted down the drain.
            List<ServerPlayer> players = player.serverLevel().players();
            net.minecraft.network.protocol.Packet<?> packet = lecternBlockEntity.getUpdatePacket();
            if (packet != null) {
                players.forEach(serverPlayer -> serverPlayer.connection.send(packet));
            }
        }
    }

    private Filterable<String> filterableFromOutgoing(ServerPlayer player, FilteredText filteredText) {
        return player.isTextFilteringEnabled() ? Filterable.passThrough(filteredText.filteredOrEmpty()) : Filterable.from(filteredText);
    }

    private <T, R> CompletableFuture<R> filterTextPacket(ServerPlayer player, T message, BiFunction<TextFilter, T, CompletableFuture<R>> processor) {
        return processor.apply(player.getTextFilter(), message).thenApply(object -> {
            if (!player.connection.isAcceptingMessages()) {
                Scholar.LOGGER.debug("Ignoring packet due to disconnection");
                throw new CancellationException("disconnected");
            }
            return object;
        });
    }

    private CompletableFuture<List<FilteredText>> filterTextPacket(ServerPlayer player, List<String> texts) {
        return this.filterTextPacket(player, texts, TextFilter::processMessageBundle);
    }
}
