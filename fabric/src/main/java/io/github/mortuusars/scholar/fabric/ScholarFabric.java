package io.github.mortuusars.scholar.fabric;

import fuzs.forgeconfigapiport.api.config.v2.ForgeConfigRegistry;
import io.github.mortuusars.scholar.Config;
import io.github.mortuusars.scholar.Scholar;
import io.github.mortuusars.scholar.item.ColoredWritableBookItem;
import io.github.mortuusars.scholar.visual.BookColors;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.fml.config.ModConfig;

public class ScholarFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        ForgeConfigRegistry.INSTANCE.register(Scholar.ID, ModConfig.Type.COMMON, Config.Common.SPEC);
        ForgeConfigRegistry.INSTANCE.register(Scholar.ID, ModConfig.Type.CLIENT, Config.Client.SPEC);

        Scholar.init();

        addItemsToCreativeTab();
    }

    private static void addItemsToCreativeTab() {
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.TOOLS_AND_UTILITIES).register(content -> {
            Item lastItem = Items.WRITABLE_BOOK;
            for (DyeColor color : BookColors.getSortedColors().keySet()) {
                ColoredWritableBookItem coloredBook = Scholar.Items.COLORED_WRITABLE_BOOKS.get(color).get();
                content.addAfter(lastItem, coloredBook);
                lastItem = coloredBook;
            }
        });
    }
}