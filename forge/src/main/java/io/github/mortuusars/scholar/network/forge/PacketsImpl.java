package io.github.mortuusars.scholar.network.forge;


import io.github.mortuusars.scholar.network.PacketDirection;
import io.github.mortuusars.scholar.network.packet.Packet;
import io.github.mortuusars.scholar.network.packet.serverbound.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.function.Supplier;

public class PacketsImpl {
    private static final String PROTOCOL_VERSION = "1";
    private static int id = 0;

    private static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation("scholar:packets"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals);

    public static void register() {
        // SERVER
        CHANNEL.messageBuilder(LecternEditBookC2SP.class, id++, NetworkDirection.PLAY_TO_SERVER)
                .encoder(LecternEditBookC2SP::toBuffer)
                .decoder(LecternEditBookC2SP::fromBuffer)
                .consumerMainThread(PacketsImpl::handlePacket)
                .add();

        CHANNEL.messageBuilder(SetBookmarkC2SP.class, id++, NetworkDirection.PLAY_TO_SERVER)
              .encoder(SetBookmarkC2SP::toBuffer)
              .decoder(SetBookmarkC2SP::fromBuffer)
              .consumerMainThread(PacketsImpl::handlePacket)
              .add();

        CHANNEL.messageBuilder(SetCustomAuthorInHandC2SP.class, id++, NetworkDirection.PLAY_TO_SERVER)
              .encoder(SetCustomAuthorInHandC2SP::toBuffer)
              .decoder(SetCustomAuthorInHandC2SP::fromBuffer)
              .consumerMainThread(PacketsImpl::handlePacket)
              .add();

        CHANNEL.messageBuilder(SetCustomAuthorOnLecternC2SP.class, id++, NetworkDirection.PLAY_TO_SERVER)
              .encoder(SetCustomAuthorOnLecternC2SP::toBuffer)
              .decoder(SetCustomAuthorOnLecternC2SP::fromBuffer)
              .consumerMainThread(PacketsImpl::handlePacket)
              .add();

        CHANNEL.messageBuilder(SetGoldenSkinInHandC2SP.class, id++, NetworkDirection.PLAY_TO_SERVER)
              .encoder(SetGoldenSkinInHandC2SP::toBuffer)
              .decoder(SetGoldenSkinInHandC2SP::fromBuffer)
              .consumerMainThread(PacketsImpl::handlePacket)
              .add();

        CHANNEL.messageBuilder(SetGoldenSkinOnLecternC2SP.class, id++, NetworkDirection.PLAY_TO_SERVER)
              .encoder(SetGoldenSkinOnLecternC2SP::toBuffer)
              .decoder(SetGoldenSkinOnLecternC2SP::fromBuffer)
              .consumerMainThread(PacketsImpl::handlePacket)
              .add();

        CHANNEL.messageBuilder(StartedReadingInHandC2SP.class, id++, NetworkDirection.PLAY_TO_SERVER)
              .encoder(StartedReadingInHandC2SP::toBuffer)
              .decoder(StartedReadingInHandC2SP::fromBuffer)
              .consumerMainThread(PacketsImpl::handlePacket)
              .add();

        CHANNEL.messageBuilder(StoppedReadingInHandC2SP.class, id++, NetworkDirection.PLAY_TO_SERVER)
              .encoder(StoppedReadingInHandC2SP::toBuffer)
              .decoder(StoppedReadingInHandC2SP::fromBuffer)
              .consumerMainThread(PacketsImpl::handlePacket)
              .add();
    }

    public static void sendToServer(Packet packet) {
        CHANNEL.sendToServer(packet);
    }

    public static void sendToClient(Packet packet, ServerPlayer player) {
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), packet);
    }

    public static void sendToAllClients(Packet packet) {
        CHANNEL.send(PacketDistributor.ALL.noArg(), packet);
    }

    private static <T extends Packet> void handlePacket(T packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        packet.handle(direction(context.getDirection()), context.getSender());
    }

    private static PacketDirection direction(NetworkDirection direction) {
        if (direction == NetworkDirection.PLAY_TO_SERVER)
            return PacketDirection.TO_SERVER;
        else if (direction == NetworkDirection.PLAY_TO_CLIENT)
            return PacketDirection.TO_CLIENT;
        else
            throw new IllegalStateException("Can only convert direction for Client/Server, not others.");
    }
}