package io.github.mortuusars.scholar.network.packet.server;

import com.google.common.collect.Lists;
import io.github.mortuusars.scholar.Config;
import io.github.mortuusars.scholar.Scholar;
import io.github.mortuusars.scholar.network.PacketDirection;
import io.github.mortuusars.scholar.network.packet.IPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.FilteredText;
import net.minecraft.server.network.TextFilter;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

/**
 * Similar to {@link net.minecraft.network.protocol.game.ServerboundEditBookPacket} but for lecterns.
 * Contains logic copied from {@link net.minecraft.server.network.ServerGamePacketListenerImpl}, which is not ideal, but hopefully it'll not cause any issues.
 */
public record LecternEditBookC2SP(BlockPos lecternPos, List<String> pages, Optional<String> title) implements IPacket {
    public static final ResourceLocation ID = Scholar.resource("lectern_edit_book");

    private static final int TITLE_MAX_CHARS = 128;
    private static final int PAGE_MAX_CHARS = 8192;
    private static final int MAX_PAGES_COUNT = 200;

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    public static LecternEditBookC2SP fromBuffer(FriendlyByteBuf buffer) {
        return new LecternEditBookC2SP(
                buffer.readBlockPos(),
                buffer.readCollection(FriendlyByteBuf.limitValue(Lists::newArrayListWithCapacity, MAX_PAGES_COUNT),
                        friendlyByteBuf -> friendlyByteBuf.readUtf(PAGE_MAX_CHARS)),
                buffer.readOptional(friendlyByteBuf -> friendlyByteBuf.readUtf(TITLE_MAX_CHARS)));
    }

    @Override
    public FriendlyByteBuf toBuffer(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(lecternPos);
        buffer.writeCollection(pages, (friendlyByteBuf, string) -> friendlyByteBuf.writeUtf(string, PAGE_MAX_CHARS));
        buffer.writeOptional(title, (friendlyByteBuf, string) -> friendlyByteBuf.writeUtf(string, TITLE_MAX_CHARS));
        return buffer;
    }

    @Override
    public boolean handle(PacketDirection direction, @Nullable Player player) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            Scholar.LOGGER.error("Cannot handle {} packet: player is not ServerPlayer.", getId());
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
              ? list -> signBook(serverPlayer, list.get(0), list.subList(1, list.size()), lecternBlockEntity)
              : list -> updateBookContents(serverPlayer, list, lecternBlockEntity);
        this.filterTextPacket(serverPlayer,bookPages).thenAccept(consumer);

        return true;
    }

    private void updateBookContents(ServerPlayer player, List<FilteredText> pages, LecternBlockEntity lecternBlockEntity) {
        ItemStack itemStack = lecternBlockEntity.getBook();
        if (!itemStack.is(Items.WRITABLE_BOOK)) return;
        updateBookPages(player, pages, UnaryOperator.identity(), itemStack, lecternBlockEntity);
    }

    private void signBook(ServerPlayer player, FilteredText title, List<FilteredText> pages, LecternBlockEntity lecternBlockEntity) {
        ItemStack itemStack = lecternBlockEntity.getBook();

        if (!itemStack.is(Items.WRITABLE_BOOK)) return;

        ItemStack writtenBookStack = new ItemStack(Items.WRITTEN_BOOK);
        CompoundTag compoundTag = itemStack.getTag();
        if (compoundTag != null) {
            writtenBookStack.setTag(compoundTag.copy());
        }
        writtenBookStack.addTagElement("author", StringTag.valueOf(player.getName().getString()));
        if (player.isTextFilteringEnabled()) {
            writtenBookStack.addTagElement("title", StringTag.valueOf(title.filteredOrEmpty()));
        } else {
            writtenBookStack.addTagElement("filtered_title", StringTag.valueOf(title.filteredOrEmpty()));
            writtenBookStack.addTagElement("title", StringTag.valueOf(title.raw()));
        }

        this.updateBookPages(player, pages, string -> Component.Serializer.toJson(Component.literal(string)), writtenBookStack, lecternBlockEntity);

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

    private void updateBookPages(ServerPlayer player, List<FilteredText> pages, UnaryOperator<String> updater, ItemStack book, LecternBlockEntity lecternBlockEntity) {
        ListTag listTag = new ListTag();
        if (player.isTextFilteringEnabled()) {
            pages.stream().map(filteredText -> StringTag.valueOf(updater.apply(filteredText.filteredOrEmpty()))).forEach(listTag::add);
        } else {
            CompoundTag compoundTag = new CompoundTag();
            int j = pages.size();
            for (int i = 0; i < j; ++i) {
                FilteredText filteredText2 = pages.get(i);
                String string = filteredText2.raw();
                listTag.add(StringTag.valueOf(updater.apply(string)));
                if (!filteredText2.isFiltered()) continue;
                compoundTag.putString(String.valueOf(i), updater.apply(filteredText2.filteredOrEmpty()));
            }
            if (!compoundTag.isEmpty()) {
                book.addTagElement("filtered_pages", compoundTag);
            }
        }
        book.addTagElement("pages", listTag);
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
