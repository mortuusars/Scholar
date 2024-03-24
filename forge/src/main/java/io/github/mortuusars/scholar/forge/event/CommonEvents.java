package io.github.mortuusars.scholar.forge.event;

import io.github.mortuusars.scholar.Scholar;
import io.github.mortuusars.scholar.item.ColoredWritableBookItem;
import io.github.mortuusars.scholar.visual.BookColors;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public class CommonEvents {
    public static class ModBus {
//        @SubscribeEvent
//        public static void commonSetup(FMLCommonSetupEvent event) {
//            event.enqueueWork(() -> {
//            });
//        }

        @SubscribeEvent
        public static void onCreativeTabsBuild(BuildCreativeModeTabContentsEvent event) {
            if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
                for (DyeColor color : BookColors.getSortedColors().keySet()) {
                    ColoredWritableBookItem coloredBook = Scholar.Items.COLORED_WRITABLE_BOOKS.get(color).get();
                    event.accept(coloredBook);
                }
            }
        }
    }

    public static class ForgeBus {
//        @SubscribeEvent
//        public static void serverStarting(ServerStartingEvent event) {
//            Exposure.initServer(event.getServer());
//        }
//
//        @SubscribeEvent
//        public static void registerCommands(RegisterCommandsEvent event) {
//            ArgumentTypeInfos.registerByClass(ShaderLocationArgument.class, SingletonArgumentInfo.contextFree(ShaderLocationArgument::new));
//
//            ExposureCommand.register(event.getDispatcher());
//            ShaderCommand.register(event.getDispatcher());
//            TestCommand.register(event.getDispatcher());
//        }
    }
}
