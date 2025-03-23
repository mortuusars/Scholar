package io.github.mortuusars.scholar.client.resource;

import io.github.mortuusars.scholar.PlatformHelper;
import io.github.mortuusars.scholar.Scholar;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class BuiltInResourcePacks {
    public static List<Pack> get() {
        List<Pack> packs = new java.util.ArrayList<>();

        packs.add(new Pack(
                Scholar.resource("colored_books"),
                Component.translatable("resourcepack.scholar.colored_books.name"),
                new Activation(ActivationType.DEFAULT_ENABLED)));
        packs.add(new Pack(
                Scholar.resource("chiseled_bookshelf_colored_books"),
                Component.translatable("resourcepack.scholar.chiseled_bookshelf_colored_books.name"),
                new Activation(ActivationType.DEFAULT_ENABLED)));

        if (PlatformHelper.isModLoading("lolmcbv")) {
            packs.add(new Pack(
                    Scholar.resource("chiseled_bookshelf_colored_books_lolmcbv_compat"),
                    Component.translatable("resourcepack.scholar.chiseled_bookshelf_colored_books_lolmcbv_compat.name"),
                    new Activation(ActivationType.DEFAULT_ENABLED, ActivationType.DEFAULT_DISABLED)));
        }

        return packs;
    }

    public record Pack(ResourceLocation id, Component name, Activation activation) {}

    public record Activation(ActivationType fabric, ActivationType neoforge) {
        public Activation(ActivationType type) {
            this(type, type);
        }
    }

    public enum ActivationType {
        DEFAULT_DISABLED,
        DEFAULT_ENABLED,
        ALWAYS_ENABLED;
    }
}
