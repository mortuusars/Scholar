package io.github.mortuusars.scholar.forge.event;

import io.github.mortuusars.scholar.Config;
import io.github.mortuusars.scholar.Scholar;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.world.item.Items;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@SuppressWarnings("unused")
public class CommonEvents {
    @Mod.EventBusSubscriber(modid = Scholar.ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModBus {
        @SubscribeEvent
        public static void commonSetup(FMLCommonSetupEvent event) {
            event.enqueueWork(() -> {
                if (Config.Common.WRITABLE_BOOK_COLORING.get()) {
                    CauldronInteraction.WATER.put(Items.WRITABLE_BOOK, CauldronInteraction.DYED_ITEM);
                }

                if (Config.Common.WRITTEN_BOOK_COLORING.get()) {
                    CauldronInteraction.WATER.put(Items.WRITTEN_BOOK, CauldronInteraction.DYED_ITEM);
                }
            });
        }
    }

    public static class ForgeBus {
    }
}
