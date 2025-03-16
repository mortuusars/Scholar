package io.github.mortuusars.scholar.network.fabric;

import io.github.mortuusars.scholar.network.packet.CommonPackets;
import io.github.mortuusars.scholar.network.packet.Packet;
import io.github.mortuusars.scholar.network.packet.S2CPackets;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public class FabricS2CPacketHandler {
    @SuppressWarnings("unchecked")
    public static void register() {
        for (var definition : S2CPackets.getDefinitions()) {
            ClientPlayNetworking.registerGlobalReceiver(
                    (CustomPacketPayload.Type<Packet>) definition.type(), FabricS2CPacketHandler::handleClientboundPacket);
        }

        for (var definition : CommonPackets.getDefinitions()) {
            ClientPlayNetworking.registerGlobalReceiver(
                    (CustomPacketPayload.Type<Packet>) definition.type(), FabricS2CPacketHandler::handleClientboundPacket);
        }
    }

    private static <T extends Packet> void handleClientboundPacket(T payload, ClientPlayNetworking.Context context) {
        payload.handle(PacketFlow.CLIENTBOUND, context.player());
    }
}
