package io.github.mortuusars.scholar.network.packet;

import io.github.mortuusars.scholar.network.packet.server.LecternEditBookC2SP;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.util.List;

public class C2SPackets {
    public static List<CustomPacketPayload.TypeAndCodec<? extends FriendlyByteBuf, ? extends CustomPacketPayload>> getDefinitions() {
        return List.of(
                new CustomPacketPayload.TypeAndCodec<>(LecternEditBookC2SP.TYPE, LecternEditBookC2SP.STREAM_CODEC)
        );
    }
}
