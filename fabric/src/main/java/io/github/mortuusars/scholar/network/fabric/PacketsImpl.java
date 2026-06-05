package io.github.mortuusars.scholar.network.fabric;

import io.github.mortuusars.scholar.Scholar;
import io.github.mortuusars.scholar.network.PacketDirection;
import io.github.mortuusars.scholar.network.packet.Packet;
import io.github.mortuusars.scholar.network.packet.serverbound.*;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class PacketsImpl {
    @Nullable
    private static MinecraftServer server;

    public static void registerC2SPackets() {
        ServerPlayNetworking.registerGlobalReceiver(LecternEditBookC2SP.ID, new ServerHandler(LecternEditBookC2SP::fromBuffer));
        ServerPlayNetworking.registerGlobalReceiver(SetBookmarkC2SP.ID, new ServerHandler(SetBookmarkC2SP::fromBuffer));
        ServerPlayNetworking.registerGlobalReceiver(SetCustomAuthorInHandC2SP.ID, new ServerHandler(SetCustomAuthorInHandC2SP::fromBuffer));
        ServerPlayNetworking.registerGlobalReceiver(SetCustomAuthorOnLecternC2SP.ID, new ServerHandler(SetCustomAuthorOnLecternC2SP::fromBuffer));
        ServerPlayNetworking.registerGlobalReceiver(SetGoldenSkinInHandC2SP.ID, new ServerHandler(SetGoldenSkinInHandC2SP::fromBuffer));
        ServerPlayNetworking.registerGlobalReceiver(SetGoldenSkinOnLecternC2SP.ID, new ServerHandler(SetGoldenSkinOnLecternC2SP::fromBuffer));
        ServerPlayNetworking.registerGlobalReceiver(StartedReadingInHandC2SP.ID, new ServerHandler(StartedReadingInHandC2SP::fromBuffer));
        ServerPlayNetworking.registerGlobalReceiver(StoppedReadingInHandC2SP.ID, new ServerHandler(StoppedReadingInHandC2SP::fromBuffer));
    }

    public static void registerS2CPackets() {
        ClientPackets.registerS2CPackets();
    }

    public static void sendToServer(Packet packet) {
        ClientPackets.sendToServer(packet);
    }

    public static void sendToClient(Packet packet, ServerPlayer player) {
        ServerPlayNetworking.send(player, packet.getId(), packet.toBuffer(PacketByteBufs.create()));
    }

    public static void sendToAllClients(Packet packet) {
        if (server == null) {
            Scholar.LOGGER.error("Cannot send a packet to all players. Server is not present.");
            return;
        }

        FriendlyByteBuf packetBuffer = packet.toBuffer(PacketByteBufs.create());
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            ServerPlayNetworking.send(player, packet.getId(), packetBuffer);
        }
    }

    public static void onServerStarting(MinecraftServer server) {
        // Store server to access from static context:
        PacketsImpl.server = server;
    }

    public static void onServerStopped(MinecraftServer server) {
        PacketsImpl.server = null;
    }

    private record ServerHandler(Function<FriendlyByteBuf, Packet> decodeFunction) implements ServerPlayNetworking.PlayChannelHandler {
        @Override
        public void receive(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl handler,
                            FriendlyByteBuf buf, PacketSender responseSender) {
            Packet packet = decodeFunction.apply(buf);
            // Execute on main thread
            server.execute(() -> packet.handle(PacketDirection.TO_SERVER, player));
        }
    }
}
