package io.github.mortuusars.scholar.network.fabric;

import io.github.mortuusars.scholar.network.PacketDirection;
import io.github.mortuusars.scholar.network.packet.Packet;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;

import java.util.function.Function;

public class ClientPackets {
    public static void registerS2CPackets() {
    }

    public static void sendToServer(Packet packet) {
        ClientPlayNetworking.send(packet.getId(), packet.toBuffer(PacketByteBufs.create()));
    }

    private record ClientHandler(Function<FriendlyByteBuf, Packet> decodeFunction) implements ClientPlayNetworking.PlayChannelHandler {
        @Override
        public void receive(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buf, PacketSender responseSender) {
            Packet packet = decodeFunction.apply(buf);
            packet.handle(PacketDirection.TO_CLIENT, null);
        }
    }
}
