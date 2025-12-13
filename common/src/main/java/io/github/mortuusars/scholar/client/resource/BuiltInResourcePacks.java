package io.github.mortuusars.scholar.client.resource;

import io.github.mortuusars.scholar.Scholar;
import io.github.mortuusars.scholar.integration.Mods;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

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

        if (Mods.MCBV.isLoading()) {
            packs.add(new Pack(
                    Scholar.resource("chiseled_bookshelf_colored_books_lolmcbv_compat"),
                    Component.translatable("resourcepack.scholar.chiseled_bookshelf_colored_books_lolmcbv_compat.name"),
                    new Activation(ActivationType.DEFAULT_ENABLED)));
        }

        if (Mods.WOODWORKS.isLoading()) {
            packs.add(new Pack(
                    Scholar.resource("chiseled_bookshelf_colored_books_abnww_compat"),
                    Component.translatable("resourcepack.scholar.chiseled_bookshelf_colored_books_abnww_compat.name"),
                    new Activation(ActivationType.DEFAULT_ENABLED)));
        }

        if (Mods.WOODSTER.isLoading()) {
            packs.add(new Pack(
                    Scholar.resource("chiseled_bookshelf_colored_books_woodster_compat"),
                    Component.translatable("resourcepack.scholar.chiseled_bookshelf_colored_books_woodster_compat.name"),
                    new Activation(ActivationType.DEFAULT_ENABLED)));
        }

        return packs;
    }

    public record Pack(Identifier id, Component name, Activation activation) {}

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
