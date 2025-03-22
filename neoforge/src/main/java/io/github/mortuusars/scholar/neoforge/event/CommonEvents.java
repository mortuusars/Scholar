package io.github.mortuusars.scholar.neoforge.event;

import io.github.mortuusars.scholar.Scholar;
import io.github.mortuusars.scholar.network.neoforge.PacketsImpl;
import io.github.mortuusars.scholar.network.packet.C2SPackets;
import io.github.mortuusars.scholar.network.packet.CommonPackets;
import io.github.mortuusars.scholar.network.packet.Packet;
import io.github.mortuusars.scholar.network.packet.S2CPackets;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@SuppressWarnings("unused")
public class CommonEvents {
    @EventBusSubscriber(modid = Scholar.ID, bus = EventBusSubscriber.Bus.MOD)
    public static class ModBus {
        @SubscribeEvent
        public static void commonSetup(FMLCommonSetupEvent event) {
            event.enqueueWork(() -> {
                CauldronInteraction.WATER.map().put(Items.WRITABLE_BOOK, CauldronInteraction.DYED_ITEM);
                CauldronInteraction.WATER.map().put(Items.WRITTEN_BOOK, CauldronInteraction.DYED_ITEM);
            });
        }

        @SuppressWarnings("unchecked")
        @SubscribeEvent
        public static void registerPackets(RegisterPayloadHandlersEvent event) {
            PayloadRegistrar registrar = event.registrar("1");
            // This monstrosity is to avoid having to define packets for forge and fabric separately.
            for (CustomPacketPayload.TypeAndCodec<? extends FriendlyByteBuf, ? extends CustomPacketPayload> definition : S2CPackets.getDefinitions()) {
                registrar.playToClient((CustomPacketPayload.Type<Packet>) definition.type(),
                        (StreamCodec<FriendlyByteBuf, Packet>) definition.codec(), PacketsImpl::handle);
            }

            for (CustomPacketPayload.TypeAndCodec<? extends FriendlyByteBuf, ? extends CustomPacketPayload> definition : C2SPackets.getDefinitions()) {
                registrar.playToServer((CustomPacketPayload.Type<Packet>) definition.type(),
                        (StreamCodec<FriendlyByteBuf, Packet>) definition.codec(), PacketsImpl::handle);
            }

            for (CustomPacketPayload.TypeAndCodec<? extends FriendlyByteBuf, ? extends CustomPacketPayload> definition : CommonPackets.getDefinitions()) {
                registrar.playBidirectional((CustomPacketPayload.Type<Packet>) definition.type(),
                        (StreamCodec<FriendlyByteBuf, Packet>) definition.codec(), PacketsImpl::handle);
            }
        }
    }

    public static class GameBus {
    }
}
