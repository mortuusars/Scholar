package io.github.mortuusars.scholar.client.chiseled_bookshelf;

import com.mojang.serialization.Codec;
import io.github.mortuusars.scholar.Scholar;
import io.github.mortuusars.scholar.client.util.HexColor;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.HashMap;
import java.util.Map;

public class BookshelfItemColorsReloadListener extends SimpleJsonResourceReloadListener<Map<Identifier, Integer>> {
    public static final Identifier ID = Scholar.resource("bookshelf_item_colors_reload_listener");

    public BookshelfItemColorsReloadListener() {
        super(Codec.unboundedMap(Identifier.CODEC, HexColor.CODEC), FileToIdConverter.json("chiseled_bookshelf/item_colors"));
    }

    @Override
    protected void apply(Map<Identifier, Map<Identifier, Integer>> map, ResourceManager resourceManager, ProfilerFiller profiler) {
        Map<Identifier, Integer> result = new HashMap<>();

        for (Map<Identifier, Integer> value : map.values()) {
            result.putAll(value);
        }

        ChiseledBookshelfColors.ITEM_COLORS = result;
    }
}