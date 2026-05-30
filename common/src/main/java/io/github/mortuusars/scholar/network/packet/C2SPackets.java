package io.github.mortuusars.scholar.network.packet;

import io.github.mortuusars.scholar.network.packet.serverbound.*;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.util.List;

public class C2SPackets {
    public static List<CustomPacketPayload.TypeAndCodec<? extends FriendlyByteBuf, ? extends CustomPacketPayload>> getDefinitions() {
        return List.of(
                new CustomPacketPayload.TypeAndCodec<>(LecternEditBookC2SP.TYPE, LecternEditBookC2SP.STREAM_CODEC),
                new CustomPacketPayload.TypeAndCodec<>(SetBookmarkC2SP.TYPE, SetBookmarkC2SP.STREAM_CODEC),
                new CustomPacketPayload.TypeAndCodec<>(SetCustomAuthorInHandC2SP.TYPE, SetCustomAuthorInHandC2SP.STREAM_CODEC),
                new CustomPacketPayload.TypeAndCodec<>(SetCustomAuthorOnLecternC2SP.TYPE, SetCustomAuthorOnLecternC2SP.STREAM_CODEC),
                new CustomPacketPayload.TypeAndCodec<>(StartedReadingInHandC2SP.TYPE, StartedReadingInHandC2SP.STREAM_CODEC),
                new CustomPacketPayload.TypeAndCodec<>(StoppedReadingInHandC2SP.TYPE, StoppedReadingInHandC2SP.STREAM_CODEC),
                new CustomPacketPayload.TypeAndCodec<>(SetGoldenSkinInHandC2SP.TYPE, SetGoldenSkinInHandC2SP.STREAM_CODEC),
                new CustomPacketPayload.TypeAndCodec<>(SetGoldenSkinOnLecternC2SP.TYPE, SetGoldenSkinOnLecternC2SP.STREAM_CODEC)
        );
    }
}
